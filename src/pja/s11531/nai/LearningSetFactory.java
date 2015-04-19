package pja.s11531.nai;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Kris on 2015-03-24.
 */
public class LearningSetFactory {
    private final BigDecimal[]         center;
    private final BigDecimal           variance;
    private final int                  quantity;
    private final BigDecimal           memberClass;
    private final DistributionFunction distribution;
    private       Random               random;
    private       LearningElement[]    learningSet;
    
    public LearningSetFactory ( BigDecimal[] center, BigDecimal variance, int quantity, BigDecimal memberClass,
                                DistributionFunction distribution )
    {
        this.center = center;
        this.variance = variance;
        this.quantity = quantity;
        this.memberClass = memberClass;
        this.distribution = distribution;
        
        setNewSeed();
    }
    
    public LearningElement[] getLearningSet () {
        if ( learningSet == null )
            generateLearningSet();
        
        return learningSet;
    }
    
    private void generateLearningSet () {
        learningSet = new LearningElement[quantity];
        for ( int i = 0; i < quantity; i++ ) {
            BigDecimal radius = new BigDecimal( distribution.distribute( random.nextDouble() ) * variance.doubleValue() );
            double[] angle = random.doubles( center.length, 0, Math.PI * 2 )
                                   .toArray();
            BigDecimal[] coordinates = IntStream.range( 0, center.length )
                                                .parallel()
                                                .mapToObj( xi -> {
                                                    BigDecimal x = IntStream.range( 0, xi )
                                                                            .parallel()
                                                                            .mapToObj( ri -> new BigDecimal( Math.sin( angle[ri] ) ) )
                                                                            .reduce( BigDecimal
                                                                                            .ONE,
                                                                                    BigDecimal::multiply );
                                                    if ( xi < center.length - 1 ) {
                                                        x = x.multiply( new BigDecimal( Math.cos( angle[xi] ) ) );
                                                    }
                                                    return x.multiply( radius )
                                                            .add( center[xi] );
                                                } )
                                                .collect( Collectors.toList() )
                                                .toArray( new BigDecimal[center.length] );
            learningSet[i] = new LearningElement( coordinates, new BigDecimal[]{memberClass} );
        }
    }
    
    public BigDecimal[] getCenter () {
        return center;
    }
    
    public BigDecimal getVariance () {
        return variance;
    }
    
    public int getQuantity () {
        return quantity;
    }
    
    public BigDecimal getMemberClass () {
        return memberClass;
    }
    
    @Override
    public String toString () {
        return String.format( "Learning set factory: %d objects [center at %s] variance: %.2f, class: %s",
                quantity,
                Arrays.stream( center )
                      .map( n -> n.setScale( 2, BigDecimal.ROUND_HALF_UP )
                                  .toPlainString() )
                      .collect( Collectors.joining( ", " ) ),
                variance.doubleValue(),
                memberClass.toPlainString() );
    }
    
    public void setNewSeed () {
        random = new Random();
        learningSet = null;
    }
    
    public void setNewSeed ( long seed ) {
        random = new Random( seed );
        learningSet = null;
    }
}
