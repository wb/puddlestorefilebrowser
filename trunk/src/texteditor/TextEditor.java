package texteditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import filebrowser.FileBrowserSettings;

public class TextEditor extends JFrame {

	private static final long serialVersionUID = 7139460774094010717L;
	private static final String EDITOR_TITLE = "PuddleStore TextEditor - ";
	private JTextArea _textArea;
	private File _openFile = null;
	private FileBrowserSettings _settings;

	public TextEditor(FileBrowserSettings settings) {

		_settings = settings;

		// create a JTextArea to do the editing in
		_textArea = new JTextArea();
		_textArea.setWrapStyleWord(true);
		_textArea.setLineWrap(true);

		// put it all in a scrollPane so that we can see everything
		JScrollPane scrollPane = new JScrollPane(_textArea);
		scrollPane.setSize(new Dimension(TextEditorConstants.WIDTH, TextEditorConstants.HEIGHT));
		scrollPane.setPreferredSize(new Dimension(TextEditorConstants.WIDTH, TextEditorConstants.HEIGHT));
		scrollPane.setBackground(Color.white);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		// set our size to the same as the scroll pane
		this.setSize(new Dimension(TextEditorConstants.WIDTH, TextEditorConstants.HEIGHT));
		this.setPreferredSize(new Dimension(TextEditorConstants.WIDTH, TextEditorConstants.HEIGHT));
		this.setMinimumSize(new Dimension(TextEditorConstants.WIDTH, TextEditorConstants.HEIGHT));
		this.setBackground(Color.white);

		
		// create actions for the submenu for locking
		Action readLockAction = new ReadLockAction();
		Action writeLockAction = new WriteLockAction();
		Action renewLockAction = new RenewLockAction();
		//renewLockAction.setEnabled(false);
		Action releaseLockAction = new ReleaseLockAction();
		//releaseLockAction.setEnabled(false);
		
		// create the locking submenu
		JMenuBar lockBar = new JMenuBar();
		JMenu lockMenu = lockBar.add(new JMenu("Locking"));
		lockMenu.add(readLockAction);
		lockMenu.add(writeLockAction);
		lockMenu.add(renewLockAction);
		lockMenu.add(releaseLockAction);
		
		// create actions for the menu bar
		Action openAction = new OpenAction();
		Action saveAction = new SaveAction();
		Action appendAction = new AppendAction();
		Action exitAction = new ExitAction();
		Action reloadAction = new ReloadAction();

		
		// create the menu bar
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = menuBar.add(new JMenu("File"));
		fileMenu.setMnemonic('F');
		fileMenu.add(openAction);
		fileMenu.add(saveAction);
		fileMenu.add(appendAction);
		fileMenu.addSeparator();
		fileMenu.add(reloadAction);
		fileMenu.addSeparator();
		fileMenu.add(lockMenu);
		fileMenu.addSeparator();
		fileMenu.add(exitAction);
		this.setJMenuBar(menuBar);

		// add the scroll pane
		this.add(scrollPane, BorderLayout.CENTER);

		// don't let the user resize (it messes up the grid layout)
		this.setResizable(true);

		// let there be light!
		this.setVisible(true);


		_textArea.addKeyListener(
				new KeyListener(){
					public void keyPressed(KeyEvent e){
						if (e.getKeyCode() == 83 && e.getModifiers() == 2) {
							TextEditor.this.saveFile();
						}
						else if ((e.getKeyCode() == 87 || e.getKeyCode() == 81) && e.getModifiers() == 2) {
							System.out.println("Bye bye!");
							TextEditor.this.dispose();
						}
						else if (e.getKeyCode() == 82 && e.getModifiers() == 2) {
							TextEditor.this.openFile(_openFile);
						}
					}

					@Override
					public void keyReleased(KeyEvent e) {
						// TODO Auto-generated method stub

					}

					@Override
					public void keyTyped(KeyEvent e) {
						// TODO Auto-generated method stub

					}
				}
		);

	}

