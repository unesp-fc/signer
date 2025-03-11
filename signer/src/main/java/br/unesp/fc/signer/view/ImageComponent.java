package br.unesp.fc.signer.view;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class ImageComponent extends JLabel {

    private BufferedImage image;

    public ImageComponent() {
        //setOpaque(false);
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        if (image != null) {
            setIcon(new ImageIcon(image));
            setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        } else {
            setIcon(null);
            setPreferredSize(new Dimension(0, 0));
        }
    }

}
