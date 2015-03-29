package pja.s11531.nai.gui;

import pja.s11531.nai.LearningElement;
import pja.s11531.nai.LearningSetFactory;
import pja.s11531.nai.Neuron;
import pja.s11531.nai.StepTransferFunction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumSet;
import java.util.List;

import static pja.s11531.nai.gui.VisualizationPanel.DrawOption.*;

/**
 * Created by Kris on 2015-03-20.
 */
public class VisualizationPanel extends JComponent {
    protected static final BigDecimal TWO      = new BigDecimal( 2 );
    private                int        markSize = 2;
    private Neuron       neuron;
    private MouseAdapter mouseAdapter;
    
    private       int        defaultScale = 10;
    private       BigDecimal range        = BigDecimal.TEN.setScale( defaultScale, BigDecimal.ROUND_HALF_UP );
    private final BigDecimal doubleRange  = range.multiply( TWO );
    private List<LearningSetFactory> learningSets;
    private List<Neuron>             learningHistory;
    
    private EnumSet<DrawOption> drawOptions = EnumSet.of( DrawOption.DRAW_AXIS,
            DrawOption.DRAW_AXIS_TICKS,
            DrawOption.DRAW_UNITY_LABELS,
            DrawOption.ANTI_ALIASING );
    
    public enum DrawOption {
        DRAW_AXIS,
        DRAW_AXIS_TICKS,
        DRAW_UNITY_LABELS,
        DRAW_NEGATIVE_UNITY_LABELS,
        DRAW_ALL_LABELS,
        DRAW_GRID,
        DRAW_POINT_GRID,
        
        DRAW_NEURON_VALUES_GRID,
        
        DRAW_LEARNING_SET_RADIUS,
        DRAW_LEARNING_SET_CIRCLE,
        DRAW_LEARNING_SET_POINTS,
        DRAW_LARGER_LEARNING_SET_POINTS,
        
        FADE_LEARNING_HISTORY,
        
        ANTI_ALIASING;
        
        public long getOptionValue () {
            return 1 << this.ordinal();
        }
    }
    
    public EnumSet<DrawOption> getDrawOptions ( long optionsValue ) {
        EnumSet<DrawOption> drawOptions = EnumSet.noneOf( DrawOption.class );
        for ( DrawOption drawOption : DrawOption.values() ) {
            long flagValue = drawOption.getOptionValue();
            if ( ( flagValue & optionsValue ) == flagValue ) {
                drawOptions.add( drawOption );
            }
        }
        
        return drawOptions;
    }
    
    public void setDrawOption ( DrawOption option, boolean value ) {
        if ( value )
            drawOptions.add( option );
        else
            drawOptions.remove( option );
    }
    
    public void setDrawOptions ( DrawOption... options ) {
        for ( DrawOption option : options ) {
            setDrawOption( option, true );
        }
    }
    
    private boolean drawAnyOf ( DrawOption... options ) {
        if ( options.length == 0 ) throw new IllegalArgumentException( "No options specified. At least one is required." );
        for ( DrawOption option : options ) {
            if ( option( option ) ) return true;
        }
        return false;
    }
    
    private boolean drawAllOf ( DrawOption... options ) {
        if ( options.length == 0 ) throw new IllegalArgumentException( "No options specified. At least one is required." );
        for ( DrawOption option : options ) {
            if ( !option( option ) ) return false;
        }
        return true;
    }
    
    private boolean option ( DrawOption option ) {
        return drawOptions.contains( option );
    }
    
    public VisualizationPanel () {
        super();
        setMinimumSize( new Dimension( 400, 400 ) );
    }
    
    @Override
    protected void paintComponent ( Graphics g ) {
        super.paintComponent( g );
        
        Graphics2D g2d = ( (Graphics2D) g );
        if ( option( ANTI_ALIASING ) )
            g2d.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON );
        
