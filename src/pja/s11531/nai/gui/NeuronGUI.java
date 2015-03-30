package pja.s11531.nai.gui;

import eu.arrvi.common.UIUtilities;
import jdk.internal.util.xml.impl.Input;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * Created by Kris on 2015-03-29.
 */
public class NeuronGUI {
    
    private final JFrame frame;
    private final NeuronTester neuronTester;

    public static void main ( String[] args ) {
        UIUtilities.setSystemLookAndFeel();
        SwingUtilities.invokeLater( NeuronGUI::new );
    }
    
    public NeuronGUI () {
        frame = new JFrame( "Neuron GUI" );

        neuronTester = new NeuronTester();
        neuronTester.setFrame(frame);
        frame.setContentPane(neuronTester.getMainPanel());
        frame.setResizable( false );
        frame.setJMenuBar( createJMenuBar() );
        UIUtilities.packAndShow( frame );
    }
    
    private JMenuBar createJMenuBar () {
        JMenuBar menu = new JMenuBar();
        
        JMenu file = new JMenu( "File" );
        file.setMnemonic( 'f' );
        file.add( new JMenuItem( quitAction ) );
        menu.add( file );
        
        JMenu view = new JMenu( "View" );
        view.setMnemonic( 'v' );
        view.add( new JCheckBoxMenuItem( changeNumberGridVisibility ) );
        view.add( new JCheckBoxMenuItem( changePointSizeAction ));
        menu.add(view);
        
        return menu;
    }
    
    private Action quitAction = new AbstractAction() {
        {
            putValue( NAME, "Quit" );
            putValue( MNEMONIC_KEY, KeyEvent.VK_Q );
            putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_Q, InputEvent.CTRL_MASK ) );
        }
    
        @Override
        public void actionPerformed ( ActionEvent e ) {
            frame.dispose();
        }
    };
    
    private Action changeNumberGridVisibility = new AbstractAction() {
        {
            putValue( NAME, "Neuron numeric grid" );
            putValue( MNEMONIC_KEY, KeyEvent.VK_N );
        }
        
        @Override
        public void actionPerformed ( ActionEvent e ) {
            neuronTester.setDrawOption(VisualizationPanel.DrawOption.DRAW_NEURON_VALUES_GRID, ((JCheckBoxMenuItem) e.getSource()).isSelected());
        }
    };
    
    private Action changePointSizeAction = new AbstractAction() {
        {
            putValue( NAME, "Large learning set points" );
            putValue( MNEMONIC_KEY, KeyEvent.VK_L );
        }
    
        @Override
        public void actionPerformed ( ActionEvent e ) {
            neuronTester.setDrawOption(VisualizationPanel.DrawOption.DRAW_LARGER_LEARNING_SET_POINTS, ((JCheckBoxMenuItem) e.getSource()).isSelected());
        }
    };
}
