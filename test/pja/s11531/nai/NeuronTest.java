package pja.s11531.nai;

import org.junit.Test;
import pja.s11531.nai.neuron.Neuron;
import pja.s11531.nai.neuron.StepTransferFunction;

import java.math.BigDecimal;

public class NeuronTest {
    @Test
    public void testBasic() throws Exception {
        Neuron[] neurons = {
                new Neuron(new BigDecimal[]{BigDecimal.ONE, BigDecimal.ONE}, BigDecimal.ZERO, StepTransferFunction.CEIL_BIPOLAR)
        };
    }
}
