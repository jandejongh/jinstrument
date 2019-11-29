package org.javajdj.jinstrument.r1.swing.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.util.EnumMap;
import java.util.logging.Logger;
import javax.swing.JLabel;

public class JSevenSegment
  extends JLabel
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private static final Logger LOG = Logger.getLogger (JSevenSegment.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / CLONING / FACTORIES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JSevenSegment ()
  {
    setPreferredSize (JSevenSegment.REFERENCE_DIMENSION);

    setOpaque (true);
    setBackground (Color.black);

    x = 0;
    y = 0;

    number = zero;

    createSegments ();

    number = zero;
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // REFERENCE DIMENSION
  // SEGMENT
  // SEGMENT PAINT MAP
  //
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
  public final static Dimension REFERENCE_DIMENSION = new Dimension(/* 110 */ 160, 180);
  
  /** The segments in the display, with their {@link Shape}s.
   * 
   */
  public static enum Segment
  {
     
    a   (new Polygon (new int[]{20, 90,  98,  90, 20, 12}, new int[]{  8,   8,  15,  22,  22,  15}, 6)),
    b   (new Polygon (new int[]{91, 98, 105, 105, 98, 91}, new int[]{ 23,  18,  23,  81,  89,  81}, 6)),
    c   (new Polygon (new int[]{91, 98, 105, 105, 98, 91}, new int[]{ 97,  89,  97, 154, 159, 154}, 6)),
    d   (new Polygon (new int[]{20, 90,  98,  90, 20, 12}, new int[]{155, 155, 162, 169, 169, 162}, 6)),
    e   (new Polygon (new int[]{ 5, 12,  19,  19, 12,  5}, new int[]{ 97,  89,  97, 154, 159, 154}, 6)),
    f   (new Polygon (new int[]{ 5, 12,  19,  19, 12,  5}, new int[]{ 23,  18,  23,  81,  89,  81}, 6)),
    g   (new Polygon (new int[]{20, 90,  95,  90, 20, 15}, new int[]{ 82,  82,  89,  96,  96,  89}, 6)),
    dot (new Ellipse2D.Double (125,150,20,20))
    ;
      
    Segment (final Shape shape)
    {
      this.shape = shape;
    }
      
    private final Shape shape;
      
    public final Shape getShape ()
    {
      return this.shape;
    }
      
  }
  
  /** Internal map dictating which segments are to be (actively) displayed.
   * 
   */
  private final EnumMap<Segment, Boolean> segmentPaintMap = new EnumMap<> (Segment.class);
  















  
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    public void writeNumber(int n) {
        switch (n)
        {
            case 0:
                number = zero;
                break;
            case 1:
                number = one;
                break;
            case 2:
                number = two;
                break;
            case 3:
                number = three;
                break;
            case 4:
                number = four;
                break;
            case 5:
                number = five;
                break;
            case 6:
                number = six;
                break;
            case 7:
                number = seven;
                break;
            case 8:
                number = eight;
                break;
            case 9:
                number = nine;
                break;
            default: /* other number */
                number = zero;
                break;
        }
    }
   
    @ Override public void paintComponent(Graphics g) {
        // System.out.printf("paintComponent\n");
       
        super.paintComponent(g); // this is needed to set the background color
        final int w = getWidth ();
        final int h = getHeight ();
        final double scaleX = w / 160.0;
        final double scaleY = h / 180.0;
        Graphics2D g2 = (Graphics2D) g;
        AffineTransform orig = g2.getTransform();
        AffineTransform af = new AffineTransform (orig);
        af.translate (-20.0, -8.0);
        af.scale (scaleX, scaleY);
        af.translate (20.0 / scaleX, 8.0 / scaleY);
        // af.rotate(0.025);
        g2.setTransform(af);       
        for (int i=0; i<SEGMENT_NUMBER; i++)
            setSegmentState(g, segments[i], number[i]);
        if (this.decimalPoint)
          g.setColor(on);
        else
          g.setColor (off);
        g2.draw (this.dot);
        g2.fill (this.dot);
        g2.setTransform(orig);
    }
   
    private void createSegments() {
        segments = new Polygon[SEGMENT_NUMBER];
           
        segments[A] = new Polygon();       
        segments[A].addPoint(x+20,y+8);
        segments[A].addPoint(x+90,y+8);
        segments[A].addPoint(x+98,y+15);
        segments[A].addPoint(x+90,y+22);
        segments[A].addPoint(x+20,y+22);
        segments[A].addPoint(x+12,y+15);
       
        segments[B] = new Polygon();       
        segments[B].addPoint(x+91,y+23);
        segments[B].addPoint(x+98,y+18);
        segments[B].addPoint(x+105,y+23);
        segments[B].addPoint(x+105,y+81);
        segments[B].addPoint(x+98,y+89);
        segments[B].addPoint(x+91,y+81);
       
        segments[C] = new Polygon();
        segments[C].addPoint(x+91,y+97);
        segments[C].addPoint(x+98,y+89);
        segments[C].addPoint(x+105,y+97);
        segments[C].addPoint(x+105,y+154);
        segments[C].addPoint(x+98,y+159);
        segments[C].addPoint(x+91,y+154);
       
        segments[D] = new Polygon();
        segments[D].addPoint(x+20,y+155);
        segments[D].addPoint(x+90,y+155);
        segments[D].addPoint(x+98,y+162);
        segments[D].addPoint(x+90,y+169);
        segments[D].addPoint(x+20,y+169);
        segments[D].addPoint(x+12,y+162);
       
        segments[E] = new Polygon();
        segments[E].addPoint(x+5,y+97);
        segments[E].addPoint(x+12,y+89);
        segments[E].addPoint(x+19,y+97);
        segments[E].addPoint(x+19,y+154);
        segments[E].addPoint(x+12,y+159);
        segments[E].addPoint(x+5,y+154);
              
        segments[F] = new Polygon();
        segments[F].addPoint(x+5,y+23);
        segments[F].addPoint(x+12,y+18);
        segments[F].addPoint(x+19,y+23);
        segments[F].addPoint(x+19,y+81);
        segments[F].addPoint(x+12,y+89);
        segments[F].addPoint(x+5,y+81);
       
        segments[G] = new Polygon();
        segments[G].addPoint(x+20,y+82);
        segments[G].addPoint(x+90,y+82);
        segments[G].addPoint(x+95,y+89);
        segments[G].addPoint(x+90,y+96);
        segments[G].addPoint(x+20,y+96);
        segments[G].addPoint(x+15,y+89);
    }
   
    private void setSegmentState(Graphics graphics, Polygon segment, int state) {
        if (state == OFF)  graphics.setColor(off);
        else graphics.setColor(on);
       
        graphics.fillPolygon(segment);       
        graphics.drawPolygon(segment);
    }
   
    private final int x;
    private final int y;
   
    private Polygon[] segments;
    private int[] number;
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // DECIMAL POINT
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    Ellipse2D.Double dot = new Ellipse2D.Double (125,150,20,20);
    
    private boolean decimalPoint = false;
    
    public void setDecimalPoint ()
    {
      // old
      this.decimalPoint = true;
      // new
      this.segmentPaintMap.put (Segment.dot, true);
    }
    
    public void resetDecimalPoint ()
    {
      // old
      this.decimalPoint = false;
      // new
      this.segmentPaintMap.put (Segment.dot, false);
    }
    
    public void setDecimalPoint (final boolean decimalPoint)
    {
      // old
      this.decimalPoint = decimalPoint;
      // new
      this.segmentPaintMap.put (Segment.dot, decimalPoint);      
    }
    
    /* Constants */
    private final static int SEGMENT_NUMBER = 7;
    private final static int A = 0;
    private final static int B = 1;
    private final static int C = 2;
    private final static int D = 3;
    private final static int E = 4;
    private final static int F = 5;
    private final static int G = 6;
   
    private final static int OFF = 0;
    private final static int ON = 1;
   
    private final static int zero[] = { ON, ON, ON, ON, ON, ON, OFF };
    private final static int one[] = { OFF, ON, ON, OFF, OFF, OFF, OFF };
    private final static int two[] = { ON, ON, OFF, ON, ON, OFF, ON };
    private final static int three[] = { ON, ON, ON, ON, OFF, OFF, ON };
    private final static int four[] = { OFF, ON, ON, OFF, OFF, ON, ON };
    private final static int five[] = { ON, OFF, ON, ON, OFF, ON, ON };
    private final static int six[] = { ON, OFF, ON, ON, ON, ON, ON };
    private final static int seven[] = { ON, ON, ON, OFF, OFF, OFF, OFF };
    private final static int eight[] = { ON, ON, ON, ON, ON, ON, ON };
    private final static int nine[] = { ON, ON, ON, ON, OFF, ON, ON };
   
    private final static Color off = Color.red.darker().darker().darker().darker();
    private final static Color on = Color.red.brighter().brighter().brighter().brighter().brighter();
  
}
