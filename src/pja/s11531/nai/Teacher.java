package pja.s11531.nai;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Created by Kris on 2015-03-29.
 */
public class Teacher implements Callable<Neuron> {
    private final Neuron neuron;
    private final LearningSetFactory factory;
    private final BigDecimal learningFactor;
    
    public Teacher ( Neuron neuron, LearningSetFactory factory, BigDecimal learningFactor ) {
        this.neuron = neuron;
        this.factory = factory;
        this.learningFactor = learningFactor;
    }
    
    @Override
    public Neuron call () throws Exception {
        Neuron currentState = neuron;
        
        for ( LearningElement element : factory.getLearningSet() ) {
            currentState = currentState.learn( element.getArguments(), element.getValue(), learningFactor );
        }
        
        return currentState;
    }
}
