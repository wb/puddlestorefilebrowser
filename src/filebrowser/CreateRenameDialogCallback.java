package filebrowser;

import java.io.File;

public interface CreateRenameDialogCallback {

	public void performAction(String text, FileType type, FileAction action, File file);
}
