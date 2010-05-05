package filebrowser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JScrollPane;


public class FileBrowser extends JFrame {

	private static final long serialVersionUID = 4838120751269642102L;
	private FileBrowserSettings _settings;
	public static int instanceCount = 0;
	
	public FileBrowser(FileBrowserSettings settings) {

		FileBrowser.instanceCount++;
		
		// save the settings so that we can open a new instance
		_settings = settings;

		BorderLayout layout = new BorderLayout();
		this.setLayout(layout);

		MenuBar menuBar = new MenuBar(settings);
		this.add(menuBar, BorderLayout.NORTH);

		this.setTitle("PuddleStore FileBrowser");
		Footer footer = new Footer();
		this.add(footer, BorderLayout.SOUTH);

		// load the root file
		File file = settings.getRootFile();

		// create a new file view at the root directory
		FileView fileView = new FileView(file, menuBar, footer, settings, this);

		// put it all in a scrollPane so that we can see everything
		JScrollPane scrollPane = new JScrollPane(fileView);
		scrollPane.setSize(new Dimension(FileBrowserConstants.WIDTH, FileBrowserConstants.SCROLL_PANE_HEIGHT));
		scrollPane.setPreferredSize(new Dimension(FileBrowserConstants.WIDTH, FileBrowserConstants.SCROLL_PANE_HEIGHT));
		scrollPane.setBackground(Color.white);

		// set our size to the same as the scroll pane
		this.setSize(new Dimension(FileBrowserConstants.WIDTH, FileBrowserConstants.SCROLL_PANE_HEIGHT));
		this.setPreferredSize(new Dimension(FileBrowserConstants.WIDTH, FileBrowserConstants.SCROLL_PANE_HEIGHT));
		this.setMinimumSize(new Dimension(FileBrowserConstants.WIDTH, FileBrowserConstants.SCROLL_PANE_HEIGHT));

		// add the scroll pane
		this.add(scrollPane, BorderLayout.CENTER);

		// don't let the user resize (it messes up the grid layout)
		this.setResizable(true);

		// let there be light!
		this.setVisible(true);

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				FileBrowser.instanceCount--;
				if (FileBrowser.instanceCount == 0) {
					System.exit(0);
				}
					
			}
		});
	
	}

	public void newInstance() {
		new FileBrowser(_settings);
	}

}
