package gui.elements;

import gui.laf.AppColor;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Enumeration of angle styles.
 * @author alexeev
 */
public enum AngleStyle {
  SINGLE("single") {
    @Override
    public void paint(Graphics2D g, int w, int h) {
      super.paint(g, w, h);
      int angleDegrees = (int)Math.round(Math.toDegrees(Math.atan((double)h / w)));
      int arcRadius = (int)Math.round(w * 0.75);
      g.drawArc(2 - arcRadius, h - 2 - arcRadius, arcRadius * 2, arcRadius * 2, 0, angleDegrees);
    }
  },
  DOUBLE("double") {
    @Override
    public void paint(Graphics2D g, int w, int h) {
      super.paint(g, w, h);
      int angleDegrees = (int)Math.round(Math.toDegrees(Math.atan((double)h / w)));
      int arcRadius = (int)Math.round(w * 0.75);
      int arcRadius2 = arcRadius - 4;
      g.drawArc(2 - arcRadius, h - 2 - arcRadius, arcRadius * 2, arcRadius * 2, 0, angleDegrees);
      g.drawArc(2 - arcRadius2, h - 2 - arcRadius2, arcRadius2 * 2, arcRadius2 * 2, 0, angleDegrees);
    }
  },
  TRIPLE("triple") {
    @Override
    public void paint(Graphics2D g, int w, int h) {
      super.paint(g, w, h);
      int angleDegrees = (int)Math.round(Math.toDegrees(Math.atan((double)h / w)));
      int arcRadius = (int)Math.round(w * 0.75);
      int arcRadius2 = arcRadius - 4;
      int arcRadius3 = arcRadius - 8;
      g.drawArc(2 - arcRadius, h - 2 - arcRadius, arcRadius * 2, arcRadius * 2, 0, angleDegrees);
      g.drawArc(2 - arcRadius2, h - 2 - arcRadius2, arcRadius2 * 2, arcRadius2 * 2, 0, angleDegrees);
      g.drawArc(2 - arcRadius3, h - 2 - arcRadius3, arcRadius3 * 2, arcRadius3 * 2, 0, angleDegrees);
    }
  },
  WAVED("waved") {
    @Override
    public void paint(Graphics2D g, int w, int h) {
      super.paint(g, w, h);
      double angle = Math.atan((double)h / w);

      double[] wavePts_x = new double[5];
      double[] wavePts_y = new double[5];
      double[] bezier1_x = new double[5];
      double[] bezier1_y = new double[5];
      double[] bezier2_x = new double[5];
      double[] bezier2_y = new double[5];
      double arcRadius = w * 0.75;
      double innerRadius = arcRadius * 0.95;
      double outerRadius = arcRadius * 1.05;

      for( int i = 0; i < 5; i++ ){
        double currAngle = angle * (i + 1) / 5;
        double currBezier1Angle = currAngle - angle * 4 / 25;
        double currBezier2Angle = currAngle - angle / 25;
        wavePts_x[i] = Math.cos(currAngle) * arcRadius;
        wavePts_y[i] = Math.sin(currAngle) * arcRadius;
        if( i % 2 == 0 ){
          bezier1_x[i] = Math.cos(currBezier1Angle) * outerRadius;
          bezier1_y[i] = Math.sin(currBezier1Angle) * outerRadius;
          bezier2_x[i] = Math.cos(currBezier2Angle) * outerRadius;
          bezier2_y[i] = Math.sin(currBezier2Angle) * outerRadius;
        } else {
          bezier1_x[i] = Math.cos(currBezier1Angle) * innerRadius;
          bezier1_y[i] = Math.sin(currBezier1Angle) * innerRadius;
          bezier2_x[i] = Math.cos(currBezier2Angle) * innerRadius;
          bezier2_y[i] = Math.sin(currBezier2Angle) * innerRadius;
        }
      }

      Path2D.Double path = new Path2D.Double();
      path.moveTo(2 + arcRadius, h - 2);
      for( int i = 0; i < 5; i++ ){
        path.curveTo(2 + bezier1_x[i], h - bezier1_y[i],
                2 + bezier2_x[i], h - bezier2_y[i], 2 + wavePts_x[i], h - wavePts_y[i]);
      }

      g.draw(path);
    }
  };

  public String getKey(){
    return _key;
  }

  public void paint(Graphics2D g, int w, int h){
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(AppColor.DARK_GRAY.color());
    g.setStroke(new BasicStroke(2));
    g.drawLine(2, h - 2, w - 2, h - 2);
    double angle = Math.atan((double) h / w);
    g.drawLine(2, h - 2, 2 + (int) (Math.cos(angle) * (w - 4)),
            h - (int) (Math.sin(angle) * (w - 4)) - 2);
    g.setStroke(new BasicStroke(1));
  }

  private AngleStyle(String key) {
    _key = key;
  }

  public static AngleStyle getByKey(String key) {
    for (AngleStyle item : values()){
      if (key.equals(item.getKey())){
        return item;
      }
    }
    throw new RuntimeException("Error: style of angle <" + key + "> is not supported");
  }

  private String _key;

  public static void main(String[] args) {
    JFrame fr = new JFrame();
    fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    fr.setVisible(true);
    fr.setMinimumSize(new Dimension(200, 200));
    JPanel p = new JPanel() {
      @Override
      public void paintComponent(Graphics g){
        TRIPLE.paint((Graphics2D)g, 30, 30);
      }
    };
    fr.add(p);
    fr.revalidate();
    fr.repaint();
  }
}
