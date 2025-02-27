package br.unesp.fc.signer.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.security.InvalidParameterException;
import org.springframework.stereotype.Component;

@Component
public class PdfViewModel {

    public static final String CURRENT_PAGE = "currentPage";
    public static final String NUMBER_PAGES = "numberPages";
    public static final String SCALE = "scale";

    private final PropertyChangeSupport changes = new PropertyChangeSupport(this);
    private int currentPage = 0;
    private int numberPages = 0;
    private float scale = 1f;

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        if (currentPage < 0 || currentPage >= numberPages) {
            throw new InvalidParameterException("Invalid page number");
        }
        if (this.currentPage != currentPage) {
            int old = this.currentPage;
            this.currentPage = currentPage;
            changes.firePropertyChange(CURRENT_PAGE, old, currentPage);
        }
    }

    public int getNumberPages() {
        return numberPages;
    }

    public void setNumberPages(int numberPages) {
        if (this.numberPages != numberPages) {
            int old = this.numberPages;
            this.numberPages = numberPages;
            changes.firePropertyChange(NUMBER_PAGES, old, numberPages);
            if (this.currentPage >= this.numberPages) {
                old = this.currentPage;
                this.currentPage = this.numberPages - 1;
                changes.firePropertyChange(CURRENT_PAGE, old, currentPage);
            }
        }
    }

    public void setPage(int currentPage, int numberPages) {
        if (numberPages <= 0) {
            throw new InvalidParameterException("Invalid number of pages");
        }
        if (currentPage < 0 || currentPage >= numberPages) {
            throw new InvalidParameterException("Invalid page number");
        }
        if (this.currentPage != currentPage) {
            int old = this.currentPage;
            this.currentPage = currentPage;
            changes.firePropertyChange(CURRENT_PAGE, old, currentPage);
        }
        if (this.numberPages != numberPages) {
            int old = this.numberPages;
            this.numberPages = numberPages;
            changes.firePropertyChange(NUMBER_PAGES, old, numberPages);
        }
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        if (this.scale != scale) {
            float old = this.scale;
            this.scale = scale;
            changes.firePropertyChange(SCALE, old, scale);
        }
    }

    public void addListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }

    public void removeListener(PropertyChangeListener listener) {
        changes.removePropertyChangeListener(listener);
    }

    public void nextPage() {
        if (currentPage < numberPages - 1) {
            changes.firePropertyChange(CURRENT_PAGE, currentPage, ++currentPage);
        }
    }

    public void previousPage() {
        if (currentPage > 0) {
            changes.firePropertyChange(CURRENT_PAGE, currentPage, --currentPage);
        }
    }

    public boolean isFirstPage() {
        return currentPage == 0;
    }

    public boolean isLastPage() {
        return currentPage == numberPages - 1;
    }

}
