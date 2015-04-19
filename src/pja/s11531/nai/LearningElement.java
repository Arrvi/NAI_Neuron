package pja.s11531.nai;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by Kris on 2015-03-28.
 */
public class LearningElement {
    private final BigDecimal[] input;
    private final BigDecimal[] output;
    
    public LearningElement ( BigDecimal[] input, BigDecimal[] output ) {
        this.input = input;
        this.output = output;
    }
    
    public BigDecimal[] getInput () {
        return input;
    }
    
    public BigDecimal[] getOutput () {
        return output;
    }
    
    @Override
    public String toString () {
        return String.format( "[%s] -> %s",
                Arrays.stream( input )
                      .sequential()
                      .map( x -> x.setScale( 2, BigDecimal.ROUND_HALF_UP )
                                  .toPlainString() )
                      .collect(
                              Collectors.joining( ", " ) ),
                Arrays.stream( output )
                      .sequential()
                      .map( x -> x.setScale( 2, BigDecimal.ROUND_HALF_UP )
                                  .toPlainString() )
                      .collect(
                              Collectors.joining( ", " ) ) );
    }
}
