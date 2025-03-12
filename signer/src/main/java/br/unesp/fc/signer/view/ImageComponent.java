package br.unesp.fc.signer.view;

import java.awt.Dimension;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class ImageComponent extends JLabel {

    private Image image;

    public ImageComponent() {
        //setOpaque(false);
    }

    public void setImage(Image image) {
        this.image = image;
        if (image != null) {
            setIcon(new ImageIcon(image));
            setPreferredSize(new Dimension(image.getWidth(this), image.getHeight(this)));
        } else {
            setIcon(null);
            setPreferredSize(new Dimension(0, 0));
        }
    }

}
