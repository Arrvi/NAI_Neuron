package pja.s11531.nai.neuron;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by s11531 on 2015-03-16.
 */
public class Neuron implements BlackBox {
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
    
    @Override
    public BigDecimal[] calculate ( BigDecimal[] inputs ) {
        BigDecimal net = calculateNET( inputs );
        
        return new BigDecimal[] { func.transfer( net ) };
    }
    
    protected BigDecimal calculateNET ( BigDecimal[] inputs ) {
        if ( inputs.length != weights.length - 1 ) {
            throw new IllegalArgumentException( "Wrong number of inputs provided" );
        }
        
        BigDecimal net = BigDecimal.ZERO;
        
        for ( int i = 0; i < inputs.length; i++ ) {
            net = net.add( inputs[i].multiply( weights[i] ) );
        }
        net = net.add( getBias().multiply( BigDecimal.ONE.negate() ) );
        return net;
    }
    
    public Neuron learn ( BigDecimal[] input, BigDecimal error, BigDecimal learningFactor ) {
        BigDecimal[] newWeights = new BigDecimal[weights.length];
        
        for ( int i = 0; i < weights.length; ++i ) {
            newWeights[i] = weights[i].add(
                    error
                            .multiply( ( i < weights.length - 1 ) ? input[i] : BigDecimal.ONE.negate() )
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
    
    @Override
    public int getInputLength() {
        return weights.length-1;
    }
    
    @Override
    public int getOutputLength () {
        return 1;
    }
    
    public BigDecimal difference ( Neuron toCompare ) {
        if ( toCompare.weights.length != weights.length ) {
            throw new IllegalArgumentException( "Cannot calculate difference between neurons with different number of inputs" );
        }
        
        BigDecimal result = BigDecimal.ZERO;
        
        for ( int i = 0; i < weights.length; i++ ) {
            result = result.add( weights[i].subtract( toCompare.weights[i] )
                                           .abs() );
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
