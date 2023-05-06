package maquettes;

import geom.ExDegeneration;
import geom.ExGeom;
import geom.Polygon3d;
import geom.Rib3d;
import geom.Vect3d;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import toxi.geom.AABB;
import toxi.geom.Vec3D;
import toxi.geom.mesh.BoxSelector;
import toxi.geom.mesh.Face;
import toxi.geom.mesh.Mesh3D;
import toxi.geom.mesh.STLReader;
import toxi.geom.mesh.Vertex;
import toxi.geom.mesh.VertexSelector;
import toxi.geom.mesh.WEFace;
import toxi.geom.mesh.WETriangleMesh;
import toxi.geom.mesh.WEVertex;
import toxi.geom.mesh.WingedEdge;
import geom.Checker;
import geom.Plane3d;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import opengl.Drawer;
import opengl.Render;
import opengl.TypeFigure;
import opengl.colorgl.ColorGL;
import opengl.drawing.DrawingQueue;
import opengl.drawing.i_DrawingAction;

/**
 * Реализация алгоритма выполнения булевых операций над триангулированными проверхностями
 * @author Vladimir
 */
public class MeshIntersect {
    private static ArrayList<Mesh3D> _meshes;
    public static Render _ren;
    public static ArrayList<Rib3d> triangl;
    
    /**
     * Главный метод класса, который выполняет операцию пересечения mesh3D
     * @param meshes
     * @param ren - для тестирования
     * @return
     * @throws geom.ExDegeneration
     */
    public static Mesh3D intersectMeshes(Render ren, ArrayList<Mesh3D> meshes) throws ExDegeneration{
        _ren = ren;
        _meshes = new ArrayList<>(meshes);
        
        //блок ниже - исключительно временная мера и предназначена для отладки на тестовых моделях
        STLReader stlr = new STLReader();
        _meshes.clear();
        //tetr4 + tetr5 = ok
        //tetr4 + cube3 = fail
        //cube3 + hex = ok
        //tetr4 + hex = ok
        _meshes.add(stlr.loadBinary("shape_tetrahedron4.stl", WETriangleMesh.class));
        _meshes.add(stlr.loadBinary("shape_tetrahedron5.stl", WETriangleMesh.class));

        //для каждого mesh3D получаем его AABB и добавляем в список
        ArrayList<AABB> boundingBoxes = new ArrayList<>();
        for (Mesh3D tmp : _meshes) {
            boundingBoxes.add(tmp.getBoundingBox());
        }

        //строим индикаторный список треугольников
        //помечаем те, что находятся в пересечении AABB своих mesh3D
        //первый индекс - номер меша, второй - треугольника в нем
        ArrayList<Integer> indicator[] = indicateMeshes(boundingBoxes);

        //ищем первое пересечение треугольников
        int[] indexes = new int[4];
        ArrayList<Vec3D> currentIntersect = findIntersect(indicator, indexes);

        //reTriangularList - hashmap в котором содержится информация о том, 
        //какие рёбра из цикла пересечения через какой треугольник проходят -
        //каждому треугольнику сопоставляется список рёбер
        HashMap<Face, ArrayList<WingedEdge>> reTriangularList = new HashMap<>();
        try{
            //строим цикл пересечения
            buildLoopIntersect(currentIntersect, indexes, reTriangularList);
        } catch (Throwable ex){
            System.out.println("buildLoopIntersect " + ex.getLocalizedMessage());
        }
        //currentIntersect - цикл пересечения

        //Для каждого треугольника, через который проходит цикл пересечения
        //вызываем метод ре-триангуляции с ограничениями.
        //В качестве ограничений служат те рёбра из общего цикла пересечения,
        //которые задевают данный треугольник.
        ArrayList<Face> newFaces;
        for (Face f : reTriangularList.keySet()){
            newFaces = (ArrayList)reTriangular(f, reTriangularList.get(f));
            
            //Удаляем из mesh3D треугольник, для которого вызывали метод ретриангуляции
            //и на его место вставляем список треугольников, 
            //полученных из исходного методом ретриангуляции с ограничениями
            if (!((WETriangleMesh)_meshes.get(f.accessory)).faces.remove(f)) {
                f.accessory = 1 - f.accessory;
                ((WETriangleMesh)_meshes.get(f.accessory)).faces.remove(f);
            }
            for(Face add : newFaces) 
                _meshes.get(f.accessory).addFace(add.a, add.b, add.c, f.normal);//add.normal, add.uvA, add.uvB, add.uvC);
        }

        WETriangleMesh sub1 = (WETriangleMesh) divideSurface(currentIntersect, 0) // наружность
                , //внутренность 0 меша
                sub2 = (WETriangleMesh) divideSurface(currentIntersect, 1); //внутренность
        WETriangleMesh result = new WETriangleMesh();
        result = result.addMesh(sub1); 
        //result = result.addMesh(sub2);
        return result;
    }

