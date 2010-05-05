package filebrowser;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import texteditor.TextEditor;

public class MenuBar extends JPanel {

	private static final long serialVersionUID = -5273523003402659881L;
	private FileView _fileView;
	private JButton _upALevel;
	private JButton _refresh;
	private FileBrowserSettings _settings;
	
	public MenuBar(FileBrowserSettings settings) {

		_settings = settings;
		
		this.setSize(new Dimension(FileBrowserConstants.WIDTH, FileBrowserConstants.MENU_BAR_HEIGHT));
		this.setPreferredSize(new Dimension(FileBrowserConstants.WIDTH, FileBrowserConstants.MENU_BAR_HEIGHT));
		this.setBackground(Color.lightGray);

		_upALevel = new JButton("Up A Level");
		_upALevel.addActionListener(new UpALevelListener());
		this.add(_upALevel);
		
		_refresh = new JButton("Refresh");
		_refresh.addActionListener(new RefreshListener());
		this.add(_refresh);
		
		JButton newWindow = new JButton("New Window");
		newWindow.addActionListener(new NewWindowListener());
		this.add(newWindow);
		
		JButton newEditor = new JButton("New Editor");
		newEditor.addActionListener(new NewEditorListener());
		this.add(newEditor);
		
		
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
	
	private class NewWindowListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (_fileView != null) {
				_fileView.newInstance();
			}
		}

	}
	
	private class NewEditorListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			//System.out.println("Opening new TextEditor.");
			new TextEditor(_settings);
		}

	}

}
