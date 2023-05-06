package gui.action;

import builders.param.BuilderParamType;
import builders.param.BuilderParam;
import config.Config;
import opengl.scenegl.Scene2d;
import opengl.scenegl.Scene3d;
import opengl.scenegl.SceneGL;
import bodies.BodyType;
import builders.*;
import editor.*;
import geom.ExDegeneration;
import gui.EdtController;
import gui.IconList;
import gui.dialog.LabelRibDialog;
import gui.dialog.OpenSceneDialog;
import gui.dialog.PrintingThicknessDialog;
import gui.mode.CoverScreenMode;
import java.awt.Desktop;
import maquettes.Maquette;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import maquettes.Carcasse;
import maquettes.MeshFileType;
import opengl.Drawer;
import opengl.colortheme.CurrentTheme;
import opengl.scenegl.SceneSTL;
import opengl.scenegl.SceneType;
import org.apache.commons.io.FilenameUtils;
import toxi.geom.Vec3D;
import toxi.geom.mesh.Face;
import toxi.geom.mesh.OBJWriter;
import toxi.geom.mesh.STLWriter;
import toxi.geom.mesh.WETriangleMesh;
import util.FileUtils;

/**
 * Factory of frequently used actions.
 * @author alexeev
 */
public enum ActionFactory implements i_ActionFactory {
  APP_CLOSE,
  FILE_NEW,
  FILE_OPEN,
  FILE_SAVE,
  FILE_SAVE_AS,
  FILE_SAVE_AS_3D,
  FILE_EXPORT_3D,
  FILE_EXPORT_PNG,
  FILE_IMPORT_3D,
  CIRCUMSCRIBE_CIRCLE,
  CIRCUMSCRIBE_SPHERE,
  DELETE_FOCUSED_BODIES,
  DISSECT_RIB,
  INSCRIBE_CIRCLE,
  INSCRIBE_SPHERE,
  CREATE_DEFAULT_PLANE,
  TURN_SCENE_UP,
  TURN_SCENE_DOWN,
  TURN_SCENE_RIGHT,
  TURN_SCENE_LEFT,
  SHOW_LOG,
  SWITCH_SCENE_COLORS,
  SWITCH_SCENE_ANTIALIASING,
  SWITCH_VIEW_MODE,
  SWITCH_PROJECTION,
  SWITCH_MODE_2D_3D,
  ZOOM_IN,
  ZOOM_OUT,
  UNDO,
  REDO,
  INCREASE_PLANES,
  REDUCE_PLANES,
  INCREASE_INIT_GRID_INTENSITY,
  REDUCE_INIT_GRID_INTENSITY,
  TOGGLE_INSPECTOR,
  TOGGLE_STL_VIEW,
  CENTROID,
  ORTHOCENTER,
  PLANE_BY_POLYGON,
  PLANE_BY_TRIANGLE,
  LABEL_RIB;

