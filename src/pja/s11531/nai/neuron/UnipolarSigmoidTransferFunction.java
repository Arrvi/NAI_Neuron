package pja.s11531.nai.neuron;

import org.nevec.rjm.BigDecimalMath;

import java.math.BigDecimal;

/**
 * Created by Kris on 2015-04-19.
 */
public class UnipolarSigmoidTransferFunction implements TransferFunction {
    private final BigDecimal alpha;
    private static final int DEFAULT_SCALE = 20;
    
    public UnipolarSigmoidTransferFunction ( BigDecimal alpha ) {
        this.alpha = alpha;
    }
    
    @Override
    public BigDecimal transfer ( BigDecimal x ) {
        return BigDecimal.ONE.divide( BigDecimal.ONE.add( BigDecimalMath.exp( alpha.multiply( x )
                                                                                   .negate() ) ), DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP );
    }
    
    @Override
    public BigDecimal derivativeTransfer ( BigDecimal x ) {
        BigDecimal output = transfer( x );
        //noinspection BigDecimalMethodWithoutRoundingCalled
        return output.multiply(
                BigDecimal.ONE.setScale( DEFAULT_SCALE )
                              .subtract( output ) );
    }
}
