package br.unesp.fc.signer.view;

import br.unesp.fc.signer.model.PdfViewModel;
import br.unesp.fc.signer.model.SelectedFileModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.security.InvalidParameterException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BottomPanel extends javax.swing.JPanel {

    private PdfViewModel pdfViewModel;
    private SelectedFileModel selectedFileModel;

    private boolean updateTxtPage = true;

    private final PropertyChangeListener pdfViewModelListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            switch (evt.getPropertyName()) {
                case PdfViewModel.NUMBER_PAGES -> {
                    lblNumPages.setText(String.valueOf(evt.getNewValue()));
                    txtPage.setPreferredSize(lblNumPages.getPreferredSize());
                    btnNext.setEnabled(!pdfViewModel.isLastPage());
                }
                case PdfViewModel.CURRENT_PAGE -> {
                    setTxtPageValue((int) evt.getNewValue() + 1);
                    btnPrevious.setEnabled(!pdfViewModel.isFirstPage());
                    btnNext.setEnabled(!pdfViewModel.isLastPage());
                }
                case PdfViewModel.SCALE -> {
                    sldZoom.setValue((int) ((float) evt.getNewValue() * 100));
                    String value = String.valueOf(Float.valueOf((float) evt.getNewValue() * 100).intValue()) + " %";
                    var model = (DefaultComboBoxModel<String>) cmbZoom.getModel();
                    for (int i = 0; i < model.getSize(); i++) {
                        String option = model.getElementAt(i);
                        if (option.equals(value)) {
                            model.setSelectedItem(option);
                            break;
                        }
                    }
                }
            }
        }
    };

    private final PropertyChangeListener selectedFileModelListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (selectedFileModel.getFileModel() != null) {
                btnPrevious.setEnabled(!pdfViewModel.isFirstPage());
                btnNext.setEnabled(!pdfViewModel.isLastPage());
                txtPage.setEditable(true);
                cmbZoom.setEnabled(true);
                sldZoom.setEnabled(true);
                lblNumPages.setText(String.valueOf(pdfViewModel.getNumberPages()));
                setTxtPageValue(pdfViewModel.getCurrentPage() + 1);
            } else {
                btnPrevious.setEnabled(false);
                btnNext.setEnabled(false);
                txtPage.setEditable(false);
                txtPage.setValue(null);
                lblNumPages.setText("");
                cmbZoom.setEnabled(false);
                sldZoom.setEnabled(false);
                sldZoom.setValue(100);
                cmbZoom.getModel().setSelectedItem(((DefaultComboBoxModel<String>) cmbZoom.getModel()).getElementAt(0));
            }
        }
    };

    /**
     * Creates new form BottomPanel
     */
    public BottomPanel() {
        initComponents();
        setPdfViewModel(null);
        ((NumberFormatter)txtPage.getFormatter()).setValueClass(Integer.class);
        txtPage.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                update(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update(e);
            }
            private void update(DocumentEvent e) {
                if (pdfViewModel == null) {
                    return;
                }
                if (txtPage.getValue() == null) {
                    return;
                }
                if (!updateTxtPage) {
                    return;
                }
                try {
                    updateTxtPage = false;
                    pdfViewModel.setCurrentPage((int) txtPage.getValue() - 1);
                } catch (InvalidParameterException ex) {
                    SwingUtilities.invokeLater(() -> {
                        setTxtPageValue(pdfViewModel.getCurrentPage() + 1);
                    });
                } finally {
                    updateTxtPage = true;
                }
            }
        });
        sldZoom.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                int scale = source.getValue();
                if ((scale - source.getMinimum()) % source.getMinorTickSpacing() == 0) {
                    pdfViewModel.setScale(scale / 100f);
                }
            }
        });
        btnPrevious.setText(null);
        btnNext.setText(null);
    }

    @Autowired
    public void setPdfViewModel(PdfViewModel pdfViewModel) {
        if (this.pdfViewModel != null) {
            this.pdfViewModel.removeListener(pdfViewModelListener);
        }
        this.pdfViewModel = pdfViewModel;
        if (this.pdfViewModel != null) {
            this.pdfViewModel.addListener(pdfViewModelListener);
            pdfViewModelListener.propertyChange(new PropertyChangeEvent(this.pdfViewModel, PdfViewModel.NUMBER_PAGES, 0, pdfViewModel.getNumberPages()));
            pdfViewModelListener.propertyChange(new PropertyChangeEvent(this.pdfViewModel, PdfViewModel.CURRENT_PAGE, 0, pdfViewModel.getCurrentPage()));
            pdfViewModelListener.propertyChange(new PropertyChangeEvent(this.pdfViewModel, PdfViewModel.SCALE, 0, pdfViewModel.getScale()));
        }
    }

    @Autowired
    public void setSelectedFileModel(SelectedFileModel selectedFileModel) {
        if (this.selectedFileModel != null) {
            this.selectedFileModel.removeListener(selectedFileModelListener);
        }
        this.selectedFileModel = selectedFileModel;
        this.selectedFileModel.addListener(selectedFileModelListener);
        selectedFileModelListener.propertyChange(new PropertyChangeEvent(this.selectedFileModel, SelectedFileModel.FILE_MODEL, null, this.selectedFileModel.getFileModel()));
    }

    private void setTxtPageValue(Object object) {
        if (!updateTxtPage) {
            return;
        }
        try {
            updateTxtPage = false;
            txtPage.setValue(object);
        } finally {
            updateTxtPage = true;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        sldZoom = new javax.swing.JSlider();
        cmbZoom = new javax.swing.JComboBox<>();
        lblNumPages = new javax.swing.JLabel();
        btnPrevious = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtPage = new javax.swing.JFormattedTextField();

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        sldZoom.setMajorTickSpacing(100);
        sldZoom.setMaximum(400);
        sldZoom.setMinimum(100);
        sldZoom.setMinorTickSpacing(50);
        sldZoom.setSnapToTicks(true);

        cmbZoom.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "100 %", "150 %", "200 %", "250 %", "300 %", "350 %", "400 %" }));
        cmbZoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbZoomActionPerformed(evt);
            }
        });

        lblNumPages.setText("XX");

        btnPrevious.setIcon(new MaterialSymbols.SymbolIcon(MaterialSymbols.get("arrow_back_ios_new")));
        btnPrevious.setText("<");
        btnPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousActionPerformed(evt);
            }
        });

        btnNext.setIcon(new MaterialSymbols.SymbolIcon(MaterialSymbols.get("arrow_forward_ios")));
        btnNext.setText(">");
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });

        jLabel2.setText("-");

        txtPage.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        txtPage.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtPage.setText("99");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnPrevious)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPage, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblNumPages)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnNext)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 353, Short.MAX_VALUE)
                .addComponent(sldZoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbZoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btnPrevious)
                .addComponent(lblNumPages)
                .addComponent(btnNext)
                .addComponent(sldZoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(cmbZoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel2)
                .addComponent(txtPage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousActionPerformed
        pdfViewModel.previousPage();
    }//GEN-LAST:event_btnPreviousActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        pdfViewModel.nextPage();
    }//GEN-LAST:event_btnNextActionPerformed

    private void cmbZoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbZoomActionPerformed
        String value = (String) cmbZoom.getModel().getSelectedItem();
        value = value.substring(0, value.length() - 2);
        float scale = Integer.valueOf(value) / 100f;
        pdfViewModel.setScale(scale);
    }//GEN-LAST:event_cmbZoomActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPrevious;
    private javax.swing.JComboBox<String> cmbZoom;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel lblNumPages;
    private javax.swing.JSlider sldZoom;
    private javax.swing.JFormattedTextField txtPage;
    // End of variables declaration//GEN-END:variables
}
