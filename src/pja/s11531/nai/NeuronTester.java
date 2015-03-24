package pja.s11531.nai;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;

/**
 * Created by s11531 on 2015-03-19.
 */
public class NeuronTester {
    private static JFrame                    frame;
    private DefaultListModel<LearningSetFactory> listModel = new DefaultListModel<>();
    private JPanel                    mainPanel;
    private VisualizationPanel        visualizationPane;
    private JPanel                    configPane;
    private JTextField                input1;
    private JTextField                input2;
    private JTextField                input0;
    private JRadioButton              unipolarRadioButton;
    private JRadioButton              bipolarRadioButton;
    private JComboBox                 functionType;
    private JButton                   calculateButton;
    private JRadioButton              upRadioButton;
    private JRadioButton              downRadioButton;
    private JTextField                learningSetCenter;
    private JButton                   pointPickerButton;
    private JTextField                learningSetVariance;
    private JTextField                learningSetQuantity;
    private JButton                   addLearningSet;
    private JSlider                   learningFactor;
    private JTextField                learningEpochs;
    private JButton                   learnButton;
    private JList<LearningSetFactory> learningSetsList;
    private JComboBox                 distributionFunction;
    private JButton                   clearButton;
    private JComboBox  learningSetMemberClassCombo;
    private JTextField learningSetMemberClass;
    
    public NeuronTester () {
        calculateButton.addActionListener( ( evt ) -> calculate() );
        pointPickerButton.addActionListener( ( evt ) -> startPointPicker() );
        clearButton.addActionListener( ( evt ) -> visualizationPane.setNeuron( null ) );
        addLearningSet.addActionListener( ( evt ) -> {
            try {
                parseAndAddLearningSet();
            } 
            catch (NumberFormatException e) {
                JOptionPane.showMessageDialog( frame, "Invalid number format", "Error", JOptionPane.ERROR_MESSAGE );
            }
            catch ( Exception e ) {
                JOptionPane.showMessageDialog( frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
            }
        } );
        learningSetsList.setModel( listModel );
        learningSetMemberClassCombo.addActionListener( (evt) -> setMemberClass() );
    }
    
    private void parseAndAddLearningSet () throws Exception {
        if ( learningSetCenter.getText().length() == 0 ) {
            throw new Exception( "Center point has to be specified" );
        }
        String[] coords = learningSetCenter.getText().split( ",\\s*" );
        if ( coords.length != 2 ) {
            throw new Exception( "Wrong point format. There should be 2 numbers separated by comma" );
        }
        BigDecimal x;
        BigDecimal y;
        BigDecimal variance;
        BigInteger quantity;
        BigDecimal memberClass;
        
        x = new BigDecimal( coords[0] );
        y = new BigDecimal( coords[1] );
        
        if ( learningSetVariance.getText().length() == 0 ) {
            learningSetVariance.setText( "0" );
        }
        variance = new BigDecimal( learningSetVariance.getText() );
        
        if ( learningSetQuantity.getText().length() == 0 ) {
            learningSetQuantity.setText( "0" );
        }
        quantity = new BigInteger( learningSetQuantity.getText() );
        
        if ( learningSetMemberClass.getText().length() == 0 ) {
            setMemberClass();
        }
        
        memberClass = new BigDecimal( learningSetMemberClass.getText() );
            
        addLearningSet( x, y, variance, quantity, memberClass );
        learningSetCenter.setText( "" );
    }
    
    private void setMemberClass () {
        String mClass;
        if ( learningSetMemberClassCombo.getModel().getSelectedItem().equals( "HIGH" ) ) mClass = "1";
        else {
            if ( unipolarRadioButton.isSelected() ) mClass = "0";
            else mClass = "-1";
        }
        learningSetMemberClass.setText( mClass );
    }
    
    private void addLearningSet ( BigDecimal x, BigDecimal y, BigDecimal variance, BigInteger quantity, BigDecimal memberClass ) {
        LearningSetFactory factory = new LearningSetFactory( x, y, variance, quantity, memberClass );
        listModel.addElement( factory );
        
        visualizationPane.setLearningSets( Collections.list( listModel.elements() ));
    }
    
    public static void main ( String[] args ) {
        try {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
        } catch ( ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e ) {
            e.printStackTrace();
        }
        
        frame = new JFrame( "NeuronTester" );
        frame.setContentPane( new NeuronTester().mainPanel );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.pack();
        frame.setVisible( true );
        frame.setResizable( false );
    }
    
    private void calculate () {
        Neuron neuron;
        try {
            TransferFunction func = new StepTransferFunction(
                    ( unipolarRadioButton.isSelected() ? StepTransferFunction.UNIPOLAR : StepTransferFunction.BIPOLAR ) |
                            ( upRadioButton.isSelected() ? StepTransferFunction.CEIL : StepTransferFunction.FLOOR )
            );
            neuron = new Neuron( new BigDecimal[] {
                    new BigDecimal( input1.getText() ),
                    new BigDecimal( input2.getText() ),
                    new BigDecimal( input0.getText() )
            }, func );
        } catch ( NumberFormatException exc ) {
            JOptionPane.showMessageDialog( frame, "Wrong number format", "Number error", JOptionPane.ERROR_MESSAGE );
            return;
        }
        
        visualizationPane.setNeuron( neuron );
    }
    
    private void startPointPicker () {
        String orgButtonText = pointPickerButton.getText();
        visualizationPane.startPointPicker(
                ( x, y ) -> {
                    learningSetCenter.setText(
                            String.format( "%s, %s",
                                    x.setScale( 2, BigDecimal.ROUND_HALF_UP ).toPlainString(),
                                    y.setScale( 2, BigDecimal.ROUND_HALF_UP ).toPlainString() ) );
                    pointPickerButton.setText( orgButtonText );
                    pointPickerButton.setEnabled( true );
                } );
        
        pointPickerButton.setText( "Pick a point" );
        pointPickerButton.setEnabled( false );
        
        frame.getContentPane().addMouseListener( new MouseAdapter() {
            @Override
            public void mouseClicked ( MouseEvent e ) {
                super.mouseClicked( e );
                if ( e.getSource() == visualizationPane ) return;
                visualizationPane.stopPointPicker();
                frame.getContentPane().removeMouseListener( this );
                pointPickerButton.setText( orgButtonText );
                pointPickerButton.setEnabled( true );
            }
        } );
    }
}
