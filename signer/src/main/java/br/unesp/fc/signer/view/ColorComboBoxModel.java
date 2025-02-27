package br.unesp.fc.signer.view;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ColorComboBoxModel extends AbstractListModel<ColorNames.NamedColor> implements ComboBoxModel<ColorNames.NamedColor> {

    private final boolean hasTransparent;
    private final boolean hasCustom;
    private final List<ColorNames.NamedColor> colors = new ArrayList<>(ColorNames.values().length + 2);
    private ColorNames.NamedColor custom = null;
    private int selected = -1;

    public ColorComboBoxModel(boolean hasTransparent, boolean hasCustom) {
        this.hasTransparent = hasTransparent;
        this.hasCustom = hasCustom;
        if (hasTransparent) {
            colors.add(ColorNames.TRANSPARENT);
        }
        Stream.of(ColorNames.values()).forEach(c -> colors.add(c.getNamedColor()));
    }

    @Override
    public void setSelectedItem(Object item) {
        if (item instanceof ColorNames.NamedColor color) {
            selected = colors.indexOf(item);
            if (selected < 0 && hasCustom) {
                var old = custom;
                custom = color;
                selected = colors.size();
                if (old == null) {
                    fireIntervalAdded(this, colors.size(), colors.size());
                }
                fireContentsChanged(this, -1, -1);
            } else {
                custom = null;
                fireIntervalRemoved(this, colors.size(), colors.size());
                fireContentsChanged(this, -1, -1);
            }
            fireStateChange(this);
        }
    }

    @Override
    public Object getSelectedItem() {
        if (selected == colors.size()) {
            return custom;
        }
        if (selected >= 0) {
            return colors.get(selected);
        }
        return null;
    }

    @Override
    public int getSize() {
        return custom != null ? colors.size() + 1 : colors.size();
    }

    @Override
    public ColorNames.NamedColor getElementAt(int index) {
        if (index == colors.size()) {
            return custom;
        }
        return colors.get(index);
    }

    public boolean hasTransparent() {
        return hasTransparent;
    }

    public boolean hasCustom() {
        return hasCustom;
    }

    public void setColor(Color color) {
        setSelectedItem(new ColorNames.NamedColor("Custom", color));
    }

    public Color getColor() {
        var color = getSelectedItem();
        return color != null ? ((ColorNames.NamedColor) color).color() : null;
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

}