  /**
   * Get action.
   * @param ctrl
   * @param args arguments for action (if present)
   * @return
   */
  @Override
  public EdtAction getAction(final EdtController ctrl, final Object... args) {
    switch(this) {
      case PLANE_BY_TRIANGLE:
          return new EdtAction(ctrl,
            "Построить плоскость треугольника",
            "Построить плоскость треугольника",
            IconList.TR_PLANE.getMediumIcon(),
            IconList.TR_PLANE.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              HashMap<String, BuilderParam> params = new HashMap<>();
              params.put("name", new BuilderParam("name", "Имя", BuilderParamType.NAME, BodyType.POINT.getName(ctrl.getEditor())));
              params.put("triangle", new BuilderParam("triangle", "треугольник", BuilderParamType.BODY, (String)args[0]));
              PlaneByTriangleBuilder builder = new PlaneByTriangleBuilder(params);
              ctrl.add(builder, ctrl.error().getStatusStripHandler(), false);
              ctrl.redraw();
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.getScene().is3d());
            }
          };
      case PLANE_BY_POLYGON:
          return new EdtAction(ctrl,
            "Построить плоскость многоугольника",
            "Построить плоскость многоугольника",
            IconList.POL_PLANE.getMediumIcon(),
            IconList.POL_PLANE.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              HashMap<String, BuilderParam> params = new HashMap<>();
              params.put("name", new BuilderParam("name", "Имя", BuilderParamType.NAME, BodyType.POINT.getName(ctrl.getEditor())));
              params.put("poly", new BuilderParam("poly", "многоугольник", BuilderParamType.BODY, (String)args[0]));
              PlaneByPolygonBuilder builder = new PlaneByPolygonBuilder(params);
              ctrl.add(builder, ctrl.error().getStatusStripHandler(), false);
              ctrl.redraw();
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.getScene().is3d());
            }
          };
      case ORTHOCENTER:
        return new EdtAction(ctrl,
          "Поставить точку пересечения высот",
          "Поставить точку пересечения высот",
          IconList.ORTHOCENTER.getMediumIcon(),
          IconList.ORTHOCENTER.getLargeIcon()){
          @Override
          public void actionPerformed(ActionEvent e) {
            HashMap<String, BuilderParam> params = new HashMap<>();
            params.put("name", new BuilderParam("name", "Имя", BuilderParamType.NAME, BodyType.POINT.getName(ctrl.getEditor())));
            params.put("triangle", new BuilderParam("triangle", "треугольник", BuilderParamType.BODY, (String)args[0]));
            OrtoCenterBuilder builder = new OrtoCenterBuilder(params);
            ctrl.add(builder, ctrl.error().getStatusStripHandler(), false);
            ctrl.redraw();
          }
          @Override
          public void updateEditorState() {
            //!! TODO:
          }
        };
      case CENTROID:
       return new EdtAction(ctrl,
          "Поставить точку пересечения медиан",
          "Поставить точку пересечения медиан",
          IconList.CENTROID.getMediumIcon(),
          IconList.CENTROID.getLargeIcon()){
          @Override
          public void actionPerformed(ActionEvent e) {
            HashMap<String, BuilderParam> params = new HashMap<>();
            params.put("name", new BuilderParam("name", "Имя", BuilderParamType.NAME, BodyType.POINT.getName(ctrl.getEditor())));
            params.put("triangle", new BuilderParam("triangle", "Треугольник", BuilderParamType.BODY, (String)args[0]));
            CentroidBuilder builder = new CentroidBuilder(params);
            ctrl.add(builder, ctrl.error().getStatusStripHandler(), false);
            ctrl.redraw();
          }
          @Override
          public void updateEditorState() {
            //!! TODO:
          }
        };
      case SWITCH_MODE_2D_3D:
        return new EdtAction(ctrl,
          "Переключить режим 2D / 3D",
          "Переключить режим 2D / 3D",
          IconList.EMPTY.getMediumIcon(),
          IconList.EMPTY.getLargeIcon()) {
          @Override
          public void actionPerformed(ActionEvent ae) {
            int reply;
            if (!config.Temp.FILE_SAVED) {
              reply = JOptionPane.showConfirmDialog(ctrl.getFrame(),
                      "Сохранить изменения в старой сцене?",
                      "Сохранение",
                      JOptionPane.YES_NO_CANCEL_OPTION);
            } else {
              reply = JOptionPane.NO_OPTION;
            }
            switch(reply) {
              case JOptionPane.YES_OPTION:
                if (config.Temp.CURRENT_FILE != null) {
                  ctrl.getEditor().writeToFile(config.Temp.CURRENT_FILE);
                } else {
                  ActionFactory.FILE_SAVE_AS.getAction(ctrl).actionPerformed(ae);
                }
                // see next case
              case JOptionPane.NO_OPTION:
                ctrl.getFocusCtrl().clearFocus();
                ctrl.clearEditor();
                ctrl.fitEditor();
                ctrl.notifyEditorStateChange();
                boolean is3d = !ctrl.getScene().is3d();
                if (is3d) {
                  ctrl.getMainCanvasCtrl().getCanvas().setScene(new Scene3d(ctrl.getEditor()));
                } else {
                  ctrl.getMainCanvasCtrl().getCanvas().setScene(new Scene2d(ctrl.getEditor()));
                }
                ctrl.getMainCanvasCtrl().setDefaultMode();
                ctrl.notifyEditorStateChange();
                ctrl.notifyGlobalModeChange();
                break;
              case JOptionPane.CANCEL_OPTION:
                break;
            }
          }
        };
      case FILE_NEW:
        return new EdtAction(ctrl,
          "Создать",
          "<html><strong>Создать</strong><br>Создать новую сцену",
          IconList.FILE_NEW.getMediumIcon(),
          IconList.FILE_NEW.getLargeIcon()) {
          @Override
          public void actionPerformed(ActionEvent ae) {
            int reply;
            if (!config.Temp.FILE_SAVED) {
              reply = JOptionPane.showConfirmDialog(ctrl.getFrame(),
                      "Сохранить изменения в старой сцене?",
                      "Сохранение",
                      JOptionPane.YES_NO_CANCEL_OPTION);
            } else {
              reply = JOptionPane.NO_OPTION;
            }
            switch(reply) {
              case JOptionPane.YES_OPTION:
                if (config.Temp.CURRENT_FILE != null) {
                  ctrl.getEditor().writeToFile(config.Temp.CURRENT_FILE);
                } else {
                  ActionFactory.FILE_SAVE_AS.getAction(ctrl).actionPerformed(ae);
                }
                                          // see next case
              case JOptionPane.NO_OPTION:
                ctrl.getFocusCtrl().clearFocus();
                ctrl.clearEditor();
                ctrl.notifyEditorStateChange();
                ctrl.getMainCanvasCtrl().setDefaultMode();
                ctrl.redraw();
                break;
              case JOptionPane.CANCEL_OPTION:
                break;
            }
          }
        };
      case FILE_OPEN:
        return new EdtAction(ctrl,
          "Открыть",
          "<html><strong>Открыть</strong><br>Открыть существующую сцену",
          IconList.FILE_OPEN.getMediumIcon(),
          IconList.FILE_OPEN.getLargeIcon()) {
          @Override
          public void actionPerformed(ActionEvent e) {
            int reply;
            if (!config.Temp.FILE_SAVED) {
              reply = JOptionPane.showConfirmDialog(ctrl.getFrame(),
                      "Сохранить изменения в старой сцене?",
                      "Сохранение",
                      JOptionPane.YES_NO_CANCEL_OPTION);
            } else {
              reply = JOptionPane.NO_OPTION;
            }
            switch(reply) {
              case JOptionPane.YES_OPTION:
                if (config.Temp.CURRENT_FILE != null) {
                  ctrl.getEditor().writeToFile(config.Temp.CURRENT_FILE);
                } else {
                  ActionFactory.FILE_SAVE_AS.getAction(ctrl).actionPerformed(e);
                }
                                          // see next case
              case JOptionPane.NO_OPTION:
                boolean isFilenameSpecified = (args.length != 0);
                boolean isActionApproved = isFilenameSpecified;
                final String fname;
                if( !isFilenameSpecified ){
                  // имя файла не задано
                  JFileChooser jfc = new JFileChooser(config.Temp.SEARCH_DIR);
                  jfc.setFileFilter(new EdtFileFilter());
                  int retValue = jfc.showDialog(ctrl.getFrame(), "Открыть");
                  isActionApproved = (retValue == JFileChooser.APPROVE_OPTION);
                  if( isActionApproved ){
                    fname = jfc.getSelectedFile().getAbsolutePath();
                    config.Temp.SEARCH_DIR = jfc.getCurrentDirectory().getPath();
                  } else {
                    fname = null;
                  }
                } else {
                  // имя файла задано
                  fname = (String)args[0];
                }
                if( isActionApproved ){
                  // открытие файла подтверждено
                  final boolean is3d;
                  switch( util.Util.is3d(fname) ){
                    case util.Util.SCENE_3D:
                      is3d = true;
                      break;
                    case util.Util.SCENE_2D:
                      is3d = false;
                      break;
                    case util.Util.SCENE_UNKNOWN:
                      OpenSceneDialog osd = new OpenSceneDialog(ctrl.getFrame());
                      osd.setVisible(true);
                      is3d = !osd.isAccepted() || osd.is3d();
                      break;
                    default:
                      is3d = false;
                  }

                  ctrl.getLayoutController().lockFrame();

                  //!! SIC: All events in event dispatching thread must be handled before scene reset.
                  SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                      try {
                        ctrl.getFocusCtrl().clearFocus();
                        ctrl.loadFile(fname);
                        if( ctrl.getScene().is3d() != is3d ){
                          ctrl.getMainCanvasCtrl().getCanvas().setScene(
                              (is3d) ? new Scene3d(ctrl.getEditor()) : new Scene2d(ctrl.getEditor()));
                          ctrl.getScene().getRender().setSceneIn3d(is3d);
                        }
                        ctrl.fitEditor();
                      } catch( Exception ex ) {
                        ctrl.status().error("Ошибка при открытии файла");
                      } finally {
                        ctrl.getMainCanvasCtrl().setDefaultMode();
                        ctrl.notifyEditorStateChange();
                        ctrl.notifyGlobalModeChange();
                        ctrl.redraw();
                        ctrl.getLayoutController().unlockFrame();
                      }
                    }
                  });
                }
                break;
              case JOptionPane.CANCEL_OPTION:
                break;
            }
          }
        };
      case FILE_SAVE:
        return new EdtAction(ctrl,
          "Сохранить",
          "Сохранить",
          IconList.FILE_SAVE.getMediumIcon(),
          IconList.FILE_SAVE.getLargeIcon()) {
          @Override
          public void actionPerformed(ActionEvent e) {
            String fname = config.Temp.CURRENT_FILE;
            ctrl.getEditor().writeToFile(fname);
            config.Temp.FILE_SAVED = true;
          }

          @Override
          public void updateEditorState() {
            setEnabled(config.Temp.CURRENT_FILE != null && !config.Temp.FILE_SAVED);
          }
        };
      case FILE_SAVE_AS:
        return new EdtAction(ctrl,
          "Сохранить как...",
          "<html><strong>Сохранить</strong><br>Сохранить в формате JSON",
          IconList.FILE_SAVE.getMediumIcon(),
          IconList.FILE_SAVE.getLargeIcon()) {
          @Override
          public void actionPerformed(ActionEvent e) {
            JFileChooser jfc = new JFileChooser(config.Temp.SEARCH_DIR);
            String defFilename = (config.Temp.CURRENT_FILE != null) ? config.Temp.CURRENT_FILE :
                              (ctrl.getScene().is3d() ? "default.sc3" : "default.sc2");
            jfc.setSelectedFile(new File(defFilename));
            jfc.setFileFilter(new EdtFileFilter());
            int retValue = jfc.showDialog(ctrl.getFrame(), "Сохранить");
            if (retValue == JFileChooser.APPROVE_OPTION) {
              String fname = jfc.getSelectedFile().getAbsolutePath();
              fname = FileUtils.setExtension(fname, ctrl.getScene().is3d() ? "sc3" : "sc2");
              ctrl.getEditor().writeToFile(fname);
              config.Temp.SEARCH_DIR = jfc.getCurrentDirectory().getPath();
              config.Temp.CURRENT_FILE = fname;
              config.Temp.FILE_SAVED = true;
            }
          }
        };
       case FILE_SAVE_AS_3D:
        return new EdtAction(ctrl,
          "Сохранить модель как...",
          "<html><strong>Сохранить</strong><br>Сохранить в форматах 3D",
          IconList.FILE_SAVE.getMediumIcon(),
          IconList.FILE_SAVE.getLargeIcon()) {
          @Override
          public void actionPerformed(ActionEvent e) {
              final JFileChooser jfc = new JFileChooser(config.Temp.SEARCH_DIR);
              jfc.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                  jfc.setSelectedFile(new File("default.stl"));
                  return FilenameUtils.isExtension(f.getName(), "stl") || f.isDirectory();
                }
                @Override
                public String getDescription() {
                  return "Файлы формата STL";
                }
              });
