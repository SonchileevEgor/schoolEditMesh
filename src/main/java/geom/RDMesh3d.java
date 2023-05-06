/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package geom;

import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author e-son
 */
public class RDMesh3d {
    private ArrayList<RhombicDodecahedron3d> _elements;
    private float radius = 2.0f; // радиус ромбододекаэдра
    private float spacing = 1.0f; // расстояние между ромбододекаэдрами
    
    public RDMesh3d() {
        this._elements = new ArrayList<>();
    }
    
    public RDMesh3d(ArrayList<RhombicDodecahedron3d> elements) {
        this._elements = elements;
    }
    
    public ArrayList<RhombicDodecahedron3d> getElements() {
        return _elements;
    }

    public void setElements(ArrayList<RhombicDodecahedron3d> _elements) {
        this._elements = _elements;
    }

    @Override
    public String toString() {
        return "RDMesh3d{" + "_elements=" + _elements + '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RDMesh3d other = (RDMesh3d) obj;
        return Objects.equals(this._elements, other._elements);
    }
    
    public void add(RhombicDodecahedron3d rhombicDodecahedron) {
        this._elements.add(rhombicDodecahedron);
    }
    
    public void clear() {
        this._elements.clear();
    }
    
    public void generate(int limit) throws ExGeom {
        Vect3d center = new Vect3d(0.0f, 0.0f, 0.0f); // центр первого ромбододекаэдра

        RhombicDodecahedron3d firstRhombicDodecahedron = new RhombicDodecahedron3d(center, center, center);
//        firstRhombicDodecahedron.setPosition(center.x, center.y, center.z);
//        firstRhombicDodecahedron.render();

        ArrayList<Vect3d> positions = new ArrayList<>();
        positions.add(center);

        for (int i = 1; i < limit; i++) {
            Vect3d position = findNextPosition(positions, radius + spacing);
            if (position != null) {
                RhombicDodecahedron3d rhombicDodecahedron = new RhombicDodecahedron3d(position, position, position);
//                rhombicDodecahedron.setPosition(position.x, position.y, position.z);
//                rhombicDodecahedron.render();
                positions.add(position);
            }
        }
    }

    private Vect3d findNextPosition(ArrayList<Vect3d> positions, float minDistance) {
        for (int i = 0; i < 100; i++) {
            Vect3d position = generateRandomPosition(minDistance);
            boolean isValid = true;
            for (Vect3d existingPosition : positions) {
                if (Vect3d.dist(existingPosition, position) < minDistance) {
                    isValid = false;
                    break;
                }
            }
            if (isValid) {
                return position;
            }
        }
        return null;
    }

    private Vect3d generateRandomPosition(float minDistance) {
        float x = (float) (Math.random() * 2 - 1) * radius * 2;
        float y = (float) (Math.random() * 2 - 1) * radius * 2;
        float z = (float) (Math.random() * 2 - 1) * radius * 2;
        return new Vect3d(x, y, z).normalize().mul(minDistance + radius);
    }
}
