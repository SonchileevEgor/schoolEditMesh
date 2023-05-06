package maquettes;

import geom.Orientation;
import bodies.*;
import static config.Config.MAQUETTE_MIN_PRINT_THICKNESS;
import editor.state.DisplayParam;
import editor.IntersectAnalyser;
import editor.i_Anchor;
import editor.i_Body;
import geom.Cube3d;
import geom.ExDegeneration;
import geom.ExGeom;
import geom.ExZeroDivision;
import geom.Line3d;
import geom.Arc3d;
import geom.Circle3d;
import geom.Plane3d;
import geom.Polygon3d;
import geom.Rib3d;
import geom.Vect3d;
import gui.EdtController;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import static maquettes.BodyDimension.getBodyDimension;
import opengl.Render;
import opengl.SmartPlane;
import opengl.colorgl.ColorGL;
import toxi.geom.Vec3D;
import toxi.geom.mesh.Mesh3D;
import toxi.geom.mesh.WETriangleMesh;
import toxi.geom.mesh.WingedEdge;
import toxi.util.FileUtils;
import util.Fatal;
import util.Log;

/**
 * @author Leonid Ivanovsky
 * @author Vladimir Kurgansky
 */

public class Carcasse {

    private EdtController _controller; // controller of program
    private ArrayList<i_Body> _bodies; // bodies from scene
    private ArrayList<i_Anchor> _anchors; // anchors from scene
    private ArrayList<ColorEdge> _edges; // all edges from drawing on the scene
    private ArrayList<ColorPoint> _vertices; // all vertices from drawing on the scene
    private ArrayList<ColorFace> _faces; // all faces from drawing on the scene
    private ArrayList<BodyDimension> _dimBodies; // dimensions of  bodies on drawing
    private ArrayList<i_Cover> _covers; // cover with generated points around every edges and vertices
    private static ArrayList<Mesh3D> _meshes;

    public Carcasse(EdtController ec) {
      _controller = ec;
      _bodies = ec.getBodyContainer().getAllBodies();
      _anchors = ec.getAnchorContainer().getAnchors();
      _edges = new ArrayList<>();
      _vertices = new ArrayList<>();
      _faces = new ArrayList<>();
      _dimBodies = new ArrayList<>();
      _covers = new ArrayList<>(); 
      _meshes = new ArrayList<>();
    }
 
    /**
     * Build printing elements from carcasse
     */
    private void buildPrintingElements() {
      for (int i=0; i<_anchors.size(); i++) {
          i_Anchor anchor = _anchors.get(i);
          if (anchor.isVisible()) {
              switch (anchor.getAnchorType()) {
                  case ANC_POINT:
                      addVertice(anchor);
                      break;
                  case ANC_RIB:
                      addEdge(anchor);
                      break;
                  case ANC_POLY:
                      addFace(anchor);
                      break;
                  default: break;
              }
          }
      }
    }

    /**
     * add bounding box for cover, which got from params
     * @param TF - array list triangle faces
     * @param CP - array list points
     */
    private WETriangleMesh addMesh(ArrayList<TriangFace> TF, ArrayList<ColorPoint> CP, int cover_size){
        WETriangleMesh mesh = new WETriangleMesh();
        for(TriangFace tmp: TF){
            ColorPoint oldVertex = CP.get(tmp.getFirstPoint() - cover_size);
            Vec3D  newVertex1 = new Vec3D((float)oldVertex.getPoint().x(), (float)oldVertex.getPoint().y(), (float)oldVertex.getPoint().z());

            oldVertex = CP.get(tmp.getSecondPoint() - cover_size);
            Vec3D  newVertex2 = new Vec3D((float)oldVertex.getPoint().x(), (float)oldVertex.getPoint().y(), (float)oldVertex.getPoint().z());

            oldVertex = CP.get(tmp.getThirdPoint() - cover_size);
            Vec3D  newVertex3 = new Vec3D((float)oldVertex.getPoint().x(), (float)oldVertex.getPoint().y(), (float)oldVertex.getPoint().z());

            mesh.addFace(newVertex1, newVertex2, newVertex3);
        }
        _meshes.add(mesh);
        return mesh;
    }

