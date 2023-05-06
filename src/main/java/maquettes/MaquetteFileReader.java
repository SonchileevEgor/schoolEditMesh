package maquettes;


import opengl.colorgl.ColorGL;
import geom.Vect3d;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class MaquetteFileReader {

  private Cover _cover = new Cover();

  public MaquetteFileReader(String filename) throws Exception{
    FileInputStream fstream = new FileInputStream(filename);
    BufferedReader buffer = new BufferedReader(new InputStreamReader(fstream));
    int vertextCount = 0, faceCount = 0;
    boolean end_header = false;
    while(!end_header) {
      String line = buffer.readLine();
      String[] array = line.split(" ");
      if (array[0].equals("element"))
        if (array[1].equals("vertex"))
          vertextCount = Integer.parseInt(array[2]);
        else
          faceCount = Integer.parseInt(array[2]);
      else if(line.trim().equals("end_header"))
        end_header = true;
    }
    ArrayList<ColorPoint> points = readPoints(buffer, vertextCount);
    ArrayList<TriangFace> faces = readFaces(buffer, faceCount);
    buffer.close();
    fstream.close();
    _cover.addPoints(points);
    _cover.addTriangleFace(faces);
  }

  private ArrayList<ColorPoint> readPoints(BufferedReader buffer, int count) throws Exception {
    ArrayList<ColorPoint> points = new ArrayList<>();
    while(count > 0) {
      String line = buffer.readLine();
      String[] str = line.split(" ");
      float x = Float.parseFloat(str[0]);
      float y = Float.parseFloat(str[1]);
      float z = Float.parseFloat(str[2]);
      double r = Double.parseDouble(str[3]) / 256.0;
      double b = Double.parseDouble(str[5]) / 256.0;
      double g = Double.parseDouble(str[4]) / 256.0;
      ColorGL color = new ColorGL(r, g, b);
      ColorPoint cP = new ColorPoint(new Vect3d(x, y, z), color, "");
      points.add(cP);
      count--;
    }
    return points;
  }

  private ArrayList<TriangFace> readFaces(BufferedReader buffer, int count) throws Exception {
    ArrayList<TriangFace> faces = new ArrayList<>();
    while(count > 0) {
      String line = buffer.readLine();
      String[] str = line.split(" ");
      int one = Integer.parseInt(str[1]);
      int two = Integer.parseInt(str[2]);
      int three = Integer.parseInt(str[3]);
      faces.add(new TriangFace(one, two, three));
      count--;
    }

    return faces;
  }

  public Cover getCover() {
    return _cover;
  }

}