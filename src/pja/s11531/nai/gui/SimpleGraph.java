package pja.s11531.nai.gui;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Arrvi on 2015-03-30.
 */
public class SimpleGraph extends JComponent {
    private List<BigDecimal> data;
    private int dataCount = -1;

    public SimpleGraph() {
        data = new ArrayList<>();
//        addComponentListener(new ComponentAdapter() {
//            @Override
//            public void componentResized(ComponentEvent e) {
//                repaint();
//                System.out.println("Graph resized.");
//            }
//        });
    }

    public SimpleGraph(int dataCount) {
        this();
        this.dataCount = dataCount;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        if ( data.size() == 0 ) return;

        Graphics2D g2d = (Graphics2D) g;
        Paint gradient = new GradientPaint(0,getHeight(), new Color(0x66000000, true), 
                0, 0, new Color(0x11000000, true));

        Polygon graph = new Polygon();
        graph.addPoint(0, getHeight());


        int steps = Math.max(dataCount, data.size());
        double step = (double)getWidth()/steps;
        double max = data.stream().parallel().max(Comparator.<BigDecimal>naturalOrder()).get().doubleValue();
        for (int i = 0; i < data.size(); i++) {
            graph.addPoint((int) (step * i), (int) (getHeight() - getHeight() * (data.get(i).doubleValue() / max)));
        }
        g2d.setPaint(gradient);
        graph.addPoint((int) (step*data.size()-1), getHeight());
        
        g2d.fillPolygon(graph);
    }

    public List<BigDecimal> getData() {
        return data;
    }

    public void setData(List<BigDecimal> data) {
        this.data = data;
        repaint();
    }
    
    public void addRecord(BigDecimal record) {
        data.add(record);
        repaint();
    }

    public int getDataCount() {
        return dataCount;
    }

    public void setDataCount(int dataCount) {
        this.dataCount = dataCount;
    }
}
