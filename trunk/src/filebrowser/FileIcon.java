package filebrowser;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class FileIcon extends JPanel {

	private static final long serialVersionUID = -8374748260961167745L;
	private File _file;
	private static final int FONT_SIZE = 12;
	private boolean _selected = false;

	public FileIcon(File file) {

		super();

		// set the file
		_file = file;

		// make sure java doesn't screw with us
		this.setSize(new Dimension(FileBrowserConstants.ICON_SIZE, FileBrowserConstants.ICON_SIZE));
		this.setPreferredSize(new Dimension(FileBrowserConstants.ICON_SIZE, FileBrowserConstants.ICON_SIZE));
		this.setMaximumSize(new Dimension(FileBrowserConstants.ICON_SIZE, FileBrowserConstants.ICON_SIZE));
		this.setMinimumSize(new Dimension(FileBrowserConstants.ICON_SIZE, FileBrowserConstants.ICON_SIZE));

		// compute the size based on what type of file this is
		String sizeText;
		if (_file.isDirectory()) {
			int fileCount = _file.listFiles().length;
			sizeText = "(" + fileCount + " item" + (fileCount != 1 ? "s" : "") + ")";
		} else {
			sizeText = "(" + _file.length() + " bytes)";
		}

		// make it transparent and give it a tool tip
		this.setOpaque(false);
		this.setToolTipText(file.getName() + " " + sizeText);

	}

	public File getFile() {
		return _file;
	}

	@Override
	protected void paintComponent(Graphics g) {

		// draw selected rectangle if this is selected
		if (_selected) {
			Graphics2D graphics2 = (Graphics2D) g;
			graphics2.setColor(new Color(121, 173, 232, 200));
			RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(10, 83, 80, 15, 10, 10);
			graphics2.fill(roundedRectangle);
			graphics2.setColor(new Color(74, 122, 176, 255));
			graphics2.draw(roundedRectangle);
		}

		// set the color to black
		g.setColor(Color.black);

		// place the text
		Font font = new Font("Default", Font.PLAIN, FONT_SIZE);

		FontMetrics fm = g.getFontMetrics(font);
		String name = _file.getName();

		// get the width of the name
		double nameWidth = fm.stringWidth(name);

		// if it wont fit, use ellipses
		if (nameWidth > 70) {

			// get the approximate number of characters to cut off
			int characterCount = name.length();
			double percentage = 70.0 / nameWidth;
			int shortenedCount = (int) (percentage * ((double) characterCount)) - 2;
			int halfShortenedCount = shortenedCount / 2;
			name = name.substring(0, halfShortenedCount) + "..." + name.substring(characterCount - halfShortenedCount, characterCount);

			// recalculate
			nameWidth = fm.stringWidth(name);
		}

		int offsetX = (int) ((100 - nameWidth) / 2);
		g.setFont(font);
		g.drawString(name, offsetX, 95);

		if (_file.isDirectory()) {
			try {
				BufferedImage image = ImageIO.read(getClass().getResourceAsStream("/filebrowser/images/folder-small.png"));
				g.drawImage(image, 10, 10, 80, 75, this);
			} catch (IOException e) {
				System.err.println("Couldn't draw folder icon.");
			} catch (Exception e) {
				System.err.println("Couldn't draw folder icon.");
			}
		} else {
			try {
				BufferedImage image = ImageIO.read(getClass().getResourceAsStream("/filebrowser/images/file-small.png"));
				g.drawImage(image, 10, 10, 80, 75, this);
			} catch (IOException e) {
				System.err.println("Couldn't draw file icon.");
			} catch (Exception e) {
				System.err.println("Couldn't draw file icon.");
			}
		}

		super.paintComponent(g);
	}

	public void setSelected(boolean selected) {
		_selected = selected;
	}

	public boolean isSelected() {
		return _selected;
	}

	public void toggleSelected() {
		_selected = !_selected;
	}
}
