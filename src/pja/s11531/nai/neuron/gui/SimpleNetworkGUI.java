package pja.s11531.nai.neuron.gui;

import pja.s11531.nai.neuron.SimpleNetwork;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.util.Random;

/**
 * Created by Kris on 2015-04-26.
 */
public class SimpleNetworkGUI {
    JFrame frame;
    SimpleNetwork network;
    
    public static void main ( String[] args ) {
        SwingUtilities.invokeLater( SimpleNetworkGUI::new );
    }
    
    public SimpleNetworkGUI () {
        frame = new JFrame( "Multi-layer neural network" );
        
        frame.setSize( 1024, 1024 );
        frame.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
        frame.setLocationRelativeTo( null );
        frame.setVisible( true );
    }
}