    /**
     * Метод, который выделяет из _meshes[indexMesh] подповерхность
     * @param loop - цикл пересечения
     * @param indexMesh - индекс исходного меша в списке всех мешей
     * @return подповерхность (саб-меш)
     */
    private static Mesh3D divideSurface(List<Vec3D> loop, int indexMesh) {
        WETriangleMesh resultMesh = new WETriangleMesh();
        ArrayList<Face> triangles = new ArrayList<>(((WETriangleMesh)_meshes.get(indexMesh)).faces);
        float tolerance = 1e-5f; //eps for compare vertex

         if (loop.size() < 3)
            return new WETriangleMesh();

        boolean added = true;
        int k = 1;
        while (added){
            while(k < loop.size() - 1) {
                if (!added) k++;
                int head = k , tail = k - 1;
                added = false;
                for(Face f : triangles){
                    if ((f.b.equalsWithTolerance(loop.get(head), tolerance) && f.a.equalsWithTolerance(loop.get(tail), tolerance))
                        || (f.c.equalsWithTolerance(loop.get(head), tolerance) && f.b.equalsWithTolerance(loop.get(tail), tolerance))
                        || (f.a.equalsWithTolerance(loop.get(head), tolerance) && f.c.equalsWithTolerance(loop.get(tail), tolerance))){
                        Vec3D newVertex = null;
                        if (!loop.get(head).equalsWithTolerance(f.a, tolerance) &&
                                !loop.get(tail).equalsWithTolerance(f.a, tolerance)){
                            newVertex = f.a;
                        } else if (!loop.get(head).equalsWithTolerance(f.b, tolerance) &&
                                !loop.get(tail).equalsWithTolerance(f.b, tolerance)){
                            newVertex = f.b;
                        } else if (!loop.get(head).equalsWithTolerance(f.c, tolerance) &&
                                !loop.get(tail).equalsWithTolerance(f.c, tolerance)){
                            newVertex = f.c;
                        }
                        if (loop.size() == 3) {
                            triangles.remove(f);
                            return resultMesh.addFace(f.a, f.b, f.c);
                        }
                        if (loop.contains(newVertex)) {
                            int newPoint = loop.indexOf(newVertex);

                            resultMesh.addFace(f.a, f.b, f.c);
                            triangles.remove(f);
                            added = true;

                            if (newPoint > head) {
                                List<Vec3D> subList = new ArrayList<>();
                                    subList.addAll(loop.subList(newPoint, loop.size()));
                                    subList.addAll(loop.subList(0, head));
                                try{
                                    return resultMesh.addMesh(divideSurface(loop.subList(head, newPoint + 1), indexMesh))
                                                .addMesh(divideSurface(subList, indexMesh));
                                } catch(IndexOutOfBoundsException ex){
                                    System.out.println("maquettes.Carcasse.divideSurface() " + triangles.size()
                                            + " " + ex.getMessage() + " "
                                            + "newPoint > head");
                                }
                            } else if (newPoint < head){
                                List<Vec3D> subList = new ArrayList<>();
                                    subList.addAll(loop.subList(head, loop.size()));
                                    subList.addAll(loop.subList(0, newPoint + 1));
                                try{
                                    return resultMesh.addMesh(divideSurface(loop.subList(newPoint, head), indexMesh))
                                            .addMesh(divideSurface(subList, indexMesh));
                                } catch(IndexOutOfBoundsException ex){
                                    System.out.println("maquettes.Carcasse.divideSurface() " + triangles.size()
                                            + " " + ex.getMessage() + " "
                                            + "newPoint < head");
                                }
                            } else {
                                System.out.println("maquettes.Carcasse.divideSurface() equals newPoint and head");
                            }
                        } else {
                            loop.add(head, newVertex);
                            resultMesh.addFace(f.a, f.b, f.c);
                            triangles.remove(f);
                            added = true;
                        }
                        break;
                    }
                }
            }
        }
        return resultMesh;
    }

    /**
     * Метод построения всех возможных рёбер на данных точках
     * @param points - исходные точки
     * @return набор всех возможных рёбер
     */
    private static List<Rib3d> buildAllRibs(List<Vect3d> points) {
        List<Rib3d> result = new ArrayList<>();
        for(Vect3d p1 : points)
            for (Vect3d p2 : points) {
                if (Vect3d.equals(p1, p2)) continue;
                Rib3d rib = null;
                try {
                    rib = new Rib3d(p1, p2);
                } catch (ExDegeneration ex) {
                    System.out.println("Вырождение при построении всех возможных отрезков на данном наборе точек");
                }
                if (rib != null && !result.contains(rib))
                    result.add(rib);
        }
        return result;
    }
    