    /**
     * Build cover around every edges and vertices
     */
    private void buildCover() throws ExDegeneration, ExGeom, ExZeroDivision {
        buildCoverAroundEveryVertices();
        buildCoverAroundEveryEdges();
        buildCoverAroundEveryFaces();
        buildCoverAroundEveryNonAnchorsElements();
    }


    /**
     * Build cover around every edges
     */
    private void buildCoverAroundEveryEdges() {
        double eps_for_ribs = PrintingThickness.getEdgeThickness()/2.0;
        for (int i=0; i<_edges.size(); i++) {
            int cover_size = 0;
            for (int k = 0; k < _covers.size(); k++)
                cover_size += _covers.get(k).getCover().getPointCover().size();
            EdgeCover rib_cover = new EdgeCover(_edges.get(i), eps_for_ribs, cover_size, _edges.get(i).getID());
            rib_cover.sculptModels();
            _covers.add(rib_cover);

            ArrayList<TriangFace> triangleForMesh = rib_cover.getCover().getTriangleFaces();
            ArrayList<ColorPoint> pointsForMesh = rib_cover.getCover().getPointCover();
            rib_cover.setMesh(addMesh(triangleForMesh, pointsForMesh, cover_size));
        }
    }

    /**
     * Build cover around every vertices
     */
    private void buildCoverAroundEveryVertices() {
        for (int i=0; i<_vertices.size(); i++) {
            int cover_size = 0;
            for (int k = 0; k < _covers.size(); k++)
                cover_size += _covers.get(k).getCover().getPointCover().size();
            PointCover vertice_cover = new PointCover(_vertices.get(i), cover_size, _vertices.get(i).getID());
            vertice_cover.sculptModels();
            _covers.add(vertice_cover);

            ArrayList<TriangFace> triangleForMesh = vertice_cover.getCover().getTriangleFaces();
            ArrayList<ColorPoint> pointsForMesh = vertice_cover.getCover().getPointCover();
            vertice_cover.setMesh(addMesh(triangleForMesh, pointsForMesh, cover_size));
        }
    }

    /**
     * Build cover around every faces
     */
    private void buildCoverAroundEveryFaces() throws ExDegeneration {
        double gen_thickness = PrintingThickness.getFaceThickness()/2.0;
        for (int i=0; i<_faces.size(); i++) {
            int cover_size = 0;
            for (int k = 0; k < _covers.size(); k++)
                cover_size += _covers.get(k).getCover().getPointCover().size(); 
            FaceCover face_cover = new FaceCover(_faces.get(i), gen_thickness, 
                    _dimBodies.get(i), cover_size, _faces.get(i).getID());
            face_cover.sculptModels();
            _covers.add(face_cover);

            ArrayList<TriangFace> triangleForMesh = face_cover.getCover().getTriangleFaces();
            ArrayList<ColorPoint> pointsForMesh = face_cover.getCover().getPointCover();
            face_cover.setMesh(addMesh(triangleForMesh, pointsForMesh, cover_size));
        }
    }

