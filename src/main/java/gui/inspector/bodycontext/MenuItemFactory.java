
package gui.inspector.bodycontext;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_ALPHA;
import static builders.BodyBuilder.BLDKEY_BETA;
import static builders.BodyBuilder.BLDKEY_RIB;

import static builders.BodyBuilder.BLDKEY_A;
import static builders.BodyBuilder.BLDKEY_B;
import static builders.BodyBuilder.BLDKEY_C;
import static builders.BodyBuilder.BLDKEY_D;
import builders.RhombicDodecahedronBuilder;

import builders.DivideRibInRelationBuilder;
import builders.RhombicDodByRhombBuilder;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.InvalidBodyNameException;
import editor.i_AnchorContainer;
import editor.i_Body;
import geom.Polygon3d;
import geom.Vect3d;
import geom.i_OrientableGeom;
import gui.EdtController;
import gui.IconList;
import gui.dialog.RenameBodyDialog;
import gui.elements.PositiveSpinner;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import opengl.sceneparameters.CameraPosition;

/**
 * Body menu item builders.
 *
 * @author alexeev.
 */
public class MenuItemFactory {
  public static JMenuItem createRemoveMI(final EdtController ctrl, final String bodyID) throws ExNoBody {
    JMenuItem mi = new JMenuItem("Удалить", IconList.DELETE.getMediumIcon());
    i_Body bd = ctrl.getBody(bodyID);
    if (bd.exists() && bd.hasBuilder()) {
      mi.addActionListener(new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent ae) {
          ctrl.removeBody(bodyID);
          ctrl.redraw();
        }
      });
    } else {
      mi.setEnabled(false);
    }
    return mi;
  }

  public static JMenuItem createVisMI(final EdtController ctrl, final String bodyID) throws ExNoBody {
    i_Body bd = ctrl.getBody(bodyID);
    JMenuItem mi;
    if (bd.exists()) {
      final boolean isVisible = ctrl.isBodyVisible(bodyID);
      mi = new JMenuItem(isVisible ? "Скрыть123" : "Показать123",
              isVisible ? IconList.ACTION_HIDE.getMediumIcon() : IconList.ACTION_SHOW.getMediumIcon());
      mi.addActionListener(new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent ae) {
          try {
            i_Body bd = ctrl.getBody(bodyID);
            ctrl.setBodyVisible(bodyID, !isVisible,
                    !isVisible || bd.type() == BodyType.POINT, true, true, true);
            ctrl.notifyBodyStateChange(bodyID);
            ctrl.setUndo(isVisible ? "показать тело" : "скрыть тело");
            ctrl.redraw();
          } catch (ExNoBody ex) { }
        }
      });
    } else {
      mi = new JMenuItem("Показать", IconList.ACTION_SHOW.getMediumIcon());
      mi.setEnabled(false);
    }
    return mi;
  }
  
  public static JMenuItem buildRhombicDod(final EdtController ctrl, final String bodyID) throws ExNoBody {
    i_Body bd = ctrl.getBody(bodyID);
    JMenuItem mi;
    if (bd.exists()) {
      mi = new JMenuItem("Прилепить ромбододекаэдр", IconList.RHOMBIC_DODECAHEDRON.getMediumIcon());
      mi.addActionListener(new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent ae) {
          try {
            i_Body bd = ctrl.getBody(bodyID);
            System.out.println(bd.getGeom().type());
            
            System.out.println(bd.getAnchors());

            System.out.println(bd.getAnchors().get("0"));
            System.out.println(bd.getAnchors().get("1"));
            System.out.println(bd.getAnchors().get("2"));
            System.out.println(bd.getAnchors().get("3"));

//            ctrl.getEditor().addAnchor(bd.getAnchors(), bd, DEFAULT);
                RhombicDodByRhombBuilder builder = new RhombicDodByRhombBuilder("ТЕСТ123");
                builder.setValue(BLDKEY_A, bd.getAnchors().get("0"));
                builder.setValue(BLDKEY_B, bd.getAnchors().get("1"));
                builder.setValue(BLDKEY_C, bd.getAnchors().get("2"));
                builder.setValue(BLDKEY_D, bd.getAnchors().get("3"));
                ctrl.add(builder, null, false);
                
                builder.create(ctrl.getEditor(), null);
            
            
//            ctrl.setBodyVisible(bodyID, !isVisible,
//                    !isVisible || bd.type() == BodyType.POINT, true, true, true);
//            ctrl.notifyBodyStateChange(bodyID);
//            ctrl.setUndo(isVisible ? "показать тело" : "скрыть тело");
//            ctrl.redraw();
          } catch (ExNoBody ex) { }
        }
      });
    } else {
      mi = new JMenuItem("Прилепить ромбододекаэдр", IconList.RHOMBIC_DODECAHEDRON.getMediumIcon());
//      mi.setEnabled(false);
    }
    return mi;
  }
  
  public static JMenuItem buildRhombicDodMesh(final EdtController ctrl, final String bodyID) throws ExNoBody {
    JMenuItem mi = new JMenuItem("Облепить ромбододекаэдрами", IconList.RHOMBUS.getMediumIcon());
    mi.addActionListener(new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
            i_Body bd = null;
          try {
              bd = ctrl.getBody(bodyID);
          } catch (ExNoBody ex) {
              Logger.getLogger(MenuItemFactory.class.getName()).log(Level.SEVERE, null, ex);
          }
            System.out.println(bd.getGeom().type());
            
            HashMap<String, String> pointsAnchors = new HashMap<String, String>();

            for(Map.Entry<String, String> entry : bd.getAnchors().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (!key.contains("plane") && !key.contains("rib")) {
                    pointsAnchors.put(key, value);
                }
            }
            
            ArrayList<String> pnts = new ArrayList<String>(pointsAnchors.keySet());
            
//            System.out.println(bd.getAllFaces(ctrl.getEditor()));
            
//            i_AnchorContainer anchors = ctrl.getEditor().anchors();

//            System.out.println(bd.getAllFaces(ctrl.getEditor()).get(0).points().get(0));
//                try {
//                    System.out.println(anchors.getVect(bd.getAnchors().get(pnts.get(0))));
//                } catch (ExNoAnchor ex) {
//                    Logger.getLogger(MenuItemFactory.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            System.out.println(bd.getAnchors().get(pnts.get(0)));

//System.out.println(bd.getAnchors());
//System.out.println(pnts.get(0));
//System.out.println(pointsAnchors.get(pnts.get(0)));
//System.out.println(pointsAnchors.get("A"));
//System.out.println(pointsAnchors.get("C"));
//System.out.println(pointsAnchors.get("D"));
            
            for (int i=0; i < bd.getAllFaces(ctrl.getEditor()).size(); i++) {
                RhombicDodByRhombBuilder builder = new RhombicDodByRhombBuilder("ТЕСТ123");
//                
                i_AnchorContainer anchors = ctrl.getEditor().anchors();
//                System.out.println("anchors");
//                System.out.println(anchors);
//                System.out.println("anchors");
//                System.out.println("bd.getAnchors()");
//                System.out.println(bd.getAnchors());
//                System.out.println("bd.getAnchors()");
//                System.out.println("pnts");
//                System.out.println(pnts);
//                System.out.println("pnts");



                for (int j=0; j<pnts.size(); j++) {
//                    System.out.println(bd.getAllFaces(ctrl.getEditor()).get(i).points().get(2));
//                    try {
//                        System.out.println(anchors.getVect(bd.getAnchors().get(pnts.get(j))));
//                    } catch (ExNoAnchor ex) {
//                        Logger.getLogger(MenuItemFactory.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                    
//                    try {
//                        System.out.println(anchors.getVect(bd.getAnchors().get(pnts.get(j))) == bd.getAllFaces(ctrl.getEditor()).get(i).points().get(2));
//                    } catch (ExNoAnchor ex) {
//                        Logger.getLogger(MenuItemFactory.class.getName()).log(Level.SEVERE, null, ex);
//                    }
                    
                    
                    
                    try {
                        if (bd.getAllFaces(ctrl.getEditor()).get(i).points().get(0).equals(anchors.getVect(bd.getAnchors().get(pnts.get(j))))) {                        
                            builder.setValue(BLDKEY_A, pointsAnchors.get(pnts.get(j)));                        }
                    } catch (ExNoAnchor ex) {
                        Logger.getLogger(MenuItemFactory.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    try {
                        if (bd.getAllFaces(ctrl.getEditor()).get(i).points().get(1).equals(anchors.getVect(bd.getAnchors().get(pnts.get(j))))) {                        
                            builder.setValue(BLDKEY_B, pointsAnchors.get(pnts.get(j)));
                        }
                    } catch (ExNoAnchor ex) {
                        Logger.getLogger(MenuItemFactory.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    try {
                        if (bd.getAllFaces(ctrl.getEditor()).get(i).points().get(2).equals(anchors.getVect(bd.getAnchors().get(pnts.get(j))))) {                        
                            builder.setValue(BLDKEY_C, pointsAnchors.get(pnts.get(j)));
                        }
                    } catch (ExNoAnchor ex) {
                        Logger.getLogger(MenuItemFactory.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    try {
                        if (bd.getAllFaces(ctrl.getEditor()).get(i).points().get(3).equals(anchors.getVect(bd.getAnchors().get(pnts.get(j))))) {                        
                            builder.setValue(BLDKEY_D, pointsAnchors.get(pnts.get(j)));
                        }
                    } catch (ExNoAnchor ex) {
                        Logger.getLogger(MenuItemFactory.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                ;
                        
//                builder.setValue(BLDKEY_B, bd.getAllFaces(ctrl.getEditor()).get(i).points().get(1));
//                builder.setValue(BLDKEY_C, bd.getAllFaces(ctrl.getEditor()).get(i).points().get(2));
//                builder.setValue(BLDKEY_D, bd.getAllFaces(ctrl.getEditor()).get(i).points().get(3));
                ctrl.add(builder, null, false);
//                
                builder.create(ctrl.getEditor(), null);
            }
      }
    });
    return mi;
  }

  public static JMenuItem createParamsMI(final EdtController ctrl, final String bodyID) throws ExNoBody {
    JMenuItem mi = new JMenuItem("Параметры", IconList.OPTIONS.getMediumIcon());
    mi.addActionListener(new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ctrl.getFocusCtrl().setFocusOnBody(bodyID);
        ctrl.showBodySettingsDialog();
      }
    });
    return mi;
  }

  /**
   * Добавить элемент меню - переключение на вид сверху относительно плоскости.
   * @param ctrl
   * @param bodyID
   * @return
   * @throws editor.ExNoBody
   */
  public static JMenuItem createViewFromAboveMI(final EdtController ctrl, String bodyID) throws ExNoBody {
    JMenuItem mi = new JMenuItem("Вид сверху", IconList.VIEW_IN_FRONT_OF_TRIANG.getMediumIcon());
    i_Body bd = ctrl.getBody(bodyID);
    if (bd.exists()) {
      // Координата Z вектора нормали должна быть больше нуля
      final Vect3d n = ((i_OrientableGeom)bd.getGeom()).getUpVect();
      mi.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          CameraPosition cam = ctrl.getScene().getCameraPosition();
          cam.turnCamera(Vect3d.mul(Vect3d.getNormalizedVector(n), -cam.distance()));
          ctrl.redraw();
        }
      });
    }
    return mi;
  }

  public static JMenuItem createRenameMI(final EdtController ctrl, final String bodyID) throws ExNoBody {
    JMenuItem mi = new JMenuItem("Переименовать", IconList.RENAME.getMediumIcon());
    mi.addActionListener(new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        try {
          RenameBodyDialog dialog = new RenameBodyDialog(ctrl.getFrame(), ctrl.getBodyTitle(bodyID)) {
            @Override
            public boolean isValidTitle(String newTitle) {
              try {
                ctrl.validateBodyTitle(newTitle);
                return true;
              } catch(InvalidBodyNameException ex) {
                return false;
              }
            }
          };
          dialog.setVisible(true);
          if (dialog.accepted) {
            ctrl.renameBody(bodyID, dialog.newTitle());
            ctrl.rebuild();
            ctrl.setUndo("переименовать тело");
            ctrl.redraw();
          }
        } catch (ExNoBody ex) {}
      }
    });
    return mi;
  }
  
  public static JMenuItem createDivideRibMI(final EdtController ctrl, final String anchorID) {
    JMenuItem mi = new JMenuItem();
    final PositiveSpinner ps1 = new PositiveSpinner(1);
    final PositiveSpinner ps2 = new PositiveSpinner(2);
    mi.setLayout(new MigLayout(new LC().flowX().leftToRight(false)));
    mi.setIcon(IconList.DISSECT_RIB.getMediumIcon());
    mi.setText("Разбить в отношении...");
    mi.add(ps2, new CC().width("50!"));
    mi.add(new JLabel(":"));
    mi.add(ps1, new CC().width("50!"));
    mi.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        DivideRibInRelationBuilder builder = new DivideRibInRelationBuilder();
        builder.setValue(BLDKEY_RIB, anchorID);
        builder.setValue(BLDKEY_ALPHA, ps1.getValueAsDouble());
        builder.setValue(BLDKEY_BETA, ps2.getValueAsDouble());
        ctrl.add(builder, ctrl.error().getStatusStripHandler(), false);
        ctrl.redraw();
      }
    });
    return mi;
  }
}