        if ( option( DRAW_AXIS ) ) {
            g2d.drawLine( unitsToWidth( BigDecimal.ZERO ), unitsToHeight( range.negate() ), unitsToWidth( BigDecimal.ZERO ), unitsToHeight( range ) );
            g2d.drawLine( unitsToWidth( range.negate() ), unitsToHeight( BigDecimal.ZERO ), unitsToWidth( range ), unitsToHeight( BigDecimal.ZERO ) );
        }
        
        
        for ( int i = 1; i < 10; ++i ) {
            BigDecimal bdi = new BigDecimal( i ), bdni = bdi.negate();
            int x = unitsToWidth( bdi );
            int nx = unitsToWidth( bdni );
            int h0 = unitsToHeight( BigDecimal.ZERO );
            int y = unitsToHeight( bdi );
            int ny = unitsToHeight( bdni );
            int w0 = unitsToWidth( BigDecimal.ZERO );
            
            if ( option( DRAW_AXIS_TICKS ) ) {
                g2d.drawLine(
                        x, h0 - markSize,
                        x, h0 + markSize );
                g2d.drawLine(
                        nx, h0 - markSize,
                        nx, h0 + markSize );
                g2d.drawLine(
                        w0 - markSize, y,
                        w0 + markSize, y );
                g2d.drawLine(
                        w0 - markSize, ny,
                        w0 + markSize, ny );
            }
            
            if ( option( DRAW_ALL_LABELS ) || i == 1 && drawAnyOf( DRAW_UNITY_LABELS, DRAW_NEGATIVE_UNITY_LABELS ) ) {
                if ( drawAnyOf( DRAW_ALL_LABELS, DRAW_UNITY_LABELS ) ) {
                    g2d.drawString( bdi.toPlainString(), x, h0 + markSize + 12 );
                    g2d.drawString( bdi.toPlainString(), w0 + markSize + 2, y + 5 );
                }
                if ( drawAnyOf( DRAW_ALL_LABELS, DRAW_NEGATIVE_UNITY_LABELS ) ) {
                    g2d.drawString( bdni.toPlainString(), nx, h0 + markSize + 12 );
                    g2d.drawString( bdni.toPlainString(), w0 + markSize + 2, ny + 5 );
                }
            }
        }
        
        if ( option( DRAW_GRID ) ) {
            if ( option( DRAW_POINT_GRID ) ) {
                g2d.setColor( Color.BLACK );
                for ( int x = range.negate()
                                   .intValue() + 1; x < range.intValue(); ++x ) {
                    if ( x == 0 ) continue;
                    for ( int y = range.negate()
                                       .intValue() + 1; y < range.intValue(); ++y ) {
                        if ( y == 0 ) continue;
                        
                        int px = unitsToWidth( new BigDecimal( x ) );
                        int py = unitsToHeight( new BigDecimal( y ) );
                        
                        g2d.drawLine( px, py, px, py );
                    }
                }
            }
            else {
                g2d.setColor( new Color( 0x88000000, true ) );
                for ( int i = range.negate()
                                   .intValue() + 1; i < range.intValue(); i++ ) {
                    BigDecimal bdi = new
                            BigDecimal( i );
                    g2d.drawLine(
                            unitsToWidth( range.negate() ),
                            unitsToHeight( bdi ),
                            unitsToWidth( range ),
                            unitsToHeight( bdi ) );
                    g2d.drawLine(
                            unitsToWidth( bdi ),
                            unitsToHeight( range.negate() ),
                            unitsToWidth( bdi ),
                            unitsToHeight( range ) );
                }
            }
        }
        
        if ( neuron != null ) drawNeuron( g2d );
        
        if ( learningSets != null && !learningSets.isEmpty() ) drawLearningSets( g2d );
        
