package br.unesp.fc.signer.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SelectedFileModel {

    private FileModel fileModel;

    private final PropertyChangeSupport changes = new PropertyChangeSupport(this);

    public static final String FILE_MODEL = "fileModel";

    @Autowired
    private PdfViewModel pdfViewModel;

    public FileModel getFileModel() {
        return fileModel;
    }

    public void setPdfViewModel(PdfViewModel pdfViewModel) {
        this.pdfViewModel = pdfViewModel;
    }

    public void setFileModel(FileModel fileModel) {
        // Pode verificar apenas se é a mesma instância
        if (this.fileModel != fileModel) {
            var old = this.fileModel;
            this.fileModel = fileModel;
            changes.firePropertyChange(FILE_MODEL, old, fileModel);
            if (fileModel != null) {
                pdfViewModel.setNumberPages(fileModel.getDocument().getNumberOfPages());
            } else {
                pdfViewModel.setNumberPages(0);
            }
        }
    }

    public void addListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }

    public void removeListener(PropertyChangeListener listener) {
        changes.removePropertyChangeListener(listener);
    }

}
