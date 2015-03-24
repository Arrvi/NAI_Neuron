package pja.s11531.nai;

/**
 * Created by Kris on 2015-03-24.
 */
public abstract class Distribution {
    public class Uniform implements DistributionFunction {
        @Override
        public double translate ( double x ) {
            return x;
        }
    }
    
    public class Linear implements DistributionFunction {
        public double translate ( double x ) {
            return x * x;
        }
    }
    
    public class Exponential implements DistributionFunction {
        @Override
        public double translate ( double x ) {
            return x * x * x;
        }
    }
    
    public class InvertedExponential implements DistributionFunction {
        @Override
        public double translate ( double x ) {
            return 1 - x * x * x;
        }
    }
}