    /*
    * Build cover around every NonAnchors elements
    */
    private void buildCoverAroundEveryNonAnchorsElements() throws ExDegeneration, ExGeom, ExZeroDivision {
        double gen_thickness = PrintingThickness.getFaceThickness()/2.0;
        for (int i=0; i<_bodies.size(); i++) {
            i_Body element = _bodies.get(i);
            BodyType element_type = element.type();
            int cover_size = 0;
            for (int k = 0; k < _covers.size(); k++)
                cover_size += _covers.get(k).getCover().getPointCover().size();
            ArrayList<ColorPoint> pointsForMesh = new ArrayList<>();
            ArrayList<TriangFace> triangleForMesh = new ArrayList<>();
            if (element.getState().isVisible() && element.getState().hasParam(DisplayParam.FILL_COLOR)) {
                ColorGL color = (ColorGL) element.getState().getParam(DisplayParam.FILL_COLOR);
                switch (element_type) {
                    case PLANE:
                        PlaneBody plane_body = (PlaneBody) element;
                        Plane3d plane = plane_body.plane();
                        ArrayList<Vect3d> face_points = IntersectAnalyser.getIntersectionPoints(_controller.getEditor(), plane);
                        SmartPlane smart_face = new SmartPlane(face_points, plane);
                        ArrayList<Vect3d> rectangle_points = smart_face.getAsRectangle(0.5);
                        Polygon3d rectangle = new Polygon3d(rectangle_points);
                        ColorFace face = new ColorFace(rectangle, color, element.id());
                        FaceCover plane_cover = new FaceCover(face, gen_thickness,  
                                getBodyDimension(element_type), cover_size, face.getID());
                        plane_cover.sculptModels();
                        _covers.add(plane_cover);

                        triangleForMesh = plane_cover.getCover().getTriangleFaces();
                        pointsForMesh = plane_cover.getCover().getPointCover();
                        plane_cover.setMesh(addMesh(triangleForMesh, pointsForMesh, cover_size));
                        break;
                    case CYLINDER:
                        CylinderBody cylinder_body = (CylinderBody) element;
                        ColorCylinder cylinder = new ColorCylinder(cylinder_body.cylinder(), color);
                        CylinderCover cylinder_cover = new CylinderCover(cylinder_body, cylinder, gen_thickness, cover_size);
                        cylinder_cover.sculptModels();
                        _covers.add(cylinder_cover);

                        triangleForMesh = cylinder_cover.getCover().getTriangleFaces();
                        pointsForMesh = cylinder_cover.getCover().getPointCover();
                        cylinder_cover.setMesh(addMesh(triangleForMesh, pointsForMesh, cover_size));
                        break;
                    case SPHERE:
                        SphereBody sphere_body = (SphereBody) element;
                        ColorPoint point = new ColorPoint(sphere_body.getCenter(), color, sphere_body.getRadius(), element.id());
                        PointCover point_cover = new PointCover(point, cover_size, point.getID());
                        point_cover.sculptModels();
                        _covers.add(point_cover);
                        ColorPoint inside_point = new ColorPoint(sphere_body.getCenter(), color, sphere_body.getRadius() - gen_thickness, element.id());
                        int inside_cover_size = 0;
                        for (int k = 0; k < _covers.size(); k++)
                            inside_cover_size += _covers.get(k).getCover().getPointCover().size();
                        PointCover inside_point_cover = new PointCover(inside_point, inside_cover_size, point.getID());
                        inside_point_cover.SculptModels(Orientation.LEFT);
                        _covers.add(inside_point_cover);

                        triangleForMesh = point_cover.getCover().getTriangleFaces();
                        pointsForMesh = point_cover.getCover().getPointCover();
                        point_cover.setMesh(addMesh(triangleForMesh, pointsForMesh, cover_size));
                        
                        ArrayList<ColorPoint> insidePointsForMesh = inside_point_cover.getCover().getPointCover();
                        ArrayList<TriangFace> insideTriangleForMesh = inside_point_cover.getCover().getTriangleFaces();
                        inside_point_cover.setMesh(addMesh(insideTriangleForMesh, insidePointsForMesh, inside_cover_size));
                        break;
                    case CONE:
                        ConeBody cone_body = (ConeBody) element;
                        ColorCone cone = new ColorCone(cone_body.cone(), color);
                        ConeCover cone_cover = new ConeCover(cone_body, cone, gen_thickness, cover_size);
                        cone_cover.sculptModels();
                        _covers.add(cone_cover);

                        triangleForMesh = cone_cover.getCover().getTriangleFaces();
                        pointsForMesh = cone_cover.getCover().getPointCover();
                        cone_cover.setMesh(addMesh(triangleForMesh, pointsForMesh, cover_size));
                        break;
                    default: break;
                }
            }
            else {
              if (element.getState().isVisible())
              switch (element_type) {
                case LINE:{
                    LineBody line_body = (LineBody) element;
                    Line3d line = line_body.line();

                    double scene_size_milt = 1.5; //взять из диалог.окна при экспорте на печать
                    double koord = _controller.getEditor().getMaxDistance() * scene_size_milt;
                    Vect3d v1 = new Vect3d( -koord, -koord, -koord);
                    Vect3d v2 = new Vect3d( koord, -koord, -koord);
                    Cube3d cube = Cube3d.cube3dByTwoPointsAndAngle(v2, v1, Math.PI / 2);

                    ArrayList<Vect3d> point_list = cube.intersect(line);
                    Rib3d rib = new Rib3d(point_list.get(0), point_list.get(1));

                    double eps_for_ribs = PrintingThickness.getEdgeThickness()/2.0;
                    ColorGL color = (ColorGL) element.getState().getParam(DisplayParam.FILL_COLOR);
                    ColorEdge color_edge = new ColorEdge(rib, color, element.id());
                    EdgeCover rib_cover = new EdgeCover(color_edge, eps_for_ribs, cover_size, element.id());

                    rib_cover.sculptModels();
                    _covers.add(rib_cover);

                    triangleForMesh = rib_cover.getCover().getTriangleFaces();
                    pointsForMesh = rib_cover.getCover().getPointCover();
                    rib_cover.setMesh(addMesh(triangleForMesh, pointsForMesh, cover_size));
                  break;
                }
                case ARC:{
                    ArcBody arc_body = (ArcBody) element;
                    Arc3d arc = (Arc3d) arc_body.getGeom();

                    double eps = MAQUETTE_MIN_PRINT_THICKNESS.value();
                    ColorGL color = (ColorGL) element.getState().getParam(DisplayParam.FILL_COLOR);
                    ColorArc color_arc = new ColorArc(arc, color);
                    ArcCover arc_cover = new ArcCover(arc_body, color_arc, eps, cover_size);
                    arc_cover.sculptModels();
                    _covers.add(arc_cover);

                    triangleForMesh = arc_cover.getCover().getTriangleFaces();
                    pointsForMesh = arc_cover.getCover().getPointCover();
                    arc_cover.setMesh(addMesh(triangleForMesh, pointsForMesh, cover_size));
                  break;
                }
                case CIRCLE:{
                  CircleBody circle_body = (CircleBody) element;
                  Circle3d circle = (Circle3d) circle_body.circle();

                  double eps = MAQUETTE_MIN_PRINT_THICKNESS.value();
                  ColorGL color = (ColorGL) element.getState().getParam(DisplayParam.FILL_COLOR);
                  ColorCircle color_circle = new ColorCircle(circle, color);
                  CircleCover circle_cover = new CircleCover(circle_body, color_circle, eps, cover_size);
                  circle_cover.sculptModels();
                  _covers.add(circle_cover);

                  triangleForMesh = circle_cover.getCover().getTriangleFaces();
                  pointsForMesh = circle_cover.getCover().getPointCover();
                  circle_cover.setMesh(addMesh(triangleForMesh, pointsForMesh, cover_size));
                  break;
                }
                default: break;
              }
            }
        }
    }

