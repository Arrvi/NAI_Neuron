package pja.s11531.nai.neuron.gui;

import org.json.JSONException;
import pja.s11531.nai.neuron.LearningElement;
import pja.s11531.nai.neuron.SimpleNetwork;
import pja.s11531.nai.neuron.util.FileInterpreter;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Created by Kris on 2015-04-26.
 */
public class SimpleNetworkGUI {
    private NetworkVisualizationPanel visualization;
    JFrame frame;
    SimpleNetwork network;
    JFileChooser fileChooser = new JFileChooser();

    public static void main ( String[] args ) {
        SwingUtilities.invokeLater( SimpleNetworkGUI::new );
    }
    
    public SimpleNetworkGUI () {
        frame = new JFrame( "Multi-layer neural network" );
        
        fileChooser.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().matches("/.+?\\.(json|neuron)/i");
            }

            @Override
            public String getDescription() {
                return "Neural network file (*.neuron, *.json)";
            }
        });
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(true);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.addActionListener(e->{
            try {
                File selectedFile = fileChooser.getSelectedFile();
                if ( selectedFile == null ) return;
                
                FileInterpreter interpreter = new FileInterpreter(selectedFile);
                SimpleNetwork simpleNetwork = (SimpleNetwork) interpreter.getContents();

                LearningElement learningElement = new LearningElement(
                        new BigDecimal[] {
                                new BigDecimal( -3 ),
                                new BigDecimal( 4 )
                        }, new BigDecimal[] {
                        BigDecimal.ONE,
                        BigDecimal.ZERO,
                        BigDecimal.ONE
                } );
                
                simpleNetwork.learn(learningElement, BigDecimal.ONE);
                
                visualization.setNetwork(simpleNetwork);
            } catch (ClassNotFoundException | IOException | JSONException e1) {
                e1.printStackTrace();
            }
        });
        
        JMenuBar menu = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(openFileAction);
        menu.add(fileMenu);
        frame.setJMenuBar(menu);

        visualization = new NetworkVisualizationPanel();
        frame.getContentPane().add(visualization);
        
        frame.setSize(512, 512);
        frame.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
        frame.setLocationRelativeTo( null );
        frame.setVisible( true );
    }
    
    private Action openFileAction = new AbstractAction () {
        {
            putValue(NAME, "Open...");
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            fileChooser.showOpenDialog(frame);
        }
    };
}
