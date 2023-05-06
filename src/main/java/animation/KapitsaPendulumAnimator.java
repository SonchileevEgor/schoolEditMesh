package animation;

import builders.PointBuilder;
import geom.ExDegeneration;
import geom.ExZeroDivision;
import geom.NewSystemOfCoor;
import geom.Plane3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.List;
import javax.swing.SwingWorker;
import org.apache.commons.math3.ode.FirstOrderConverter;
import org.apache.commons.math3.ode.SecondOrderDifferentialEquations;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;

/**
 *
 * @author alexeev
 */
public class KapitsaPendulumAnimator implements i_Animator {
  private ClassicalRungeKuttaIntegrator _integrator;
  
  private static double _a = 0.25;  // амплитуда вибраций
  private static double _v = 30;    // частота вибраций
  
  private EdtController _ctrl;
  private PointBuilder _center;
  private PointBuilder _endpoint;
  
  private Vect3d _gravity = new Vect3d(0, 0, -1);
  
  private int _time;
  private double _currVelocity;
  private boolean _isRunning;
  
  
  private Vect3d _normal;
  NewSystemOfCoor _coords;
  
  private double _initCenterZ;

  public KapitsaPendulumAnimator(EdtController ctrl, PointBuilder center, PointBuilder endpoint)
          throws ExDegeneration, ExZeroDivision {
    _integrator = new ClassicalRungeKuttaIntegrator(0.01);
    _ctrl = ctrl;
    _center = center;
    _endpoint = endpoint;
    _normal = new Plane3d(_center.getValueAsVect(PointBuilder.BLDKEY_POINT),
            Vect3d.sum(_center.getValueAsVect(PointBuilder.BLDKEY_POINT), _gravity),
            _endpoint.getValueAsVect(PointBuilder.BLDKEY_POINT)).n();
    _coords = new NewSystemOfCoor(_center.getValueAsVect(PointBuilder.BLDKEY_POINT),
            _gravity, _endpoint.getValueAsVect(PointBuilder.BLDKEY_POINT));
    _initCenterZ = _center.getValueAsVect(PointBuilder.BLDKEY_POINT).z();
    
    _time = 0;
    _currVelocity = 0;
    _isRunning = false;
  }
  
  @Override
  public void start() {
    _isRunning = true;
    SwingWorker w = new SwingWorker<Boolean, Integer>(){
      @Override
      protected Boolean doInBackground() throws Exception {
        while(_isRunning){
          Thread.sleep(5);
          _time += 0.003;
          publish(_time);
        }
        return true;
      }
      
      @Override
      protected void process(List<Integer> chunks) {
        try {
          System.out.println(chunks.get(0));
          update(chunks.get(0));
          _ctrl.rebuild();
          _ctrl.redraw();
        } catch( ExDegeneration | ExZeroDivision ex ){
          cancel(true);
        }
      }
      
      @Override
      protected void done(){
        stop();
      }
    };
    
    w.execute();
  }

  @Override
  public void stop() {
    _time = 0;
    _currVelocity = 0;
    _isRunning = false;
  }

  @Override
  public void update(int time) throws ExDegeneration, ExZeroDivision {
    Vect3d center = _center.getValueAsVect(PointBuilder.BLDKEY_POINT);
    Vect3d endPoint = _endpoint.getValueAsVect(PointBuilder.BLDKEY_POINT);
    
    final double angle = _coords.getPolarAngleInOxy(endPoint);    
    final double dist = Vect3d.dist(center, endPoint);
    
    System.out.println(String.format("%.3f; %.3f", angle, dist));
    
    SecondOrderDifferentialEquations ode = new SecondOrderDifferentialEquations() {
      @Override
      public int getDimension() {
        return 1;
      }

      @Override
      public void computeSecondDerivatives(double t, double[] x0, double[] x1, double[] x2) {
        x2[0] = -(_a * _v * _v * Math.cos(t * _v) + 9.8) * Math.sin(x0[0]) / dist;
      }
    };
    
    FirstOrderConverter firstOrderConverter = new FirstOrderConverter(ode);
    double[] next = _integrator.singleStep(
            firstOrderConverter, _time,
            new double[]{angle, _currVelocity},
            _time + 0.003);
    
    
    
    System.out.println(String.format("%.3f; %.3f", next[0], next[1]));
    _currVelocity = next[1];
            
    Vect3d newEndPoint = Vect3d.sum(center,
            Vect3d.sub(endPoint, center).rotate(next[0] - angle, _normal));
    
    double newZ = (center.z() - _initCenterZ + 2 * _a * _v * 0.003) % _a + _initCenterZ;
    double difZ = newZ - center.z();
    _endpoint.movePoint(new Vect3d(newEndPoint.x(), newEndPoint.y(), newEndPoint.z() + difZ));
    _center.movePoint(new Vect3d(center.x(), center.y(), newZ));
  }
}
