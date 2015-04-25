package pja.s11531.nai.neuron.gui;

import pja.s11531.nai.neuron.LearningSetFactory;
import pja.s11531.nai.neuron.Neuron;
import pja.s11531.nai.neuron.Teacher;

import javax.swing.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static pja.s11531.nai.neuron.gui.VisualizationPanel.DrawOption.*;

public class LearningDialog extends JDialog {
    private JPanel             contentPane;
    private JButton            buttonCancel;
    private JProgressBar       progress;
    private JLabel             epochField;
    private JLabel             differenceField;
    private JLabel             errorField;
    private VisualizationPanel visualizationPanel;
    private JLabel status;
    private JLabel result;
    private JButton loadBackButton;
    private SimpleGraph differenceGraph;
    private SimpleGraph errorGraph;

    private List<LearningSetFactory> factories;
    private Neuron                   neuron;
    private int                      epochs;
    private BigDecimal               learningFactor;
    private List<Neuron> learningHistory;

    public LearningDialog ( NeuronTester parent, List<LearningSetFactory> factories, Neuron neuron, int epochs, BigDecimal learningFactor ) {
        this.factories = factories;
        this.neuron = neuron;
        this.epochs = epochs;
        this.learningFactor = learningFactor;
        
        setContentPane( contentPane );
        setModal( true );
        
        buttonCancel.addActionListener( e -> onCancel() );
        loadBackButton.addActionListener( e -> {
            parent.loadNeuron(learningHistory.get(learningHistory.size()-1));
            dispose();
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
        addWindowListener( new WindowAdapter() {
            public void windowClosing ( WindowEvent e ) {
                onCancel();
            }
        } );

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
        
        learningWorker.addPropertyChangeListener( ( evt ) -> {
            if ( evt.getPropertyName()
                    .equals( "progress" ) )
                progress.setValue( (Integer) evt.getNewValue() );
        } );
        
        visualizationPanel.setDrawOptions( DRAW_ALL_LABELS, DRAW_LEARNING_SET_POINTS, DRAW_GRID, DRAW_POINT_GRID, FADE_LEARNING_HISTORY );
        if ( factories.stream().mapToInt( LearningSetFactory::getQuantity ).sum() < 500 )
            visualizationPanel.setDrawOptions( DRAW_LARGER_LEARNING_SET_POINTS );
        visualizationPanel.setLearningSets( factories );
        visualizationPanel.setNeuron( neuron );
        
        differenceGraph.setDataCount(epochs);
        errorGraph.setDataCount(epochs);
        
        pack();
        learn();
    }
    
    private void onCancel () {
        if ( learningWorker.getState() == SwingWorker.StateValue.STARTED ) learningWorker.cancel( true );
        dispose();
    }
    
    private void learn () {
        learningWorker.execute();
    }
    
    private class LearningReport {
        private final int        epoch;
        private final BigDecimal difference;
        private final int        errors;
        
        public LearningReport ( int epoch, BigDecimal difference, int errors ) {
            this.epoch = epoch;
            this.difference = difference;
            this.errors = errors;
        }
        
        public int getEpoch () {
            return epoch;
        }
        
        public BigDecimal getDifference () {
            return difference;
        }
        
        public int getErrors () {
            return errors;
        }
    }
    
    
    private SwingWorker<List<Neuron>, LearningReport> learningWorker = new SwingWorker<List<Neuron>, LearningReport>() {
        
        @Override
        protected List<Neuron> doInBackground () throws Exception {
//            ArrayList<LearningSetFactory> factories = Collections.list( listModel.elements() );
            List<Neuron> history = new ArrayList<>();
            history.add( neuron );
            Neuron currentState = neuron;
            for ( int epoch = 0; epoch < epochs; epoch++ ) {
                if ( isCancelled() ) {
                    System.out.println("Cancelled.");
                    break;
                }
                
                int error = 0;
                Teacher teacher = new Teacher( currentState, factories.toArray( new LearningSetFactory[factories.size()] ), learningFactor );
                currentState = teacher.call();
                error += teacher.getError( currentState );
                BigDecimal difference = history.get( history.size() - 1 )
                                               .difference( currentState )
                                               .setScale( 10, BigDecimal.ROUND_HALF_UP );
                System.out.printf( "Epoch %d difference: %s, error: %d%n", epoch, difference.toPlainString(), error );
                history.add( currentState );
    
                setProgress( (int) ( 100.0 * epoch / epochs ) );
                publish( new LearningReport( epoch, difference, error ) );
    
                if ( difference.compareTo( BigDecimal.ZERO ) == 0 ) {
                    System.out.println( "No difference. Ending now." );
                    break;
                }
                if ( error == 0 ) {
                    System.out.println( "No error. Ending now." );
                    break;
                }
            }
            return history;
        }
    
        @Override
        protected void process ( List<LearningReport> chunks ) {
            LearningReport report = chunks.get( chunks.size() - 1 );
        
            epochField.setText( String.valueOf( report.getEpoch() + 1 ) );
            differenceField.setText( report.getDifference()
                                           .toPlainString() );
            errorField.setText( String.valueOf( report.getErrors() ) );

            for (LearningReport chunk : chunks) {
                differenceGraph.addRecord(chunk.getDifference());
                errorGraph.addRecord(new BigDecimal(chunk.getErrors()));
            }
        }
    
        @Override
        protected void done () {
            try {
                System.out.println( "Learning completed." );
                setProgress( 100 );
                learningHistory = get();
                visualizationPanel.setLearningHistory(learningHistory);
                status.setText( isCancelled() ? "Cancelled." : "Done." );
                result.setText( learningHistory.get(learningHistory.size() - 1)
                                       .toString() );
                loadBackButton.setEnabled(true);
            } catch ( InterruptedException | ExecutionException e ) {
                e.printStackTrace();
            }
        }
    };
}