    /**
     * Метод добавляет треугольник из vertex в набор result 
     * (при условии, что в этом наборе еще нет такого же)
     * @param vertex - вершины добавляемого треугольника
     * @param result - результирующий набор
     * @return true - если треугольник добавлен, false - в противном случае.
     */
    private static boolean addFaceInTriangulation(ArrayList<Vect3d> vertex, List<Face> result) {
        boolean duplicate = false;
        for(Face face : result){
            Vect3d a = new Vect3d(face.a.x(), face.a.y(), face.a.z());
            Vect3d b = new Vect3d(face.b.x(), face.b.y(), face.b.z());
            Vect3d c = new Vect3d(face.c.x(), face.c.y(), face.c.z());
            if (Vect3d.equals(a, vertex.get(0)) || Vect3d.equals(a, vertex.get(1)) || Vect3d.equals(a, vertex.get(2)))
                if (Vect3d.equals(b, vertex.get(0)) || Vect3d.equals(b, vertex.get(1)) || Vect3d.equals(b, vertex.get(2)))
                    if (Vect3d.equals(c, vertex.get(0)) || Vect3d.equals(c, vertex.get(1)) || Vect3d.equals(c, vertex.get(2))){
                        duplicate = true;
                        break;
                    }
        }
        if (!duplicate){
            Face newFace = new Face(
                    new Vertex(new Vec3D((float) vertex.get(0).x(),
                                            (float) vertex.get(0).y(),
                                            (float) vertex.get(0).z()), 0), 
                    new Vertex(new Vec3D((float) vertex.get(1).x(), 
                                            (float) vertex.get(1).y(), 
                                            (float) vertex.get(1).z()), 0),
                    new Vertex(new Vec3D((float) vertex.get(2).x(), 
                                            (float) vertex.get(2).y(), 
                                            (float) vertex.get(2).z()), 0));
            newFace.computeNormal();
            result.add(newFace); 
            return true;
        } 
        return false;
    }
    
    /**
     * Строим треугольники по списку рёбер (фактически собираем триангуляцию по имеющимся рёбрам из нёё)
     * @param edges - рёбра, на которых строим треугольники
     * @return список построенных треугольников
     */
    private static List<Face> buildFacesListByRibsList(List<Rib3d> triangulation, Face f) {
        byte[] used = new byte[triangulation.size()]; 
        ArrayList<Face> result = new ArrayList<>();
        ArrayList<Vect3d> vertex = new ArrayList<>();
        
        for (int indexRib1 = 0; indexRib1 < triangulation.size() - 1; indexRib1++){
            Rib3d rib1 = triangulation.get(indexRib1);
            for(int indexRib2 = indexRib1 + 1; indexRib2 < triangulation.size(); indexRib2++){
                if (used[indexRib1] >= 2) break;
                else if (used[indexRib2] >= 2) continue;
                Rib3d rib2 = triangulation.get(indexRib2);
                if (!Vect3d.equals(rib1.a(), rib2.a())
                        &&!Vect3d.equals(rib1.a(), rib2.b())
                        &&!Vect3d.equals(rib1.b(), rib2.b())
                        &&!Vect3d.equals(rib1.b(), rib2.a())) continue;
                
                if (Vect3d.equals(rib1.a(), rib2.a())) {
                    Rib3d newRib = null;
                    try {
                        newRib = new Rib3d(rib1.b(), rib2.b());
                    } catch (ExDegeneration ex) {
                        System.out.println("Вырождение при выдлении треугольников по рёбрам, совпали точки");
                    }
                    if (triangulation.contains(newRib) && used[triangulation.indexOf(newRib)] < 2){
                        vertex.clear();
                        vertex.add(rib1.a()); vertex.add(rib1.b()); vertex.add(rib2.b());
                        if (addFaceInTriangulation(vertex, result)) {
                            used[triangulation.indexOf(rib1)]++;
                            used[triangulation.indexOf(rib2)]++;
                            used[triangulation.indexOf(newRib)]++;
                        }
                    }
                    
                } else if (Vect3d.equals(rib1.a(), rib2.b())){
                    Rib3d newRib = null;
                    try {
                        newRib = new Rib3d(rib1.b(), rib2.a());
                    } catch (ExDegeneration ex) {
                        System.out.println("вырождение при выдлении треугольников по рёбрам, совпали точки");
                    }
                    if (triangulation.contains(newRib) && used[triangulation.indexOf(newRib)] < 2){
                        vertex.clear();
                        vertex.add(rib1.a()); vertex.add(rib1.b()); vertex.add(rib2.a());
                        if (addFaceInTriangulation(vertex, result)) {
                            used[triangulation.indexOf(rib1)]++;
                            used[triangulation.indexOf(rib2)]++;
                            used[triangulation.indexOf(newRib)]++;
                        }
                    }
                    
                } else if (Vect3d.equals(rib1.b(), rib2.b())) {
                    Rib3d newRib = null;
                    try {
                        newRib = new Rib3d(rib1.a(), rib2.a());
                    } catch (ExDegeneration ex) {
                        System.out.println("вырождение при выдлении треугольников по рёбрам, совпали точки");
                    }
                    if (triangulation.contains(newRib) && used[triangulation.indexOf(newRib)] < 2){
                        vertex.clear();
                        vertex.add(rib1.a()); vertex.add(rib1.b()); vertex.add(rib2.a());
                        if (addFaceInTriangulation(vertex, result)) {
                            used[triangulation.indexOf(rib1)]++;
                            used[triangulation.indexOf(rib2)]++;
                            used[triangulation.indexOf(newRib)]++;
                        }
                    }
                    
                } else if (Vect3d.equals(rib1.b(), rib2.a())) {
                    Rib3d newRib = null;
                    try {
                        newRib = new Rib3d(rib1.a(), rib2.b());
                    } catch (ExDegeneration ex) {
                        System.out.println("Вырождение при выдлении треугольников по рёбрам, совпали точки");
                    }
                    if (triangulation.contains(newRib) && used[triangulation.indexOf(newRib)] < 2){
                        vertex.clear();
                        vertex.add(rib1.a()); vertex.add(rib1.b()); vertex.add(rib2.b());
                        if (addFaceInTriangulation(vertex, result)) {
                            used[triangulation.indexOf(rib1)]++;
                            used[triangulation.indexOf(rib2)]++;
                            used[triangulation.indexOf(newRib)]++;
                        }
                    }
                }
            }
        }
        
        for(Face newFace : result)
            if (newFace.normal.angleBetween(f.normal) != 0) 
                newFace.normal = newFace.normal.getInverted();
        return result;
    }
    
