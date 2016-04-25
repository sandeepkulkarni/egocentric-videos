package player;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

public class ImageReaderComponent extends JComponent {

	private static final long serialVersionUID = 1L;
	/**
	 * Paint component method.
	 */
	public void paintComponent(Graphics g) {

		// Recover Graphics2D
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(img,0,0,this);
	}

	/**
	 * Sets this img to the new img.
	 * @param newimg The new BufferedImage
	 */
	public void setImg(BufferedImage newimg) {
		this.img = newimg;
	}

	private BufferedImage img;
}