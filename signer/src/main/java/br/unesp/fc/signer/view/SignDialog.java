/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package br.unesp.fc.signer.view;

import br.unesp.fc.signer.model.KeyStoreTableModel;
import br.unesp.fc.signer.model.SignModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import org.apache.commons.lang3.SystemUtils;

/**
 *
 * @author demitrius
 */
public class SignDialog extends javax.swing.JDialog {

    private KeyStoreTableModel tableModel = new KeyStoreTableModel();
    private final SignModel signModel;
    private KeyStore keyStore;
    private File keyStoreFile;

    /**
     * Creates new form SignDialog
     */
    public SignDialog(java.awt.Frame parent, boolean modal, SignModel signModel) {
        super(parent, modal);
        this.signModel = signModel;
        initComponents();
        btnOpen.setText("");
        jTable1.getSelectionModel().addListSelectionListener((e) -> {
            btnContinuar.setEnabled(jTable1.getSelectedRow() >= 0);
        });
        rdbArquivo.getModel().addItemListener((e) -> {
            var checked = rdbArquivo.getModel().isSelected();
            txtKeyStoreFile.setEditable(checked);
            btnOpen.setEnabled(checked);
            if (checked) {
                try {
                    if (keyStoreFile != null) {
                        loadFileKeyStore();
                    } else {
                        tableModel.setKeyStore(null);
                    }
                } catch (Exception ex) {
                }
            }
        });
        rdbSistema.getModel().addItemListener((e) -> {
            try {
                if (rdbSistema.isSelected()) {
                    loadSystemKeyStore();
                }
            } catch (Exception ex) {
                Logger.getLogger(SignDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        if (SystemUtils.IS_OS_WINDOWS) {
            try {
                loadSystemKeyStore();
            } catch (Exception ex) {
                Logger.getLogger(SignDialog.class.getName()).log(Level.SEVERE, null, ex);
                rdbArquivo.getModel().setSelected(true);
                rdbSistema.setEnabled(false);
            }
        } else {
            rdbArquivo.setSelected(true);
            rdbSistema.setEnabled(false);
        }
    }

    private void loadSystemKeyStore() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        keyStore = KeyStore.getInstance("Windows-MY");
        keyStore.load(null, null);
        tableModel.setKeyStore(keyStore);
    }

    private void loadFileKeyStore() throws KeyStoreException, FileNotFoundException, IOException, NoSuchAlgorithmException, CertificateException {
        keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new FileInputStream(keyStoreFile), null);
        tableModel.setKeyStore(keyStore);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnGroupCertificado = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        txtKeyStoreFile = new javax.swing.JTextField();
        btnOpen = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        btnContinuar = new javax.swing.JButton();
        btnCacelar = new javax.swing.JButton();
        rdbSistema = new javax.swing.JRadioButton();
        rdbArquivo = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Certificado");

        txtKeyStoreFile.setEditable(false);
        txtKeyStoreFile.setEnabled(false);

        btnOpen.setIcon(new MaterialSymbols.SymbolIcon(MaterialSymbols.get("file_open")));
        btnOpen.setText("O");
        btnOpen.setEnabled(false);
        btnOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenActionPerformed(evt);
            }
        });

        jTable1.setModel(tableModel);
        jScrollPane1.setViewportView(jTable1);

        btnContinuar.setText("Continuar");
        btnContinuar.setEnabled(false);
        btnContinuar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnContinuarActionPerformed(evt);
            }
        });

        btnCacelar.setText("Cancelar");
        btnCacelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCacelarActionPerformed(evt);
            }
        });

        btnGroupCertificado.add(rdbSistema);
        rdbSistema.setSelected(true);
        rdbSistema.setText("Sistema");

        btnGroupCertificado.add(rdbArquivo);
        rdbArquivo.setText("Arquivo");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 591, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnContinuar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnCacelar)))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(txtKeyStoreFile)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnOpen)
                                .addGap(5, 5, 5))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(rdbArquivo)
                                    .addComponent(rdbSistema))
                                .addGap(0, 0, Short.MAX_VALUE))))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rdbSistema)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rdbArquivo)
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtKeyStoreFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnOpen))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCacelar)
                    .addComponent(btnContinuar))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenActionPerformed
        var jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setFileFilter(new FileNameExtensionFilter("Certificado", "p12"));
        int ret = jfc.showOpenDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            try {
                keyStoreFile = jfc.getSelectedFile();
                txtKeyStoreFile.setText(keyStoreFile.getAbsolutePath());
                loadFileKeyStore();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnOpenActionPerformed

    private void btnContinuarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContinuarActionPerformed
        try {
            signModel.setKeyStore(keyStore);
            signModel.setAlias((String) tableModel.getValue(jTable1.getSelectedRow()));
        } catch (KeyStoreException ex) {
            Logger.getLogger(SignDialog.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
        this.dispose();
    }//GEN-LAST:event_btnContinuarActionPerformed

    private void btnCacelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCacelarActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCacelarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SignDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SignDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SignDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SignDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SignDialog dialog = new SignDialog(new javax.swing.JFrame(), true, new SignModel());
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCacelar;
    private javax.swing.JButton btnContinuar;
    private javax.swing.ButtonGroup btnGroupCertificado;
    private javax.swing.JButton btnOpen;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JRadioButton rdbArquivo;
    private javax.swing.JRadioButton rdbSistema;
    private javax.swing.JTextField txtKeyStoreFile;
    // End of variables declaration//GEN-END:variables
}
