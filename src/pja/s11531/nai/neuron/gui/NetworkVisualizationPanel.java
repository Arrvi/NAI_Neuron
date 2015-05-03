package pja.s11531.nai.neuron.gui;

import pja.s11531.nai.neuron.LearningElement;
import pja.s11531.nai.neuron.LearningSetFactory;
import pja.s11531.nai.neuron.SimpleNetwork;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static pja.s11531.nai.neuron.gui.NetworkVisualizationPanel.DrawOption.*;

/**
 * Created by Kris on 2015-03-20.
 */
public class NetworkVisualizationPanel extends JComponent {
    protected static final BigDecimal TWO = new BigDecimal(2);
    private int markSize = 2;
    private MouseAdapter mouseAdapter;
    private List<LearningSetFactory> learningSets;
    private SimpleNetwork network;

    private int defaultScale = 10;
    private BigDecimal range = BigDecimal.TEN.setScale(defaultScale, BigDecimal.ROUND_HALF_UP);
    private final BigDecimal doubleRange = range.multiply(TWO);
    private int outputResolution = 100;
    private double maxOpacity = 0.9;

    private EnumSet<DrawOption> drawOptions = EnumSet.of(DrawOption.DRAW_AXIS,
            DrawOption.DRAW_AXIS_TICKS,
            DrawOption.DRAW_UNITY_LABELS,
            DrawOption.ANTI_ALIASING);

    public enum DrawOption {
        DRAW_AXIS,
        DRAW_AXIS_TICKS,
        DRAW_UNITY_LABELS,
        DRAW_NEGATIVE_UNITY_LABELS,
        DRAW_ALL_LABELS,
        DRAW_GRID,
        DRAW_POINT_GRID,

        DRAW_NEURON_VALUES_GRID,

        DRAW_LEARNING_SET_RADIUS,
        DRAW_LEARNING_SET_CIRCLE,
        DRAW_LEARNING_SET_POINTS,
        DRAW_LARGER_LEARNING_SET_POINTS,

        FADE_LEARNING_HISTORY,

        ANTI_ALIASING;

        public long getOptionValue() {
            return 1 << this.ordinal();
        }
    }

    public EnumSet<DrawOption> getDrawOptions(long optionsValue) {
        EnumSet<DrawOption> drawOptions = EnumSet.noneOf(DrawOption.class);
        for (DrawOption drawOption : DrawOption.values()) {
            long flagValue = drawOption.getOptionValue();
            if ((flagValue & optionsValue) == flagValue) {
                drawOptions.add(drawOption);
            }
        }

        return drawOptions;
    }

    public void setDrawOption(DrawOption option, boolean value) {
        if (value)
            drawOptions.add(option);
        else
            drawOptions.remove(option);

        repaint();
    }

    public void setDrawOptions(DrawOption... options) {
        for (DrawOption option : options) {
            setDrawOption(option, true);
        }
    }

    private boolean drawAnyOf(DrawOption... options) {
        if (options.length == 0) throw new IllegalArgumentException("No options specified. At least one is required.");
        for (DrawOption option : options) {
            if (option(option)) return true;
        }
        return false;
    }

    private boolean drawAllOf(DrawOption... options) {
        if (options.length == 0) throw new IllegalArgumentException("No options specified. At least one is required.");
        for (DrawOption option : options) {
            if (!option(option)) return false;
        }
        return true;
    }

    private boolean option(DrawOption option) {
        return drawOptions.contains(option);
    }

    public NetworkVisualizationPanel() {
        super();
        setMinimumSize(new Dimension(400, 400));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = ((Graphics2D) g);
        if (option(ANTI_ALIASING))
            g2d.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);


        if (network != null) {
            drawNetworkClasses(g2d);
        }

        if (option(DRAW_AXIS)) {
            g2d.drawLine(unitsToWidth(BigDecimal.ZERO), unitsToHeight(range.negate()), unitsToWidth(BigDecimal.ZERO), unitsToHeight(range));
            g2d.drawLine(unitsToWidth(range.negate()), unitsToHeight(BigDecimal.ZERO), unitsToWidth(range), unitsToHeight(BigDecimal.ZERO));
        }


