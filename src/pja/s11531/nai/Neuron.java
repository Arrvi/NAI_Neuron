package pja.s11531.nai;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by s11531 on 2015-03-16.
 */
public class Neuron {
    private final BigDecimal[]     weights;
    private final TransferFunction func;
    
    public Neuron ( BigDecimal[] weights, TransferFunction func ) {
        this.weights = weights;
        this.func = func;
    }
    
    public Neuron ( BigDecimal[] weights, BigDecimal bias, TransferFunction func ) {
        BigDecimal[] w = new BigDecimal[weights.length + 1];
        w[weights.length] = bias;
        this.weights = w;
        this.func = func;
    }
    
    public BigDecimal calculate ( BigDecimal[] inputs ) {
        if ( inputs.length != weights.length - 1 ) {
            throw new IllegalArgumentException( "Wrong number of inputs provided" );
        }
        
        BigDecimal net = BigDecimal.ZERO;
        
        for ( int i = 0; i < inputs.length; i++ ) {
            net = net.add( inputs[i].multiply( weights[i] ) );
        }
        net = net.add( getBias().multiply( BigDecimal.ONE.negate() ) );
        
        return func.transfer( net );
    }
    
    public Neuron learn ( BigDecimal[] learningSet, BigDecimal expectedValue, BigDecimal learningFactor ) {
        BigDecimal calculatedValue = calculate( learningSet );
        
        BigDecimal[] newWeights = new BigDecimal[weights.length];
        
        for ( int i = 0; i < weights.length; ++i ) {
            newWeights[i] = weights[i].add(
                    expectedValue.subtract( calculatedValue )
                                 .multiply( ( i < weights.length - 1 ) ? learningSet[i] : BigDecimal.ONE.negate() )
                                 .multiply( learningFactor ) );
        }
        
        return new Neuron( newWeights, func );
    }
    
    public BigDecimal getBias () {
        return weights[weights.length - 1];
    }
    
    public BigDecimal[] getWeights () {
        return weights;
    }
    
    public TransferFunction getFunc () {
        return func;
    }
    
    public BigDecimal difference (Neuron toCompare) {
        if ( toCompare.weights.length != weights.length ) {
            throw new IllegalArgumentException( "Cannot calculate difference between neurons with different number of inputs" );
        }
        
        BigDecimal result = BigDecimal.ZERO;
    
        for ( int i = 0; i < weights.length; i++ ) {
            result = result.add( weights[i].subtract( toCompare.weights[i] ).abs() );
        }
        
        return result;
    }
    
    @Override
    public String toString () {
        return String.format( "Neuron [%s], func=%s",
                Arrays.stream( weights )
                      .map( w -> w.setScale( 2, BigDecimal.ROUND_HALF_UP )
                                  .toPlainString() )
                      .collect( Collectors.joining( ", " ) ),
                func.toString() );
    }
}
