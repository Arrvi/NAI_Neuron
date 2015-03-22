package pja.s11531.nai;

import javax.swing.*;
import java.math.BigDecimal;

/**
 * Created by s11531 on 2015-03-19.
 */
public class NeuronTester {
    private static JFrame frame;
    private JPanel mainPanel;
    private JPanel       visualizationPane;
    private JPanel       configPane;
    private JTextField   input1;
    private JTextField   input2;
    private JTextField   input0;
    private JRadioButton unipolarRadioButton;
    private JRadioButton bipolarRadioButton;
    private JComboBox    functionType;
    private JButton      calculateButton;
    private JRadioButton upRadioButton;
    private JRadioButton downRadioButton;
    
    public NeuronTester () {
        calculateButton.addActionListener( ( actionEvent ) -> calculate() );
    }
    
    public static void main ( String[] args ) {
        frame = new JFrame( "NeuronTester" );
        frame.setContentPane( new NeuronTester().mainPanel );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.pack();
        frame.setVisible( true );
        frame.setResizable( false );
    }
    
    private void createUIComponents () {
        visualizationPane = new VisualizationPanel();
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
        } catch (NumberFormatException exc) {
            JOptionPane.showMessageDialog( frame, "Wrong number format", "Number error", JOptionPane.ERROR_MESSAGE );
            return;
        }
    
        ( (VisualizationPanel) visualizationPane ).setNeuron( neuron );
    }
}