//              jfc.setFileFilter(new FileFilter() {
//                @Override
//                public boolean accept(File f) {
//                  jfc.setSelectedFile(new File("default.obj"));
//                  return FilenameUtils.isExtension(f.getName(), "obj") || f.isDirectory();
//                }
//                @Override
//                public String getDescription() {
//                  return "Файлы формата OBJ";
//                }
//              });
              jfc.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                  jfc.setSelectedFile(new File("default.ply"));
                  return FilenameUtils.isExtension(f.getName(), "ply") || f.isDirectory();
                }
                @Override
                public String getDescription() {
                  return "Файлы формата PLY";
                }
              });
              int retValue = jfc.showDialog(ctrl.getFrame(), "Экспорт в 3D файл");
              if (retValue == JFileChooser.APPROVE_OPTION) {
                final String fname = jfc.getSelectedFile().getAbsolutePath();
                ctrl.status().showMessage("Экспортирую в файлы для 3D-печати...");
                ctrl.status().simulateAction();
                ctrl.getLayoutController().lockFrame();
                (new SwingWorker<Object, Object>(){
                  @Override
                  protected String doInBackground() {
                      System.out.println("Run maquette constructing...");
                      MeshFileType type = null;
                      switch(FilenameUtils.getExtension(fname)) {
                          case "ply":
                              type = MeshFileType.PLY;
                              break;
                          case "stl":
                              type = MeshFileType.STL;
                              break;
                          case "obj":
                              type = MeshFileType.OBJ;
                              break;
                          default:
                              FileFilter[] ff = jfc.getChoosableFileFilters();
                              if (ff[0].getDescription().endsWith("STL"))
                                  type = MeshFileType.STL;
                              else if (ff[0].getDescription().endsWith("OBJ"))
                                  type = MeshFileType.OBJ;
                              else type = MeshFileType.PLY;
                      }
                      WETriangleMesh mesh = new WETriangleMesh();
                      for (int k = 0; k < ((SceneSTL)ctrl.getMainCanvasCtrl().getScene()).getMeshesSize(); k++)
                        mesh.addMesh(((SceneSTL)ctrl.getMainCanvasCtrl().getScene()).getMesh(k));
                      if (type == MeshFileType.PLY) {
                            toxi.geom.mesh.PLYWriter out = new toxi.geom.mesh.PLYWriter();
                            out.saveMesh(mesh,fname);
                        } else if (type == MeshFileType.STL) {
                            STLWriter out = new STLWriter();
                            out.beginSave(fname, mesh.getNumFaces());
                            for (Face f : mesh.getFaces())
                                out.face(f.a, f.b, f.c, f.normal, 0);
                            out.endSave();
                        } else if (type == MeshFileType.OBJ) {
                            // TODO: write currect obj-saving
                            OBJWriter out = new OBJWriter();
                            out.beginSave(fname);
                            for (Object v : mesh.getVertices().toArray())
                                out.vertex((Vec3D)v);
                            out.endSave();
                        }
                      System.out.println("Complete.");
                      return null;
                  }
                  @Override
                  protected void done() {
                    ctrl.status().clear();
                    ctrl.getLayoutController().unlockFrame();
                  }
                }).execute();
                config.Temp.SEARCH_DIR = jfc.getCurrentDirectory().getPath();
              config.Temp.CURRENT_FILE = fname;
              config.Temp.FILE_SAVED = true;
              }
            }
        };
      case FILE_EXPORT_3D:
        return new EdtAction(ctrl,
          "Экспорт в файл для 3D-печати",
          "<html><strong>Сохранить</strong><br>Экспорт в файл для 3D-печати",
          IconList.FILE_SAVE.getMediumIcon(),
          IconList.FILE_SAVE.getLargeIcon()) {
          @Override
          public void actionPerformed(ActionEvent e) {
            final Maquette maq = new Maquette(ctrl, PrintingThicknessDialog.TMP_FILENAME);
            PrintingThicknessDialog thickness_dialog = null;
              try {
                  thickness_dialog = new PrintingThicknessDialog(ctrl.getFrame(),
                          maq,
                          ctrl.getMaxEdgeLength(),
                          ctrl.getMinEdgeLength(),
                          ctrl.getMaxFaceArea(),
                          ctrl.getMinFaceArea(),
                          ctrl);
              } catch (IOException ex) {
                  Logger.getLogger(ActionFactory.class.getName()).log(Level.SEVERE, null, ex);
              }
            thickness_dialog.setVisible(true);
            if (thickness_dialog.isAccepted()) {
              final JFileChooser jfc = new JFileChooser(config.Temp.SEARCH_DIR);
              jfc.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                  jfc.setSelectedFile(new File("default.stl"));
                  return FilenameUtils.isExtension(f.getName(), "stl") || f.isDirectory();
                }
                @Override
                public String getDescription() {
                  return "Файлы формата STL";
                }
              });
              jfc.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                  jfc.setSelectedFile(new File("default.ply"));
                  return FilenameUtils.isExtension(f.getName(), "ply") || f.isDirectory();
                }
                @Override
                public String getDescription() {
                  return "Файлы формата PLY";
                }
              });
              int retValue = jfc.showDialog(ctrl.getFrame(), "Экспорт в 3D файл");
              if (retValue == JFileChooser.APPROVE_OPTION) {
                final String fname = jfc.getSelectedFile().getAbsolutePath();
                maq.setFilename(fname);
                ctrl.status().showMessage("Экспортирую в файлы для 3D-печати...");
                ctrl.status().simulateAction();
                ctrl.getLayoutController().lockFrame();
                (new SwingWorker<Object, Object>(){
                  @Override
                  protected String doInBackground() throws ExDegeneration {
                    try {
                      System.out.println("Run maquette constructing...");
                      MeshFileType type = null;
                      switch(FilenameUtils.getExtension(fname)) {
                          case "ply":
                              type = MeshFileType.PLY;
                              break;
                          case "stl":
                              type = MeshFileType.STL;
                              break;
                          case "obj":
                              type = MeshFileType.OBJ;
                              break;
                          default:
                              FileFilter[] ff = jfc.getChoosableFileFilters();
                              if (ff[0].getDescription().endsWith("STL"))
                                  type = MeshFileType.STL;
                              else if (ff[0].getDescription().endsWith("OBJ"))
                                  type = MeshFileType.OBJ;
                              else type = MeshFileType.PLY;
                      }
                      maq.buildMaquette(type);
                      System.out.println("Complete.");
                      return null;
                    }
                    catch (IOException ex) {
                      System.out.println("Error: maquette was not built!");
                      //!!TODO: reply about error
                      return null;
                    }
                  }
                  @Override
                  protected void done() {
                    ctrl.status().clear();
                    ctrl.getLayoutController().unlockFrame();
                  }
                }).execute();
                config.Temp.SEARCH_DIR = jfc.getCurrentDirectory().getPath();
              }
            }
          }
        };
      case FILE_EXPORT_PNG:
        return new EdtAction(ctrl,
          "Экспорт в изображение PNG",
          "<html><strong>Сохранить</strong><br>Экспорт в изображение PNG",
          IconList.FILE_SAVE.getMediumIcon(),
          IconList.FILE_SAVE.getLargeIcon()) {
          @Override
          public void actionPerformed(ActionEvent e) {
            JFileChooser jfc = new JFileChooser(config.Temp.SEARCH_DIR);
            jfc.setSelectedFile(new File("default.png"));
            jfc.setFileFilter(new FileFilter() {
              @Override
              public boolean accept(File f) {
                return FilenameUtils.isExtension(f.getName(), "png") || f.isDirectory();
              }
              @Override
              public String getDescription() {
                return "Файлы формата .PNG";
              }
            });
            int retValue = jfc.showDialog(ctrl.getFrame(), "Экспорт в PNG");
            if (retValue == JFileChooser.APPROVE_OPTION) {
              String fname = jfc.getSelectedFile().getAbsolutePath();

              try {
                 File f = new File(fname);
                 ImageIO.write(ctrl.getScene().getImage(), "png", f);
              } catch (IOException ex) { }

              config.Temp.SEARCH_DIR = jfc.getCurrentDirectory().getPath();
            }
          }
        };
      case APP_CLOSE:
        return new EdtAction(ctrl,
          "Выход",
          "<html><strong>Выход</strong><br>Выход из приложения",
          IconList.FILE_EXIT.getMediumIcon(),
          IconList.FILE_EXIT.getLargeIcon()) {
          @Override
          public void actionPerformed(ActionEvent e) {
            int reply;
            if (!config.Temp.FILE_SAVED) {
              reply = JOptionPane.showConfirmDialog(ctrl.getFrame(),
                  "Сохранить изменения?", "Сохранение", JOptionPane.YES_NO_CANCEL_OPTION);
            } else {
              reply = JOptionPane.NO_OPTION;
            }
            switch(reply) {
              case JOptionPane.YES_OPTION:
                if (config.Temp.CURRENT_FILE != null)
                  ActionFactory.FILE_SAVE.getAction(ctrl).actionPerformed(
                          new ActionEvent(ctrl.getFrame(), ActionEvent.ACTION_PERFORMED, null));
                else
                  ActionFactory.FILE_SAVE_AS.getAction(ctrl).actionPerformed(
                          new ActionEvent(ctrl.getFrame(), ActionEvent.ACTION_PERFORMED, null));
                                      // see next case
              case JOptionPane.NO_OPTION:
                System.exit(0);
              case JOptionPane.CANCEL_OPTION:
                break;
            }
          }
        };
      case CREATE_DEFAULT_PLANE:
        return new EdtAction(ctrl,
          "<html><strong>Плоскость Oxy</strong>",
          "<html><strong>Плоскость Oxy</strong>",
          IconList.PLANE_OXY.getLargeIcon(),
          IconList.PLANE_OXY.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              HashMap<String, BuilderParam> params = new HashMap<>();
              params.put("name", new BuilderParam("name", "Имя", BuilderParamType.NAME, BodyType.PLANE.getName(ctrl.getEditor())));
              DefaultPlaneBuilder builder = new DefaultPlaneBuilder(params);
              ctrl.add(builder, null, false); // use default error handler
              ctrl.redraw();
            }
          };
      case TURN_SCENE_UP:
        return new EdtAction() {
          @Override
          public void actionPerformed(ActionEvent e) {
            ctrl.getMainCanvasCtrl().turn(0, -0.025);
          }
        };
      case TURN_SCENE_DOWN:
        return new EdtAction() {
          @Override
          public void actionPerformed(ActionEvent e) {
            ctrl.getMainCanvasCtrl().turn(0, 0.025);
          }
        };
      case TURN_SCENE_RIGHT:
        return new EdtAction() {
          @Override
          public void actionPerformed(ActionEvent e) {
            ctrl.getMainCanvasCtrl().turn(0.025, 0);
          }
        };
      case TURN_SCENE_LEFT:
        return new EdtAction() {
          @Override
          public void actionPerformed(ActionEvent e) {
            ctrl.getMainCanvasCtrl().turn(-0.025, 0);
          }
        };
      case SWITCH_SCENE_COLORS:
        return new EdtAction() {
          @Override
          public void actionPerformed(ActionEvent e) {
            CurrentTheme.next();
            ctrl.getEditor().bodyMgr().applyTheme();
            ctrl.getEditor().anchMgr().applyTheme();
            ctrl.status().showMessage("Установлена тема \u00AB" + CurrentTheme.getName() + "\u00BB");
            ctrl.redraw();
          }
        };
      case SWITCH_SCENE_ANTIALIASING:
        return new EdtAction() {
          @Override
          public void actionPerformed(ActionEvent e) {
            ctrl.getScene().getRender().setUseAntialiasing(!ctrl.getScene().getRender().isUseAntialiasing());
            ctrl.redraw();
          }
        };
      case SWITCH_VIEW_MODE:
        return new EdtAction() {
          @Override
          public void actionPerformed(ActionEvent e) {
            ctrl.getScene().changeViewMode();
            ctrl.redraw();
          }
        };
      case SWITCH_PROJECTION:
        return new EdtAction() {
          @Override
          public void actionPerformed(ActionEvent e) {
            ctrl.getScene().switchProjection();
            ctrl.redraw();
          }
        };
      case ZOOM_IN:
        return new EdtAction() {
          @Override
          public void actionPerformed(ActionEvent e) {
            ctrl.getScene().changeCameraDistance(-0.25);
          }
        };
      case ZOOM_OUT:
        return new EdtAction() {
          @Override
          public void actionPerformed(ActionEvent e) {
            ctrl.getScene().changeCameraDistance(0.25);
          }
        };
      case REDUCE_PLANES:
        return new EdtAction() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Drawer.setSmartPlaneIndent(Drawer.getSmartPlaneIndent() - 0.1);
            ctrl.redraw();
          }
        };
      case INCREASE_PLANES:
        return new EdtAction() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Drawer.setSmartPlaneIndent(Drawer.getSmartPlaneIndent() + 0.1);
            ctrl.redraw();
          }
        };
      case REDUCE_INIT_GRID_INTENSITY:
        return new EdtAction() {
          @Override
          public void actionPerformed(ActionEvent e) {
            SceneGL scene = ctrl.getScene();
            scene.setGridColorIntensity(
                Math.min(1.0, scene.getInitialGridColorIntensity() + 0.1)
            );
            ctrl.redraw();
          }
        };
      case INCREASE_INIT_GRID_INTENSITY:
        return new EdtAction() {
          @Override
          public void actionPerformed(ActionEvent e) {
            SceneGL scene = ctrl.getScene();
            scene.setGridColorIntensity(
                Math.max(0.0, scene.getInitialGridColorIntensity() - 0.1)
            );
            ctrl.redraw();
          }
        };
      case UNDO:
        return new EdtAction(ctrl,
          "Отменить",
          "Отменить",
          IconList.UNDO.getMediumIcon(),
          IconList.UNDO.getSmallIcon()) {
          @Override
          public void actionPerformed(ActionEvent e) {
            ctrl.getMainCanvasCtrl().setDefaultMode();
            ctrl.undo();
            ctrl.redraw();
          }
          @Override
          public void updateEditorState() {
            setEnabled(!ctrl.isUndoEmpty());
          }
        };
      case REDO:
        return new EdtAction(ctrl,
          "Повторить",
          "Повторить",
          IconList.REDO.getMediumIcon(),
          IconList.REDO.getSmallIcon()) {
          @Override
          public void actionPerformed(ActionEvent e) {
            ctrl.getMainCanvasCtrl().setDefaultMode();
            ctrl.redo();
            ctrl.redraw();
          }
          @Override
          public void updateEditorState() {
            setEnabled(!ctrl.isRedoEmpty());
          }
        };
      case CIRCUMSCRIBE_CIRCLE:
        return new EdtAction(ctrl,
          "Описать окружность",
          "Описать окружность",
          IconList.CIRCLE_OUT_TRIANGLE.getMediumIcon(),
          IconList.CIRCLE_OUT_TRIANGLE.getLargeIcon()){
          @Override
          public void actionPerformed(ActionEvent e) {
            HashMap<String, BuilderParam> params = new HashMap<>();
            params.put("name", new BuilderParam("name", "Имя", BuilderParamType.NAME, BodyType.CIRCLE.getName(ctrl.getEditor())));
            params.put("poly", new BuilderParam("poly", "Многоугольник", BuilderParamType.ANCHOR, (String)args[0]));
            CircleOutPolygonBuilder builder = new CircleOutPolygonBuilder(params);
            ctrl.add(builder, ctrl.error().getStatusStripHandler(), false);
            ctrl.redraw();
          }
          @Override
          public void updateEditorState() {
            //!! TODO:
          }
        };
      case CIRCUMSCRIBE_SPHERE:
        return new EdtAction(ctrl,
          "Описать сферу",
          "Описать сферу",
          IconList.OUT_SPHERE.getMediumIcon(),
          IconList.OUT_SPHERE.getLargeIcon()){
          @Override
          public void actionPerformed(ActionEvent e) {
            try {
              HashMap<String, BuilderParam> params = new HashMap<>();
              i_BodyBuilder builder;
              params.put("name", new BuilderParam("name", "Имя", BuilderParamType.NAME, BodyType.SPHERE.getName(ctrl.getEditor())));
              if (ctrl.getBody((String)args[0]).type() == BodyType.TETRAHEDRON || ctrl.getBody((String)args[0]).type() == BodyType.REG_TETRAHEDRON) {
                params.put("tetrahedron", new BuilderParam("tetrahedron", "Тетраэдр", BuilderParamType.BODY, (String)args[0]));
                builder = new SphereOutTetrahedronBuilder(params);
              } else if (ctrl.getBody((String)args[0]).type() == BodyType.CUBE) {
                params.put("cube", new BuilderParam("cube", "Куб", BuilderParamType.BODY, (String)args[0]));
                builder = new SphereOutCubeBuilder(params);
              } else if (ctrl.getBody((String)args[0]).type() == BodyType.PYRAMID) {
                params.put("pyramid", new BuilderParam("pyramid", "Пирамида", BuilderParamType.BODY, (String)args[0]));
                builder = new SphereOutPyramidBuilder(params);
              } else if (ctrl.getBody((String)args[0]).type() == BodyType.PRISM
                      || ctrl.getBody((String)args[0]).type() == BodyType.PARALLELEPIPED) {
                params.put("prism", new BuilderParam("prism", "Призма", BuilderParamType.BODY, (String)args[0]));
                builder = new SphereOutPrismBuilder(params);
              } else if (ctrl.getBody((String)args[0]).type() == BodyType.CONE) {
                params.put("cone", new BuilderParam("cone", "Конус", BuilderParamType.BODY, (String)args[0]));
                builder = new SphereOutConeBuilder(params);
              } else if (ctrl.getBody((String)args[0]).type() == BodyType.CYLINDER) {
                params.put("cylinder", new BuilderParam("cylinder", "Цилиндр", BuilderParamType.BODY, (String)args[0]));
                builder = new SphereOutCylinderBuilder(params);
              } else if (ctrl.getBody((String)args[0]).type() == BodyType.ICOSAHEDRON) {
                params.put("ico", new BuilderParam("ico", "Икосаэдр", BuilderParamType.BODY, (String)args[0]));
                builder = new SphereOutIcosahedronBuilder(params);
              } else if (ctrl.getBody((String)args[0]).type() == BodyType.OCTAHEDRON) {
                params.put("oct", new BuilderParam("oct", "Октаэдр", BuilderParamType.BODY, (String)args[0]));
                builder = new SphereOutOctahedronBuilder(params);
              } else if (ctrl.getBody((String)args[0]).type() == BodyType.DODECAHEDRON) {
                params.put("dodec", new BuilderParam("dodec", "Додекаэдр", BuilderParamType.BODY, (String)args[0]));
                builder = new SphereOutDodecahedronBuilder(params);
              } else if (ctrl.getBody((String)args[0]).type() == BodyType.ELLIPSOID) {
                params.put("ellipsoid", new BuilderParam("ellipsoid", "элипсоид", BuilderParamType.BODY, (String)args[0]));
                builder = new SphereOutEllipsoidBuilder(params);
              }
              else throw new ExNoBody("");
              ctrl.add(builder, ctrl.error().getStatusStripHandler(), false);
              ctrl.redraw();
            } catch (ExNoBody ex) { }
          }
          @Override
          public void updateEditorState() {
            try {
              setEnabled(ctrl.getBody((String)args[0]).exists());
            } catch (ExNoBody ex) {
              setEnabled(false);
            }
          }
        };
      case DISSECT_RIB: // args[0] is anchorID
        return new EdtAction(ctrl,
          "Отметить середину отрезка",
          "Отметить середину отрезка",
          IconList.DISSECT_RIB.getMediumIcon(),
          IconList.DISSECT_RIB.getLargeIcon()){
          @Override
          public void actionPerformed(ActionEvent e) {
            String anchorID = (String)args[0];
            try {
              i_Anchor rib = ctrl.getAnchor(anchorID);
              HashMap<String, BuilderParam> params = new HashMap<>();
              params.put("name", new BuilderParam("name", "Имя", BuilderParamType.NAME, BodyType.POINT.getName(ctrl.getEditor())));
              params.put("A", new BuilderParam("A", "A", BuilderParamType.ANCHOR, rib.arrayIDs().get(0)));
              params.put("B", new BuilderParam("B", "B", BuilderParamType.ANCHOR, rib.arrayIDs().get(1)));
              params.put("alpha", new BuilderParam("alpha", "Коэффициент", BuilderParamType.DOUBLE, 0.5));
              PointBuilderHull2 builder = new PointBuilderHull2(params);
              ctrl.add(builder, ctrl.error().getStatusStripHandler(), false);
              ctrl.redraw();
            } catch (ExNoAnchor ex) {
              //!! TODO: сообщение об ошибке.
            }
          }
          @Override
          public void updateEditorState() {
            try {
              setEnabled(ctrl.getBody((String)args[0]).exists());
            } catch (ExNoBody ex) {}
          }
        };
      case LABEL_RIB:
        return new EdtAction(ctrl,
          "Надпись над отрезком",
          "Надпись над отрезком",
          IconList.EMPTY.getMediumIcon(),
          IconList.EMPTY.getLargeIcon()){
          @Override
          public void actionPerformed(ActionEvent e) {
            String anchorID = (String)args[0];
            try {
              i_Anchor rib = ctrl.getAnchor(anchorID);
              LabelRibDialog dialog = new LabelRibDialog(
                      ctrl.getFrame(), rib.getState().getLabel());
              dialog.setVisible(true);
              if (dialog.accepted) {
                rib.getState().setLabel(dialog.newLabel());
                ctrl.rebuild();
                ctrl.setUndo("переименовать тело");
                ctrl.redraw();
              }
              ctrl.redraw();
            } catch (ExNoAnchor ex) {
              //!! TODO: сообщение об ошибке.
            }
          }
          @Override
          public void updateEditorState() {
            try {
              setEnabled(ctrl.getBody((String)args[0]).exists());
            } catch (ExNoBody ex) {}
          }
        };
      case INSCRIBE_SPHERE:
        return new EdtAction(ctrl,
          "Вписать сферу",
          "Вписать сферу",
          IconList.IN_SPHERE.getMediumIcon(),
          IconList.IN_SPHERE.getLargeIcon()){
          @Override
          public void actionPerformed(ActionEvent e) {
            try {
              HashMap<String, BuilderParam> params = new HashMap<>();
              i_BodyBuilder builder;
              params.put("name", new BuilderParam("name", "Имя", BuilderParamType.NAME, BodyType.SPHERE.getName(ctrl.getEditor())));
              if (ctrl.getBody((String)args[0]).type() == BodyType.TETRAHEDRON || ctrl.getBody((String)args[0]).type() == BodyType.REG_TETRAHEDRON) {
                params.put("tetrahedron", new BuilderParam("tetrahedron", "Тетраэдр", BuilderParamType.BODY, (String)args[0]));
                builder = new SphereInTetrahedronBuilder(params);
              } else if (ctrl.getBody((String)args[0]).type() == BodyType.PYRAMID) {
                params.put("pyramid", new BuilderParam("pyramid", "Пирамида", BuilderParamType.BODY, (String)args[0]));
                builder = new SphereInPyramidBuilder(params);
              } else if (ctrl.getBody((String)args[0]).type() == BodyType.CUBE) {
                params.put("cube", new BuilderParam("cube", "Куб", BuilderParamType.BODY, (String)args[0]));
                builder = new SphereInCubeBuilder(params);
              } else if (ctrl.getBody((String)args[0]).type() == BodyType.PRISM
                      || ctrl.getBody((String)args[0]).type() == BodyType.PARALLELEPIPED) {
                params.put("prism", new BuilderParam("prism", "Призма", BuilderParamType.BODY, (String)args[0]));
                builder = new SphereInPrismBuilder(params);
              } else if (ctrl.getBody((String)args[0]).type() == BodyType.CONE) {
                params.put("cone", new BuilderParam("cone", "Конус", BuilderParamType.BODY, (String)args[0]));
                builder = new SphereInConeBuilder(params);
              } else if (ctrl.getBody((String)args[0]).type() == BodyType.CYLINDER) {
                params.put("cylinder", new BuilderParam("cylinder", "Цилиндер", BuilderParamType.BODY, (String)args[0]));
                builder = new SphereInCylinderBuilder(params);
              } else if (ctrl.getBody((String)args[0]).type() == BodyType.ICOSAHEDRON) {
                params.put("ico", new BuilderParam("ico", "Икосаэдр", BuilderParamType.BODY, (String)args[0]));
                builder = new SphereInIcosahedronBuilder(params);
              } else if (ctrl.getBody((String)args[0]).type() == BodyType.OCTAHEDRON) {
                params.put("oct", new BuilderParam("oct", "Октаэдр", BuilderParamType.BODY, (String)args[0]));
                builder = new SphereInOctahedronBuilder(params);
              } else if (ctrl.getBody((String)args[0]).type() == BodyType.DODECAHEDRON) {
                params.put("dodec", new BuilderParam("dodec", "Додекаэдр", BuilderParamType.BODY, (String)args[0]));
                builder = new SphereInDodecahedronBuilder(params);
              } else if (ctrl.getBody((String)args[0]).type() == BodyType.ELLIPSOID) {
                params.put("ellipsoid", new BuilderParam("ellipsoid", "элипсоид", BuilderParamType.BODY, (String)args[0]));
                builder = new SphereInEllipsoidBuilder(params);
              }
              else throw new ExNoBody("");
              ctrl.add(builder, ctrl.error().getStatusStripHandler(), false);
              ctrl.redraw();
            } catch (ExNoBody ex) { }
          }
          @Override
          public void updateEditorState() {
            try {
              setEnabled(ctrl.getBody((String)args[0]).exists());
            } catch (ExNoBody ex) {
              setEnabled(false);
            }
          }
        };
      case INSCRIBE_CIRCLE:
        return new EdtAction(ctrl,
          "Вписать окружность",
          "Вписать окружность",
          IconList.CIRCLE_IN_TRIANGLE.getMediumIcon(),
          IconList.CIRCLE_IN_TRIANGLE.getLargeIcon()){
          @Override
          public void actionPerformed(ActionEvent e) {
            HashMap<String, BuilderParam> params = new HashMap<>();
            params.put("name", new BuilderParam("name", "Имя", BuilderParamType.NAME, BodyType.CIRCLE.getName(ctrl.getEditor())));
            params.put("poly", new BuilderParam("poly", "Многоугольник", BuilderParamType.ANCHOR, (String)args[0]));
            CircleInPolygonBuilder builder = new CircleInPolygonBuilder(params);
            ctrl.add(builder, ctrl.error().getStatusStripHandler(), false);
            ctrl.redraw();
          }
          @Override
          public void updateEditorState() {
            //!! TODO:
          }
        };
      case SHOW_LOG:
        return new EdtAction(ctrl,
          "Показать лог",
          "Показать лог",
          IconList.SHOW_LOG.getMediumIcon(),
          IconList.SHOW_LOG.getLargeIcon()){
          @Override
          public void actionPerformed(ActionEvent e) {
            try {
              Desktop.getDesktop().open(new File(Config.LOG_DIR, "sch3dedit.log"));
            } catch (IOException ex) { }
          }
        };
      case TOGGLE_INSPECTOR:
        return new EdtAction(ctrl,
          "Переключить инспектор",
          "Переключить инспектор",
          IconList.EMPTY.getMediumIcon(),
          IconList.EMPTY.getLargeIcon()){
          @Override
          public void actionPerformed(ActionEvent e) {
            ctrl.getLayoutController().toggleInspector();
          }
        };
      case DELETE_FOCUSED_BODIES:
        return new EdtAction(ctrl,
          "Удалить тела",
          "Удалить тела",
          IconList.DELETE.getMediumIcon(),
          IconList.DELETE.getLargeIcon()){
          @Override
          public void actionPerformed(ActionEvent e) {
            if (ctrl.getFocusCtrl().containsRemovableBody()) {
              ctrl.removeBodies(ctrl.getFocusCtrl().getFocusedBodies());
              ctrl.redraw();
            }
          }
        };
      case FILE_IMPORT_3D:
        return new EdtAction(ctrl,
        "Открыть 3D файл",
        "Открыть 3D файл",
        IconList.FILE_OPEN.getMediumIcon(),
        IconList.FILE_OPEN.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
                int reply;
                if (!config.Temp.FILE_SAVED) {
                  reply = JOptionPane.showConfirmDialog(ctrl.getFrame(),
                          "Сохранить изменения?",
                          "Сохранение",
                          JOptionPane.YES_NO_CANCEL_OPTION);
                } else {
                  reply = JOptionPane.NO_OPTION;
                }
                switch(reply) {
                  case JOptionPane.YES_OPTION:
                    if (config.Temp.CURRENT_FILE != null) {
                      //TODO: сделать сохранение текущих изменений
                      String fname = config.Temp.CURRENT_FILE;
                      WETriangleMesh mesh = new WETriangleMesh();
                      for (int k = 0; k < ((SceneSTL)ctrl.getStlCanvasCtrl().getScene()).getMeshesSize(); k++)
                        mesh.addMesh(((SceneSTL)ctrl.getStlCanvasCtrl().getScene()).getMesh(k));
                      if ( "ply".equals(FilenameUtils.getExtension(fname))
                              || "PLY".equals(FilenameUtils.getExtension(fname))) {
                              toxi.geom.mesh.PLYWriter out = new toxi.geom.mesh.PLYWriter();
                              out.saveMesh(mesh, fname);
                      } else if ( "stl".equals(FilenameUtils.getExtension(fname))
                              || "STL".equals(FilenameUtils.getExtension(fname))) {
                              STLWriter out = new STLWriter();
                              out.beginSave(fname, mesh.getNumFaces());
                              for (Face f : mesh.getFaces())
                                  out.face(f.a, f.b, f.c, f.normal, 0);
                              out.endSave();
                      }
                    } else {
                      ActionFactory.FILE_SAVE_AS_3D.getAction(ctrl).actionPerformed(e);
                    }
                    // see next case
                  case JOptionPane.NO_OPTION:
                    ctrl.getFocusCtrl().clearFocus();
                    ctrl.clearEditor();
                    ctrl.fitEditor();
                    ctrl.notifyEditorStateChange();

                    boolean isFilenameSpecified = (args.length != 0);
                    boolean isActionApproved = isFilenameSpecified;
                    final String fname;
                    if( !isFilenameSpecified ){
                      // имя файла не задано
                      JFileChooser jfc = new JFileChooser(config.Temp.SEARCH_DIR);
                      jfc.setFileFilter(new FileFilter() {
                        @Override
                        public boolean accept(File f) {
                          return FilenameUtils.isExtension(f.getName(), new String[]{"stl", "ply"}) ||
                                 f.isDirectory();
                        }
                        @Override
                        public String getDescription() {
                          return "Файлы PLY, STL";
                        }
                      });
                      int retValue = jfc.showDialog(ctrl.getFrame(), "Открыть");
                      isActionApproved = (retValue == JFileChooser.APPROVE_OPTION);
                      if( isActionApproved ){
                        fname = jfc.getSelectedFile().getAbsolutePath();
                        config.Temp.SEARCH_DIR = jfc.getCurrentDirectory().getPath();
                        config.Temp.CURRENT_FILE = fname;
                      } else {
                        fname = null;
                      }
                    } else {
                      // имя файла задано
                      fname = (String)args[0];
                      config.Temp.CURRENT_FILE = fname;
                    }
                    if( isActionApproved ){
                      // открытие файла подтверждено
                      final boolean isPLY;
                      switch( util.Util.isPLY(fname) ){
                        case util.Util.SCENE_PLY:
                          isPLY = true;
                          break;
                        case util.Util.SCENE_STL:
                          isPLY = false;
                          break;
                        case util.Util.SCENE_UNKNOWN:
                          break;
                        default:
                          isPLY = false;
                      }

                      ctrl.getLayoutController().lockFrame();

                      SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                          try {
                            ctrl.getFocusCtrl().clearFocus();
                            SceneSTL sc = null;
                            try {
                                sc = new SceneSTL(ctrl.getEditor(), fname);
                            } catch (IOException ex) {
                                Logger.getLogger(ActionFactory.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            ctrl.getStlCanvasCtrl().getCanvas().setScene(sc);
                            ctrl.fitEditor();
                          } catch( Exception ex ) {
                            ctrl.status().error("Ошибка при открытии файла");
                          } finally {
                            ctrl.getStlCanvasCtrl().setMode(new CoverScreenMode(ctrl));
                            ctrl.notifyEditorStateChange();
                            ctrl.notifyGlobalModeChange();
                            ctrl.redraw();
                            ctrl.getLayoutController().unlockFrame();
                          }
                        }
                      });
                    }
                    break;
                  case JOptionPane.CANCEL_OPTION:
                    break;
                }
            }
        };
      case TOGGLE_STL_VIEW:
        return new EdtAction(ctrl,
          "STL View",
          "STL View",
          IconList.EMPTY.getMediumIcon(),
          IconList.EMPTY.getLargeIcon()){
          @Override
          public void actionPerformed(ActionEvent e) {
                if (ctrl.getScene().getSceneType() != SceneType.SceneSTL) {
                    final Carcasse carcasse = new Carcasse(ctrl);
                    ctrl.getMainCanvasCtrl().setDefaultMode();
                    ctrl.getStlCanvasCtrl().setMode(new CoverScreenMode(ctrl));
                    ctrl.getFocusCtrl().clearFocus();
                    ctrl.clearEditor();
                    ctrl.fitEditor();
                    ctrl.notifyEditorStateChange();
                    int in3D = JOptionPane.showConfirmDialog(ctrl.getFrame(), 
                          "Построить для сцены 3D модели?",
                          "Построение",
                          JOptionPane.YES_NO_CANCEL_OPTION);
                    if (in3D == JOptionPane.YES_OPTION) {
                        ctrl.status().showMessage("Экспортирую в файлы для 3D-печати...");
                        ctrl.status().simulateAction();
                        ctrl.getLayoutController().lockFrame();
                        System.out.println("Run maquette constructing...");
                        carcasse.sculptModels();
                        System.out.println("Complete.");
                        ctrl.status().clear();
                        ctrl.getLayoutController().unlockFrame();
                        SceneSTL sc = new SceneSTL(ctrl.getEditor(), carcasse.getCovers());
                        ctrl.getStlCanvasCtrl().getCanvas().setScene(sc);
                    } else if (in3D == JOptionPane.NO_OPTION)
                        ctrl.getStlCanvasCtrl().getCanvas().setScene(new SceneSTL(ctrl.getEditor()));
                    //необходимо сбрасывать режим построения, выбранный в предыдущем режиме
                    ctrl.getStlCanvasCtrl().initMouseListeners();
                    ctrl.notifyEditorStateChange();
                    ctrl.notifyGlobalModeChange();
                }
                ctrl.redraw();
          }
        };
      default:
        throw new IllegalArgumentException();
    }
  }
}
