package br.unesp.fc.signer.view;

import br.unesp.fc.signer.model.SignVerifyInfoModel;
import java.util.List;
import javax.swing.JColorChooser;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Component;

@Component
public class PropertiesPanel extends javax.swing.JPanel {

    private final ColorComboBoxModel textoColorComboBoxModel = new ColorComboBoxModel(false, true);
    private final ColorComboBoxModel fundoColorComboBoxModel = new ColorComboBoxModel(true, true);
    private final ColorComboBoxModel linkColorComboBoxModel = new ColorComboBoxModel(false, true);
    private final FontComboBoxModel textoFontComboBoxModel = new FontComboBoxModel(List.of(
            Standard14Fonts.FontName.TIMES_ROMAN,
            Standard14Fonts.FontName.TIMES_BOLD,
            Standard14Fonts.FontName.TIMES_ITALIC,
            Standard14Fonts.FontName.TIMES_BOLD_ITALIC,
            Standard14Fonts.FontName.HELVETICA,
            Standard14Fonts.FontName.HELVETICA_BOLD,
            Standard14Fonts.FontName.HELVETICA_OBLIQUE,
            Standard14Fonts.FontName.HELVETICA_BOLD_OBLIQUE
    ));
    private final FontComboBoxModel codigoFontComboBoxModel = new FontComboBoxModel(List.of(
            Standard14Fonts.FontName.COURIER,
            Standard14Fonts.FontName.COURIER_BOLD,
            Standard14Fonts.FontName.COURIER_OBLIQUE,
            Standard14Fonts.FontName.COURIER_BOLD_OBLIQUE
    ));

