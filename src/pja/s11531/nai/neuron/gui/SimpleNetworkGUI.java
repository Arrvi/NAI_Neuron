package pja.s11531.nai.neuron.gui;

import pja.s11531.nai.neuron.SimpleNetwork;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Created by Kris on 2015-04-26.
 */
public class SimpleNetworkGUI {
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
                return f.getName().matches("/.+?\\.(json|neuron)/i");
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
            System.out.println(fileChooser.getSelectedFile());
        });
        
        JMenuBar menu = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(openFileAction);
        menu.add(fileMenu);
        frame.setJMenuBar(menu);
        
        
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
