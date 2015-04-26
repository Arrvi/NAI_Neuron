package pja.s11531.nai;

import com.sun.istack.internal.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.function.Function;

/**
 * Created by Kris on 2015-04-25.
 */
public class Kalejdoskop {
    JFrame frame;
    
    public static void main ( String[] args ) {
        int q =30;
        SwingUtilities.invokeLater(
                () -> new Kalejdoskop( 1000, 1000, q, i -> new Color( i * 200 / q, 0, i * 50 / q), 0.1 ) );
    }
    
    public Kalejdoskop ( int w, int h, int quantity, @NotNull Function<Integer, Color> colorGenerator, double ratio ) {
        //noinspection SpellCheckingInspection
        frame = new JFrame( "Kalejdoskop" );
        
        JPanel panel = new JPanel( new BorderLayout() );
        panel.add( new FancyRectangles( quantity, ratio, colorGenerator ), BorderLayout.CENTER );
        
        frame.setContentPane( panel );
        frame.setSize( w, h );
        frame.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
        frame.setLocationRelativeTo( null );
        frame.setVisible( true );
    }
    
    class FancyRectangles extends JComponent {
        private final int                      quantity;
        private final double                   ratio;
        private final Function<Integer, Color> colorGenerator;
        
        public FancyRectangles ( int quantity, double ratio, @NotNull Function<Integer, Color> colorGenerator ) {
            if ( ratio <= 0 || ratio >= 1 ) throw new IllegalArgumentException( "Invalid ratio" );
            this.quantity = quantity;
            this.ratio = ratio;
            this.colorGenerator = colorGenerator;
        }
        
        @Override
        protected void paintComponent ( Graphics g ) {
            super.paintComponent( g );
            Graphics2D g2d = (Graphics2D) g;
            
            g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            
            
            Point point = new Point( 0, 0 );
            for ( int i = 0; i < quantity; i++ ) {
                Point[] corners = getCorners( point );
                
                Shape rect = new Polygon(
                        Arrays.stream( corners )
                              .mapToInt( e -> e.x )
                              .toArray(),
                        Arrays.stream( corners )
                              .mapToInt( e -> e.y )
                              .toArray(), 4 );
                g2d.setColor( colorGenerator.apply( i ) );
                g2d.draw( rect );
                
                point = new Point(
                        (int) ( corners[0].x * ratio + corners[1].x * ( 1 - ratio ) ),
                        (int) ( corners[0].y * ratio + corners[1].y * ( 1 - ratio ) ) );
            }
        }
        
        private Point[] getCorners ( Point point ) {
            double edgeRatio = (double) getWidth() / (double) getHeight();
            return new Point[] {
                    new Point( point.x, point.y ),
                    new Point( (int) ( point.y * edgeRatio ), getHeight() - (int) ( point.x / edgeRatio ) ),
                    new Point( getWidth() - point.x, getHeight() - point.y ),
                    new Point( getWidth() - (int) ( point.y * edgeRatio ), (int) ( point.x / edgeRatio ) )
            };
        }
    }
}
