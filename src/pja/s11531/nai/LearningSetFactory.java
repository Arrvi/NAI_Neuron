package pja.s11531.nai;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by Kris on 2015-03-24.
 */
public class LearningSetFactory {
    private final BigDecimal x;
    private final BigDecimal y;
    private final BigDecimal variance;
    private final BigInteger quantity;
    private final BigDecimal memberClass;
    
    public LearningSetFactory ( BigDecimal x, BigDecimal y, BigDecimal variance, BigInteger quantity, BigDecimal memberClass ) {
        this.x = x;
        this.y = y;
        this.variance = variance;
        this.quantity = quantity;
        this.memberClass = memberClass;
    }
    
    public BigDecimal[] getLearningSet () {
        return null; // TODO Learning set generator
    }
    
    public BigDecimal getX () {
        return x;
    }
    
    public BigDecimal getY () {
        return y;
    }
    
    public BigDecimal getVariance () {
        return variance;
    }
    
    public BigInteger getQuantity () {
        return quantity;
    }
    
    public BigDecimal getMemberClass () {
        return memberClass;
    }
    
    @Override
    public String toString () {
        return String.format( "Learning set factory: %d objects [%.2f, %.2f] variance: %.2f", 
                quantity.intValue(), 
                x.doubleValue(), 
                y.doubleValue(),
                variance.doubleValue());
    }
}