        for (int i = 1; i < 10; ++i) {
            BigDecimal bdi = new BigDecimal(i), bdni = bdi.negate();
            int x = unitsToWidth(bdi);
            int nx = unitsToWidth(bdni);
            int h0 = unitsToHeight(BigDecimal.ZERO);
            int y = unitsToHeight(bdi);
            int ny = unitsToHeight(bdni);
            int w0 = unitsToWidth(BigDecimal.ZERO);

            if (option(DRAW_AXIS_TICKS)) {
                g2d.drawLine(
                        x, h0 - markSize,
                        x, h0 + markSize);
                g2d.drawLine(
                        nx, h0 - markSize,
                        nx, h0 + markSize);
                g2d.drawLine(
                        w0 - markSize, y,
                        w0 + markSize, y);
                g2d.drawLine(
                        w0 - markSize, ny,
                        w0 + markSize, ny);
            }

            if (option(DRAW_ALL_LABELS) || i == 1 && drawAnyOf(DRAW_UNITY_LABELS, DRAW_NEGATIVE_UNITY_LABELS)) {
                if (drawAnyOf(DRAW_ALL_LABELS, DRAW_UNITY_LABELS)) {
                    g2d.drawString(bdi.toPlainString(), x, h0 + markSize + 12);
                    g2d.drawString(bdi.toPlainString(), w0 + markSize + 2, y + 5);
                }
                if (drawAnyOf(DRAW_ALL_LABELS, DRAW_NEGATIVE_UNITY_LABELS)) {
                    g2d.drawString(bdni.toPlainString(), nx, h0 + markSize + 12);
                    g2d.drawString(bdni.toPlainString(), w0 + markSize + 2, ny + 5);
                }
            }
        }

        if (option(DRAW_GRID)) {
            if (option(DRAW_POINT_GRID)) {
                g2d.setColor(Color.BLACK);
                for (int x = range.negate()
                        .intValue() + 1; x < range.intValue(); ++x) {
                    if (x == 0) continue;
                    for (int y = range.negate()
                            .intValue() + 1; y < range.intValue(); ++y) {
                        if (y == 0) continue;

                        int px = unitsToWidth(new BigDecimal(x));
                        int py = unitsToHeight(new BigDecimal(y));

                        g2d.drawLine(px, py, px, py);
                    }
                }
            } else {
                g2d.setColor(new Color(0x88000000, true));
                for (int i = range.negate()
                        .intValue() + 1; i < range.intValue(); i++) {
                    BigDecimal bdi = new
                            BigDecimal(i);
                    g2d.drawLine(
                            unitsToWidth(range.negate()),
                            unitsToHeight(bdi),
                            unitsToWidth(range),
                            unitsToHeight(bdi));
                    g2d.drawLine(
                            unitsToWidth(bdi),
                            unitsToHeight(range.negate()),
                            unitsToWidth(bdi),
                            unitsToHeight(range));
                }
            }
        }

