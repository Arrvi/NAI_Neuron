package pja.s11531.nai;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

/**
 * Created by Kris on 2015-03-20.
 */
public class VisualizationPanel extends JPanel {
    private int markSize = 2;
    
    public VisualizationPanel () {
        super(null);
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
            g2d.drawLine( halfW+x*step, halfH-markSize, halfW+x*step, halfH+markSize );
            g2d.drawString( Integer.toString( x ), halfW+x*step, halfH+markSize+12 );
        
            g2d.drawLine( halfW - x * step, halfH - markSize, halfW - x * step, halfH + markSize );
            g2d.drawString( Integer.toString( -x ), halfW-x*step, halfH+markSize+12 );
        }
        for ( int y = 1; y < 10; ++y ) {
            g2d.drawLine( halfW-markSize, halfH+y*step, halfW+markSize, halfH+y*step );
            g2d.drawString( Integer.toString( -y ), halfW+markSize+8, halfH+y*step+5 );
    
            g2d.drawLine( halfW-markSize, halfH-y*step, halfW+markSize, halfH-y*step );
            g2d.drawString( Integer.toString( y ), halfW+markSize+8, halfH-y*step+5 );
        }
    }
}
