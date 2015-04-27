package pja.s11531.nai.neuron;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Kris on 2015-04-26.
 */
public class SimpleNetworkTest {
    
    @Before
    public void setUp () throws Exception {
        
    }
    
    @Test
    public void testExampleNetwork () throws Exception {
        BipolarSigmoidTransferFunction bipolar = new BipolarSigmoidTransferFunction( BigDecimal.ONE );
        UnipolarSigmoidTransferFunction unipolar = new UnipolarSigmoidTransferFunction( BigDecimal.ONE );
        SimpleNetwork network = new SimpleNetwork(
                new SimpleNetworkLayer[] {
                        new SimpleNetworkLayer(
                                new Neuron[] {
                                        Neuron.getInstance( new double[] { 1, 0, -3 }, bipolar ),
                                        Neuron.getInstance( new double[] { -3, 2, -8 }, bipolar ),
                                        Neuron.getInstance( new double[] { 4, -3, 2 }, bipolar ),
                                        Neuron.getInstance( new double[] { 7, -2, -1 }, bipolar ),
                                } ),
                        new SimpleNetworkLayer(
                                new Neuron[] {
                                        Neuron.getInstance( new double[] { 2, 4, 1, 3, 0 }, unipolar ),
                                        Neuron.getInstance( new double[] { 3, -5, 3, 2, 0 }, unipolar ),
                                        Neuron.getInstance( new double[] { 4, 3, -4, -3, 0 }, unipolar ),
                                } )
                } );
        
        
        LearningElement learningElement = new LearningElement(
                new BigDecimal[] {
                        new BigDecimal( -3 ),
                        new BigDecimal( 4 )
                }, new BigDecimal[] {
                BigDecimal.ONE,
                BigDecimal.ZERO,
                BigDecimal.ONE
        } );
        System.out.println( "OUTPUT: " + Arrays.toString( network.calculate( learningElement.getInput() ) ) );
        
        System.out.println( "Initial network:" );
        network.printNetwork();
        
        BigDecimal learningFactor = new BigDecimal( 1 ).setScale( 10, BigDecimal.ROUND_HALF_UP );
        for ( int epoch = 1; epoch <= 10; epoch++ ) {
            System.out.printf( "\n\n\nEpoch %d:%n", epoch );
            
            network.learn( learningElement, learningFactor );
            network.printNetwork();
            System.out.println( "OUTPUT: " + Arrays.toString( network.calculate( learningElement.getInput() ) ) );
        }
    }

    @Test
    public void testIrisDatabase() throws Exception {
        File db = new File("iris.data.txt");
        if ( !db.canRead() ) throw new FileNotFoundException();
        BufferedReader reader = new BufferedReader(new FileReader(db));

        List<LearningElement> learningSet = new ArrayList<>();
        String[] classes = {"Iris-setosa", "Iris-versicolor", "Iris-virginica"};

        reader.lines().map(line -> {
            // TODO complete iris test
            return line;
        });
    }
}
