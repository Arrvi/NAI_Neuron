package pja.s11531.nai;

import javax.swing.*;
import java.awt.Graphics;

/**
 * Created by s11531 on 2015-03-19.
 */
public class NeuronTester {
    private JPanel mainPanel;
    private JPanel visualizationPane;
    private JPanel configPane;
    private JTextField input1;
    private JTextField input2;
    private JTextField input3;
    private JRadioButton unipolarRadioButton;
    private JRadioButton bipolarRadioButton;
    private JComboBox functionType;
    
    public static void main ( String[] args ) {
        JFrame frame = new JFrame( "NeuronTester" );
        frame.setContentPane( new NeuronTester().mainPanel );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.pack();
        frame.setVisible( true );
    }
    
    private void createUIComponents () {
        visualizationPane = new VisualizationPanel();
    }
}
