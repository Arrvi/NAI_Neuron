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
    public VisualizationPanel () {
        super(null);
        setPreferredSize( new Dimension( 400, 400 ) );
    }
    
    @Override
    protected void paintComponent ( Graphics g ) {
        super.paintComponent( g );
    
        Graphics2D g2d = ( (Graphics2D) g );
        g2d.drawLine( getWidth()/2, 0, getWidth()/2, getHeight() );
        g2d.drawLine( 0, getHeight()/2, getWidth(), getHeight()/2 );
    }
}
