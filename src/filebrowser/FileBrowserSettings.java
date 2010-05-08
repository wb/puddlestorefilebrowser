package filebrowser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

public interface FileBrowserSettings {

	public File getRootFile();
	public File createFileFromPath(String path);
	public OutputStream getOutputStream(File file, boolean append) throws FileNotFoundException;
	public InputStream getInputStream(File file) throws FileNotFoundException;
}
