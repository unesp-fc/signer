package br.unesp.fc.signer.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

public class FontComboBoxModel extends AbstractListModel<Standard14Fonts.FontName> implements ComboBoxModel<Standard14Fonts.FontName> {

    private final List<Standard14Fonts.FontName> fonts;
    private int selected = -1;

    public FontComboBoxModel(Collection<Standard14Fonts.FontName> fonts) {
        this.fonts = new ArrayList<>(fonts);
    }

    @Override
    public int getSize() {
        return fonts.size();
    }

    @Override
    public Standard14Fonts.FontName getElementAt(int index) {
        return fonts.get(index);
    }

    @Override
    public void setSelectedItem(Object anItem) {
        selected = fonts.indexOf(anItem);
        fireContentsChanged(this, -1, -1);
        fireStateChange(this);
    }

    @Override
    public Object getSelectedItem() {
        if (selected >= 0) {
            return fonts.get(selected);
        }
        return null;
    }

    public void addStateChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }

    public void removeStateChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }

    protected void fireStateChange(Object source)
    {
        Object[] listeners = listenerList.getListenerList();
        ChangeEvent e = null;

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                if (e == null) {
                    e = new ChangeEvent(source);
                }
                ((ChangeListener)listeners[i+1]).stateChanged(e);
            }
        }
    }

    public Standard14Fonts.FontName getFont() {
        return (Standard14Fonts.FontName) getSelectedItem();
    }

}
