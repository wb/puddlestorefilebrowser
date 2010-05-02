package filebrowser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CreateRenameDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 9169217190073062258L;
	private CreateRenameDialogCallback _callback;
	private JTextField _textField;
	private FileType _type;
	private FileAction _action;
	private File _file;
	private JButton _button;

	public CreateRenameDialog(JFrame parent, String title, String buttonText, CreateRenameDialogCallback callback, FileType type, FileAction action, File file) {
		super(parent, title, true);
		_callback = callback;
		_type = type;
		_action = action;
		_file = file;

		if (parent != null) {
			Dimension parentSize = parent.getSize();
			Point p = parent.getLocation();
			setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
		}

		JPanel textFieldPane = new JPanel();
		_textField = new JTextField();
		_textField.setColumns(20);

		// set the name in the textfield if there is one
		if (file != null)
			_textField.setText(file.getName());
		textFieldPane.add(_textField);
		getContentPane().add(textFieldPane, BorderLayout.WEST);

		JPanel buttonPane = new JPanel();
		_button = new JButton(buttonText);
		buttonPane.add(_button);
		_button.addActionListener(this);

		_textField.addKeyListener(
				new KeyListener(){
					public void keyPressed(KeyEvent e){
						if (KeyEvent.VK_ENTER == e.getKeyCode()) {
							_button.doClick();
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

		getContentPane().add(buttonPane, BorderLayout.EAST);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		_callback.performAction(_textField.getText(), _type, _action, _file);
		setVisible(false);
		dispose();
	}

}