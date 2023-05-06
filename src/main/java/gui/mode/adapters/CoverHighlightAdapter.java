package gui.mode.adapters;

import editor.ExBadRef;
import editor.i_Anchor;
import editor.i_Body;
import java.awt.event.MouseEvent;
import geom.ExDegeneration;
import geom.Plane3d;
import geom.Ray3d;
import geom.Vect3d;
import gui.EdtController;
import gui.StlEdtCanvasController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import maquettes.i_Cover;
import opengl.scenegl.Scene2d;

/**
 * HihgLight adapter for covers
 * @author Vladimir
 */
public class CoverHighlightAdapter extends BasicEdtMouseAdapter {
    EdtController _ctrl;
    String _selectedID;
    
    public CoverHighlightAdapter(EdtController ctrl, StlEdtCanvasController canvas) {
        super(canvas);
        _ctrl = ctrl;
        _selectedID = null;
    }
    
    
    @Override
    public void mousePressed(MouseEvent e) {
        try {
            if (_block) return;
            boolean isLMB = SwingUtilities.isLeftMouseButton(e);
            boolean isRMB = SwingUtilities.isRightMouseButton(e);
            if (!isLMB && !isRMB) return;
            _selectedID = getSelectedID(e.getX(), e.getY());
            
            if (_selectedID != null) {
                if (isLMB) {
                    _ctrl.getFocusCtrl().setFocusOnCover(_selectedID);
                    _ctrl.getFocusCtrl().highlightCover(_selectedID);
                    _ctrl.redraw();
                }
            }
        } catch (ExDegeneration ex) {
            Logger.getLogger(CoverHighlightAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        try {
            if (_block) return;
            
            String selectedID = getSelectedID(e.getX(), e.getY());
            if (selectedID != null) {
                // Выделяем тело, не фокусируясь на нём.
                if (!selectedID.equals(_selectedID)) {
                    _selectedID = selectedID;
                    _ctrl.getFocusCtrl().highlightCover(_selectedID);
                }
            }
        } catch (ExDegeneration ex) {
            Logger.getLogger(CoverHighlightAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public final HashMap<String, Vect3d> getPickedMeshes(int x, int y) throws ExDegeneration {
        HashMap<String, Vect3d> result = new HashMap<>();
        double[] coord = new double[2];
        ArrayList<i_Cover> points = (ArrayList<i_Cover>) _ctrl.getMeshes();
        for (i_Cover point : points) 
          result.put(point.id(), point.intersectWithRay(_ctrl.getScene().getRender(), canvas().getSightRay(x, y), x, y));

        return result;
    }
    
    private String getSelectedID(int mouseX, int mouseY) throws ExDegeneration {
        
        HashMap<String, Vect3d> intersectSet = new HashMap<>();
        intersectSet.putAll(getPickedMeshes(mouseX, mouseY));
        // Составляем список точек пересечения прямой с остальными мешами
        if (intersectSet.isEmpty()) {
          intersectSet.putAll(getSelectedMeshes(mouseX, mouseY));
        }

        String selectedID = null;

        if (!intersectSet.isEmpty()) {
          // Находим ближайшую точку пересечения луча с телом.
          double currDist = Double.MAX_VALUE;
          for (Map.Entry entry: intersectSet.entrySet()) {
            Vect3d v = (Vect3d)entry.getValue();
            if (v == null) continue;
            double tempDist = Vect3d.dist(_canvas.getScene().getCameraPosition().eye(), v);
            if (currDist > tempDist) {
              selectedID = (String)entry.getKey();
              currDist = tempDist;
            }
          }
        }
        return selectedID;
    }
    
    
    public final HashMap<String, Vect3d> getSelectedMeshes(int x, int y) {
        HashMap<String, Vect3d> result = new HashMap<>();
        try {
          Ray3d sight = canvas().getSightRay(x, y);
          result.putAll(getPickedMeshes(x, y));
          ArrayList<Vect3d> intersectPoints = new ArrayList<>();
            for (i_Cover cover : _ctrl.getMeshes()) {
              intersectPoints.add(cover.intersectWithRay(_canvas.getScene().getRender(), sight, x, y)); //!! TODO: запилить пересечение с лучом
              Vect3d intersect = null;
              if (intersectPoints.size() == 1) {
                if (canvas().isInViewingCube(intersectPoints.get(0)))
                  intersect = intersectPoints.get(0);
              } else if (intersectPoints.size() > 1){
                double currDist = Double.MAX_VALUE;
                Vect3d currPoint = null;
                for (Vect3d intersectPoint : intersectPoints) {
                  if (canvas().isInViewingCube(intersectPoint)) {
                    double tempDist = Vect3d.dist(canvas().getScene().getCameraPosition().eye(), intersectPoint);
                    if (currDist > tempDist) {
                      currDist = tempDist;
                      currPoint = intersectPoint;
                    }
                  }
                }
                intersect = currPoint;
              }
              // Добавляем ближайшую видимую точку.
              if (intersect != null) {
                result.put(cover.id(), intersect);
              }
            }
        } catch (ExDegeneration ex) {}
        return result;
    }
        
    public Vect3d getVect(double mouseX, double mouseY) throws ExDegeneration {
        Ray3d sight = _canvas.getSightRay(mouseX, mouseY);
        Plane3d viewPlane = ((Scene2d)_canvas.getScene()).getViewPlane();
        return sight.intersectionWithPlane(viewPlane);
    }
    
    public String id() {
      return _selectedID;
    }
    
}
