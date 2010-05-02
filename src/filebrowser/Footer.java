package filebrowser;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Footer extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextArea _pathPane;
	private static final String DIR_TEXT = "Current Directory: ";
	
	public Footer() {
		this.setSize(new Dimension(FileBrowserConstants.WIDTH, FileBrowserConstants.FOOTER_HEIGHT));
		this.setPreferredSize(new Dimension(FileBrowserConstants.WIDTH, FileBrowserConstants.FOOTER_HEIGHT));
		this.setBackground(Color.lightGray);
		_pathPane = new JTextArea();
		_pathPane.setFont(new Font("Default", Font.PLAIN, 11));
		_pathPane.setEditable(false);
		this.add(_pathPane);
	}

	public void setPath(String path) {
		if (!path.endsWith("/"))
			path += "/";
		_pathPane.setText(DIR_TEXT + path);
		_pathPane.setOpaque(false);
	}
}
