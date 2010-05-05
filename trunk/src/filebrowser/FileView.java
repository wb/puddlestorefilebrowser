package filebrowser;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import texteditor.TextEditor;

public class FileView extends JPanel implements CreateRenameDialogCallback {

	private static final long serialVersionUID = 502295914956195639L;
	private File _currentDirectory;
	private ClickHandler _clickHandler;
	private MenuBar _menuBar;
	private Footer _footer;
	private GridLayout _gridLayout;
	private List<FileIcon> _currentFileIcons;
	private FileBrowserSettings _settings;
	private FileBrowser _frame;

	public FileView(File root, MenuBar menuBar, Footer footer, FileBrowserSettings settings, FileBrowser frame) {

		// store the frame
		_frame = frame;

		// store the settings
		
		_settings = settings;

		// hold icons here (for selection, de-selection)
		_currentFileIcons = new ArrayList<FileIcon>();

		// set layout to grid layout
		_gridLayout = new GridLayout(0, 4);
		this.setLayout(_gridLayout);
		this.setBackground(Color.white);
		this.setSize(FileBrowserConstants.WIDTH, this.getHeight());

		// store hte menu bar and footer
		_menuBar = menuBar;
		_footer = footer;
		menuBar.setFileView(this);

		// set up the click handler
		_clickHandler = new ClickHandler();
		this.addMouseListener(_clickHandler);

		// load the root directory
		this.loadDirectory(root);

	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		int extraSpace = this.getWidth() - _gridLayout.getColumns() * FileBrowserConstants.ICON_SIZE;
		int border = extraSpace / 8;

		// if the border gets too small, then reduce columns by 1
		if (border < 20) {
			_gridLayout.setColumns(_gridLayout.getColumns() - 1);
		} else if (border > 50) {
			_gridLayout.setColumns(_gridLayout.getColumns() + 1);
		}

	}

	public boolean reload() {
		return this.loadDirectory(_currentDirectory);
	}

	public boolean loadDirectory(File file) {

		if (file == null)
			return false;

		// only load directories into the panel
		if (file.isDirectory()) {

			// set the full path in the footer
			_footer.setPath(file.getAbsolutePath());

			// clear the previous contents
			this.removeAll();
			this.validate();
			this.repaint();

			// clear the list of files
			_currentFileIcons.clear();

			// get the list of files for this directory
			File[] files = file.listFiles();

			// loop through them and display them on the screen
			for (File currentFile : files) {

				// ignore hidden files
				if (currentFile.isHidden())
					continue;

				FileIcon icon = new FileIcon(currentFile);
				icon.addMouseListener(_clickHandler);

				JPanel holder = new JPanel();
				holder.setOpaque(false);
				holder.add(icon);
				this.add(holder);

				_currentFileIcons.add(icon);

			}

			// update the current directory
			_currentDirectory = file;

			// check to see if we should re-enable the button
			if (_currentDirectory.getParent() != null) {
				_menuBar.enableButton();
			}

		} else {
			// create a new TextEditor and open this file
			TextEditor editor = new TextEditor(_settings);
			editor.openFile(file);
		}

		this.validate();

		return true;

	}

	public boolean canGoUpDirectory() {
		if (_currentDirectory.getParent() == null)
			return false;
		else
			return true;
	}

	/**
	 * Returns false if there are no more levels to go up after this change has
	 * been made. True otherwise.
	 * 
	 * @return
	 */
	public boolean upADirectory() {
		File parent = _currentDirectory.getParentFile();
		this.loadDirectory(parent);

		return (_currentDirectory.getParent() != null);
	}

	private class ClickHandler implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {

			// left click
			if (e.getButton() == MouseEvent.BUTTON1) {
				for (FileIcon icon : _currentFileIcons) {
					icon.setSelected(false);
				}

				FileView.this.repaint();

				if (e.getSource() instanceof FileIcon) {
					FileIcon sourceFolder = (FileIcon) e.getSource();

					// we want double clicks
					if (e.getClickCount() == 2) {

						FileView.this.loadDirectory(sourceFolder.getFile());
					} else if (e.getClickCount() == 1) {
						sourceFolder.setSelected(true);
						FileView.this.repaint();
					}
				}
			}

