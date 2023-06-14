package maquettes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import org.smurn.jply.Element;
import org.smurn.jply.ElementReader;
import org.smurn.jply.ElementType;
import org.smurn.jply.PlyReaderFile;
import toxi.geom.Vec3D;
import toxi.geom.mesh.Vertex;
import toxi.geom.mesh.WETriangleMesh;

/**
 *
 * @author Vladimir Kurgansky
 */
public class PLYReader {
    private final String _fileName;
    public int[][] _vertexColor;
    
    public PLYReader(String fileName) throws FileNotFoundException{
        _fileName = fileName;
    }
    
    public WETriangleMesh loadPLY() throws IOException{
        WETriangleMesh mesh = new WETriangleMesh(_fileName);
        ArrayList<Vertex> vertex = new ArrayList<>();
        
        PlyReaderFile prf = new PlyReaderFile(_fileName);
        ElementReader elemReader = prf.nextElementReader();
        while (elemReader != null) {
            ElementType elemType = elemReader.getElementType();
            switch (elemType.getName()){
                case "vertex": {
                    vertex = saveVertex(elemReader);
                    break;
                }
                case "face" :{
                    mesh = saveFaces(elemReader, vertex);
                    break;
                }
                default: break;
            }
            elemReader.close();
            elemReader = prf.nextElementReader();
        }
        return mesh;
    }
    //добавить чтение нормалей
    private ArrayList<Vertex> saveVertex(ElementReader elemReader) throws IOException {
        ArrayList<Vertex> result = new ArrayList<>(elemReader.getCount());
        _vertexColor = new int[elemReader.getCount()][3];
        int index = 0;
        Element elem = elemReader.readElement();
        while (elem != null){
            result.add(new Vertex(new Vec3D((float)elem.getDouble("x"), (float)elem.getDouble("y"), (float)elem.getDouble("z")), index));
            try {
                _vertexColor[index][0] = elem.getInt("red");
                _vertexColor[index][1] = elem.getInt("green");
                _vertexColor[index][2] = elem.getInt("blue");
            } catch (Exception ex) {
            }
            
            index++;
            elem = elemReader.readElement();
        }
        
        return result;
    }

    private WETriangleMesh saveFaces(ElementReader elemReader, ArrayList<Vertex> vertex) throws IOException{
        WETriangleMesh mesh = new WETriangleMesh();
        int[] indexList;
        Element elem = elemReader.readElement();
        
        while (elem != null){
            elem.getType().getName();
            try{
                indexList = elem.getIntList("vertex_index");
            } catch(IllegalArgumentException e){
                indexList = elem.getIntList("vertex_indices");
            }
            mesh.addFace(vertex.get(indexList[0]), vertex.get(indexList[1]), vertex.get(indexList[2]));
            elem = elemReader.readElement();
        }
        
        return mesh;
    }
}