    /**
     * Метод трингуляции треугольника f с ограничениями weList (жадина)
     * @param f - исходный треугольник
     * @param weList - набор структурных линий
     * @return триангуляцию в виде списка рёбер
     * @throws geom.ExDegeneration
     */
    public static List<Face> reTriangular(final Face f, List<WingedEdge> weList) throws ExDegeneration {
        //конвертируем рёбра из WingedEdge в Rib3d
        final ArrayList<Rib3d> ribsList = new ArrayList<>(); //список структурных рёбер
        for (WingedEdge e : weList)
            ribsList.add(new Rib3d(new Vect3d(e.a.x(), e.a.y(), e.a.z()),
                                    new Vect3d(e.b.x(), e.b.y(), e.b.z())));
        //конвертируем вершины f в Vect3d
        Vect3d fa = new Vect3d(f.a.x(), f.a.y(), f.a.z());
        Vect3d fb = new Vect3d(f.b.x(), f.b.y(), f.b.z());
        Vect3d fc = new Vect3d(f.c.x(), f.c.y(), f.c.z());
        
        //получаем все точки из треугольника и структурных рёбер
        ArrayList<Vect3d> vertex = new ArrayList<>();
        vertex.add(fa); vertex.add(fb); vertex.add(fc);
        for(Rib3d e : ribsList) {
            if (!Checker.isPlaneContainPoint(new Plane3d(fa, fb, fc), e.a()))
                System.err.println(e.a().toString(15, true) + " not contains on plane " + fa.toString(15, true)
                + " " + fb.toString(15, true) + " " + fc.toString(15, true) );
            if (!Checker.isPlaneContainPoint(new Plane3d(fa, fb, fc), e.b()))
                System.err.println(e.b().toString() + " not contains on plane " + fa.toString(15, true)
                + " " + fb.toString(15, true) + " " + fc.toString(15, true) );
            if (!vertex.contains(e.a()))
                vertex.add(e.a());
            if (!vertex.contains(e.b()))
                vertex.add(e.b());
        }
        
        
        
        //строим все возможные отрезки
        final ArrayList<Rib3d> edges = (ArrayList<Rib3d>) buildAllRibs(vertex); 
        
        //удаляем из рассмотрения структурные ребра и добавляем их в триангуляцию
        for (Rib3d e : ribsList) 
            edges.remove(e);
        
        //сортируем все рёбра по длине
        Comparator<Rib3d> compareLength = new Comparator<Rib3d>() {
            @Override
            public int compare(Rib3d o1, Rib3d o2) {
                return Double.compare(o1.length(), o2.length());
            }
        };
        Collections.sort(edges, compareLength);

        //добавляем в триангуляцию структурные рёбра
        final List<Rib3d> triangulation = new ArrayList<>();
        for(Rib3d e : ribsList)
            triangulation.add(e);
            
        DrawingQueue.clear();
            DrawingQueue.add(new i_DrawingAction() {
                @Override
                public void draw(Render ren) {
                    Drawer.setObjectColor(_ren, ColorGL.BLACK);
                    drawRibs(edges);
                    Drawer.setObjectColor(_ren, ColorGL.BLUE);
                    drawRibs(triangulation);
                }

                @Override
                public int priority() {
                    return 1;
                }
            });
           // _ren.getDrawable().display();
        //бежим по всем рёбрам от самого короткого и добавляем их, 
        //если они не пересекают уже имеющиеся в триангуляции
        for (Rib3d e : edges) {
            //проверить на пересечение с имеющимися отрезками и добавить
            if (!Checker.checkIntersectRib(triangulation, e)) 
                triangulation.add(e);
        }
        triangl = new ArrayList(triangulation);
        //собрать из списка рёбер треугольники
        final ArrayList<Face> result = (ArrayList<Face>) buildFacesListByRibsList(triangulation, f);
        
        //тестирование корректности(проверка кол-ва рёбер, точек, граней для планарного графа):
        //если нарушено условие после комментария, то на Oxy рисуется результат reTriangular
        //как тестировать: нажать экспорт сцены в модель на 2D сцене 
        if (vertex.size() - triangulation.size() + result.size() + 1 != 2){
            Iterator<Rib3d> iter = triangulation.iterator();
            while (iter.hasNext()) {
                Rib3d e = iter.next();
                ArrayList<Rib3d> testList = new ArrayList<>(triangulation);
                testList.remove(e);
                if (Checker.checkIntersectRib(testList, e))
                    System.out.println("внутреннее ребро триангуляции пересекается с другими рёбрами");
            }
            
            try {
                FileWriter fw = new FileWriter("error.txt");
                for (Rib3d e : triangulation) {
                    fw.write(triangulation.indexOf(e) + " " + e.toString(3) + "\n");
                }
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(MeshIntersect.class.getName()).log(Level.SEVERE, null, ex);
            }
            DrawingQueue.clear();
            DrawingQueue.add(new i_DrawingAction() {
                @Override
                public void draw(Render ren) {
                    Drawer.setObjectColor(_ren, ColorGL.BLUE);
                    drawRibs(triangulation);
                }

                @Override
                public int priority() {
                    return 1;
                }
            });
            //_ren.getDrawable().display();
            
            DrawingQueue.clear();
            DrawingQueue.add(new i_DrawingAction() {
                @Override
                public void draw(Render ren) {
                    drawFaces(result);
                }

                @Override
                public int priority() {
                    return 1;
                }
            });
            //_ren.getDrawable().display();
        }
        
        return result;
    }

