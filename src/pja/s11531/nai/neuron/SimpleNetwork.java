package pja.s11531.nai.neuron;

import java.math.BigDecimal;

/**
 * Created by Kris on 2015-04-19.
 */
public class SimpleNetwork implements BlackBox {
    private SimpleNetworkLayer[] layers;
    
    public SimpleNetwork ( SimpleNetworkLayer[] layers ) {
        for ( int i = 0; i < layers.length - 1; i++ ) {
            if ( layers[i].getOutputLength() != layers[i + 1].getInputLength() )
                throw new IllegalArgumentException( String.format( "Input and output mismatch between layers %d and %d", i, i + 1 ) );
        }
        this.layers = layers;
    }
    
    @Override
    public BigDecimal[] calculate ( BigDecimal[] input ) {
        BigDecimal[] result = input;
        for ( SimpleNetworkLayer layer : layers ) {
            result = layer.calculate( result );
        }
        return result;
    }
    
    @Override
    public int getInputLength () {
        return layers[0].getInputLength();
    }
    
    @Override
    public int getOutputLength () {
        return layers[layers.length - 1].getOutputLength();
    }
    
    public void learn ( LearningElement element, BigDecimal learningFactor ) {
        BigDecimal[][] outputs = new BigDecimal[layers.length][];
        outputs[0] = layers[0].calculate( element.getInput() );
        for ( int i = 1; i < layers.length; i++ ) {
            outputs[i] = layers[i].calculate( outputs[i - 1] );
        }
        
        BigDecimal[][] errors = new BigDecimal[layers.length+1][];
        
        errors[layers.length] = calculateFirstError( element.getOutput(), outputs[outputs.length-1] );
        
//        errors[layers.length - 1] = layers[layers.length - 1].calculateError(
//                outputs[outputs.length - 2], 
//                calculateFirstError(
//                        element.getOutput(), outputs[outputs.length - 1] ) 
//        );
        
        for ( int i = layers.length - 1; i >= 0; i-- ) {
            errors[i] = layers[i].calculateError( (i==0)?element.getInput():outputs[i-1], errors[i+1] );
        }
        
        layers[0].learn( element.getInput(), errors[1], learningFactor );
        for ( int i = 1; i < layers.length; i++ ) {
            layers[i].learn( outputs[i - 1], errors[i+1], learningFactor );
        }
    }
    
    private BigDecimal[] calculateFirstError ( BigDecimal[] expectedOutput, BigDecimal[] calculatedOutput ) {
        for ( int i = 0; i < expectedOutput.length; i++ ) {
            expectedOutput[i] = expectedOutput[i].subtract( calculatedOutput[i] );
        }
        return expectedOutput;
    }
    
    public void printNetwork () {
        System.out.println("Neural network");
        for ( int i = 0; i < layers.length; i++ ) {
            System.out.printf( "Layer: %d:%n", i );
            layers[i].printLayer();
        }
    }
}
