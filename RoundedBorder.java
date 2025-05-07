import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.border.AbstractBorder;

public class RoundedBorder extends AbstractBorder{
    private int radius;
    public RoundedBorder(int radius){
        this.radius = radius;
    }
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3));
        // Vẽ một hình chữ nhật bo tròn (drawRoundRect)
        g2.drawRoundRect(x, y, width-1, height-1, radius, radius);
        g2.dispose(); // Giải phóng bộ nhớ đồ họa
    }
}
