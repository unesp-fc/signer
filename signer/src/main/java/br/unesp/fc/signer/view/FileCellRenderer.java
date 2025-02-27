package br.unesp.fc.signer.view;

import br.unesp.fc.signer.model.FileModel;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.filechooser.FileSystemView;

//public class FileCellRenderer extends JLabel implements ListCellRenderer<FileModel> {
//
//    public FileCellRenderer() {
//        setOpaque(true);
//    }
//
//    @Override
//    public Component getListCellRendererComponent(JList<? extends FileModel> list, FileModel value, int index, boolean isSelected, boolean cellHasFocus) {
//        if (isSelected) {
//            setBackground(list.getSelectionBackground());
//            setForeground(list.getSelectionForeground());
//        } else {
//            setBackground(list.getBackground());
//            setForeground(list.getForeground());
//        }
//        setIcon(FileSystemView.getFileSystemView().getSystemIcon(value.getFile()));
//        setText(value.getFile().getName());
//        return this;
//    }
//
//}

public class FileCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        var fileModel = (FileModel) value;
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        setIcon(FileSystemView.getFileSystemView().getSystemIcon(fileModel.getFile()));
        setText(fileModel.getFile().getName());
        return this;
    }

}