package pja.s11531.nai;

import java.math.BigDecimal;

/**
 * Created by s11531 on 2015-03-16.
 */
public class StepTransferFunction implements TransferFunction {
    public final static int UNIPOLAR = 1;
    public final static int BIPOLAR  = 2;
    public final static int CEIL     = 4;
    public final static int FLOOR    = 8;
    
    public final static StepTransferFunction CEIL_UNIPOLAR  = new StepTransferFunction( CEIL | UNIPOLAR );
    public final static StepTransferFunction CEIL_BIPOLAR   = new StepTransferFunction( CEIL | BIPOLAR );
    public final static StepTransferFunction FLOOR_UNIPOLAR = new StepTransferFunction( FLOOR | UNIPOLAR );
    public final static StepTransferFunction FLOOR_BIPOLAR  = new StepTransferFunction( FLOOR | BIPOLAR );
    
    private final int funcType;
    
    public StepTransferFunction ( int funcType ) {
        if ( ( funcType & UNIPOLAR ) != 0 && ( funcType & BIPOLAR ) != 0 ) {
            throw new IllegalArgumentException( "Step transfer function cannot be unipolar and bipolar at the same time." );
        }
        if ( ( funcType & CEIL ) != 0 && ( funcType & FLOOR ) != 0 ) {
            throw new IllegalArgumentException( "Step transfer function cannot be ceil anf floor at the same type." );
        }
        if ( ( funcType & BIPOLAR ) == 0 ) funcType |= UNIPOLAR;
        if ( ( funcType & FLOOR ) == 0 ) funcType |= CEIL;
        
        this.funcType = funcType;
    }
    
    @Override
    public BigDecimal transfer ( BigDecimal x ) {
        final BigDecimal LOW, HIGH = BigDecimal.ONE;
        if ( isUnipolar() ) {
            LOW = BigDecimal.ZERO;
        }
        else {
            LOW = BigDecimal.ONE.negate();
        }
        
        if ( isCeil() ) {
            return ( x.compareTo( BigDecimal.ZERO ) >= 0 ) ? HIGH : LOW;
        }
        else {
            return ( x.compareTo( BigDecimal.ZERO ) > 0 ) ? HIGH : LOW;
        }
    }
    
    public boolean isCeil () {
        return ( funcType & CEIL ) != 0;
    }
    
    public boolean isFloor () {
        return !isCeil();
    }
    
    public boolean isUnipolar () {
        return ( funcType & UNIPOLAR ) != 0;
    }
    
    public boolean isBipolar () {
        return !isUnipolar();
    }
    
    @Override
    public String toString () {
        return String.format( "StepTransferFunction: %s %s", isBipolar() ? "bipolar" : "unipolar", isCeil() ? "ceil" : "floor" );
    }
}
