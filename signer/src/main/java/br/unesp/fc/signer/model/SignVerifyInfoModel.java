package br.unesp.fc.signer.model;

import br.unesp.fc.signer.SignerVerifyInfoWrite;
import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BaseMultiResolutionImage;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDImmutableRectangle;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.RenderDestination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class SignVerifyInfoModel {

    private final float INTERNAL_SCALE = 96f / 72f;

    private Color background;
    private Color foreground = Color.BLACK;
    private Color linkColor = Color.BLUE;
    private Standard14Fonts.FontName textFont = Standard14Fonts.FontName.TIMES_ROMAN;
    private Standard14Fonts.FontName codeFont = Standard14Fonts.FontName.COURIER;
    private BufferedImage image;
    private float fontSize = 8;
    private float scale = 1.0f;
    private float x = 0;
    private float y = 0;
    private float width = 0;
    private float height = 0;
    private int rotation = 0;

    private final PropertyChangeSupport changes = new PropertyChangeSupport(this);

    public static final String BACKGROUND = "background";
    public static final String FOREGROUND = "foreground";
    public static final String LINK_COLOR = "linkColor";
    public static final String FONT_SIZE = "fontSize";
    public static final String SCALE = "scale";
    public static final String TEXT_FONT = "textFont";
    public static final String CODE_FONT = "codeFont";
    public static final String ROTATION = "rotation";
    public static final String BOUNDS = "bounds";

    @Autowired @Lazy
    private SignerVerifyInfoWrite signerVerifyIndoWrite;

    public Rectangle2D.Float getRect() {
        return new Rectangle2D.Float(x, y, width, height);
    }

    private BufferedImage generate(float screenScale) {
        try (PDDocument doc = new PDDocument()) {
            var size = signerVerifyIndoWrite.calcSize(fontSize);
            PDPage page;
            if (rotation == 0 || rotation == 180) {
                page = new PDPage(new PDImmutableRectangle(size.width, size.height));
            } else {
                page = new PDPage(new PDImmutableRectangle(size.height, size.width));
            }
            doc.addPage(page);
            signerVerifyIndoWrite.write(doc, page, true);
            PDFRenderer renderer = new PDFRenderer(doc);
            this.image = renderer.renderImage(0, scale * INTERNAL_SCALE, ImageType.ARGB, RenderDestination.EXPORT.VIEW);
            verifyMinBounds(true);
            if (screenScale != 1) {
                BufferedImage image = renderer.renderImage(0, screenScale * scale * INTERNAL_SCALE, ImageType.ARGB, RenderDestination.EXPORT.VIEW);
                return image;
            }
        } catch (IOException ex) {
            Logger.getLogger(SignVerifyInfoModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this.image;
    }

    public Image getImage(double screenScale) {
        var image = generate((float) screenScale);
        if (screenScale != 1) {
            return new BaseMultiResolutionImage(this.image, image);
        } else {
            return new BaseMultiResolutionImage(this.image);
        }
    }

    public Color getBackground() {
        return background;
    }

    public void setBackground(Color background) {
        if (this.background != null && !this.background.equals(background)
                || this.background == null && background != null) {
            Color old = this.background;
            this.background = background;
            changes.firePropertyChange(BACKGROUND, old, this.background);
        }
    }

    public Color getForeground() {
        return foreground;
    }

    public void setForeground(Color foreground) {
        if (!this.foreground.equals(foreground)) {
            Color old = this.foreground;
            this.foreground = foreground;
            generate(1.0f);
            changes.firePropertyChange(FOREGROUND, old, this.foreground);
        }
    }

    public Color getLinkColor() {
        return linkColor;
    }

    public void setLinkColor(Color linkColor) {
        if (!this.linkColor.equals(linkColor)) {
            Color old = this.linkColor;
            this.linkColor = linkColor;
            generate(1.0f);
            changes.firePropertyChange(LINK_COLOR, old, this.linkColor);
        }
    }

    public float getFontSize() {
        return fontSize;
    }

    public void setFontSize(float fontSize) {
        if (this.fontSize != fontSize) {
            float old = this.fontSize;
            this.fontSize = fontSize;
            generate(1.0f);
            changes.firePropertyChange(FONT_SIZE, old, this.fontSize);
        }
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        if (this.scale != scale) {
            float old = this.scale;
            this.scale = scale;
            generate(1.0f);
            changes.firePropertyChange(SCALE, old, this.scale);
        }
    }

    public void addListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }

    public void removeListener(PropertyChangeListener listener) {
        changes.removePropertyChangeListener(listener);
    }

    public void setBounds(float x, float y, float width, float height) {
        float scale = this.scale * INTERNAL_SCALE;
        var old = new Rectangle.Float(this.x * scale, this.y * scale, this.width * scale, this.height * scale);
        this.x = x / scale;
        this.y = y / scale;
        this.width = width / scale;
        this.height = height / scale;
        changes.firePropertyChange(BOUNDS, old, new Rectangle.Float(x, y, this.width, this.height));
    }

    public Rectangle getBounds() {
        float scale = this.scale * INTERNAL_SCALE;
        return new Rectangle((int) (this.x * scale), (int) (this.y * scale), (int) (this.width * scale), (int)(this.height * scale));
    }

    private boolean verifyMinBounds(boolean firePropertyChange) {
        float scale = this.scale * INTERNAL_SCALE;
        var old = new Rectangle.Float(this.x * scale, this.y * scale, this.width * scale, this.height * scale);
        var changed = false;
        if (this.width < image.getWidth() / scale) {
            this.width = image.getWidth() / scale;
            changed = true;
        }
        if (this.height < image.getHeight() / scale) {
            this.height = image.getHeight() / scale;
            changed = true;
        }
        if (changed && firePropertyChange) {
            changes.firePropertyChange(BOUNDS, old, new Rectangle.Float(x * scale, y * scale, this.width * scale, this.height * scale));
        }
        return changed;
    }

    public Standard14Fonts.FontName getTextFont() {
        return textFont;
    }

    public void setTextFont(Standard14Fonts.FontName textFont) {
        if (this.textFont != textFont) {
            var old = this.textFont;
            this.textFont = textFont;
            generate(1.0f);
            changes.firePropertyChange(TEXT_FONT, old, this.textFont);
        }
    }

    public Standard14Fonts.FontName getCodeFont() {
        return codeFont;
    }

    public void setCodeFont(Standard14Fonts.FontName codeFont) {
        if (this.codeFont != codeFont) {
            var old = this.codeFont;
            this.codeFont = codeFont;
            generate(1.0f);
            changes.firePropertyChange(CODE_FONT, old, this.codeFont);
        }
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        if (this.rotation != rotation) {
            if (rotation % 90 != 0) {
                throw new RuntimeException("Only 90, 180 and 270 are valid!");
            }
            rotation %= 360;
            int old = this.rotation;
            this.rotation = rotation;
            if ((old == 0 || old == 180) && (rotation == 90 || rotation == 270)
                    || (rotation == 0 || rotation == 180) && (old == 90 || old == 270)) {
                var width = this.width;
                this.width = this.height;
                this.height = width;
            }
            generate(1.0f);
            changes.firePropertyChange(ROTATION, old, rotation);
        }
    }

    public Rectangle.Float getPosition() {
        return new Rectangle.Float(x, y, width, height);
    }

    public void setPosition(Rectangle.Float rect) {
        float scale = this.scale * INTERNAL_SCALE;
        var oldBounds = new Rectangle.Float(x * scale, y * scale, width * scale, height * scale);
        boolean changed = false;
        if (x != rect.x) {
            x = rect.x;
            changed = true;
        }
        if (y != rect.y) {
            y = rect.y;
            changed = true;
        }
        if (width != rect.width) {
            width = rect.width;
            changed = true;
        }
        if (height != rect.height) {
            height = rect.height;
            changed = true;
        }
        verifyMinBounds(false);
        if (changed) {
            changes.firePropertyChange(BOUNDS, oldBounds, new Rectangle.Float(x * scale, y * scale, this.width * scale, this.height * scale));
        }
    }

}
