/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maquettes;

import geom.Ray3d;
import geom.Sphere3d;
import geom.Vect3d;
import geom.i_Geom;
import opengl.Render;
import toxi.geom.Sphere;
import toxi.geom.Vec3D;
import toxi.geom.mesh.Mesh3D;
import toxi.geom.mesh.WETriangleMesh;

/**
 * i_Cover for loading meshes
 * @author Vladimir
 */
public class ImportCover implements i_Cover{
    private boolean _choosen = false;
    private final WETriangleMesh _mesh;
    private final Sphere3d _sphere; //приближаем все загружаемые меши - шарами минимального радиуса
    private String _id = new String(); //!! TODO: реализовать раздачу IDшников загружаемым мешам по порядку
    
    public ImportCover(WETriangleMesh mesh) {
        _mesh = mesh;
        Sphere boundSphere = _mesh.center(Vec3D.ZERO).getBoundingSphere();
        _sphere = new Sphere3d(new Vect3d(boundSphere.x, boundSphere.y, boundSphere.z), boundSphere.radius);
    }
    
    @Override
    public boolean getChosen() {
        return _choosen;
    }

    @Override
    public void setChosen(boolean state) {
        _choosen = state;
    }

    @Override
    public CoverType type() {
        return CoverType.IMPORT;
    }

    @Override
    public i_Geom getGeom() {        
        return _sphere;
    }

    @Override
    public Cover getCover() {
        return new Cover(); //заглушка. Преобразования из Mesh3D в Cover не имеют смысла.
                            //данный метод используется исключительно для построения модели, 
                            //а она уже есть и в удобном для нас Mesh3D формате. Cover - пережитки прошлого.
    }

    @Override
    public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
        Vect3d result = new Vect3d();
        try{
            result = _sphere.intersect(ray).get(0);
        } catch(IndexOutOfBoundsException ex) {
        }
        return result;
    }

    @Override
    public String id() {
        return _id;
    }

    @Override
    public void setMesh(WETriangleMesh mesh) {
        //нельзя устанавливать меш данному типу i_cover, т.к. он как раз инициализируется таким мешем.
    }
    
    @Override
    public Mesh3D getMesh() {
        return _mesh;
    }
}