        if (learningSets != null && !learningSets.isEmpty()) drawLearningSets(g2d);
    }

    private BufferedImage[] networkOutputBuffer;

    private void drawNetworkClasses(Graphics2D g2d) {
        if (networkOutputBuffer == null) generateNetworkOutput();
        for (BufferedImage channel : networkOutputBuffer) {
            g2d.drawImage(channel, 0, 0, getWidth(), getHeight(), 0, 0, outputResolution, outputResolution, this);
        }
    }
    
    Executor outputExecutor = Executors.newFixedThreadPool(16);

    private void generateNetworkOutput() {
        if (networkOutputBuffer == null) createBuffer();
        
        outputExecutor.execute(()->{
            Color[] neuronColor = new Color[network.getOutputLength()];
            for (int i = 0; i < neuronColor.length; i++) {
                neuronColor[i] = Color.getHSBColor((i + 1) / (float) (neuronColor.length), 1, 0.5f);
            }

            for (int y = 0; y < outputResolution; y++) {
                System.out.printf("Generating row %d of %d%n", y, outputResolution);
                for (int x = 0; x < outputResolution; x++) {
                    BigDecimal[] output = network.calculate(new BigDecimal[]{
                            widthToUnits(x, outputResolution),
                            heightToUnits(y, outputResolution)
                    });
                    for (int i = 0; i < output.length; i++) {
                        int alpha = (int) (output[i].doubleValue() * maxOpacity * 0xff);
                        networkOutputBuffer[i].setRGB(x, y, neuronColor[i].getRGB() & 0xffffff + (alpha << 24));
                    }
                }
                repaint();
            }

            System.out.println("Done.");
        });
    }

    private void createBuffer() {
        networkOutputBuffer = new BufferedImage[network.getOutputLength()];
        for (int i = 0; i < networkOutputBuffer.length; i++) {
            networkOutputBuffer[i] = new BufferedImage(outputResolution, outputResolution, BufferedImage.TYPE_INT_ARGB);
        }
    }

    public int getOutputResolution() {
        return outputResolution;
    }

    public void setOutputResolution(int outputResolution) {
        this.outputResolution = outputResolution;
        createBuffer();
        generateNetworkOutput();
    }

    private void drawLearningSets(Graphics2D g2d) {
        for (LearningSetFactory set : learningSets) {
            if (set.getCenter().length != 2) return;

            int x = unitsToWidth(set.getCenter()[0]);
            int y = unitsToHeight(set.getCenter()[1]);
            int xRadius = unitsToLength(set.getVariance());
            int yRadius = unitsToLength(set.getVariance());

            g2d.setColor(set.getMemberClass()
                    .equals(BigDecimal.ONE) ? Color.GREEN.darker() : Color.RED.darker());
            g2d.fill(new Ellipse2D.Double(x, y, 3, 3));
            if (option(DRAW_LEARNING_SET_CIRCLE))
                g2d.draw(new Ellipse2D.Double(
                        x - xRadius,
                        y - yRadius,
                        xRadius * 2,
                        yRadius * 2));
            if (option(DRAW_LEARNING_SET_RADIUS))
                g2d.drawLine(x, y, x + xRadius, y);

            if (option(DRAW_LEARNING_SET_POINTS))
                for (LearningElement learningElement : set.getLearningSet()) {
                    int ex = unitsToWidth(learningElement.getInput()[0]);
                    int ey = unitsToHeight(learningElement.getInput()[1]);

                    if (option(DRAW_LARGER_LEARNING_SET_POINTS))
                        g2d.fill(new Ellipse2D.Double(ex - 1, ey - 1, 3, 3));
                    else
                        g2d.drawLine(ex, ey, ex, ey);
                }
        }
    }

    public void startPointPicker(PointPickerCallback callback) {
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getSource() != NetworkVisualizationPanel.this) return;

                if (e.getButton() == MouseEvent.BUTTON1) {
                    BigDecimal x = widthToUnits(e.getX());
                    BigDecimal y = heightToUnits(e.getY());
                    if (!e.isShiftDown()) {
                        x = x.setScale(0, BigDecimal.ROUND_HALF_UP);
                        y = y.setScale(0, BigDecimal.ROUND_HALF_UP);
                    }
                    callback.callback(x, y);
                }

                stopPointPicker();
            }
        };
        addMouseListener(mouseAdapter);
    }

    public void stopPointPicker() {
        setCursor(Cursor.getDefaultCursor());
        if (mouseAdapter != null) {
            removeMouseListener(mouseAdapter);
        }
    }

    private BigDecimal widthToUnits(int x) {
        int width = getWidth();
        return widthToUnits(x, width);
    }

    private BigDecimal widthToUnits(int x, int width) {
        BigDecimal out = new BigDecimal(x);
        out = out.setScale(defaultScale, BigDecimal.ROUND_HALF_UP)
                .multiply(doubleRange)
                .divide(new BigDecimal(width), BigDecimal.ROUND_HALF_UP)
                .subtract(range);
        return out;
    }

    private BigDecimal heightToUnits(int y) {
        int height = getHeight();
        return heightToUnits(y, height);
    }

    private BigDecimal heightToUnits(int y, int height) {
        BigDecimal out = new BigDecimal(y);
        out = out.setScale(defaultScale, BigDecimal.ROUND_HALF_UP)
                .multiply(doubleRange)
                .divide(new BigDecimal(height), BigDecimal.ROUND_HALF_UP)
                .subtract(range)
                .negate();
        return out;
    }

    private int unitsToWidth(BigDecimal x) {
        int width = getWidth();
        return unitsToWidth(x, width);
    }

    private int unitsToWidth(BigDecimal x, int width) {
        return x.setScale(defaultScale, BigDecimal.ROUND_HALF_UP)
                .add(range)
                .divide(doubleRange, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal(width))
                .intValue();
    }

    private int unitsToHeight(BigDecimal y) {
        int height = getHeight();
        return unitsToHeight(y, height);
    }

    private int unitsToHeight(BigDecimal y, int height) {
        return y.setScale(defaultScale, BigDecimal.ROUND_HALF_UP)
                .negate()
                .add(range)
                .divide(doubleRange, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal(height))
                .intValue();
    }

    private int unitsToLength(BigDecimal s) {
        return s.setScale(defaultScale, BigDecimal.ROUND_HALF_UP)
                .divide(doubleRange, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal(getWidth()))
                .intValue();
    }

    public void setLearningSets(List<LearningSetFactory> elements) {
        learningSets = elements;
        repaint();
    }

    public SimpleNetwork getNetwork() {
        return network;
    }

    public void setNetwork(SimpleNetwork network) {
        this.network = network;
        generateNetworkOutput();
        repaint();
    }

    public interface PointPickerCallback {
        void callback(BigDecimal x, BigDecimal y);
    }
}
