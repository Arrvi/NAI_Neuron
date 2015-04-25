package pja.s11531.nai.neuron;

import java.math.BigDecimal;

/**
 * Created by Kris on 2015-04-19.
 */
public interface BlackBox {
    BigDecimal[] calculate(BigDecimal[] input);
    int getInputLength();
    int getOutputLength();
}
