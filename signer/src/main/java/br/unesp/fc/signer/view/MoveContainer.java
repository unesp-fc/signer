package br.unesp.fc.signer.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.AbstractBorder;

public class MoveContainer extends JPanel {

    private JComponent component;
    private final MoveBorder moveBorder = new MoveBorder();
    private final MouseMove mouseMove = new MouseMove();
    private Rectangle constraints;

    public MoveContainer(JComponent component) {
        this.component = component;
        this.setLayout(new GridBagLayout());
        if (component != null) {
            this.add(component, new GridBagConstraints());
        }
        this.setBorder(moveBorder);
        this.addMouseListener(mouseMove);
        this.addMouseMotionListener(mouseMove);
    }

    public JComponent getComponent() {
        return component;
    }

    public void setComponent(JComponent component) {
        if (this.component != null) {
            this.remove(this.component);
        }
        this.component = component;
        if (component != null) {
            this.add(component, new GridBagConstraints());
        }
    }

    public void setConstraints(Rectangle constraints) {
        this.constraints = constraints;
    }

    protected Rectangle getConstraints() {
        if (constraints == null) {
            return new Rectangle(0, 0, getParent().getWidth(), getParent().getHeight());
        }
        return constraints;
    }

    public class MouseMove extends MouseAdapter {

        private final Cursor MOVE = new Cursor(Cursor.MOVE_CURSOR);
        private final Cursor NORTH = new Cursor(Cursor.N_RESIZE_CURSOR);
        private final Cursor SOUTH = new Cursor(Cursor.S_RESIZE_CURSOR);
        private final Cursor WEST = new Cursor(Cursor.W_RESIZE_CURSOR);
        private final Cursor EAST = new Cursor(Cursor.E_RESIZE_CURSOR);
        private final Cursor NORTH_EAST = new Cursor(Cursor.NE_RESIZE_CURSOR);
        private final Cursor NORTH_WEAST = new Cursor(Cursor.NW_RESIZE_CURSOR);
        private final Cursor SOUTH_EAST = new Cursor(Cursor.SE_RESIZE_CURSOR);
        private final Cursor SOUTH_WEAST = new Cursor(Cursor.SW_RESIZE_CURSOR);

        @Override
        public void mouseMoved(MouseEvent e) {
            var p = e.getPoint();
            if (moveBorder.getTop().contains(p)) {
                setCursor(NORTH);
            } else if (moveBorder.getBottom().contains(p)) {
                setCursor(SOUTH);
            } else if (moveBorder.getLeft().contains(p)) {
                setCursor(WEST);
            } else if (moveBorder.getRight().contains(p)) {
                setCursor(EAST);
            } else if (moveBorder.getTopLeft().contains(p)) {
                setCursor(NORTH_WEAST);
            } else if (moveBorder.getTopRight().contains(p)) {
                setCursor(NORTH_EAST);
            } else if (moveBorder.getBottomLeft().contains(p)) {
                setCursor(SOUTH_WEAST);
            } else if (moveBorder.getBottomRight().contains(p)) {
                setCursor(SOUTH_EAST);
            } else {
                setCursor(MOVE);
            }
        }

        private Cursor cursor;
        private Point point;
        private Point location;
        private Dimension size;
        private Dimension preferredSize;
        private Rectangle constraints;

        @Override
        public void mousePressed(MouseEvent e) {
            point = e.getPoint();
            cursor = getCursor();
            size = getSize();
            preferredSize = getPreferredSize();
            location = getLocation();
            constraints = getConstraints();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (point == null) {
                return;
            }
            int dx = e.getX() - point.x;
            int dy = e.getY() - point.y;
            var p = getLocation();
            if (cursor == MOVE) {
                p.translate(dx, dy);
                if (p.x < constraints.x) {
                    p.x = constraints.x;
                }
                if (p.y < constraints.y) {
                    p.y = constraints.y;
                }
                if (p.x + size.width > constraints.width) {
                    p.x = constraints.width - size.width;
                }
                if (p.y + size.height > constraints.height) {
                    p.y = constraints.height - size.height;
                }
                setLocation(p);
            } else {
                var newSize = new Dimension(size);
                boolean locationChanged = false;
                if (cursor == EAST || cursor == SOUTH_EAST || cursor == NORTH_EAST) {
                    int newWidth = size.width + dx;
                    if (newWidth + p.x > constraints.width) {
                        newWidth = constraints.width - p.x;
                    }
                    newSize.width = newWidth > preferredSize.width ? newWidth : preferredSize.width;
                }
                if (cursor == SOUTH || cursor == SOUTH_EAST || cursor == SOUTH_WEAST) {
                    int newHeight = size.height + dy;
                    if (newHeight + p.y > constraints.height) {
                        newHeight = constraints.height - p.y;
                    }
                    newSize.height = newHeight > preferredSize.height ? newHeight : preferredSize.height;
                }
                if (cursor == NORTH || cursor == NORTH_WEAST || cursor == NORTH_EAST) {
                    p.translate(0, dy);
                    if (p.y < constraints.y) {
                        p.y = constraints.y;
                    }
                    int newHeight = size.height + location.y - p.y;
                    if (newHeight > preferredSize.height) {
                        newSize.height = newHeight;
                    } else {
                        p.y = location.y + size.height - preferredSize.height;
                        newSize.height = preferredSize.height;
                    }
                    locationChanged = true;
                }
                if (cursor == WEST || cursor == NORTH_WEAST || cursor == SOUTH_WEAST) {
                    p.translate(dx, 0);
                    if (p.x < constraints.x) {
                        p.x = constraints.x;
                    }
                    int newWidth = size.width + location.x - p.x;
                    if (newWidth > preferredSize.width) {
                        newSize.width = newWidth;
                    } else {
                        p.x = location.x + size.width - preferredSize.width;
                        newSize.width = preferredSize.width;
                    }
                    locationChanged = true;
                }
                if (locationChanged) {
                    setLocation(p);
                }
                setSize(newSize);
                doLayout();
            }
        }

    }

