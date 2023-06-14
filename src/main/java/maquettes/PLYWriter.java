package maquettes;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import toxi.geom.Vec3D;
import toxi.util.FileUtils;
import util.Log;

/**
 *
 * @author Vladimir Kurgansky
 */
public class PLYWriter {
    private final byte[] buf = new byte[4];


    /**
     * Little-endian version for float.
     *
     * @param f
     * @return
     */
    private byte[] le(float f) {
        return le(Float.floatToRawIntBits(f));
    }

    /**
     * Little-endian version for int.
     *
     * @param i
     * @return
     */
    private byte[] le(int i) {
        buf[3] = (byte) (i >>> 24);
        buf[2] = (byte) (i >> 16 & 0xff);
        buf[1] = (byte) (i >> 8 & 0xff);
        buf[0] = (byte) (i & 0xff);
        return buf;
    }

    /**
     * @param mesh cover to export
     * @param stream output stream
     */
    public void saveMesh(Cover mesh, OutputStream stream) {
        try (BufferedOutputStream out = new BufferedOutputStream(stream, 0x20000)) {
            out.write("ply\n".getBytes());
            out.write("format binary_little_endian 1.0\n".getBytes());
            out.write(("element vertex " + mesh.getPointCover().size() + "\n")
                    .getBytes());
            out.write("property float x\n".getBytes());
            out.write("property float y\n".getBytes());
            out.write("property float z\n".getBytes());
            out.write("property uchar red\n".getBytes());
            out.write("property uchar green\n".getBytes());
            out.write("property uchar blue\n".getBytes());
            out.write(("element face " + mesh.getTriangleFaces().size() + "\n").getBytes());
            out.write("property list uchar int vertex_indices\n".getBytes());
            out.write("end_header\n".getBytes());
            Vec3D[] verts = new Vec3D[mesh.getTriangleFaces().size()];
            int i = 0, j;
            for (ColorPoint v : mesh.getPointCover()) {
                Vec3D pnt = new Vec3D((float)v.getPoint().x(), (float)v.getPoint().y(), (float)v.getPoint().z());
                verts[i++] = pnt;
            }
            try {
                for (i = 0, j = 0; i < verts.length; i++, j += 3) {
                    Vec3D v = verts[i];
                    out.write(le(v.x));
                    out.write(le(v.y));
                    out.write(le(v.z));

                    ArrayList<ColorPoint> points = mesh.getPointCover();
                    ColorPoint point_cover = points.get(i);
                    int[] rgb_components = point_cover.getColor().getRGBComponents();
                    out.write((byte)rgb_components[0]);
                    out.write((byte)rgb_components[1]);
                    out.write((byte)rgb_components[2]);
                }
            } catch (Exception e) {}
            for (TriangFace f : mesh.getTriangleFaces()) {
                out.write((byte) 3);
                out.write(le(f.getFirstPoint()));
                out.write(le(f.getSecondPoint()));
                out.write(le(f.getThirdPoint()));
            }
            out.flush();
        } catch (IOException e) {
            Log.out.printf("Error: mesh exporting mesh");
        }
    }

    /**
     * @param mesh cover to export
     * @param path file path
     */
    public void saveMesh(Cover mesh, String path) {
        try {
            saveMesh(mesh, FileUtils.createOutputStream(new File(path)));
        } catch (IOException e) {
            Log.out.printf("Error: mesh exporting mesh");
        }
    }
}