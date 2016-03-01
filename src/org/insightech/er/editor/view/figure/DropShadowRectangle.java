package org.insightech.er.editor.view.figure;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.insightech.er.Resources;

public class DropShadowRectangle extends RoundedRectangle {

    public static int SHADOW_INSET = 5;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fillShape(final Graphics graphics) {
        Rectangle f = Rectangle.SINGLETON.setBounds(getBounds());
        final Insets shadowInset = new Insets(0, 0, SHADOW_INSET, SHADOW_INSET);
        f = shrink(f, shadowInset);
        drawShadow(f, graphics);
        final Dimension cornerDimensions = getCornerDimensions();
        graphics.fillRoundRectangle(f, cornerDimensions.width, cornerDimensions.height);
    }

    private Rectangle shrink(final Rectangle bounds, final Insets insets) {
        final Rectangle shrinked = bounds.getCopy();

        shrinked.x += insets.left;
        shrinked.y += insets.top;
        shrinked.width -= insets.getWidth();
        shrinked.height -= insets.getHeight();

        return shrinked;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Insets getInsets() {
        return new Insets(1, 1, SHADOW_INSET + 1, SHADOW_INSET + 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void outlineShape(final Graphics graphics) {
        final Rectangle f = Rectangle.SINGLETON.setBounds(getBounds());
        final Insets shadowInset = new Insets(getLineWidth() / 2, getLineWidth() / 2, getLineWidth() + SHADOW_INSET, getLineWidth() + SHADOW_INSET);
        shrink(f, shadowInset);
        final Dimension cornerDimensions = getCornerDimensions();
        graphics.drawRoundRectangle(f, cornerDimensions.width, cornerDimensions.height);
    }

    private void drawShadow(final Rectangle rectangle, final Graphics graphics) {
        int rgb = 255;
        final int delta = 255 / SHADOW_INSET;

        for (int i = 0; i < SHADOW_INSET - 1; i++) {
            rgb -= delta;
            final Color color = Resources.getColor(new int[] {rgb, rgb, rgb});
            drawShadowLayer(rectangle, graphics, SHADOW_INSET - 1 - i, color);
        }
    }

    private void drawShadowLayer(final Rectangle rectangle, final Graphics graphics, final int offset, final Color color) {

        // Save the state of the graphics object
        graphics.pushState();
        graphics.setLineWidth(0);
        graphics.setBackgroundColor(color);
        final Rectangle shadowLayer = new Rectangle(rectangle);
        shadowLayer.x += offset;
        shadowLayer.y += offset;

        final Dimension cornerDimensions = getCornerDimensions();
        graphics.fillRoundRectangle(shadowLayer, cornerDimensions.width, cornerDimensions.height);
        // Restore the start of the graphics object
        graphics.popState();
    }

}
