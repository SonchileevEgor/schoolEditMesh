package editor;

import geom.AbstractPolygon;
import editor.state.DisplayParam;
import geom.*;

import java.util.ArrayList;
import util.Fatal;

/**
 * Created by maks on 01.09.14.
 */
public class IntersectAnalyser {
  static public ArrayList<Vect3d> getIntersectionPoints(Editor edt, Plane3d plane){
    ArrayList<Vect3d> listRes = new ArrayList<>();
    for (i_Body bd : edt.bd().getAllBodies()) {
      // Ищем пересечение только с существующими и видимыми телами.
      if (bd.getState() == null) {
        Fatal.error(String.format("WTF Warning: body %s have bodyState == null", bd.getTitle()));
      }
      // Должно ли тело учитываться для поиска пересечений
      boolean includedInProcessing = true;
      if (!bd.exists())
        includedInProcessing = false;
      if (bd.getState().hasParam(DisplayParam.VISIBLE) && !bd.getState().isVisible())
        includedInProcessing = false;

      if (includedInProcessing) {
        AbstractPolygon abstractPolygon = bd.getIntersectionWithPlane(plane);
        listRes.addAll(abstractPolygon.getAsAbstractPolygon());
      }
    }
    return listRes;
  }
}
