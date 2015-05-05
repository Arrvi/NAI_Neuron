package pja.s11531.nai.neuron.gui;

import eu.arrvi.common.UIUtilities;
import org.json.JSONException;
import pja.s11531.nai.neuron.SimpleNetwork;
import pja.s11531.nai.neuron.util.FileInterpreter;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Kris on 2015-04-26.
 */
public class SimpleNetworkGUI {
    private NetworkVisualizationPanel visualization;
    JFrame        frame;
    SimpleNetwork network;
    JFileChooser fileChooser = new JFileChooser();
    
    public static void main ( String[] args ) {
        UIUtilities.setSystemLookAndFeel();
        SwingUtilities.invokeLater( SimpleNetworkGUI::new );
    }
    
    public SimpleNetworkGUI () {
        frame = new JFrame( "Multi-layer neural network" );
        
        fileChooser.addChoosableFileFilter(
                new FileFilter() {
                    @Override
                    public boolean accept ( File f ) {
                        return f.isDirectory() || f.getName()
                                                   .matches( "/.+?\\.(json|neuron)/i" );
                    }
                    
                    @Override
                    public String getDescription () {
                        return "Neural network file (*.neuron, *.json)";
                    }
                } );
        fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
        fileChooser.setAcceptAllFileFilterUsed( true );
        fileChooser.setMultiSelectionEnabled( false );
        fileChooser.addActionListener(
                e -> {
                    try {
                        File selectedFile = fileChooser.getSelectedFile();
                        if ( selectedFile == null ) return;
                        
                        loadFile( selectedFile );
                    } catch ( ClassNotFoundException | IOException | JSONException e1 ) {
                        e1.printStackTrace();
                    }
                } );
        
        JMenuBar menu = new JMenuBar();
        JMenu fileMenu = new JMenu( "File" );
        fileMenu.add(
                nameAction(
                        "Open...", e -> fileChooser.showOpenDialog( frame ) ) );
        fileMenu.add(
                nameAction(
                        "Load sample network", e -> {
                            try {
                                loadFile(
                                        getClass().getClassLoader()
                                                  .getResourceAsStream( "sample-network.json" ) );
                            } catch ( IOException | JSONException | ClassNotFoundException e1 ) {
                                e1.printStackTrace();
                            }
                        } )
        );
        fileMenu.add(nameAction( "Quit", e->System.exit( 0 ) ));
        JMenu networkMenu = new JMenu( "Network" );
        networkMenu.add(nameAction( "Edit network", e->{} ));
        
        menu.add( fileMenu );
        menu.add( networkMenu );
        frame.setJMenuBar( menu );
        
        visualization = new NetworkVisualizationPanel();
        frame.getContentPane()
             .add( visualization );
        UIUtilities.packAndShow( frame, false );
        frame.setSize( 512, 512 );
    }
    
    private void loadFile ( File selectedFile ) throws IOException, JSONException, ClassNotFoundException {
        FileInterpreter interpreter = new FileInterpreter( selectedFile );
        SimpleNetwork simpleNetwork = (SimpleNetwork) interpreter.getContents();
        visualization.setNetwork( simpleNetwork );
    }
    
    private void loadFile ( InputStream selectedFile ) throws IOException, JSONException, ClassNotFoundException {
        FileInterpreter interpreter = new FileInterpreter( selectedFile );
        SimpleNetwork simpleNetwork = (SimpleNetwork) interpreter.getContents();
        visualization.setNetwork( simpleNetwork );
    }
    
    private Action nameAction ( String name, ActionLambda action ) {
        AbstractAction abstractAction = new AbstractAction() {
            @Override
            public void actionPerformed ( ActionEvent e ) {
                action.actionPerformed( e );
            }
        };
        abstractAction.putValue( AbstractAction.NAME, name );
        return abstractAction;
    }
    
    private interface ActionLambda {
        void actionPerformed ( ActionEvent e );
    }
}
