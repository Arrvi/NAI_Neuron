package pja.s11531.nai.neuron.gui;

import pja.s11531.nai.neuron.Neuron;

import javax.swing.*;
import java.math.BigDecimal;

/**
 * Created by Arrvi on 2015-05-01.
 */
public class NeuronComponent extends JPanel {
    Neuron neuron;
    JLabel[] weights;

    public NeuronComponent(Neuron neuron) {
        super(null);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.neuron = neuron;

        weights = new JLabel[neuron.getInputLength()];
        for (int i = 0; i < neuron.getWeights().length; i++) {
            weights[i] = new JLabel();
            add(weights[i]);
        }
    }
    
    public void setNeuron(Neuron neuron) {
        if ( neuron.getWeights().length != weights.length )
            throw new IllegalArgumentException("Cannot set neuron with different input length");
        this.neuron = neuron;
        setupWeights();
    }
    
    private void setupWeights() {
        for (int i=0; i<weights.length; i++) {
            weights[i].setText(neuron.getWeights()[i].setScale(5, BigDecimal.ROUND_HALF_UP).toPlainString());
        }
    }
}
