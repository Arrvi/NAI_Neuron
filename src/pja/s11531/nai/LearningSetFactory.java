package pja.s11531.nai;

import com.sun.deploy.util.StringUtils;

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
        LearningElement[] elements = new LearningElement[quantity];
        for ( int i = 0; i < quantity; i++ ) {
            BigDecimal radius = new BigDecimal( distribution.distribute( random.nextDouble() * variance.doubleValue() ) );
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
                                                    return x.add( center[xi] );
                                                } )
                                                .map( x -> x.multiply( radius ) )
                                                .collect( Collectors.toList() )
                                                .toArray( new BigDecimal[center.length] );
            elements[i] = new LearningElement( coordinates, memberClass );
        }
        return elements;
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
        return String.format( "Learning set factory: %d objects [%s] variance: %.2f",
                quantity,
                StringUtils.join( Arrays.asList( Arrays.stream( center )
                                                       .map( n -> n.setScale( 2, BigDecimal.ROUND_HALF_UP ) )
                                                       .toArray() ), ", " ),
                variance.doubleValue() );
    }
    
    public void setNewSeed () {
        random = new Random();
    }
    
    public void setNewSeed ( long seed ) {
        random = new Random( seed );
    }
}
