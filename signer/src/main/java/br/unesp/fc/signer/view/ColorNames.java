package br.unesp.fc.signer.view;

import java.awt.Color;
import java.util.Objects;

public enum ColorNames {
    WHITE(      "White",    0xFFFFFF),
    SILVER(     "Silver",   0xC0C0C0),
    GRAY(       "Gray",     0x808080),
    BLACK(      "Black",    0x000000),
    RED(        "Red",      0xFF0000),
    MARRON(     "Marron",   0x800000),
    YELLOW(     "Yellow",   0xFFFF00),
    OLIVE(      "Olive",    0x808000),
    LIME(       "Lime",     0x00FF00),
    GREEN(      "Green",    0x008000),
    AQUA(       "Aqua",     0x00FFFF),
    TEAL(       "TEAL",     0x008080),
    BLUE(       "Blue",     0x0000FF),
    NAVY(       "Navy",     0x000080),
    FUCHSIA(    "Fuchsia",  0xFF00FF),
    PURPLE(     "Purple",   0x800080),
    ;

    public static record NamedColor(String name, Color color) {

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 31 * hash + Objects.hashCode(this.color);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final NamedColor other = (NamedColor) obj;
            return Objects.equals(this.color, other.color);
        }

    }

    private final NamedColor namedColor;

    ColorNames(String colorName, int code) {
        namedColor = new NamedColor(colorName, new Color(code));
    }

    public NamedColor getNamedColor() {
        return namedColor;
    }

    public static final NamedColor TRANSPARENT = new NamedColor("Transparent", new Color(0,0,0, 0));

}
