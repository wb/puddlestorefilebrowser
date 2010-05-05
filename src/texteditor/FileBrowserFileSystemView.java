package texteditor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;

import filebrowser.FileBrowserSettings;

public class FileBrowserFileSystemView extends FileSystemView {

	private FileBrowserSettings _settings;
	
	public FileBrowserFileSystemView(FileBrowserSettings settings) {
		_settings = settings;
	}
	
	@Override
	public File createFileObject(File dir, String filename) {
		
		String dirPath = dir.getAbsolutePath();
		if (!dirPath.endsWith("/")) {
			dirPath = dirPath + "/";
		}
		
		String filePath = dirPath + filename;
		return _settings.createFileFromPath(filePath);
		
	}

	@Override
	public File createFileObject(String path) {
		return _settings.createFileFromPath(path);
	}

	@Override
	protected File createFileSystemRoot(File f) {
		return _settings.getRootFile();
	}

	@Override
	public File getChild(File parent, String fileName) {
		
		File[] files = parent.listFiles();
		
		for (File file : files) {
			if (file.getName().equals(fileName)) {
				return file;
			}
		}
		
		return null;
	}

	@Override
	public File getDefaultDirectory() {
		System.out.println("getDefaultDirectory");
		return _settings.getRootFile();
	}

	@Override
	public File[] getFiles(File dir, boolean useFileHiding) {
		
		if (!dir.isDirectory()) {
			System.err.println("Can't call getFiles on a file.");
			return new File[0];
		}
		
		// we don't hide anything in PuddleStore
		return dir.listFiles();
	}

	@Override
	public File getHomeDirectory() {
		System.out.println("getHomeDirectory");
		File file = _settings.getRootFile();
		System.out.println("Home directory is " + file.getPath() + " with parent " + file.getParent() + " and it exists: " + file.exists());
		return file;
	}

	@Override
	public File getParentDirectory(File dir) {
		return dir.getParentFile();
	}

	@Override
	public File[] getRoots() {
		// we just have one root
		return new File[]{_settings.getRootFile()};
	}

	@Override
	public String getSystemDisplayName(File f) {
		return f.getName();
	}

	@Override
	public Icon getSystemIcon(File f) {
		if (f.isDirectory()) {
			try {
				BufferedImage image = ImageIO.read(getClass().getResourceAsStream("/filebrowser/images/folder-small.png"));
				ImageIcon icon = new ImageIcon(image);
				return icon;
			} catch (IOException e) {
				return super.getSystemIcon(f);
			}
		}
		else {
			try {
				BufferedImage image = ImageIO.read(getClass().getResourceAsStream("/filebrowser/images/file-small.png"));
				ImageIcon icon = new ImageIcon(image);
				return icon;
			} catch (IOException e) {
				return super.getSystemIcon(f);
			}
		}
	}

	@Override
	public String getSystemTypeDescription(File f) {
		return f.getName();
	}

	@Override
	public boolean isComputerNode(File dir) {
		return false;
	}

	@Override
	public boolean isDrive(File dir) {
		return (dir.equals(_settings.getRootFile()));
	}

	@Override
	public boolean isFileSystem(File f) {
		return (f.exists());
	}

	@Override
	public boolean isFileSystemRoot(File dir) {
		return (dir.equals(_settings.getRootFile()));
	}

	@Override
	public boolean isFloppyDrive(File dir) {
		return false;
	}

	@Override
	public boolean isHiddenFile(File f) {
		// we don't have hidden files
		return false;
	}

	@Override
	public boolean isParent(File folder, File file) {
		
		if (!folder.isDirectory()) {
			return false;
		}
		
		File[] files = folder.listFiles();
		for (File theFile : files) {
			if (theFile.equals(file)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean isRoot(File f) {
		return (f.equals(_settings.getRootFile()));
	}

	@Override
	public Boolean isTraversable(File f) {
		return true;
	}

	@Override
	public File createNewFolder(File containingDir) throws IOException {
		String path = containingDir.getAbsolutePath();
		
		if (!path.endsWith("/")) {
			path += "/";
		}
		
		path += "New Folder";
		File newFolder = _settings.createFileFromPath(path);
		if (newFolder.mkdir()) {
			System.out.println("New folder created.");
		}
		else {
			System.err.println("New Folder could not be created.");
		}
		return newFolder;
	}

}
