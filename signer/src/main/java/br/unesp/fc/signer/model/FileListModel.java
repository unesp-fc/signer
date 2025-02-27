package br.unesp.fc.signer.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import org.springframework.stereotype.Component;

@Component
public class FileListModel extends AbstractListModel<FileModel> {

    private final List<FileModel> files = new ArrayList<>();

    @Override
    public int getSize() {
        return files.size();
    }

    @Override
    public FileModel getElementAt(int index) {
        return files.get(index);
    }

    public void addFiles(List<File> files) throws IOException {
        int oldSize = this.files.size();
        for (var file : files) {
            this.files.add(new FileModel(file));
        }
        fireIntervalAdded(this, oldSize, this.files.size() - 1);
    }

    public void removeFiles(List<FileModel> files) {
        for (var file : files) {
            int i = this.files.indexOf(file);
            this.files.remove(i);
            fireIntervalRemoved(this, i, i);
        }
    }

}
