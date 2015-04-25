package pja.s11531.nai.neuron;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Kris on 2015-03-29.
 */
public class Teacher implements Callable<Neuron> {
    private final Neuron               neuron;
    private final LearningSetFactory[] factories;
    private final BigDecimal           learningFactor;
    
    public Teacher ( Neuron neuron, LearningSetFactory[] factories, BigDecimal learningFactor ) {
        this.neuron = neuron;
        this.factories = factories;
        this.learningFactor = learningFactor;
    }
    
    @Override
    public Neuron call () throws Exception {
        Neuron currentState = neuron;
        
        List<LearningElement> elements = new LinkedList<>();
        
        for ( LearningSetFactory factory : factories ) {
            Collections.addAll( elements, factory.getLearningSet() );
        }
        
        Collections.shuffle( elements );
        
        for ( LearningElement element : elements ) {
            BigDecimal error = element.getOutput()[0]
                                      .subtract( currentState.calculate( element.getInput() )[0] );
            currentState = currentState.learn( element.getInput(), error, learningFactor );
        }
        
        return currentState;
    }
    
    public int getError ( Neuron neuron ) {
        int error = 0;
        for ( LearningSetFactory factory : factories )
            for ( LearningElement element : factory.getLearningSet() ) {
                BigDecimal value = neuron.calculate( element.getInput() )[0];
                if ( value.compareTo( element.getOutput()[0] ) != 0 ) error++;
            }
        return error;
    }
}
