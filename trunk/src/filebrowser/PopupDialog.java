package filebrowser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PopupDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 9169217190073062258L;
	private JButton _button;

	public PopupDialog(JFrame parent, String title, String text) {
		super(parent, title, true);

		if (parent != null) {
			Dimension parentSize = parent.getSize();
			Point p = parent.getLocation();
			setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
		}

		JPanel panePane = new JPanel();
		JLabel pane = new JLabel();
		pane.setText(text);	
		panePane.add(pane);
		getContentPane().add(panePane, BorderLayout.WEST);
	
		JPanel buttonPane = new JPanel();
		_button = new JButton("Okay");
		buttonPane.add(_button);
		_button.addActionListener(this);

		_button.addKeyListener(
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
		setVisible(false);
		dispose();
	}

}