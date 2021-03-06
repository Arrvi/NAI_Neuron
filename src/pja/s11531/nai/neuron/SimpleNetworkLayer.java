package pja.s11531.nai.neuron;

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
    public int getInputLength () {
        return neurons[0].getInputLength();
    }
    
    @Override
    public int getOutputLength () {
        return neurons.length;
    }
    
    public void learn ( BigDecimal[] input, BigDecimal[] error, BigDecimal learningFactor ) {
        if (neurons.length != error.length ) {
            throw new IllegalArgumentException("Wrong error length");
        }
        for (int i = 0; i < neurons.length; i++) {
            neurons[i] = neurons[i].learn(input, error[i], learningFactor);
        }
    }
    
    public BigDecimal[] calculateError ( BigDecimal[] input, BigDecimal[] nextLayerErrors ) {
        if ( getInputLength() != input.length ) {
            throw new IllegalArgumentException( String.format( "Wrong input length %d != %d", getInputLength(), input.length ) );
        }
        if ( neurons.length != nextLayerErrors.length ) {
            throw new IllegalArgumentException( "Wrong error length" );
        }
        
        BigDecimal[] errors = new BigDecimal[neurons.length];
        for ( int i = 0; i < neurons.length; i++ ) {
            errors[i] = neurons[i].getFunc()
                                  .derivativeTransfer( neurons[i].calculateNET( input ) )
                                  .multiply( nextLayerErrors[i] );
        }
        BigDecimal[] layerErrors = new BigDecimal[getInputLength()];
    
        for ( int i = 0; i < layerErrors.length; i++ ) {
            layerErrors[i] = BigDecimal.ZERO;
            for ( int n=0; n< neurons.length; n++ ) {
                layerErrors[i] = layerErrors[i].add( errors[n].multiply( neurons[n].getWeights()[i] ) );
            }
        }
        
        return layerErrors;
    }
    
    public void printLayer () {
        System.out.println("\tNeural network layer");
        for ( int i = 0; i < neurons.length; i++ ) {
            System.out.printf( "\tNeuron %d:%n\t\t", i );
            System.out.println(neurons[i].toString());
        }
    }
}
