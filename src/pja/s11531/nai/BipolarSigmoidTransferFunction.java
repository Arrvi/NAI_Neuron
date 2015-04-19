package pja.s11531.nai;

import org.nevec.rjm.BigDecimalMath;

import java.math.BigDecimal;

/**
 * Created by Kris on 2015-04-19.
 */
public class BipolarSigmoidTransferFunction implements TransferFunction {
    private final BigDecimal alpha;
    
    public BipolarSigmoidTransferFunction ( BigDecimal alpha ) {
        this.alpha = alpha;
    }
    
    @Override
    public BigDecimal transfer ( BigDecimal x ) {
        return new BigDecimal( 2 ).divide( BigDecimal.ONE.add( BigDecimalMath.exp( alpha.multiply( x )
                                                                                        .negate() ) ), BigDecimal.ROUND_HALF_UP )
                                  .subtract( BigDecimal.ONE );
    }
    
    @Override
    public BigDecimal derivativeTransfer ( BigDecimal x ) {
        BigDecimal output = transfer( x );
        return BigDecimal.ONE.subtract( output.pow( 2 ) ).divide( new BigDecimal( 2 ), BigDecimal.ROUND_HALF_UP );
    }
}
