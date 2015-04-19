package pja.s11531.nai;

import java.math.BigDecimal;

/**
 * Created by Kris on 2015-04-19.
 */
public class SimpleNetwork implements BlackBox {
    private SimpleNetworkLayer[] layers;
    
    public SimpleNetwork ( SimpleNetworkLayer[] layers ) {
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
        return layers[layers.length-1].getOutputLength();
    }
    
    public void learn(LearningElement element, BigDecimal learningFactor) {
        
    }
}
