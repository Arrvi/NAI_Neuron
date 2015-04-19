package pja.s11531.nai;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by Kris on 2015-03-29.
 */
@RunWith(Parameterized.class)
public class LearningSetFactoryTest {
    BigDecimal[] center;
    
    public LearningSetFactoryTest ( List<BigDecimal> center ) {
        this.center = center.toArray(new BigDecimal[center.size()]);
    }
    
    @Parameterized.Parameters
    public static Collection<Object[]> data () {
        return Arrays.asList( new Object[][]{ 
                { Arrays.asList( BigDecimal.ZERO, BigDecimal.ZERO ) },
                { Arrays.asList( BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO) },
                { Arrays.asList( BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO) },
                { Arrays.asList( BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO) } 
        } );
    }
    
    @Test
    public void testGetLearningSet () throws Exception {
        BigDecimal variance = BigDecimal.TEN;
        LearningElement[] elements = new LearningSetFactory( center,
                variance,
                100000,
                BigDecimal.ZERO,
                new Distribution.Uniform() ).getLearningSet();
    
        for ( LearningElement element : elements ) {
            System.out.println(element.toString());
            for ( BigDecimal coordinate : element.getInput() ) {
                assert(coordinate.compareTo( variance ) <= 0);
            }
        }
    }
}
