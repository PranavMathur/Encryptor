package net.pranavmathur.encryptor;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Files {

	/**
	 * Retrieves a list of the Files for encryption.
	 * @param args the array to be parsed
	 * @return an {@code ArrayList} of files
	 */
	public static List<File> getFiles(String[] args) {
		List<File> files = new ArrayList<File>();
		for (String s : args) {
			files.add(new File(s));
		}
		return files;
	}
	
	/**
	 * Uses a {@code FileDialog} to retrieve the files for encryption.
	 * @return an {@code ArrayList} of files
	 */
	public static List<File> getFilesVisual() {
		FileDialog dialog = new FileDialog((Frame) null, "Open File");
		dialog.setMode(FileDialog.LOAD);
		dialog.setMultipleMode(true);
		dialog.setVisible(true);
		File[] files = dialog.getFiles();
		dialog.dispose();
		return Arrays.asList(files);
	}
	
}