			// right click
			else if (e.getButton() == MouseEvent.BUTTON3) {

				// if they click in the white space
				if (e.getSource() instanceof FileView) {
					JPopupMenu menu = new JPopupMenu();
					menu.add(new JMenuItem(new NewFileAction()));
					menu.add(new JMenuItem(new NewDirectoryAction()));
					menu.show(e.getComponent(), e.getX(), e.getY());
				} else if (e.getSource() instanceof FileIcon) {

					FileIcon source = (FileIcon) e.getSource();
					File file = source.getFile();

					JPopupMenu menu = new JPopupMenu();

					if (file.isDirectory()) {
						menu.add(new JMenuItem(new RenameFileAction("Rename Directory", FileType.DIRECTORY, file)));
						menu.add(new JMenuItem(new DeleteFileAction(file)));
					}
					else if (file.isFile()) {
						menu.add(new JMenuItem(new RenameFileAction("Rename File", FileType.FILE, file)));
						menu.add(new JMenuItem(new DeleteFileAction(file)));
					}
					menu.show(e.getComponent(), e.getX(), e.getY());
				}
			}

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	private class NewFileAction extends AbstractAction {

		private static final long serialVersionUID = -6521806944077479342L;

		public NewFileAction() {
			super("New File");
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			new CreateRenameDialog(_frame, "Create File", "Create", FileView.this, FileType.FILE, FileAction.CREATE, null);
		}

	}

	private class NewDirectoryAction extends AbstractAction {

		private static final long serialVersionUID = -6521806944077479342L;

		public NewDirectoryAction() {
			super("New Directory");
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			new CreateRenameDialog(_frame, "Create Directory", "Create", FileView.this, FileType.DIRECTORY, FileAction.CREATE, null);

		}

	}

	private class RenameFileAction extends AbstractAction {

		private static final long serialVersionUID = -6521806944077479342L;
		private String _text;
		private FileType _type;
		private File _file;

		public RenameFileAction(String text, FileType type, File file) {
			super("Rename");
			_text = text;
			_type = type;
			_file = file;
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			new CreateRenameDialog(_frame, _text, "Rename", FileView.this, _type, FileAction.RENAME, _file);
		}

	}

	private class DeleteFileAction extends AbstractAction {

		private static final long serialVersionUID = -6521806944077479342L;
		private File _file;

		public DeleteFileAction(File file) {
			super("Delete");
			_file = file;
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			boolean deleted = _file.delete();

			if (_file.isDirectory() && !deleted) {
				new PopupDialog(_frame, "Error", "Directory could not be deleted.  It may not be empty.");
			}
			else if (!deleted) {
				new PopupDialog(_frame, "Error", "File could not be deleted.");
			}

			FileView.this.reload();
		}

	}


	@Override
	public void performAction(String text, FileType type, FileAction action, File editedFile) {

		String absolutePath = _currentDirectory.getAbsolutePath();
		String path = "";
		if (absolutePath.endsWith("/"))
			path = _currentDirectory.getAbsolutePath() + text;
		else
			path = _currentDirectory.getAbsolutePath() + "/" + text;

		File file = _settings.createFileFromPath(path);

		if (text.isEmpty()) {
			// TODO: popup here
			new PopupDialog(_frame, "Error", "Enter a name, you moron!");
		} else if (type == FileType.FILE && action == FileAction.CREATE) {

			try {
				if (file.createNewFile()) {
					//System.out.println("Created file: " + path);
					this.reload();
				}
				else {
					new PopupDialog(_frame, "Error", "Could not create file with path \"" + path + "\"!");
				}
			} catch (IOException e) {
				new PopupDialog(_frame, "Error", "Could not create file with path \"" + path + "\"!");
			}
		} else if (type == FileType.DIRECTORY && action == FileAction.CREATE) {

			if (file.mkdir()) {
				//System.out.println("Created directory: " + path);
				this.reload();
			}
			else {
				new PopupDialog(_frame, "Error", "Could not create directory with path \"" + path + "\"!");
				
			}
		} else if (type == FileType.FILE && action == FileAction.RENAME && editedFile != null) {

			if (!editedFile.renameTo(file)) {
				new PopupDialog(_frame, "Error", "Could not rename file: \"" + file.getAbsolutePath() + "\"!");
			}
			else
				this.reload();

		} else if (type == FileType.DIRECTORY && action == FileAction.RENAME && editedFile != null) {
			if (!editedFile.renameTo(file)) {
				new PopupDialog(_frame, "Error", "Could not rename directory: \"" + file.getAbsolutePath() + "\"!");
			}
			else
				this.reload();
		}

	}
	
	public void newInstance() {
		_frame.newInstance();
	}
}
