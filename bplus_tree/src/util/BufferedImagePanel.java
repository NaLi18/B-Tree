package util;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

/**
 * A {@code BufferedImagePanel} displays an image buffered in memory.
 * 
 * @author Jeong-Hyon Hwang (jhh@cs.albany.edu)
 * 
 */
public abstract class BufferedImagePanel extends JPanel {

	/**
	 * Automatically generated serialization ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The buffered image.
	 */
	protected BufferedImage bufferedImage = null;

	/**
	 * Constructs a {@code BufferedImagePanel}.
	 */
	public BufferedImagePanel() {
		ComponentListener c = new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				synchronized (this) {
					Dimension d = getSize();
					if (d.width > 0 && d.height > 0)
						bufferedImage = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
					repaint();
				}
			}
		};
		addComponentListener(c);
	}

	/**
	 * Draws on the specified {@code Graphics} context.
	 * 
	 * @param g
	 *            the Graphics context
	 */
	public abstract void draw(Graphics g);

	/**
	 * Repaints this {@code BufferedImagePanel}.
	 */
	public final synchronized void repaint() {
		if (bufferedImage != null) {
			Graphics g = bufferedImage.getGraphics();
			draw(g);
			super.repaint();
		}
	}

	/**
	 * Invoked by Swing to draw components. This method should not be invoked directly. To have this component redrawn,
	 * the {@code repaint()} method should be invoked instead.
	 * 
	 * @param g
	 *            the {@code Graphics} context in which to paint
	 */
	public final synchronized void paint(Graphics g) {
		super.paint(g);
		if (bufferedImage != null) {
			g.drawImage(bufferedImage, 0, 0, null);
		}
	}

}
