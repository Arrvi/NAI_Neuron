package pja.s11531.nai;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class NeuronTest {
    @Test
    public void testBasic() throws Exception {
        Neuron[] neurons = {
                new Neuron(new BigDecimal[]{BigDecimal.ONE, BigDecimal.ONE}, BigDecimal.ZERO, StepTransferFunction.CEIL_BIPOLAR)
        };
    }
}