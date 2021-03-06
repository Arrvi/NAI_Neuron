package pja.s11531.nai.neuron.gui;

import pja.s11531.nai.neuron.*;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

import static pja.s11531.nai.neuron.gui.VisualizationPanel.DrawOption.*;

/**
 * Created by s11531 on 2015-03-19.
 */
public class NeuronTester {
    private JFrame frame;
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
    private JSlider                   learningFactorSlider;
    private JTextField                learningEpochs;
    private JButton                   learnButton;
    private JList<LearningSetFactory> learningSetsList;
    private JComboBox                 distributionFunction;
    private JButton                   clearButton;
    private JComboBox                 learningSetMemberClassCombo;
    private JTextField                learningSetMemberClass;
    private JCheckBox                 divideFactor;
    private Neuron                    neuron;
    
    public NeuronTester () {
        calculateButton.addActionListener(
                ( evt ) -> {
                    calculate();
                    learnButton.setEnabled( true );
                } );
        clearButton.addActionListener(
                ( evt ) -> {
                    neuron = null;
                    visualizationPane.setNeuron( null );
                    learnButton.setEnabled( false );
                } );
        pointPickerButton.addActionListener( ( evt ) -> startPointPicker() );
        addLearningSet.addActionListener(
                ( evt ) -> {
                    try {
                        parseAndAddLearningSet();
                    } catch ( NumberFormatException e ) {
                        JOptionPane.showMessageDialog( frame, "Invalid number format", "Error", JOptionPane.ERROR_MESSAGE );
                    } catch ( Exception e ) {
                        JOptionPane.showMessageDialog( frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
                    }
                } );
        learningSetsList.setModel( listModel );
        learningSetMemberClassCombo.addActionListener( ( evt ) -> setMemberClass() );
        learningSetsList.addMouseListener(
                new MouseAdapter() {
                    JPopupMenu menu = new JPopupMenu();
                    
                    {
                        JMenuItem item;
                        item = new JMenuItem( "Randomize" );
                        item.addActionListener( ( evt ) -> randomize() );
                        menu.add( item );
                        item = new JMenuItem( "Delete" );
                        item.addActionListener( ( evt ) -> deleteItem() );
                        menu.add( item );
                    }
                    
                    private void deleteItem () {
                        listModel.removeElementAt( learningSetsList.getSelectedIndex() );
                        updateLearningSets();
                    }
                    
                    private void randomize () {
                        learningSetsList.getSelectedValue()
                                        .setNewSeed();
                        updateLearningSets();
                    }
                    
                    @Override
                    public void mousePressed ( MouseEvent e ) {
                popupCheck( e );
            }
            
            @Override
            public void mouseReleased ( MouseEvent e ) {
                popupCheck( e );
            }
            
            private void popupCheck ( MouseEvent e ) {
                if ( !e.isPopupTrigger() ) {
                    return;
                }
                JList list = (JList) e.getSource();
                list.setSelectedIndex( list.locationToIndex( e.getPoint() ) );
                menu.show( list, e.getX(), e.getY() );
            }
        } );
        learnButton.addActionListener( ( evt ) -> learn() );
        
        visualizationPane.setDrawOptions( DRAW_GRID, DRAW_POINT_GRID, DRAW_LEARNING_SET_POINTS, DRAW_LEARNING_SET_CIRCLE, DRAW_LEARNING_SET_RADIUS );
    }
    
    private void learn () {
        if ( neuron == null ) return;
        
        int epochs = Integer.parseInt( learningEpochs.getText() );
        BigDecimal learningFactor = new BigDecimal( learningFactorSlider.getValue() / 1000.0 );
        if (learningFactor.compareTo(BigDecimal.ONE) < 0 ) learningFactor = new BigDecimal(0.001);
        if ( divideFactor.isSelected() ) 
            learningFactor = learningFactor.divide(new BigDecimal(1000), BigDecimal.ROUND_HALF_UP);
        
        ArrayList<LearningSetFactory> factories = Collections.list( listModel.elements() );
        
        LearningDialog dialog = new LearningDialog( this, factories, neuron, epochs, learningFactor );
        dialog.setVisible( true );
    }
    
    private void parseAndAddLearningSet () throws Exception {
        if ( learningSetCenter.getText()
                              .length() == 0 )
        {
            throw new Exception( "Center point has to be specified" );
        }
        String[] coords = learningSetCenter.getText()
                                           .split( ",\\s*" );
        if ( coords.length != 2 ) {
            throw new Exception( "Wrong point format. There should be 2 numbers separated by comma" );
        }
        BigDecimal x;
        BigDecimal y;
        BigDecimal variance;
        int quantity;
        BigDecimal memberClass;
        
        x = new BigDecimal( coords[0] );
        y = new BigDecimal( coords[1] );
        
        if ( learningSetVariance.getText()
                                .length() == 0 )
        {
            learningSetVariance.setText( "0" );
        }
        variance = new BigDecimal( learningSetVariance.getText() );
        
        if ( learningSetQuantity.getText()
                                .length() == 0 )
        {
            learningSetQuantity.setText( "0" );
        }
        quantity = Integer.parseInt( learningSetQuantity.getText() );
        
        if ( learningSetMemberClass.getText()
                                   .length() == 0 )
        {
            setMemberClass();
        }
        
        memberClass = new BigDecimal( learningSetMemberClass.getText() );
        
        @SuppressWarnings("unchecked")
        Class<? extends DistributionFunction>[] funcs = new Class[] {
                Distribution.Linear.class,
                Distribution.Uniform.class,
                Distribution.Exponential.class,
                Distribution.InvertedExponential.class
        };
        
        DistributionFunction distribution = funcs[distributionFunction.getSelectedIndex()].newInstance();
        
        addLearningSet( x, y, variance, quantity, memberClass, distribution );
        learningSetCenter.setText( "" );
    }
    
    private void setMemberClass () {
        String mClass;
        if ( learningSetMemberClassCombo.getModel()
                                        .getSelectedItem()
                                        .equals( "HIGH" ) ) mClass = "1";
        else {
            if ( unipolarRadioButton.isSelected() ) mClass = "0";
            else mClass = "-1";
        }
        learningSetMemberClass.setText( mClass );
    }
    
    private void addLearningSet ( BigDecimal x, BigDecimal y, BigDecimal variance, int quantity, BigDecimal memberClass,
                                  DistributionFunction distribution )
    {
        LearningSetFactory factory = new LearningSetFactory( new BigDecimal[] { x, y }, variance, quantity, memberClass, distribution );
        listModel.addElement( factory );
        
        updateLearningSets();
    }
    
    private void updateLearningSets () {
        visualizationPane.setLearningSets( Collections.list( listModel.elements() ) );
    }
    
    private void calculate () {
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
                                    x.setScale( 2, BigDecimal.ROUND_HALF_UP )
                                     .toPlainString(),
                                    y.setScale( 2, BigDecimal.ROUND_HALF_UP )
                                     .toPlainString() ) );
                    pointPickerButton.setText( orgButtonText );
                    pointPickerButton.setEnabled( true );
                } );
        
        pointPickerButton.setText( "Pick a point" );
        pointPickerButton.setEnabled( false );
        
        frame.addMouseListener( new MouseAdapter() {
            @Override
            public void mouseClicked ( MouseEvent e ) {
                super.mouseClicked( e );
                if ( e.getSource() == visualizationPane ) return;
                visualizationPane.stopPointPicker();
                frame.getContentPane()
                     .removeMouseListener( this );
                pointPickerButton.setText( orgButtonText );
                pointPickerButton.setEnabled( true );
            }
        } );
    }
    
    public JPanel getMainPanel () {
        return mainPanel;
    }
    
    public void setFrame ( JFrame frame ) {
        this.frame = frame;
    }

    public void setDrawOption(VisualizationPanel.DrawOption option, boolean value) {
        visualizationPane.setDrawOption(option, value);
    }

    public void loadNeuron(Neuron neuron) {
        if ( neuron.getWeights().length != 3 ) throw new IllegalArgumentException("Only 2D neurons!");

        input1.setText(neuron.getWeights()[0].setScale(5, BigDecimal.ROUND_HALF_UP).toPlainString());
        input2.setText(neuron.getWeights()[1].setScale(5, BigDecimal.ROUND_HALF_UP).toPlainString());
        input0.setText(neuron.getWeights()[2].setScale(5, BigDecimal.ROUND_HALF_UP).toPlainString());
        calculate();
    }
}
