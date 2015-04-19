package pja.s11531.nai;

import org.nevec.rjm.BigDecimalMath;

import java.math.BigDecimal;

/**
 * Created by Kris on 2015-04-19.
 */
public class UnipolarSigmoidTransferFunction implements TransferFunction {
    private final BigDecimal alpha;
    
    public UnipolarSigmoidTransferFunction ( BigDecimal alpha ) {
        this.alpha = alpha;
    }
    
    @Override
    public BigDecimal transfer ( BigDecimal x ) {
        return BigDecimal.ONE.divide( BigDecimal.ONE.add( BigDecimalMath.exp( alpha.multiply( x ).negate() ) ), BigDecimal.ROUND_HALF_UP );
    }
    
    @Override
    public BigDecimal derivativeTransfer ( BigDecimal x ) {
        BigDecimal output = transfer( x );
        return output.multiply( BigDecimal.ONE.subtract( output ) );
    }
}
