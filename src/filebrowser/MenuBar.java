package filebrowser;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class MenuBar extends JPanel {

	private static final long serialVersionUID = -5273523003402659881L;
	private FileView _fileView;
	private JButton _upALevel;
	private JButton _refresh;

	public MenuBar() {

		this.setSize(new Dimension(FileBrowserConstants.WIDTH, FileBrowserConstants.MENU_BAR_HEIGHT));
		this.setPreferredSize(new Dimension(FileBrowserConstants.WIDTH, FileBrowserConstants.MENU_BAR_HEIGHT));
		this.setBackground(Color.lightGray);

		_upALevel = new JButton("Up A Level");
		_upALevel.addActionListener(new UpALevelListener());
		this.add(_upALevel);
		
		_refresh = new JButton("Refresh");
		_refresh.addActionListener(new RefreshListener());
		this.add(_refresh);
		
		
		// start with buttons disabled
		_upALevel.setEnabled(false);

		this.setLayout(new FlowLayout());

	}

	public void setFileView(FileView view) {
		_fileView = view;
	}

	public void enableButton() {
		_upALevel.setEnabled(true);
	}

	private class UpALevelListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (_fileView != null) {
				boolean enabled = _fileView.upADirectory();
				_upALevel.setEnabled(enabled);
			}
		}

	}
	
	private class RefreshListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (_fileView != null) {
				_fileView.reload();
			}
		}

	}

}
