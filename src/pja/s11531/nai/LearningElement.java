package pja.s11531.nai;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by Kris on 2015-03-28.
 */
public class LearningElement {
    private final BigDecimal[] arguments;
    private final BigDecimal   value;
    
    public LearningElement ( BigDecimal[] arguments, BigDecimal value ) {
        this.arguments = arguments;
        this.value = value;
    }
    
    public BigDecimal[] getArguments () {
        return arguments;
    }
    
    public BigDecimal getValue () {
        return value;
    }
    
    @Override
    public String toString () {
        return String.format( "[%s] -> %s",
                Arrays.stream( arguments )
                      .sequential()
                      .map( x -> x.setScale( 2, BigDecimal.ROUND_HALF_UP )
                                  .toPlainString() )
                      .collect(
                              Collectors.joining( ", " ) ),
                value.toPlainString() );
    }
}