    /**
     * метод для отрисовки рёбер (для отладки)
     * @param list - список рёбер, которые нужно нарисовать
     */
    private static void drawRibs(List<Rib3d> list) {
        for (Rib3d rib : list){
            Vect3d a = new Vect3d(rib.a().x(), rib.a().y(), rib.a().z());
            Vect3d b = new Vect3d(rib.b().x(), rib.b().y(), rib.b().z());
            a = a.projectOnOXY().threeD();
            b = b.projectOnOXY().threeD();
            Drawer.drawPoint(_ren, a, 5);
            Drawer.drawPoint(_ren, b, 5);
            Drawer.drawSegment(_ren, a, b);
        }
    }
    
    /**
     * Метод для отрисовки результата ре-триангуляции (исключительно для тестирования)
     * @param faces - список треугольников, которые нужно нарисовать
     */
    private static void drawFaces(List<Face> faces) {
        ArrayList<ColorGL> colors = new ArrayList<>();
        colors.add(new ColorGL(1, 0, 0, 0.5));
        colors.add(new ColorGL(0, 1, 0, 0.5));
        colors.add(new ColorGL(0, 0, 1, 0.5));
        colors.add(new ColorGL(1, 1, 0, 0.5));
        colors.add(new ColorGL(0, 1, 1, 0.5));
        colors.add(new ColorGL(1, 0, 1, 0.5));
        colors.add(new ColorGL(0.5, 0.5, 0.5, 0.5));
        colors.add(new ColorGL(0.1, 0.1, 0.1, 0.5));
        colors.add(ColorGL.BLACK);
        Iterator<ColorGL> color = colors.iterator();
        for(Face face : faces){
            Vect3d a = new Vect3d(face.a.x(), face.a.y(), face.a.z());
            Vect3d b = new Vect3d(face.b.x(), face.b.y(), face.b.z());
            Vect3d c = new Vect3d(face.c.x(), face.c.y(), face.c.z());
            //построить проекцию на плоскость oxy
            a = a.projectOnOXY().threeD();
            b = b.projectOnOXY().threeD();
            c = c.projectOnOXY().threeD();
            Drawer.setObjectColor(_ren, color.next());
            Drawer.drawTriangle(_ren, a, b, c, TypeFigure.SOLID);
        }
        for (Face face : faces) {
            Vect3d a = new Vect3d(face.a.x(), face.a.y(), face.a.z());
            Vect3d b = new Vect3d(face.b.x(), face.b.y(), face.b.z());
            Vect3d c = new Vect3d(face.c.x(), face.c.y(), face.c.z());
            //построить проекцию на плоскость oxy
            a = a.projectOnOXY().threeD();
            b = b.projectOnOXY().threeD();
            c = c.projectOnOXY().threeD();
            Drawer.setObjectColor(_ren, ColorGL.BLACK);
            Drawer.drawPoint(_ren, a, 5);
            Drawer.drawPoint(_ren, b, 5);
            Drawer.drawPoint(_ren, c, 5);
            Drawer.drawTriangle(_ren, a, b, c, TypeFigure.WIRE);
        }
    }
    