    public void addEdges(ArrayList<ColorEdge> edges) {
        if (!edges.isEmpty()) {
            for (int i=0; i<edges.size(); i++)
                addEdge(edges.get(i));
        }
    }

    public void addEdge(i_Anchor anchor) {
        if (anchor.getState().isVisible()) {
            addEdge(new ColorEdge(anchor.getRib(), (ColorGL)anchor.getState().getParam(DisplayParam.CARCASS_COLOR), anchor.getBodyID()));
        }
    }

    public void addEdge(ColorEdge edge) {
        if (edge != null)
            _edges.add(edge);
    }


    public void addVertices(ArrayList<ColorPoint> vertices) {
        if (!vertices.isEmpty()) {
            for (int i=0; i<vertices.size(); i++)
                addVertice(vertices.get(i));
        }
    }

    public void addVertice(i_Anchor anchor) {
        double eps_for_vertices = PrintingThickness.getVerticeThickness()/2.0;
        if (anchor.getState().isVisible()) {
            addVertice(new ColorPoint(anchor.getPoint(), (ColorGL)anchor.getState().getParam(DisplayParam.POINT_COLOR), eps_for_vertices, anchor.getBodyID()));
        }
    }

    public void addVertice(ColorPoint vertice) {
        if (vertice != null)
            _vertices.add(vertice);
    }

    public void addFaces(ArrayList<ColorFace> faces) {
        if (!faces.isEmpty()) {
            for (int i=0; i<faces.size(); i++)
                addFace(faces.get(i));
        }
    }

