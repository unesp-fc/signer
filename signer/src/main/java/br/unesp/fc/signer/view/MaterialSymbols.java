package br.unesp.fc.signer.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;

public class MaterialSymbols {

    public static final String ADD = codepoint(0xe145);
    public static final String REMOVE = codepoint(0xe15b);
    public static final String PALETTE = codepoint(0xe40a);

    private static String codepoint(int codepoint) {
        return Character.toString(codepoint);
    }

    private static final Font FONT = createFont();

    private static Font createFont() {
        try {
            return FONT.createFont(Font.TRUETYPE_FONT, MaterialSymbols.class.getResourceAsStream("/icons/MaterialSymbolsOutlined.ttf"));
        } catch (FontFormatException | IOException ex) {
            Logger.getLogger(MaterialSymbols.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new JLabel().getFont();
    }

    public static Font getFont(float size) {
        return FONT.deriveFont(size);
    }

    private static final Map<String, String> codepoints = load();

    public static String get(String name) {
        return codepoints.get(name);
    }

    public static Map<String, String> load() {
        var map = new TreeMap<String, String>();
        try {
            var reader = new BufferedReader(new InputStreamReader(MaterialSymbols.class.getResourceAsStream("/icons/MaterialSymbolsOutlined.codepoints")));
            while (reader.ready()) {
                String line = reader.readLine();
                String values[] = line.split(" ");
                var codepoint = Integer.valueOf(values[1], 16);
                map.put(values[0], Character.toString(codepoint));
            }
        } catch (IOException ex) {
            Logger.getLogger(MaterialSymbols.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Collections.unmodifiableMap(map);
    }

    public static class SymbolIcon implements Icon {

        private static final int DEFAULT_SIZE = new javax.swing.JLabel("X").getPreferredSize().height;
        private String glyph;
        private Integer size;

        public SymbolIcon(String glyph) {
            this.glyph = glyph;
            size = DEFAULT_SIZE;
        }

        public SymbolIcon(String glyph, int size) {
            this.glyph = glyph;
            this.size = size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            var g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                           RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
            var font = FONT.deriveFont(72f / 96f * c.getHeight());
            var metrics = g2.getFontMetrics(font);
            x += (size - metrics.stringWidth(glyph)) / 2;
            y += (size - metrics.getHeight()) / 2 + metrics.getAscent();
            g2.setFont(font);
            if (!c.isEnabled()) {
                var jc = (JComponent) c;
                Color color = UIManager.getColor(jc.getUIClassID().substring(0, jc.getUIClassID().length() - 2) + ".disabledText");
                if (color == null) {
                    color = c.getForeground();
                }
                g2.setColor(color);
            } else {
                g2.setColor(c.getForeground());
            }
            g2.drawString(glyph, x, y);
            g2.setFont(c.getFont());
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

    }

}
