package pja.s11531.nai;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by Kris on 2015-03-20.
 */
public class VisualizationPanel extends JPanel {
    private int markSize = 2;
    private Neuron       neuron;
    private MouseAdapter mouseAdapter;
    
    private int defaultScale = 10;
    private BigDecimal range = BigDecimal.TEN.setScale( defaultScale, BigDecimal.ROUND_HALF_UP );
    private final BigDecimal doubleRange = range.multiply( new BigDecimal( 2 ) );
    
    public VisualizationPanel () {
        super( null );
        setPreferredSize( new Dimension( 400, 400 ) );
    }
    
    @Override
    protected void paintComponent ( Graphics g ) {
        super.paintComponent( g );
        
        Graphics2D g2d = ( (Graphics2D) g );
        
        g2d.drawLine( unitsToWidth( BigDecimal.ZERO ), unitsToHeight( range.negate() ), unitsToWidth( BigDecimal.ZERO ), unitsToHeight( range ) );
        g2d.drawLine( unitsToWidth( range.negate() ), unitsToHeight( BigDecimal.ZERO ), unitsToWidth( range ), unitsToHeight( BigDecimal.ZERO ) );
        
        for ( int ix = 1; ix < 10; ++ix ) {
            BigDecimal bdx = new BigDecimal( ix ), bdnx = bdx.negate();
            int x = unitsToWidth( bdx );
            int nx = unitsToWidth( bdnx );
            int h0 = unitsToHeight( BigDecimal.ZERO );
            g2d.drawLine(
                    x, h0 - markSize,
                    x, h0 + markSize );
            g2d.drawLine(
                    nx, h0 - markSize,
                    nx, h0 + markSize );
            
            if ( ix != 1 ) continue;
            g2d.drawString( bdx.toPlainString(), x, h0 + markSize + 12 );
            g2d.drawString( bdnx.toPlainString(), nx, h0 + markSize + 12 );
        }
        for ( int iy = 1; iy < 10; ++iy ) {
            BigDecimal bdy = new BigDecimal( iy ), bdny = bdy.negate();
            int y = unitsToHeight( bdy );
            int ny = unitsToHeight( bdny );
            int w0 = unitsToWidth( BigDecimal.ZERO );
            g2d.drawLine( w0 - markSize, y, w0 + markSize, y );
            g2d.drawLine( w0 - markSize, ny, w0 + markSize, ny );
            
            if ( iy != 1 ) continue;
            g2d.drawString( bdy.toPlainString(), w0 + markSize + 8, y + 5 );
            g2d.drawString( bdny.toPlainString(), w0 + markSize + 8, ny + 5 );
        }
        
        if ( neuron == null ) return;
        
        BigDecimal[] weights = neuron.getWeights();
        if ( weights.length != 3 ) {
            g2d.setColor( Color.RED );
            g2d.drawString( "Cannot draw a neuron with more or less than 3 inputs", 10, 10 );
            return;
        }
        
        double diagonalRatio = getHeight() / getWidth();
        BigDecimal w1 = weights[0].setScale( defaultScale, BigDecimal.ROUND_HALF_UP ), 
                w2 = weights[1].setScale( defaultScale, BigDecimal.ROUND_HALF_UP ), 
                w0 = weights[2].setScale( defaultScale, BigDecimal.ROUND_HALF_UP );
        
        g2d.setColor( Color.BLUE.darker() );
        
        if ( !w1.equals( BigDecimal.ZERO ) || !w2.equals( BigDecimal.ZERO ) ) {
            BigDecimal 
                    a12 = w1.divide( w2, BigDecimal.ROUND_HALF_UP ), 
                    b02 = w0.divide( w2, BigDecimal.ROUND_HALF_UP ),
                    a21 = w2.divide( w1, BigDecimal.ROUND_HALF_UP ),
                    b01 = w0.divide( w1, BigDecimal.ROUND_HALF_UP );
            if ( w1.equals( BigDecimal.ZERO ) || a12.doubleValue() <= diagonalRatio ) {
                g2d.drawLine( 
                        unitsToWidth( range.negate() ), 
                        unitsToHeight( a12.multiply( range.negate() ).negate().add( b02 ) ), 
                        unitsToWidth( range ), 
                        unitsToHeight( a12.multiply( range ).negate().add( b02 ) ) );
            }
            else {
                g2d.drawLine( 
                        unitsToWidth( a21.multiply( range.negate() ).negate().add( b01 ) ),
                        unitsToHeight( range.negate() ),
                        unitsToWidth( a21.multiply( range ).negate().add( b01 ) ),
                        unitsToHeight( range ));
            }
        }
        
        for ( int x = -9; x < 9; ++x ) {
            for ( int y = -9; y < 9; ++y ) {
                BigDecimal[] point = { new BigDecimal( x ), new BigDecimal( y ) };
                BigDecimal val = neuron.calculate( point );
                
                if ( val.compareTo( BigDecimal.ZERO ) > 0 ) {
                    g2d.setColor( Color.GREEN.darker() );
                }
                else {
                    g2d.setColor( Color.RED.darker() );
                }
                
                g2d.drawString( val.setScale( 
                                neuron.getFunc().getClass().isAssignableFrom( StepTransferFunction.class ) ? 0 : 2, 
                                RoundingMode.HALF_UP ).toPlainString(), 
                        unitsToWidth( point[0] ),
                        unitsToHeight( point[1] ) );
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
                    callback.callback( widthToUnits( e.getX() ), heightToUnits( e.getY() ) );
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
    private BigDecimal heightToUnits( int y ) {
        BigDecimal out = new BigDecimal( y );
        out = out.setScale( defaultScale, BigDecimal.ROUND_HALF_UP )
                 .multiply( doubleRange )
                .divide( new BigDecimal( getHeight() ), BigDecimal.ROUND_HALF_UP )
                .subtract( range )
                .negate();
        return out;
    }
    private int unitsToWidth(BigDecimal x) {
        return x.setScale( defaultScale, BigDecimal.ROUND_HALF_UP )
                .add(range)
                .divide( doubleRange, BigDecimal.ROUND_HALF_UP )
                .multiply( new BigDecimal( getWidth() ) )
                .intValue();
    }
    private int unitsToHeight(BigDecimal y) {
        return y.setScale( defaultScale, BigDecimal.ROUND_HALF_UP )
                .negate()
                .add(range)
                .divide( doubleRange, BigDecimal.ROUND_HALF_UP )
                .multiply( new BigDecimal( getHeight() ) )
                .intValue();
    }
    
    public Neuron getNeuron () {
        return neuron;
    }
    
    public void setNeuron ( Neuron neuron ) {
        this.neuron = neuron;
        repaint();
    }
    
    public interface PointPickerCallback {
        void callback ( BigDecimal x, BigDecimal y );
    }
}
