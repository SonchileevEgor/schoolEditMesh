package animation;

/**
 *
 * @author alexeev
 */
public interface i_Animator {
  void start();
  
  void stop();
  
  void update(int time) throws Exception;
}
