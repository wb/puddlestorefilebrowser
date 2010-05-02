package texteditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
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
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import filebrowser.FileBrowserSettings;

public class TextEditor extends JFrame {

	private static final long serialVersionUID = 7139460774094010717L;
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

		// create actions for the menu bar
		Action openAction = new OpenAction();
		openAction.setEnabled(false);

		Action saveAction = new SaveAction();

		Action exitAction = new ExitAction();

		// create the menu bar
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = menuBar.add(new JMenu("File"));
		fileMenu.setMnemonic('F');
		fileMenu.add(openAction);
		fileMenu.add(saveAction);
		fileMenu.addSeparator();
		fileMenu.add(exitAction);
		this.setJMenuBar(menuBar);

		// add the scroll pane
		this.add(scrollPane, BorderLayout.CENTER);

		// don't let the user resize (it messes up the grid layout)
		this.setResizable(true);

		// let there be light!
		this.setVisible(true);

	}

	public boolean openFile(File file) {
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
		this.setTitle(file.getName());

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

			System.err.println("Open is not supported yet.");

		}

	}

	private class SaveAction extends AbstractAction {

		private static final long serialVersionUID = 5290060732732764008L;

		public SaveAction() {
			super("Save");
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			if (_openFile != null) {

				try {
					OutputStream outputStream = _settings.getOutputStream(_openFile);
					OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
					BufferedWriter writer = new BufferedWriter(outputStreamWriter);
					writer.write(_textArea.getText());
					writer.close();
					outputStreamWriter.close();
					outputStream.close();
					System.out.println("Wrote: " + _textArea.getText());

				} catch (FileNotFoundException e1) {
					System.err.println("File not found.");
				} catch (IOException e2) {
					System.err.println("Error writing to file.");
				}

			} else {
				System.err.println("Saving blank file is not supported yet.");
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

}