    private static void addOnHashmap(HashMap<Face, ArrayList<WingedEdge>> reTriangularList, Face triangle1, Vec3D point1, Vec3D point2, int index) {
            ArrayList<WingedEdge> getValue = (reTriangularList.get(triangle1) != null?
                    reTriangularList.get(triangle1) : new ArrayList<WingedEdge>());
            getValue.add(new WingedEdge(new WEVertex(point1, point1.hashCode()), new WEVertex(point2, point2.hashCode()),
                    new WEFace((WEVertex)triangle1.a, (WEVertex)triangle1.b, (WEVertex)triangle1.c), index));
            reTriangularList.put(triangle1, getValue);
    }

    /**
     * Строит полилинию - цикл пересечения мешей (в данном случае 2х, которые дали первые точки пересечения)
     * @param currentIntersect - первое пересечение
     * @param triangle1 - текущий треугольник
     * @param triangle2 - текущий треугольник
     * результатом является лист точек currentIntersect, последовательно соеденив который, получим цикл пересечения
     */
    private static void buildLoopIntersect(ArrayList<Vec3D> currentIntersect, int[] indexes, HashMap<Face, ArrayList<WingedEdge>> reTriangularList){
        //проверить, ребру какого (или обоих) треугольника принадлежат новые точки пересечения
        boolean containedFirst = false, containedSecond = false;

        Vec3D currentVec1 = currentIntersect.get(currentIntersect.size() - 2);
        Vec3D currentVec2 = currentIntersect.get(currentIntersect.size() - 1);

        Face triangle1 = (Face) ((ArrayList)_meshes.get(indexes[0]).getFaces()).get(indexes[1]);
        triangle1.accessory = indexes[0];

        Face triangle2 = (Face) ((ArrayList)_meshes.get(indexes[2]).getFaces()).get(indexes[3]);
        triangle2.accessory = indexes[2];

        int idWE1 = ((WETriangleMesh)_meshes.get(indexes[0])).getNumEdges() + 1;
        int idWE2 = ((WETriangleMesh)_meshes.get(indexes[2])).getNumEdges() + 1;

        Face oldTriangle1 = null, oldTriangle2 = null;
        do{
            addOnHashmap(reTriangularList, triangle1, currentVec1, currentVec2, idWE1++);
            addOnHashmap(reTriangularList, triangle2, currentVec1, currentVec2, idWE2++);

            ArrayList<WingedEdge> weTriangle = new ArrayList<>(((WEFace)triangle1).edges);
            weTriangle.addAll(((WEFace)triangle2).edges);
            ArrayList<WingedEdge> forPoint2 = new ArrayList<>();
            for (WingedEdge we : weTriangle){
                if (currentVec2.equalsWithTolerance(we.closestPointTo(currentVec2), 1e-3f)) //т.е. точка на ребре
                    forPoint2.add(we);
            }

            oldTriangle1 = triangle1;
            oldTriangle2 = triangle2;
            //надо брать смежный всегда в одну сторону
            //currentVec2 всегда новая. По forPoint2 и нужно смотреть
            //FIXME: не правильно выбирает треугольники, т.е. выбираются не пересекающиеся (для случая 2х сфер - все работает)
            for (int i = 0; i < forPoint2.size(); i++){
                WingedEdge e = forPoint2.get(i);
                if (e.faces.get(0).equals(triangle1)) triangle1 = e.faces.get(1);
                else if (e.faces.get(0).equals(triangle2)) triangle2 = e.faces.get(1);
                     else if (e.faces.get(1).equals(triangle1)) triangle1 = e.faces.get(0);
                          else triangle2 = e.faces.get(0);
            }
            //получить пересечение для triangle1 и triangle2, посмотреть какая из точек совпадет с предыдущими и добавить новую в список
            //поменять ту, которая не совпала на новую, вернуться в начало цикла
            ArrayList<Vec3D> newIntersection = new ArrayList<>();
            newIntersection = intersectFaces(triangle1, triangle2);
            if (newIntersection.size() > 2)
                System.out.println("newIntersect size > 2");
            if (newIntersection.size() > 0) {
                containedFirst = currentIntersect.contains(newIntersection.get(0));
                containedSecond = currentIntersect.contains(newIntersection.get(1));
            } else {
                newIntersection = intersectFaces(oldTriangle1, triangle2);
                if (newIntersection.size() > 0) {
                    triangle1 = oldTriangle1;
                    containedFirst = currentIntersect.contains(newIntersection.get(0));
                    containedSecond = currentIntersect.contains(newIntersection.get(1));
                } else {
                    newIntersection = intersectFaces(triangle1, oldTriangle2);
                    triangle2 = oldTriangle2;
                    containedFirst = currentIntersect.contains(newIntersection.get(0));
                    containedSecond = currentIntersect.contains(newIntersection.get(1));
                }
            }

            if (!containedFirst && !containedSecond) {
                if (newIntersection.get(0).distanceTo(currentIntersect.get(currentIntersect.size() - 1)) >
                        newIntersection.get(1).distanceTo(currentIntersect.get(currentIntersect.size() - 1)))
                {
                    currentVec1 = newIntersection.get(1);
                    currentVec2 = newIntersection.get(0);
                } else {
                    currentVec1 = newIntersection.get(0);
                    currentVec2 = newIntersection.get(1);
                }
                if (!currentVec1.equals(currentIntersect.get(currentIntersect.size() - 1))){
                    newIntersection = intersectFaces(oldTriangle1, triangle2);
                    if (newIntersection.size() > 0) {
                        addOnHashmap(reTriangularList, oldTriangle1, currentIntersect.get(currentIntersect.size() - 1),
                                currentVec1, idWE1++);
                    } else if (intersectFaces(triangle1, oldTriangle2).size() > 0) {
                        addOnHashmap(reTriangularList, oldTriangle2, currentIntersect.get(currentIntersect.size() - 1),
                                currentVec1, idWE2++);
                    }
                }
                currentIntersect.addAll(newIntersection);

            } else {
                currentVec1 = newIntersection.get(containedFirst? 1 : 0);
                currentIntersect.add(currentVec1);

                Vec3D swap = currentVec1;
                    currentVec1 = currentVec2;
                    currentVec2 = swap;
            }
        } while (!(containedFirst && containedSecond));

        currentIntersect.remove(currentIntersect.size() - 1);
        addOnHashmap(reTriangularList, triangle1, currentIntersect.get(currentIntersect.size() - 1), currentIntersect.get(0), idWE1++);
        addOnHashmap(reTriangularList, triangle2, currentIntersect.get(currentIntersect.size() - 1), currentIntersect.get(0), idWE2++);
    }