    public class MoveBorder extends AbstractBorder {

        private static final int RECTANGLE_SIZE = 5;
        private static final int RECTANGLE_SIZE_MID = RECTANGLE_SIZE / 2;

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            var color = getForeground();
            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 128));
            g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{1}, 0));
            g2.drawRect(x + RECTANGLE_SIZE_MID, y + RECTANGLE_SIZE_MID, width - RECTANGLE_SIZE, height - RECTANGLE_SIZE);
            Rectangle rect;
            // top left
            rect = getTopLeft();
            g2.fillRect(rect.x, rect.y, rect.width, rect.height);
            // top
            rect = getTop();
            g2.fillRect(rect.x, rect.y, rect.width, rect.height);
            // top right
            rect = getTopRight();
            g2.fillRect(rect.x, rect.y, rect.width, rect.height);
            // left
            rect = getLeft();
            g2.fillRect(rect.x, rect.y, rect.width, rect.height);
            // right
            rect = getRight();
            g2.fillRect(rect.x, rect.y, rect.width, rect.height);
            // bottom left
            rect = getBottomLeft();
            g2.fillRect(rect.x, rect.y, rect.width, rect.height);
            // bottom
            rect = getBottom();
            g2.fillRect(rect.x, rect.y, rect.width, rect.height);
            // bottom right
            rect = getBottomRight();
            g2.fillRect(rect.x, rect.y, rect.width, rect.height);
        }

        public Rectangle getTopLeft() {
            return new Rectangle(0, 0, RECTANGLE_SIZE, RECTANGLE_SIZE);
        }

        public Rectangle getTopRight() {
            int width = MoveContainer.this.getWidth();
            return new Rectangle(width - RECTANGLE_SIZE, 0, RECTANGLE_SIZE, RECTANGLE_SIZE);
        }

        public Rectangle getBottomLeft() {
            int height = MoveContainer.this.getHeight();
            return new Rectangle(0, height - RECTANGLE_SIZE, RECTANGLE_SIZE, RECTANGLE_SIZE);
        }

        public Rectangle getBottomRight() {
            int width = MoveContainer.this.getWidth();
            int height = MoveContainer.this.getHeight();
            return new Rectangle(width - RECTANGLE_SIZE, height - RECTANGLE_SIZE, RECTANGLE_SIZE, RECTANGLE_SIZE);
        }

        public Rectangle getTop() {
            int width = MoveContainer.this.getWidth();
            return new Rectangle(width / 2 - RECTANGLE_SIZE_MID, 0, RECTANGLE_SIZE, RECTANGLE_SIZE);
        }

        public Rectangle getRight() {
            int width = MoveContainer.this.getWidth();
            int height = MoveContainer.this.getHeight();
            return new Rectangle(width - RECTANGLE_SIZE, height / 2 - RECTANGLE_SIZE_MID, RECTANGLE_SIZE, RECTANGLE_SIZE);
        }

        public Rectangle getBottom() {
            int width = MoveContainer.this.getWidth();
            int height = MoveContainer.this.getHeight();
            return new Rectangle(width / 2 - RECTANGLE_SIZE_MID, height - RECTANGLE_SIZE, RECTANGLE_SIZE, RECTANGLE_SIZE);
        }

        public Rectangle getLeft() {
            int height = MoveContainer.this.getHeight();
            return new Rectangle(0, height / 2 - RECTANGLE_SIZE_MID, RECTANGLE_SIZE, RECTANGLE_SIZE);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(0, 0, 0, 0);
            return insets;
        }

    }

}