    /**
     * Creates new form PropertiesPanel
     */
    public PropertiesPanel(SignVerifyInfoModel signVerifyInfoModel) {
        initComponents();
        cmbColorTexto.setRenderer(new ColorCellRenderer());
        cmbColorFundo.setRenderer(new ColorCellRenderer());
        cmbColorLink.setRenderer(new ColorCellRenderer());
        textoColorComboBoxModel.setColor(signVerifyInfoModel.getForeground());
        fundoColorComboBoxModel.setColor(
                        signVerifyInfoModel.getBackground() == null ? ColorNames.TRANSPARENT.color() : signVerifyInfoModel.getBackground());
        linkColorComboBoxModel.setColor(signVerifyInfoModel.getLinkColor());
        textoColorComboBoxModel.addStateChangeListener((e) -> {
            signVerifyInfoModel.setForeground(textoColorComboBoxModel.getColor());
        });
        fundoColorComboBoxModel.addStateChangeListener((e) -> {
            signVerifyInfoModel.setBackground(fundoColorComboBoxModel.getColor() != ColorNames.TRANSPARENT.color()
                    ? fundoColorComboBoxModel.getColor()
                    : null);
        });
        linkColorComboBoxModel.addStateChangeListener((e) -> {
            signVerifyInfoModel.setLinkColor(linkColorComboBoxModel.getColor());
        });
        signVerifyInfoModel.addListener((evt) -> {
            switch (evt.getPropertyName()) {
                case SignVerifyInfoModel.FOREGROUND -> textoColorComboBoxModel.setColor(signVerifyInfoModel.getForeground());
                case SignVerifyInfoModel.BACKGROUND -> fundoColorComboBoxModel.setColor(
                        signVerifyInfoModel.getBackground() == null ? ColorNames.TRANSPARENT.color() : signVerifyInfoModel.getBackground());
                case SignVerifyInfoModel.LINK_COLOR -> linkColorComboBoxModel.setColor(signVerifyInfoModel.getLinkColor());
                case SignVerifyInfoModel.BOUNDS -> {
                    var rect = signVerifyInfoModel.getPosition();
                    txtPositionX.setText(String.valueOf(rect.getX()));
                    txtPositionY.setText(String.valueOf(rect.getY()));
                    txtPositionWidth.setText(String.valueOf(rect.getWidth()));
                    txtPositionHeight.setText(String.valueOf(rect.getHeight()));
                }
            }
        });
        textoFontComboBoxModel.setSelectedItem(signVerifyInfoModel.getTextFont());
        codigoFontComboBoxModel.setSelectedItem(signVerifyInfoModel.getCodeFont());
        textoFontComboBoxModel.addStateChangeListener((e) -> {
            signVerifyInfoModel.setTextFont(textoFontComboBoxModel.getFont());
        });
        codigoFontComboBoxModel.addStateChangeListener((e) -> {
            signVerifyInfoModel.setCodeFont(codigoFontComboBoxModel.getFont());
        });
        cmbFonteTamanho.setSelectedItem(signVerifyInfoModel.getFontSize());
        cmbFonteTamanho.getModel().addListDataListener(new ListDataListener() {
            @Override
            public void intervalAdded(ListDataEvent e) {
            }
            @Override
            public void intervalRemoved(ListDataEvent e) {
            }
            @Override
            public void contentsChanged(ListDataEvent e) {
                try {
                    signVerifyInfoModel.setFontSize(Float.valueOf((String) cmbFonteTamanho.getSelectedItem()));
                } catch (Exception ex) {
                    cmbFonteTamanho.setSelectedItem(signVerifyInfoModel.getFontSize());
                }
            }
        });
        cmbFonteRotacao.setSelectedItem(signVerifyInfoModel.getRotation() + "°");
        cmbFonteRotacao.getModel().addListDataListener(new ListDataListener() {
            @Override
            public void intervalAdded(ListDataEvent e) {
            }
            @Override
            public void intervalRemoved(ListDataEvent e) {
            }
            @Override
            public void contentsChanged(ListDataEvent e) {
                try {
                    var value = Integer.valueOf(cmbFonteRotacao.getSelectedItem().toString().replace("°", ""));
                    signVerifyInfoModel.setRotation(value);
                } catch (Exception ex) {
                    cmbFonteRotacao.setSelectedItem(signVerifyInfoModel.getRotation() + "°");
                }
            }
        });
        btnChangeColorTexto.setText(null);
        btnChangeColorFundo.setText(null);
        btnChangeColorLink.setText(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cmbColorTexto = new javax.swing.JComboBox<>();
        btnChangeColorTexto = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        cmbColorFundo = new javax.swing.JComboBox<>();
        btnChangeColorFundo = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        cmbColorLink = new javax.swing.JComboBox<>();
        btnChangeColorLink = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cmbFonteTexto = new javax.swing.JComboBox<>();
        cmbFonteCodigo = new javax.swing.JComboBox<>();
        cmbFonteTamanho = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        cmbFonteRotacao = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        txtPositionX = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtPositionY = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtPositionWidth = new javax.swing.JTextField();
        txtPositionHeight = new javax.swing.JTextField();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cor", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP));

        jLabel1.setText("Texto");

        cmbColorTexto.setModel(textoColorComboBoxModel);

        btnChangeColorTexto.setIcon(new MaterialSymbols.SymbolIcon(MaterialSymbols.PALETTE));
        btnChangeColorTexto.setText("X");
        btnChangeColorTexto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangeColorTextoActionPerformed(evt);
            }
        });

        jLabel2.setText("Fundo");

        cmbColorFundo.setModel(fundoColorComboBoxModel);

        btnChangeColorFundo.setIcon(new MaterialSymbols.SymbolIcon(MaterialSymbols.PALETTE));
        btnChangeColorFundo.setText("X");
        btnChangeColorFundo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangeColorFundoActionPerformed(evt);
            }
        });

        jLabel3.setText("Link");

        cmbColorLink.setModel(linkColorComboBoxModel);

        btnChangeColorLink.setIcon(new MaterialSymbols.SymbolIcon(MaterialSymbols.PALETTE));
        btnChangeColorLink.setText("X");
        btnChangeColorLink.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangeColorLinkActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3))))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cmbColorTexto, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnChangeColorTexto))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(cmbColorLink, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnChangeColorLink))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(cmbColorFundo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnChangeColorFundo)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cmbColorLink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnChangeColorLink))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel1))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnChangeColorTexto)
                                .addComponent(cmbColorTexto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnChangeColorFundo)
                            .addComponent(cmbColorFundo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Fonte", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP));

        jLabel4.setText("Texto");

        jLabel5.setText("Código");

        cmbFonteTexto.setModel(textoFontComboBoxModel);

        cmbFonteCodigo.setModel(codigoFontComboBoxModel);

        cmbFonteTamanho.setEditable(true);
        cmbFonteTamanho.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "8", "10", "12", "14", "16", "18" }));

        jLabel6.setText("Tamanho");

        cmbFonteRotacao.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0°", "90°", "180°", "270°" }));

        jLabel7.setText("Rotação");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbFonteTexto, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmbFonteCodigo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmbFonteTamanho, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmbFonteRotacao, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cmbFonteTexto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(cmbFonteCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbFonteTamanho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbFonteRotacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Posição", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP));

        jLabel8.setText("X");

        jLabel9.setText("Y");

        jLabel10.setText("L");

        jLabel11.setText("A");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPositionX)
                    .addComponent(txtPositionWidth, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel11)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPositionY)
                    .addComponent(txtPositionHeight, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtPositionX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(txtPositionY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(txtPositionWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPositionHeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnChangeColorTextoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeColorTextoActionPerformed
        var color = textoColorComboBoxModel.getColor();
        color = JColorChooser.showDialog(this, "Cor do Texto", color, textoColorComboBoxModel.hasTransparent());
        if (color != null) {
            textoColorComboBoxModel.setColor(color);
        }
    }//GEN-LAST:event_btnChangeColorTextoActionPerformed

    private void btnChangeColorFundoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeColorFundoActionPerformed
        var color = fundoColorComboBoxModel.getColor();
        color = JColorChooser.showDialog(this, "Cor do Fundo", color, fundoColorComboBoxModel.hasTransparent());
        if (color != null) {
            fundoColorComboBoxModel.setColor(color);
        }
    }//GEN-LAST:event_btnChangeColorFundoActionPerformed

    private void btnChangeColorLinkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeColorLinkActionPerformed
        var color = linkColorComboBoxModel.getColor();
        color = JColorChooser.showDialog(this, "Cor do Link", color, linkColorComboBoxModel.hasTransparent());
        if (color != null) {
            linkColorComboBoxModel.setColor(color);
        }
    }//GEN-LAST:event_btnChangeColorLinkActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChangeColorFundo;
    private javax.swing.JButton btnChangeColorLink;
    private javax.swing.JButton btnChangeColorTexto;
    private javax.swing.JComboBox<ColorNames.NamedColor> cmbColorFundo;
    private javax.swing.JComboBox<ColorNames.NamedColor> cmbColorLink;
    private javax.swing.JComboBox<ColorNames.NamedColor> cmbColorTexto;
    private javax.swing.JComboBox<Standard14Fonts.FontName> cmbFonteCodigo;
    private javax.swing.JComboBox<String> cmbFonteRotacao;
    private javax.swing.JComboBox<String> cmbFonteTamanho;
    private javax.swing.JComboBox<Standard14Fonts.FontName> cmbFonteTexto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField txtPositionHeight;
    private javax.swing.JTextField txtPositionWidth;
    private javax.swing.JTextField txtPositionX;
    private javax.swing.JTextField txtPositionY;
    // End of variables declaration//GEN-END:variables

    public static void main(String args[]) {
        br.unesp.fc.signer.Signer.setup();
        javax.swing.SwingUtilities.invokeLater(() -> {
            var panel = new PropertiesPanel(new SignVerifyInfoModel());
            var frame = new javax.swing.JFrame();
            frame.add(panel);
            frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
            frame.setPreferredSize(new java.awt.Dimension(1024, 768));
            frame.pack();
            frame.setVisible(true);
        });
    }

}
