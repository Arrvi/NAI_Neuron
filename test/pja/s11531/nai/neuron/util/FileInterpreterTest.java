package pja.s11531.nai.neuron.util;

import org.junit.Before;
import org.junit.Test;
import pja.s11531.nai.neuron.SimpleNetwork;

public class FileInterpreterTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testGetContents() throws Exception {
//        File file = new File("sample-network.json");
//        
//        System.out.println("File exists: "+file.exists());
//        System.out.println("File is file: "+file.isFile());
//        System.out.println("File is readable: "+file.canRead());
        FileInterpreter fileInterpreter = new FileInterpreter(getClass().getClassLoader().getResourceAsStream("sample-network.json"));

        SimpleNetwork network = (SimpleNetwork) fileInterpreter.getContents();
        network.printNetwork();
    }
}
