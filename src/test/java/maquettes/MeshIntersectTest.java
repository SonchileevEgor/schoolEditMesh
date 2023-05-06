package maquettes;

import geom.ExDegeneration;
import geom.Rib3d;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import toxi.geom.Vec3D;
import toxi.geom.mesh.Face;
import toxi.geom.mesh.Vertex;
import toxi.geom.mesh.WEFace;
import toxi.geom.mesh.WEVertex;
import toxi.geom.mesh.WingedEdge;

/**
 *
 * @author Vladimir
 */
public class MeshIntersectTest {
    
    public MeshIntersectTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of intersectFaces method, of class MeshIntersect.
     */
    @Test
    public void testIntersectFaces() {
        System.out.println("intersectFaces");
        Face a = new Face(new Vertex(new Vec3D(1, 0, 0), 0), 
                new Vertex(new Vec3D(0, 1, 0), 0), 
                new Vertex(new Vec3D(0, 0, 0), 0));
        Face b = new Face(new Vertex(new Vec3D(-1, 0, 0), 0), 
                new Vertex(new Vec3D(0, -1, 0), 0), 
                new Vertex(new Vec3D(0, 0, 0), 0));
        ArrayList<Vec3D> expResult = new ArrayList<>();
        expResult.add(new Vec3D(0, 0, 0));
        ArrayList<Vec3D> result = MeshIntersect.intersectFaces(a, b);
        assertEquals(expResult, result);
        
        a = new Face(new Vertex(new Vec3D(1, 0, 0), 0), 
                new Vertex(new Vec3D(0, 1, 0), 0), 
                new Vertex(new Vec3D(0, 0, 0), 0));
        b = new Face(new Vertex(new Vec3D(-1, 0.5f, 0), 0), 
                new Vertex(new Vec3D(0, -1, 0), 0), 
                new Vertex(new Vec3D(0, 0.5f, 0), 0));
        expResult.clear();
        expResult.add(new Vec3D(0, 0.5f, 0));
        expResult.add(new Vec3D(0, 0, 0));
        result = MeshIntersect.intersectFaces(a, b);
        assertArrayEquals(expResult.toArray(), result.toArray());
        
        a = new Face(new Vertex(new Vec3D(1, 0, 0), 0), 
                new Vertex(new Vec3D(0, 1, 0), 0), 
                new Vertex(new Vec3D(0, 0, 0), 0));
        b = new Face(new Vertex(new Vec3D(1, 0, 0), 0), 
                new Vertex(new Vec3D(0, 1, 0), 0), 
                new Vertex(new Vec3D(1, 1, 0), 0));
        expResult.clear();
        expResult.add(new Vec3D(1, 0, 0));
        expResult.add(new Vec3D(0, 1, 0));
        result = MeshIntersect.intersectFaces(a, b);
        assertArrayEquals(expResult.toArray(), result.toArray());
        
        a = new Face(new Vertex(new Vec3D(2, 0, 0), 0), 
                new Vertex(new Vec3D(0, 2, 0), 0), 
                new Vertex(new Vec3D(0, 0, 0), 0));
        b = new Face(new Vertex(new Vec3D(1, 1, 0), 0), 
                new Vertex(new Vec3D(2, 1, 0), 0), 
                new Vertex(new Vec3D(1, 2, 0), 0));
        expResult.clear();
        expResult.add(new Vec3D(1, 1, 0));
        result = MeshIntersect.intersectFaces(a, b);
        assertArrayEquals(expResult.toArray(), result.toArray());
        
        a = new Face(new Vertex(new Vec3D(3, 0, 0), 0), 
                new Vertex(new Vec3D(0, 3, 0), 0), 
                new Vertex(new Vec3D(0, 0, 0), 0));
        b = new Face(new Vertex(new Vec3D(1, 1, 0), 0), 
                new Vertex(new Vec3D(3, 1, 0), 0), 
                new Vertex(new Vec3D(1, 3, 0), 0));
        expResult.clear();
        expResult.add(new Vec3D(2, 1, 0));
        expResult.add(new Vec3D(1, 2, 0));
        result = MeshIntersect.intersectFaces(a, b);
        assertArrayEquals(expResult.toArray(), result.toArray());
    }

    @Test
    public void testReTriangular() throws ExDegeneration, IOException {
        System.out.println("ReTriangular");
        MeshIntersect mi = new MeshIntersect();
        
        ArrayList<WEVertex> vertex = new ArrayList<>();
        vertex.add(new WEVertex(new Vec3D(0, 6, 0), 0));
        vertex.add(new WEVertex(new Vec3D(9, 0, 0), 1));
        vertex.add(new WEVertex(new Vec3D(0, 0, 0), 3));
        vertex.add(new WEVertex(new Vec3D(5, 1, 0), 0));
        vertex.add(new WEVertex(new Vec3D(2, 2, 0), 0));
        vertex.add(new WEVertex(new Vec3D((float) 2.97026, (float) 4.01983, 0), 0));
        vertex.add(new WEVertex(new Vec3D((float) 4.54185, (float) 2.9721, 0), 0));
        
        WEFace f = new WEFace(vertex.get(0), vertex.get(1), vertex.get(2));
        ArrayList<WingedEdge> weList = new ArrayList<>();
        weList.add(new WingedEdge(new WEVertex(new Vec3D(5, 1, 0), 0),
                new WEVertex(new Vec3D(2, 2, 0), 0), f, 0));
        weList.add(new WingedEdge(new WEVertex(new Vec3D((float) 2.97026, (float) 4.01983, 0), 0),
                new WEVertex(new Vec3D(2, 2, 0), 0), f, 0));
        weList.add(new WingedEdge(new WEVertex(new Vec3D(5, 1, 0), 0),
                new WEVertex(new Vec3D((float) 4.54185, (float) 2.9721, 0), 0), f, 0));
        List<Face> result = mi.reTriangular(f, weList);
        try (FileWriter fw = new FileWriter("testReTriangle - Face.txt")) {
            for (Face ff : result)
                fw.write(result.indexOf(ff) + " " + ff.toString() + "\n");
        }
        try (FileWriter fw = new FileWriter("testReTriangle - Rib.txt")) {
            for (Rib3d ff : mi.triangl)
                fw.write(result.indexOf(ff) + " " + ff.toString() + "\n");
        }
        assertEquals(2, vertex.size() - mi.triangl.size() + result.size() + 1);
    }
}