    /**
     * Метод выполняет поиск первого пересечения среди мешей и треугольников
     * !!!TODO: в данный момент для 2х объектов. Для нескольких необходимо выносить циклы по мешам за метод
     * @param meshes - список всех мешей
     * @param indicator - индикаторный список
     * @param triangle1 - пересекающийся треугольник
     * @param triangle2 - пересекающийся треугольник
     * @return найденное пересечение в виде листа точек
     * @throws ExGeom
     */
    private static ArrayList<Vec3D> findIntersect(ArrayList<Integer>[]indicator, int[] indexes) {
        //полный перебор по тем треугольникам, 
        //которые точно попадают в пересечение firstMesh и secondMesh (получены из indicateMeshes)
        ArrayList<Vec3D> currentIntersect = new ArrayList<>();
        for(Mesh3D firstMesh : _meshes)
            for(Mesh3D secondMesh : _meshes)
                if (!firstMesh.equals(secondMesh)){
                    for(Integer indexI : indicator[_meshes.indexOf(firstMesh)])
                        for(Integer indexJ : indicator[_meshes.indexOf(secondMesh)]){
                            Face i = ((WETriangleMesh)firstMesh).faces.get(indexI);
                            Face j = ((WETriangleMesh)secondMesh).faces.get(indexJ);
                            if (i.toTriangle().getBoundingBox().intersectsTriangle(j.toTriangle()) &&
                                            j.toTriangle().getBoundingBox().intersectsTriangle(i.toTriangle()) ){
                                            currentIntersect = intersectFaces(i, j);
                                            if (currentIntersect.size() > 1) {
                                                indexes[0] = _meshes.indexOf(firstMesh);
                                                indexes[2] = _meshes.indexOf(secondMesh);
                                                indexes[1] = ((WETriangleMesh)firstMesh).faces.indexOf(i); //пересечение найдено
                                                indexes[3] = ((WETriangleMesh)secondMesh).faces.indexOf(j);
                                                return currentIntersect;
                                            }
                            }
                        }
                }
        return currentIntersect;
    }
    /**
     * Создает индикаторный список треугольников, 
     * точно попадающий в пересечение AABB тех mesh3d, которым эти треугольники принадлежат.
     * @param boundingBoxes - список AABB для всех mesh3D
     * @return индикаторный список треугольников
     */
    private static ArrayList<Integer>[] indicateMeshes(ArrayList<AABB> boundingBoxes){
        ArrayList<Integer> indicator[] = new ArrayList[_meshes.size()];
        for (int i = 0; i < _meshes.size(); i++){
            for (int j = 0; j < _meshes.size(); j++){
                if ((i != j) && boundingBoxes.get(i).intersectsBox(boundingBoxes.get(j))){
                    //получаем все точки, которые принадлежат _meshes.get(i) и попадают в boundingBoxes.get(j)
                    VertexSelector a = (new BoxSelector(_meshes.get(i), boundingBoxes.get(j))).selectVertices();
                    indicator[i] = new ArrayList<>();
                    while (!a.getSelection().isEmpty()){
                        Vertex v = a.getSelection().iterator().next();
                        WEVertex wv = (WEVertex) v;
                        //помечаем все треугольники, которые имеют в качестве вершины полученные точки
                        for (Face f : new ArrayList<>(wv.getRelatedFaces())){
                            f.accessory = i;
                            indicator[i].add(((WETriangleMesh)_meshes.get(i)).faces.indexOf(f));
                        }
                        a.getSelection().remove(v);
                    }
                }
            }
        }
        return indicator;
    }

