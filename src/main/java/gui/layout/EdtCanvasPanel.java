package gui.layout;

import opengl.scenegl.i_GlobalModeChangeListener;
import geom.Vect3d;
import gui.MainEdtCanvas;
import gui.EdtController;
import gui.EnterModeParamsPanel;
import gui.toolbox.CanvasToolPanel;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import opengl.Render;
import opengl.colorgl.ColorGL;
import opengl.colortheme.CurrentTheme;
import opengl.i_RotationListener;
import opengl.scenegl.SceneType;
import opengl.sceneparameters.CameraPosition;
import opengl.sceneparameters.GluPerspectiveParameters;

/**
 * Layered wrapper for canvas.
 * Contains canvas, tool panel and info glass panel.
 *
 * @author alexeev
 */
public class EdtCanvasPanel extends JLayeredPane implements i_GlobalModeChangeListener  {
  private EdtController _ctrl;
  private MainEdtCanvas _canvas;
  CanvasToolPanel toolPanel;
  SceneRotationWidget rotationWidget;
  EnterModeParamsPanel modeParamsPanel;
  EdtConsole console;

  public EdtCanvasPanel(EdtController ctrl, MainEdtCanvas canvas) {
    super();
    setLayout(new MigLayout(new LC().fill().insetsAll("0"), new AC().fill(), new AC().fill()));
    _ctrl = ctrl;
    _canvas = canvas;
    rotationWidget = new SceneRotationWidget(canvas.getScene().getRender());
    toolPanel = new CanvasToolPanel(_ctrl, this);
    console = new EdtConsole(_ctrl);
    console.setVisible(false);
    modeParamsPanel = new EnterModeParamsPanel();
    rotationWidget.setVisible(false);
    modeParamsPanel.setVisible(false);
    modeParamsPanel.setOpaque(false);
    _ctrl.getMainCanvasCtrl().addModeChangeListener(modeParamsPanel);
    _ctrl.getMainCanvasCtrl().addModeParamChangeListener(modeParamsPanel);
    _ctrl.error().addErrorListener(console);
    _ctrl.addGlobalModeChangeListener(this);
    _ctrl.addAppSettingsChangeListener(toolPanel);

    JPanel layer1 = new JPanel(new MigLayout(new LC().fill().insetsAll("0"),
            new AC().fill(), new AC().fill()));
    layer1.add(_canvas);
    add(layer1, new CC().cell(0, 0));
    setLayer(layer1, 1);


    JPanel layer2 = new JPanel(new MigLayout(new LC().fill().insetsAll("0").hideMode(3),
            new AC().fill(), new AC().fill()));
    layer2.add(toolPanel, new CC().dockNorth());
    layer2.add(rotationWidget, new CC().width("75!").height("75!").dockNorth());
    layer2.add(console, new CC().dockSouth());
    layer2.add(modeParamsPanel, new CC().dockEast());
    layer2.setOpaque(false);

    add(layer2, new CC().pos("0", "0", "100%", "100%"));
    setLayer(layer2, 2);
  }

  @Override
  public void modeChanged(SceneType is3d) {
    rotationWidget.resetRender(_canvas.getScene().getRender());
    if( !is3d.is3d() ){
      rotationWidget.setVisible(false);
    }
  }

  class SceneRotationWidget extends JPanel implements GLEventListener, i_RotationListener {
    private Render baseRender;
    private GLCanvas canvas;

    SceneRotationWidget(Render ren) {
      super(new BorderLayout());
      setBackground(null);
      baseRender = ren;
      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      canvas = new GLCanvas(new GLCapabilities(GLProfile.getDefault()));
      add(canvas);
      canvas.addGLEventListener(this);
      baseRender.addRotationListener(this);
      initMouseActions();
      canvas.display();
    }

    void resetRender(Render ren) {
      baseRender.removeRotationListener(this);
      baseRender = ren;
      baseRender.addRotationListener(this);
    }

    private void initMouseActions() {
      MouseAdapter miniCanvasAdapter = new MouseAdapter() {
        private boolean _isPressed = false;
        private int _currX;
        private int _currY;

        @Override
        public void mousePressed(MouseEvent e) {
          if( SwingUtilities.isLeftMouseButton(e) ){
            _isPressed = true;
            _currX = e.getX();
            _currY = e.getY();
          }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
          if( !SwingUtilities.isLeftMouseButton(e) || !_isPressed)
            return;
          int newX = e.getX();
          int newY = e.getY();
          int dx = _currX - newX;
          int dy = _currY - newY;
          _currX = newX;
          _currY = newY;
          baseRender.getCameraPosition().turnCamera(0.025 * dx, 0.025 * dy);
          baseRender.notifySceneRotated();
        }

        @Override
        public void mouseReleased(MouseEvent me) {
          _isPressed = false;
        }
      };
      canvas.addMouseListener(miniCanvasAdapter);
      canvas.addMouseMotionListener(miniCanvasAdapter);
    }

    @Override
    public void init(GLAutoDrawable glad) {
      GL2 gl = glad.getGL().getGL2();
      gl.glClearColor(1, 1, 1, 1);
      gl.glShadeModel(GL2.GL_SMOOTH);
      gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
      gl.glPolygonOffset(1.0f, 1.0f);
      gl.glEnable(GL2.GL_LINE_SMOOTH);
      gl.glEnable(GL2.GL_POLYGON_SMOOTH);
      gl.glEnable(GL2.GL_BLEND);
      gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
    }

    @Override
    public void dispose(GLAutoDrawable glad) {}

    @Override
    public void display(GLAutoDrawable glad) {
      //!! TODO: запилить красивую отрисовку
      GL2 gl = glad.getGL().getGL2();
      GLU glu = new GLU();

      gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

      GluPerspectiveParameters perPar = baseRender.getGluPerspectiveParameters();
      CameraPosition cam = baseRender.getCameraPosition();
      perPar.setzFar(5);
      gl.glLoadIdentity();
      gl.glMatrixMode(GL2.GL_PROJECTION);
      gl.glLoadIdentity();

      Vect3d eye = cam.eye().getNormalized();

      glu.gluLookAt(eye.x(), eye.y(), eye.z(),
              0, 0, 0,
              0, 0, 1);
      gl.glMatrixMode(GL2.GL_MODELVIEW);

      gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, new float[]{5.f, 5.f, 5.f, 5.f}, 0);
      gl.glLineWidth(2);
      ArrayList<ColorGL> axesColors = CurrentTheme.getColorTheme().getColorsOfCoordinateAxes();
      gl.glColor3dv(axesColors.get(0).getArray(), 0);
      gl.glBegin(gl.GL_LINES);
      gl.glVertex3dv(new double[]{-1.,0.,0.}, 0);
      gl.glVertex3dv(new double[]{1.,0.,0.}, 0);
      gl.glEnd();
      gl.glColor3dv(axesColors.get(1).getArray(), 0);
      gl.glBegin(gl.GL_LINES);
      gl.glVertex3dv(new double[]{0.,-1.,0.}, 0);
      gl.glVertex3dv(new double[]{0.,1.,0.}, 0);
      gl.glEnd();
      gl.glColor3dv(axesColors.get(2).getArray(), 0);
      gl.glBegin(gl.GL_LINES);
      gl.glVertex3dv(new double[]{0.,0.,-1.}, 0);
      gl.glVertex3dv(new double[]{0.,0.,1.}, 0);
      gl.glEnd();
    }

    @Override
    public void reshape(GLAutoDrawable glad, int i, int i1, int i2, int i3) {}

    @Override
    public void sceneRotated() {
      canvas.display();
    }
  }
}
