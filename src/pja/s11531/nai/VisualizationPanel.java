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
    
    private BigDecimal range = BigDecimal.TEN;
    
    public VisualizationPanel () {
        super( null );
        setPreferredSize( new Dimension( 400, 400 ) );
    }
    
    @Override
    protected void paintComponent ( Graphics g ) {
        super.paintComponent( g );
        
        int halfW = getWidth() / 2;
        int halfH = getHeight() / 2;
        
        Graphics2D g2d = ( (Graphics2D) g );
        g2d.drawLine( halfW, 0, halfW, getHeight() );
        g2d.drawLine( 0, halfH, getWidth(), halfH );
        
        int step = Math.max( getWidth(), getHeight() ) / 20;
        
        for ( int x = 1; x < 10; ++x ) {
            g2d.drawLine( halfW + x * step, halfH - markSize, halfW + x * step, halfH + markSize );
            g2d.drawLine( halfW - x * step, halfH - markSize, halfW - x * step, halfH + markSize );
            
            if ( x != 1 ) continue;
            g2d.drawString( Integer.toString( x ), halfW + x * step, halfH + markSize + 12 );
            g2d.drawString( Integer.toString( -x ), halfW - x * step, halfH + markSize + 12 );
        }
        for ( int y = 1; y < 10; ++y ) {
            g2d.drawLine( halfW - markSize, halfH + y * step, halfW + markSize, halfH + y * step );
            g2d.drawLine( halfW - markSize, halfH - y * step, halfW + markSize, halfH - y * step );
            
            if ( y != 1 ) continue;
            g2d.drawString( Integer.toString( -y ), halfW + markSize + 8, halfH + y * step + 5 );
            g2d.drawString( Integer.toString( y ), halfW + markSize + 8, halfH - y * step + 5 );
        }
        
        if ( neuron == null ) return;
        
        BigDecimal[] weights = neuron.getWeights();
        if ( weights.length != 3 ) {
            g2d.setColor( Color.RED );
            g2d.drawString( "Cannot draw a neuron with more or less than 3 inputs", 10, 10 );
            return;
        }
        
        double diagonalRatio = getHeight() / getWidth();
        double w1 = weights[0].doubleValue(), w2 = weights[1].doubleValue(), w0 = weights[2].doubleValue();
        
        g2d.setColor( Color.BLUE.darker() );
        
        if ( w1 != 0 || w2 != 0 ) {
            if ( w1 == 0 || w1 / w2 <= diagonalRatio ) {
                g2d.drawLine( 0, getHeight() - (int) ( ( 10 * w1 / w2 + w0 / w2 + 10 ) * step ), getWidth(),
                        getHeight() - (int) ( ( -10 * w1 / w2 + w0 / w2 + 10 ) * step ) );
            }
            else {
                g2d.drawLine( (int) ( ( 10 * w2 / w1 + w0 / w1 + 10 ) * step ), getHeight(), (int) ( ( -10 * w2 / w1 + w0 / w1 + 10 ) * step ), 0 );
            }
        }
        
        for ( int x = -9; x < 9; ++x ) {
            for ( int y = -9; y < 9; ++y ) {
                BigDecimal val = neuron.calculate( new BigDecimal[] { new BigDecimal( x ), new BigDecimal( y ) } );
                
                if ( val.compareTo( BigDecimal.ZERO ) > 0 ) {
                    g2d.setColor( Color.GREEN.darker() );
                }
                else {
                    g2d.setColor( Color.RED.darker() );
                }
                
                g2d.drawString( val.setScale( neuron.getFunc().getClass().isAssignableFrom( StepTransferFunction.class ) ? 0 : 2, RoundingMode
                                .HALF_UP )
                                   .toString(), halfW + x * step,
                        halfH - y * step );
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
        out = out
                .multiply( range.multiply( new BigDecimal( 2 ) ) )
                .divide( new BigDecimal( getWidth() ), 100, BigDecimal.ROUND_HALF_UP )
                .subtract( range );
        return out;
    }
    private BigDecimal heightToUnits( int y ) {
        BigDecimal out = new BigDecimal( y );
        out = out
                .multiply( range.multiply( new BigDecimal( 2 ) ) )
                .divide( new BigDecimal( getHeight() ), 100, BigDecimal.ROUND_HALF_UP )
                .subtract( range )
                .negate();
        return out;
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