    /**
     * Метод пересечения двух треугольников
     * @param a - первый треугольник, который проверяется на пересечение со вторым
     * @param b - второй треугольник, который проверяется на пересечение с первым
     * @return список точек пересечения, 
     *         если их 2 - значит треугольники пересекаются по ребру(соединяем эти 2 точки)
     */
    public static ArrayList<Vec3D> intersectFaces (Face a, Face b){
        ArrayList<Vect3d> arrayVect3d = new ArrayList<>();
        Polygon3d poly1, poly2;
        
        //Конвертируем face a в Polygon3d poly1
        Vec3D[] arrayVec3D = a.toTriangle().getVertexArray();
        for (Vec3D tmp : arrayVec3D)
            arrayVect3d.add(new Vect3d(tmp.x(), tmp.y(), tmp.z()));
        try {
            poly1 = new Polygon3d(new ArrayList<>(arrayVect3d));
        } catch (ExDegeneration ex) {
            return new ArrayList<>();
        }
        
        //Конвертируем face b в Polygon3d poly2
        arrayVec3D = b.toTriangle().getVertexArray();
        arrayVect3d.clear();
        for(Vec3D tmp : arrayVec3D)
            arrayVect3d.add(new Vect3d(tmp.x(), tmp.y(), tmp.z()));
        try {
            poly2 = new Polygon3d(new ArrayList<>(arrayVect3d));
        } catch (ExDegeneration ex) {
            return new ArrayList<>();
        }
        arrayVect3d.clear();
        try {
            arrayVect3d = poly1.intersect3d(poly2); //содержит все точки пересечения 2х треугольников
        } catch (ExGeom ex) { }
        
        //конвертируем обратно из Vect3d в Vec3D(toxi)
        ArrayList<Vec3D> arrayVec = new ArrayList<>();
        for(Vect3d tmp : arrayVect3d) {
            arrayVec.add(new Vec3D((float)tmp.x(), (float)tmp.y(), (float)tmp.z()));
        }
        return arrayVec;
    }    
}