        if ( learningHistory != null && !learningHistory.isEmpty() ) drawLearningHistory( g2d );
    }
    
    private void drawLearningHistory ( Graphics2D g2d ) {
        g2d.setStroke( new BasicStroke( 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[] { 3, 3 }, 0 ) );
        for ( int i = 0; i < learningHistory.size(); i++ ) {
            int alpha = ( option( FADE_LEARNING_HISTORY ) ) ? (int) ( ( ( (double) i / learningHistory.size() ) * 0.5 + 0.5 ) * 0xff ) : 0xff;
            
            if ( i < learningHistory.size() - 1 )
                g2d.setColor( new Color( 0x0000aa + alpha << 24, true ) );
            else {
                g2d.setColor( Color.GREEN.darker() );
                g2d.setStroke( new BasicStroke( 3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{7, 7}, 0 ) );
            }
            
            drawNeuronLine( g2d,
                    learningHistory.get( i )
                                   .getWeights() );
        }
    }
    
    
    private void drawLearningSets ( Graphics2D g2d ) {
        for ( LearningSetFactory set : learningSets ) {
            if ( set.getCenter().length != 2 ) return;
            
            int x = unitsToWidth( set.getCenter()[0] );
            int y = unitsToHeight( set.getCenter()[1] );
            int xRadius = unitsToLength( set.getVariance() );
            int yRadius = unitsToLength( set.getVariance() );
            
            g2d.setColor( set.getMemberClass()
                             .equals( BigDecimal.ONE ) ? Color.GREEN.darker() : Color.RED.darker() );
            g2d.fill( new Ellipse2D.Double( x, y, 3, 3 ) );
            if ( option( DRAW_LEARNING_SET_CIRCLE ) )
                g2d.draw( new Ellipse2D.Double(
                        x - xRadius,
                        y - yRadius,
                        xRadius * 2,
                        yRadius * 2 ) );
            if ( option( DRAW_LEARNING_SET_RADIUS ) )
                g2d.drawLine( x, y, x + xRadius, y );
            
            if ( option( DRAW_LEARNING_SET_POINTS ) )
                for ( LearningElement learningElement : set.getLearningSet() ) {
                    int ex = unitsToWidth( learningElement.getArguments()[0] );
                    int ey = unitsToHeight( learningElement.getArguments()[1] );
                    
                    if ( option( DRAW_LARGER_LEARNING_SET_POINTS ) )
                        g2d.fill( new Ellipse2D.Double( ex - 1, ey - 1, 3, 3 ) );
                    else
                        g2d.drawLine( ex, ey, ex, ey );
                }
        }
    }
    
    private void drawNeuron ( Graphics2D g2d ) {
        BigDecimal[] weights = neuron.getWeights();
        if ( weights.length != 3 ) {
            g2d.setColor( Color.RED );
            g2d.drawString( "Cannot draw a neuron with more or less than 3 inputs", 10, 10 );
            return;
        }
        
        g2d.setColor( Color.BLUE.darker() );
        
        drawNeuronLine( g2d, weights );
        
        if ( option( DRAW_NEURON_VALUES_GRID ) )
            for ( int x = -9; x <= 9; ++x ) {
                for ( int y = -9; y <= 9; ++y ) {
                    BigDecimal[] point = { new BigDecimal( x ), new BigDecimal( y ) };
                    BigDecimal val = neuron.calculate( point );
                    
                    if ( val.compareTo( BigDecimal.ZERO ) > 0 ) {
                        g2d.setColor( Color.GREEN.darker() );
                    }
                    else {
                        g2d.setColor( Color.RED.darker() );
                    }
                    
                    g2d.drawString( val.setScale(
                                    neuron.getFunc()
                                          .getClass()
                                          .isAssignableFrom( StepTransferFunction.class ) ? 0 : 2,
                                    RoundingMode.HALF_UP )
                                       .toPlainString(),
                            unitsToWidth( point[0] ) + 2,
                            unitsToHeight( point[1] ) - 2 );
                }
            }
    }
    
    private void drawNeuronLine ( Graphics2D g2d, BigDecimal[] weights ) {
        double diagonalRatio = getHeight() / getWidth();
        BigDecimal w1 = weights[0].setScale( defaultScale, BigDecimal.ROUND_HALF_UP ),
                w2 = weights[1].setScale( defaultScale, BigDecimal.ROUND_HALF_UP ),
                w0 = weights[2].setScale( defaultScale, BigDecimal.ROUND_HALF_UP );
        
        
        if ( !w1.equals( BigDecimal.ZERO ) || !w2.equals( BigDecimal.ZERO ) ) {
            BigDecimal
                    a12 = w1.divide( w2, BigDecimal.ROUND_HALF_UP ),
                    b02 = w0.divide( w2, BigDecimal.ROUND_HALF_UP ),
                    a21 = w2.divide( w1, BigDecimal.ROUND_HALF_UP ),
                    b01 = w0.divide( w1, BigDecimal.ROUND_HALF_UP );
            if ( w1.equals( BigDecimal.ZERO ) || a12.doubleValue() <= diagonalRatio ) {
                g2d.drawLine(
                        unitsToWidth( range.negate() ),
                        unitsToHeight( a12.multiply( range.negate() )
                                          .negate()
                                          .add( b02 ) ),
                        unitsToWidth( range ),
                        unitsToHeight( a12.multiply( range )
                                          .negate()
                                          .add( b02 ) ) );
            }
            else {
                g2d.drawLine(
                        unitsToWidth( a21.multiply( range.negate() )
                                         .negate()
                                         .add( b01 ) ),
                        unitsToHeight( range.negate() ),
                        unitsToWidth( a21.multiply( range )
                                         .negate()
                                         .add( b01 ) ),
                        unitsToHeight( range ) );
            }
        }
    }
    
    public void startPointPicker ( PointPickerCallback callback ) {
        setCursor( Cursor.getPredefinedCursor( Cursor.CROSSHAIR_CURSOR ) );
        mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked ( MouseEvent e ) {
                super.mouseClicked( e );
                if ( e.getSource() != VisualizationPanel.this ) return;
                
                if ( e.getButton() == MouseEvent.BUTTON1 ) {
                    BigDecimal x = widthToUnits( e.getX() );
                    BigDecimal y = heightToUnits( e.getY() );
                    if ( !e.isShiftDown() ) {
                        x = x.setScale( 0, BigDecimal.ROUND_HALF_UP );
                        y = y.setScale( 0, BigDecimal.ROUND_HALF_UP );
                    }
                    callback.callback( x, y );
                }
                
                stopPointPicker();
            }
        };
        addMouseListener( mouseAdapter );
    }
    
    public void stopPointPicker () {
        setCursor( Cursor.getDefaultCursor() );
        if ( mouseAdapter != null ) {
            removeMouseListener( mouseAdapter );
        }
    }
    
    private BigDecimal widthToUnits ( int x ) {
        BigDecimal out = new BigDecimal( x );
        out = out.setScale( defaultScale, BigDecimal.ROUND_HALF_UP )
                 .multiply( doubleRange )
                 .divide( new BigDecimal( getWidth() ), BigDecimal.ROUND_HALF_UP )
                 .subtract( range );
        return out;
    }
    
    private BigDecimal heightToUnits ( int y ) {
        BigDecimal out = new BigDecimal( y );
        out = out.setScale( defaultScale, BigDecimal.ROUND_HALF_UP )
                 .multiply( doubleRange )
                 .divide( new BigDecimal( getHeight() ), BigDecimal.ROUND_HALF_UP )
                 .subtract( range )
                 .negate();
        return out;
    }
    
    private int unitsToWidth ( BigDecimal x ) {
        return x.setScale( defaultScale, BigDecimal.ROUND_HALF_UP )
                .add( range )
                .divide( doubleRange, BigDecimal.ROUND_HALF_UP )
                .multiply( new BigDecimal( getWidth() ) )
                .intValue();
    }
    
    private int unitsToHeight ( BigDecimal y ) {
        return y.setScale( defaultScale, BigDecimal.ROUND_HALF_UP )
                .negate()
                .add( range )
                .divide( doubleRange, BigDecimal.ROUND_HALF_UP )
                .multiply( new BigDecimal( getHeight() ) )
                .intValue();
    }
    
    private int unitsToLength ( BigDecimal s ) {
        return s.setScale( defaultScale, BigDecimal.ROUND_HALF_UP )
                .divide( doubleRange, BigDecimal.ROUND_HALF_UP )
                .multiply( new BigDecimal( getWidth() ) )
                .intValue();
    }
    
    public Neuron getNeuron () {
        return neuron;
    }
    
    public void setNeuron ( Neuron neuron ) {
        this.neuron = neuron;
        repaint();
    }
    
    public void setLearningSets ( List<LearningSetFactory> elements ) {
        learningSets = elements;
        repaint();
    }
    
    public List<Neuron> getLearningHistory () {
        return learningHistory;
    }
    
    public void setLearningHistory ( List<Neuron> learningHistory ) {
        this.learningHistory = learningHistory;
        repaint();
    }
    
    public interface PointPickerCallback {
        void callback ( BigDecimal x, BigDecimal y );
    }
}
