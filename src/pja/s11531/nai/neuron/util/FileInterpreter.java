package pja.s11531.nai.neuron.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import pja.s11531.nai.neuron.*;

import java.io.*;
import java.math.BigDecimal;

/**
 * Created by Arrvi on 2015-05-01.
 */
public class FileInterpreter {
    private InputStream stream;

    public FileInterpreter(InputStream stream) {
        this.stream = stream;
    }

    public FileInterpreter(File file) throws FileNotFoundException {
        if (file.isFile() && !file.canRead()) {
            throw new IllegalArgumentException("File must be readable");
        }

        stream = new FileInputStream(file);
    }

    public BlackBox getContents() throws IOException, JSONException, ClassNotFoundException {
        JSONObject root = new JSONObject(new JSONTokener(new InputStreamReader(stream)));
        if (root.has("network")) {
            return getNetwork(root.getJSONObject("network"));
        }
        if (root.has("neuron")) {
            return getNeuron(root.getJSONObject("neuron"));
        }
        throw new RuntimeException("Invalid file structure");
    }

    private Neuron getNeuron(JSONObject object) throws JSONException, ClassNotFoundException {
        JSONArray jsonWeights = object.getJSONArray("weights");
        BigDecimal[] weights = new BigDecimal[jsonWeights.length()];
        for (int i = 0; i < jsonWeights.length(); i++) {
            weights[i] = new BigDecimal(jsonWeights.getString(i));
        }

        TransferFunction function;
        Class<? extends TransferFunction> funcClass;
        try {
            funcClass = (Class<? extends TransferFunction>) Class.forName(object.getString("function"));
        } catch (ClassCastException | ClassNotFoundException e) {
            try {
                funcClass = (Class<? extends TransferFunction>) Class.forName("pja.s11531.nai.neuron." + object.getString("function"));
            } catch (ClassCastException | ClassNotFoundException e1) {
                throw e1;
            }
        }
        try {
            return new Neuron(weights, funcClass.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private SimpleNetworkLayer getLayer(JSONObject object) throws JSONException, ClassNotFoundException {
        JSONArray jsonNeurons = object.getJSONArray("neurons");
        Neuron[] neurons = new Neuron[jsonNeurons.length()];
        for (int i = 0; i < jsonNeurons.length(); i++) {
            neurons[i] = getNeuron(jsonNeurons.getJSONObject(i));
        }
        return new SimpleNetworkLayer(neurons);
    }

    private SimpleNetwork getNetwork(JSONObject object) throws JSONException, ClassNotFoundException {
        JSONArray jsonLayers = object.getJSONArray("layers");
        SimpleNetworkLayer[] layers = new SimpleNetworkLayer[jsonLayers.length()];
        for (int i = 0; i < jsonLayers.length(); i++) {
            layers[i] = getLayer(jsonLayers.optJSONObject(i));
        }
        return new SimpleNetwork(layers);
    }
}
