package br.unesp.fc.signer.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;

public class ColorCellRenderer extends DefaultListCellRenderer {

    private ColorNames.NamedColor color;
    private final ColorIcon colorIcon = new ColorIcon();

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        color = (ColorNames.NamedColor) value;
        if (color != null) {
            setText(color.name());
        }
        setIcon(colorIcon);
        return this;
    }

    private class ColorIcon implements Icon {

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            int size = ColorCellRenderer.this.getHeight() - 4;
            var color = ColorCellRenderer.this.color;
            if (color != null) {
                if (color.color().getAlpha() < 0xff) {
                    int square = size / 2;
                    g.setColor(Color.DARK_GRAY);
                    g.fillRect(x, y, square, square);
                    g.fillRect(x + square, y + square, square, square);
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillRect(x + square, y, square, square);
                    g.fillRect(x, y + square, square, square);
                }
                g.setColor(color.color());
                g.fillRect(x, y, size, size);
            }
        }

        @Override
        public int getIconWidth() {
            return ColorCellRenderer.this.getHeight() - 4;
        }

        @Override
        public int getIconHeight() {
            return ColorCellRenderer.this.getHeight() - 4;
        }

    }

}
