package pja.s11531.nai;

import java.math.BigDecimal;

/**
 * Created by s11531 on 2015-03-16.
 */
public interface TransferFunction {
    BigDecimal transfer(BigDecimal x);
    BigDecimal derivativeTransfer(BigDecimal x);
}