    public void addFaces(ArrayList<Polygon3d> faces, ColorGL clr, i_Body body) {
        if (!faces.isEmpty()) {
            for (int i=0; i<faces.size(); i++) {
                addFace(new ColorFace(faces.get(i), clr, body.id()));
                _dimBodies.add(BodyDimension.getBodyDimension(body.type()));
            }
        }
    }

    public void addFace(i_Anchor anchor) {
        i_Body host_body = body(anchor.getBodyID());
        if (host_body != null) {
            ColorGL color = (ColorGL)anchor.getState().getParam(DisplayParam.FILL_COLOR);
            Polygon3d body_face = GetAnchorFaceInBody(anchor, host_body);
            if (body_face != null) {
                BodyDimension body_dim = BodyDimension.getBodyDimension(host_body.type());
                _dimBodies.add(body_dim);
                if (body_dim == BodyDimension.PLANE) {
                    addFace(new ColorFace(Polygon3d.getFaceInRightOrientation(body_face), color, anchor.getBodyID()));
                }
                else {
                    addFace(new ColorFace(body_face, color, anchor.getBodyID()));
                }
            }
        }
    }

    public void addFace(ColorFace face) {
        if (face != null)
            _faces.add(face);
    }

    public void addFace(Polygon3d face, ColorGL color, i_Body body) {
        if (face != null && color != null)
            addFace(new ColorFace(face, color, body.id()));
    }

    /**
     * Get the body, which was determined by the input parameter, string of body_id
     * @param body_id string, which contains an id of body
     * @return body, which was determined by string
     */
    private i_Body body(String body_id) {
        for (int i=0; i<_bodies.size(); i++) {
            i_Body cur_body = _bodies.get(i);
            if (cur_body.id().equals(body_id))
                return cur_body;
        }
        return null;
    }

    /**
     * Get body face, which is the anchor in this body
     * @param anchor
     * @param anchor_body
     * @return body face
     */
    private Polygon3d GetAnchorFaceInBody(i_Anchor anchor, i_Body anchor_body) {
        Polygon3d anchor_polygon = anchor.getPoly();
        ArrayList<Polygon3d> body_faces = anchor_body.getAllFaces(_controller.getEditor());
        for (int i=0; i<body_faces.size(); i++) {
            Polygon3d cur_face = body_faces.get(i);
            if (Polygon3d.isFacesEquals(cur_face, anchor_polygon))
                return cur_face;
        }
        return null;
    }

    /**
     * Return generated points of maquette around all vertices and edges
     * @return
     */
    public ArrayList<i_Cover> getCovers() {
        return _covers;
    }

    public ArrayList<ColorFace> getFaces() {
        return _faces;
    }

    public ArrayList<ColorEdge> getEdges() {
        return _edges;
    }

    public ArrayList<ColorPoint> getVertices() {
        return _vertices;
    }

    public ArrayList<Mesh3D> getMeshes() {
        return _meshes;
    }

    /**
     * Сохраняет циклы(границы) в файл из ply-файла
     * @param name
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public ArrayList<WingedEdge> saveLoop(String name) throws FileNotFoundException, IOException{
        PLYReader reader = new PLYReader(name);
        WETriangleMesh mesh = reader.loadPLY();
        ArrayList<WingedEdge> weList = new ArrayList<>();

        for(WingedEdge we : mesh.getEdges()){
            if (we.faces.size() == 1)
                weList.add(we);
        }

        DataOutputStream out = new DataOutputStream(FileUtils.createOutputStream(new File("loop.txt")));
        out.write(weList.size());
        for(WingedEdge we : weList){
           out.writeFloat(we.a.x);
           out.writeFloat(we.a.y);
           out.writeFloat(we.a.z);

           out.writeFloat(we.b.x);
           out.writeFloat(we.b.y);
           out.writeFloat(we.b.z);
        }

        out.close();
        return weList;
    }

    /**
     * Get render
     * @return render of scene
     */
    public Render getRender() {
        return _controller.getScene().getRender();
    }
    /**
     * Sculpt model of carcasse
     */
    public void sculptModels() {
      buildPrintingElements();
      try {
        buildCover();
      } catch (ExDegeneration ex) {
        Fatal.warning("Ошибка в построении каркасной модели");
      } catch (ExGeom | ExZeroDivision ex) {
        Log.out.println(ex.getMessage());
      }
    }
}