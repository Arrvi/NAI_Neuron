package pja.s11531.nai;

import java.math.BigDecimal;

/**
 * Created by Kris on 2015-04-19.
 */
public class SimpleNetworkLayer implements BlackBox {
    private Neuron[] neurons;
    
    public SimpleNetworkLayer ( Neuron[] neurons ) {
        for ( int i = 0; i < neurons.length - 1; i++ ) {
            if ( neurons[i].getInputLength() != neurons[i + 1].getInputLength() ) 
                throw new IllegalArgumentException( "All neurons in layer must have same input length" );
        }
        this.neurons = neurons;
    }
    
    @Override
    public BigDecimal[] calculate ( BigDecimal[] input ) {
        BigDecimal[] result = new BigDecimal[neurons.length];
        for ( int i = 0; i < neurons.length; i++ ) {
            result[i] = neurons[i].calculate( input )[0];
        }
        return result;
    }
    
    @Override
    public int getInputLength() {
        return neurons[0].getInputLength();
    }
    
    @Override
    public int getOutputLength () {
        return neurons.length;
    }
    
    public void learn(BigDecimal[] input, BigDecimal[] error, BigDecimal learningFactor) {
        
    }
    
    public BigDecimal[] calculateError(BigDecimal[] input, BigDecimal[] error) {
        return null;
    }
}
