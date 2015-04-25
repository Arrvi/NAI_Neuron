package pja.s11531.nai.neuron;

/**
 * Created by Kris on 2015-03-24.
 */
public abstract class Distribution {
    public static class Uniform implements DistributionFunction {
        @Override
        public double distribute ( double x ) {
            return Math.sqrt( x );
        }
    }
    
    public static class Linear implements DistributionFunction {
        public double distribute ( double x ) {
            return x;
        }
    }
    
    public static class Exponential implements DistributionFunction {
        @Override
        public double distribute ( double x ) {
            return x * x;
        }
    }
    
    public static class InvertedExponential implements DistributionFunction {
        @Override
        public double distribute ( double x ) {
            return 1 - x * x;
        }
    }
}
