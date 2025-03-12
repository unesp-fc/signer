package br.unesp.fc.signer.view;

import br.unesp.fc.signer.model.FileModel;
import br.unesp.fc.signer.model.PdfViewModel;
import br.unesp.fc.signer.model.SelectedFileModel;
import br.unesp.fc.signer.model.SignVerifyInfoModel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BaseMultiResolutionImage;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.RenderDestination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PdfPanel extends javax.swing.JPanel {

    private static final float SCALE_FACTOR = 96f / 72f;
    private BufferedImage image;
    private BaseMultiResolutionImage scaledImage;
    private FileModel fileModel;
    private SelectedFileModel selectedFileModel;
    private PdfViewModel pdfViewModel;
    private SignVerifyInfoModel signVerifyInfoModel;

    private ImageComponent info = new ImageComponent();
    private MoveContainer move = new MoveContainer(info);
    private boolean updateBounds = true;

    private final PropertyChangeListener pdfViewModelListener = (PropertyChangeEvent evt) -> {
        renderImage();
        if (PdfViewModel.SCALE.equals(evt.getPropertyName())) {
            signVerifyInfoModel.setScale((float) evt.getNewValue());
            resize();
        }
        repaint();
    };

    private final PropertyChangeListener signVerifyInfoModelListener = (PropertyChangeEvent evt) -> {
        switch (evt.getPropertyName()) {
            case SignVerifyInfoModel.BACKGROUND:
                move.setOpaque(evt.getNewValue() != null);
                move.setBackground(evt.getNewValue() != null ? (Color) evt.getNewValue() : this.getBackground());
                break;
            default:
                info.setImage(signVerifyInfoModel.getImage(getGraphicsConfiguration().getDefaultTransform().getScaleX()));
                if (updateBounds) {
                    var bounds = signVerifyInfoModel.getBounds();
                    bounds.y = image.getHeight() - bounds.y - bounds.height;
                    try {
                        updateBounds = false;
                        move.setBounds(bounds);
                    } finally {
                        updateBounds = true;
                    }
                }
                break;
        }
    };

    private final PropertyChangeListener selectedFileModelListener = (PropertyChangeEvent evt) -> {
        setFileModel(selectedFileModel.getFileModel());
    };

    private final ComponentAdapter moveListener = new ComponentAdapter() {
        @Override
        public void componentMoved(ComponentEvent e) {
            setBounds();
        }
        @Override
        public void componentResized(ComponentEvent e) {
            setBounds();
        }
        void setBounds() {
            if (image != null && updateBounds) {
                try {
                    updateBounds = false;
                    signVerifyInfoModel.setBounds(move.getX(), image.getHeight() - move.getY() - move.getHeight(), move.getWidth(), move.getHeight());
                } finally {
                    updateBounds = true;
                }
            }
        }
    };

    /**
     * Creates new form EditorPanel
     */
    public PdfPanel() {
        initComponents();
        add(move);
        move.setLocation(-1, -1);
        move.addComponentListener(moveListener);
        move.setVisible(false);
        move.setOpaque(false);
    }

    public void setFileModel(FileModel fileModel) {
        this.fileModel = fileModel;
        renderImage();
        resize();
        move.setVisible(fileModel != null);
        if (this.image != null) {
            signVerifyInfoModelListener.propertyChange(new PropertyChangeEvent(signVerifyInfoModelListener, SignVerifyInfoModel.BOUNDS, null, null));
        }
        repaint();
    }

    @Autowired
    public void setSignVerifyInfoModel(SignVerifyInfoModel signVerifyInfoModel) {
        this.signVerifyInfoModel = signVerifyInfoModel;
        signVerifyInfoModel.addListener(signVerifyInfoModelListener);
        signVerifyInfoModelListener.propertyChange(new PropertyChangeEvent(signVerifyInfoModel, SignVerifyInfoModel.BACKGROUND, null, signVerifyInfoModel.getBackground()));
    }

    @Autowired
    public void setSelectedFileModel(SelectedFileModel selectedFileModel) {
        if (this.selectedFileModel != null) {
            this.selectedFileModel.removeListener(selectedFileModelListener);
        }
        this.selectedFileModel = selectedFileModel;
        this.selectedFileModel.addListener(selectedFileModelListener);
    }

    protected void resize() {
        if (image != null) {
            setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
            move.setConstraints(new Rectangle(0, 0, image.getWidth(), image.getHeight()));
        }
    }

    protected void renderImage() {
        if (fileModel == null) {
            image = null;
            return;
        }
        try {
            float scale = pdfViewModel.getScale() * SCALE_FACTOR;
            image = fileModel.getRenderer().renderImage(pdfViewModel.getCurrentPage(), scale, ImageType.ARGB, RenderDestination.VIEW);
            if (getGraphicsConfiguration().getDefaultTransform().getScaleX() > 1) {
                scale *= getGraphicsConfiguration().getDefaultTransform().getScaleX();
                var scaledimage = fileModel.getRenderer().renderImage(pdfViewModel.getCurrentPage(), scale, ImageType.ARGB, RenderDestination.VIEW);
                scaledImage = new BaseMultiResolutionImage( image, scaledimage);
            } else {
                scaledImage = new BaseMultiResolutionImage( image);
            }
        } catch (IOException ex) {
            Logger.getLogger(PdfPanel.class.getName()).log(Level.SEVERE, null, ex);
            image = null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (fileModel != null && scaledImage != null) {
            g.drawImage(scaledImage, 0, 0, this);
        }
    }

    @Autowired
    public void setPdfViewModel(PdfViewModel pdfViewModel) {
        if (this.pdfViewModel != null) {
            this.pdfViewModel.removeListener(pdfViewModelListener);
        }
        this.pdfViewModel = pdfViewModel;
        this.pdfViewModel.addListener(pdfViewModelListener);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