	public boolean openFile(File file) {

		// if the file is null, return false
		if (file == null) 
			return false;

		try {

			InputStream inputStream = _settings.getInputStream(file);
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader reader = new BufferedReader(inputStreamReader);

			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line).append("\n");
			}

			reader.close();
			inputStreamReader.close();
			inputStream.close();

			_textArea.setText(builder.toString());
			_textArea.setCaretPosition(0);

		} catch (FileNotFoundException e) {
			System.err.println("File not found.");
			return false;
		} catch (IOException e) {
			System.err.println("Error reading from file.");
			return false;
		}

		// save the open file
		_openFile = file;

		// set the window title
		this.setTitle(EDITOR_TITLE + file.getAbsolutePath());

		// return true
		return true;
	}

	private class OpenAction extends AbstractAction {
		private static final long serialVersionUID = -630859446328983611L;
		public OpenAction() {
			super("Open");
		}
		@Override
		public void actionPerformed(ActionEvent e) {

			JFileChooser fileChooser = new JFileChooser(_settings.getRootFile(), new FileBrowserFileSystemView(_settings));
			fileChooser.setDialogTitle("Select a File to Open");
			TextEditor.this.getContentPane().add(fileChooser);
			fileChooser.setVisible(true);
			int returnVal = fileChooser.showOpenDialog(TextEditor.this);

			if(returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				System.out.println(fileChooser.getCurrentDirectory().getPath());
				System.out.println(file.getPath());
				System.out.println("Parent: " + file.getParent());

				//TextEditor.this.openFile(file);
			}

		}
	}

	private class SaveAction extends AbstractAction {
		private static final long serialVersionUID = 5290060732732764008L;
		public SaveAction() {
			super("Save");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			TextEditor.this.saveFile();
		}
	}

	private void saveFile() {
		if (_openFile != null) {
			try {

				// check to make sure the file exists
				if (!_openFile.exists()) {
					System.out.println("File did not exist.  Trying to create it.");
					if (!_openFile.createNewFile()) {
						System.err.println("File does not exist and it could not be created. File not saved");
						return;
					}
				}

				OutputStream outputStream = _settings.getOutputStream(_openFile, false);
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
				BufferedWriter writer = new BufferedWriter(outputStreamWriter);

				writer.write(_textArea.getText());
				writer.close();
				outputStreamWriter.close();
				outputStream.close();
/*
				String oldTitle = TextEditor.this.getTitle();

				TextEditor.this.setTitle(EDITOR_TITLE + "Saved!");

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {

				}
				
				TextEditor.this.setTitle(oldTitle);*/

				//System.out.println("Wrote: " + _textArea.getText());
			} catch (FileNotFoundException e1) {
				System.err.println("File not found.");
			} catch (IOException e2) {
				System.err.println("Error writing to file.");
			}
		} else {
			JFileChooser fileChooser = new JFileChooser(_settings.getRootFile(), new FileBrowserFileSystemView(_settings));
			fileChooser.setDialogTitle("Select a File to Open");
			fileChooser.setApproveButtonText("Save");
			TextEditor.this.getContentPane().add(fileChooser);
			fileChooser.setVisible(true);
			int returnVal = fileChooser.showOpenDialog(TextEditor.this);

			if(returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				System.out.println(fileChooser.getCurrentDirectory().getAbsolutePath());
				if (!file.exists()) {
					try {
						file.createNewFile();
					} catch (IOException e1) {
						System.err.println("Couldn't save to file!");
						return;
					}
				}
				//System.out.println("Saving to " + file.getAbsolutePath());

				_openFile = file;
				TextEditor.this.setTitle(EDITOR_TITLE + file.getAbsolutePath());

				try {
					OutputStream outputStream = _settings.getOutputStream(_openFile, false);
					OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
					BufferedWriter writer = new BufferedWriter(outputStreamWriter);

					writer.write(_textArea.getText());
					writer.close();
					outputStreamWriter.close();
					outputStream.close();
					//System.out.println("Wrote: " + _textArea.getText());
				} catch (FileNotFoundException e1) {
					System.err.println("File not found.");
				} catch (IOException e2) {
					System.err.println("Error writing to file.");
				}

			}
		}
	}
	private class ExitAction extends AbstractAction {
		private static final long serialVersionUID = -6521806944077479342L;
		public ExitAction() {
			super("Close");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			_openFile = null;
			TextEditor.this.dispose();
		}
	}

	private class ReloadAction extends AbstractAction {
		private static final long serialVersionUID = -6521806944077479342L;
		public ReloadAction() {
			super("Reload");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			if (_openFile != null) {
				TextEditor.this.openFile(_openFile);
			}
		}
	}

	private class AppendAction extends AbstractAction {
		private static final long serialVersionUID = -6521806944077479342L;
		public AppendAction() {
			super("Append");
		}
		@Override
		public void actionPerformed(ActionEvent e) {

			if (_openFile != null) {
				try {

					// check to make sure the file exists
					if (!_openFile.exists()) {
						System.out.println("File did not exist.  Trying to create it.");
						if (!_openFile.createNewFile()) {
							System.err.println("File does not exist and it could not be created. File not saved");
							return;
						}
					}

					OutputStream outputStream = _settings.getOutputStream(_openFile, true);
					OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
					BufferedWriter writer = new BufferedWriter(outputStreamWriter);

					writer.write(_textArea.getText());
					writer.close();
					outputStreamWriter.close();
					outputStream.close();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e3) {}
					TextEditor.this.openFile(_openFile);

					//System.out.println("Wrote: " + _textArea.getText());
				} catch (FileNotFoundException e1) {
					System.err.println("File not found.");
				} catch (IOException e2) {
					System.err.println("Error writing to file.");
				}
			} else {
				JFileChooser fileChooser = new JFileChooser(_settings.getRootFile(), new FileBrowserFileSystemView(_settings));
				fileChooser.setDialogTitle("Select a File to Open");
				fileChooser.setApproveButtonText("Save");
				TextEditor.this.getContentPane().add(fileChooser);
				fileChooser.setVisible(true);
				int returnVal = fileChooser.showOpenDialog(TextEditor.this);

				if(returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					System.out.println(fileChooser.getCurrentDirectory().getAbsolutePath());
					if (!file.exists()) {
						try {
							file.createNewFile();
						} catch (IOException e1) {
							System.err.println("Couldn't save to file!");
							return;
						}
					}
					//System.out.println("Saving to " + file.getAbsolutePath());

					_openFile = file;
					TextEditor.this.setTitle(EDITOR_TITLE + file.getAbsolutePath());

					try {
						OutputStream outputStream = _settings.getOutputStream(_openFile, true);
						OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
						BufferedWriter writer = new BufferedWriter(outputStreamWriter);

						writer.write(_textArea.getText());
						writer.close();
						outputStreamWriter.close();
						outputStream.close();
						try {
							Thread.sleep(100);
						} catch (InterruptedException e3) {}
						TextEditor.this.openFile(_openFile);
						//System.out.println("Wrote: " + _textArea.getText());
					} catch (FileNotFoundException e1) {
						System.err.println("File not found.");
					} catch (IOException e2) {
						System.err.println("Error writing to file.");
					}

				}
			}
		}
	}
	
	Action releaseLockAction = new ReleaseLockAction();
	
	private class WriteLockAction extends AbstractAction {
		private static final long serialVersionUID = -6521806944077479342L;
		public WriteLockAction() {
			super("Get Write Lock");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Getting write lock.");
		}
	}
	
	private class ReadLockAction extends AbstractAction {
		private static final long serialVersionUID = -6521806944077479342L;
		public ReadLockAction() {
			super("Get Read Lock");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Getting read lock.");
		}
	}
	
	private class RenewLockAction extends AbstractAction {
		private static final long serialVersionUID = -6521806944077479342L;
		public RenewLockAction() {
			super("Renew Lock");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Renewing lock.");
		}
	}
	
	private class ReleaseLockAction extends AbstractAction {
		private static final long serialVersionUID = -6521806944077479342L;
		public ReleaseLockAction() {
			super("Release Lock");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Release lock.");
		}
	}
	
	

}
