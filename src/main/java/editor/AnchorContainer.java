package editor;

import anchors.DiskAnchor;
import anchors.PointAnchor;
import anchors.PolyAnchor;
import anchors.RibAnchor;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import static config.Config.LOG_LEVEL;
import geom.Vect3d;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import util.Log;

/**
 * Hashed container of anchor points.
 * @author ltwood
 */
public class AnchorContainer implements i_AnchorContainer {
  /**
   * Maps anchor ID to index in _anchors array
   */
  private HashMap<String, Integer> _map;
  private ArrayList<i_Anchor> _anchors;

  public AnchorContainer(){
    _anchors = new ArrayList<>();
    _map = new HashMap<>();
  }

  private Vect3d getVect(int idx) {
    return _anchors.get(idx).getPoint();
  }

  // i_AnchorContainer

  @Override
  public int size() {
    return _anchors.size();
  }

  @Override
  public i_Anchor get(String id) throws ExNoAnchor {
    Integer res = _map.get(id);
    if (res != null) {
      return _anchors.get(res);
    } else { // anchor with specified ID is not found
      // log event
      if (LOG_LEVEL.value() >= 2) {
        if (id != null) {
          Log.out.printf("AnchorContainer: error: can't find <%s>%n", id);
        } else {
          Log.out.printf("AnchorContainer: error: can't find anchor%n");
        }
      }
      throw new ExNoAnchor("якорь не найден");
    }
  }

  @Override
  public Vect3d getVect(String id) throws ExNoAnchor {
    Integer res = _map.get(id);
    if (res == null) {
      if (LOG_LEVEL.value() >= 2) {
        Log.out.printf("AnchorContainer: error: can't find <%s>%n", id);
      }
      throw new ExNoAnchor("якорь не найден");
    } else {
      return getVect(res);
    }
  }

  @Override
  public i_Anchor findEqual(List<String> anchorIDs){
    if (anchorIDs.size() == 2) {
      for (i_Anchor anchor : getRibAnchors()) {
        if (anchor.isEqualRib(anchorIDs.get(0), anchorIDs.get(1))) {
          return anchor;
        }
      }
    } else if (anchorIDs.size() >= 3) {
      for (i_Anchor anchor : getPolyAnchors()) {
        if (anchor.isEqualPlane(anchorIDs)) {
          return anchor;
        }
      }
    }
    return null;
  }

  @Override
  public i_Anchor findEqual(String... anchorIDs) {
    return findEqual(Arrays.asList(anchorIDs));
  }

  @Override
  public void add(i_Anchor anc){
    int idx = size();
    if (LOG_LEVEL.value() >= 2) {
      Log.out.printf("AnchorContainer: add: id=<%s>%n", anc.id());
    }
    _anchors.add(anc);
    _map.put(anc.id(), idx);
  }

  @Override
  public boolean hasTitle(String title) {
    for (i_Anchor a : _anchors) {
      if (a.getTitle().equals(title))
        return true;
    }
    return false;
  }

  @Override
  public ArrayList<i_Anchor> getAnchors() {
    return new ArrayList<>(_anchors);
  }

  @Override
  public ArrayList<String> getAnchors(String bodyID, AnchorType type) {
    ArrayList<String> anchors = new ArrayList<>();
    for (i_Anchor a : _anchors) {
      if (a.getAnchorType() == type && a.getBodyID().equals(bodyID))
        anchors.add(a.id());
    }
    return anchors;
  }

  @Override
  public void clear() {
    _anchors.clear();
    _map.clear();
  }

  @Override
  public double getMaxEdgeLength() {
    double res = 0.0;
    for (i_Anchor anchor : _anchors) {
      if (anchor.getAnchorType() == AnchorType.ANC_RIB) {
        double len = anchor.getRib().length();
        if (len > res)
          res = len;
      }
    }
    return res;
  }

  @Override
  public double getMinEdgeLength() {
    double res = Double.POSITIVE_INFINITY;
    for (i_Anchor anchor : _anchors) {
      if (anchor.getAnchorType() == AnchorType.ANC_RIB) {
        double len = anchor.getRib().length();
        if (len < res)
          res = len;
      }
    }
    if (Double.isInfinite(res))
      res = 0.0;
    return res;
  }

  @Override
  public double getMaxFaceArea() {
    double res = 0.0;
    for (i_Anchor anchor : _anchors) {
      if (anchor.getAnchorType() == AnchorType.ANC_POLY) {
        double area = anchor.getPoly().area();
        if (area > res)
          res = area;
      }
    }
    return res;
  }

  @Override
  public double getMinFaceArea() {
    double res = Double.POSITIVE_INFINITY;
    for (i_Anchor anchor : _anchors) {
      if (anchor.getAnchorType() == AnchorType.ANC_POLY) {
        double area = anchor.getPoly().area();
        if (area < res)
          res = area;
      }
    }
    if (Double.isInfinite(res))
      res = 0.0;
    return res;
  }

  @Override
  public ArrayList<i_Anchor> getAnchorsByType(final AnchorType type) {
    return new ArrayList<>(Collections2.filter(_anchors,
            new Predicate<i_Anchor>(){
              @Override
              public boolean apply(i_Anchor a){
                return a.getAnchorType() == type;
              }
            }));
  }

  @Override
  public ArrayList<PointAnchor> getPointAnchors() {
    return new ArrayList<>(Collections2.transform(getAnchorsByType(AnchorType.ANC_POINT),
            new Function<i_Anchor, PointAnchor>(){
              @Override
              public PointAnchor apply(i_Anchor f) {
                return (PointAnchor)f;
              }
            }));
  }

  @Override
  public ArrayList<RibAnchor> getRibAnchors() {
    return new ArrayList<>(Collections2.transform(getAnchorsByType(AnchorType.ANC_RIB),
            new Function<i_Anchor, RibAnchor>(){
              @Override
              public RibAnchor apply(i_Anchor f) {
                return (RibAnchor)f;
              }
            }));
  }

  @Override
  public ArrayList<PolyAnchor> getPolyAnchors() {
    return new ArrayList<>(Collections2.transform(getAnchorsByType(AnchorType.ANC_POLY),
            new Function<i_Anchor, PolyAnchor>() {
              @Override
              public PolyAnchor apply(i_Anchor f) {
                return (PolyAnchor) f;
              }
            }));
  }

  @Override
  public ArrayList<DiskAnchor> getDiskAnchors() {
    return new ArrayList<>(Collections2.transform(getAnchorsByType(AnchorType.ANC_DISK),
            new Function<i_Anchor, DiskAnchor>() {
              @Override
              public DiskAnchor apply(i_Anchor f) {
                return (DiskAnchor) f;
              }
            }));
  }
};
