package net.pranavmathur.encryptor;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;

public class Main {
	
	private static Options options;
	private static CommandLine line;

	public static void main(String[] args) {
		Option passphraseOption = Option.builder("p")
				.longOpt("passphrase")
				.desc("use the given passphrase")
				.numberOfArgs(1)
				.build();
		Option visualOption = Option.builder("V")
				.longOpt("visual")
				.desc("prompt for password and files using GUI")
				.build();
		Option verboseOption = Option.builder("v")
				.longOpt("verbose")
				.desc("print all file names as they are encrypted")
				.build();
		Option helpOption = Option.builder()
				.longOpt("help")
				.desc("print this help message")
				.build();
		Option obfuscateOption = Option.builder("o")
				.longOpt("obfuscate")
				.desc("obfuscate file names")
				.build();
		options = new Options();
		OptionGroup passphraseGroup = new OptionGroup();
		passphraseGroup.addOption(passphraseOption);
		passphraseGroup.addOption(visualOption);
		options.addOptionGroup(passphraseGroup);
		options.addOption(verboseOption);
		options.addOption(helpOption);
		options.addOption(obfuscateOption);
		CommandLineParser parser = new DefaultParser();
		try {
			line = parser.parse(options,  args);
			interpretArguments();
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			printHelp();
		}
	}
	
	private static void interpretArguments() throws ParseException {
		if (line.hasOption("help")) {
			printHelp();
			return;
		}
		List<File> files;
		if (line.hasOption("V")) {
			files = getFilesVisual();
		} else {
			files = getFiles(line.getArgs());
		}
		if (files.size() == 0) return;
		final String passphrase;
		if (line.hasOption("p")) {
			passphrase = (String) line.getParsedOptionValue("p");
		} else if (line.hasOption("V")) {
			passphrase = getPassphraseVisual();
		} else {
			passphrase = getPassphrase();
		}
		if (passphrase == null) return;
		files.stream().forEach(file -> encryptFile(file, passphrase));
	}
	
	private static String getPassphrase() {
		return new String(System.console().readPassword("Enter a passphrase: "));
	}
	
	private static String getPassphraseVisual() {
		JPasswordField pf = new JPasswordField();
		int okCxl = JOptionPane.showConfirmDialog(
				null, pf, "Enter Passphrase", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		return okCxl == JOptionPane.OK_OPTION ? new String(pf.getPassword()) : null;
	}
	
	private static List<File> getFiles(String[] args) {
		List<File> files = new ArrayList<File>();
		for (String s : args) {
			files.add(new File(s));
		}
		return files;
	}
	
	private static List<File> getFilesVisual() {
		FileDialog dialog = new FileDialog((Frame) null, "Open File");
		dialog.setMode(FileDialog.LOAD);
		dialog.setMultipleMode(true);
		dialog.setVisible(true);
		File[] files = dialog.getFiles();
		dialog.dispose();
		return Arrays.asList(files);
	}
	
	private static boolean encryptFile(File file, String passphrase) {
		if (file.isDirectory()) {
			encryptDirectory(file, passphrase);
		} else {
			file = file.getAbsoluteFile();
			byte[] passBytes = passphrase.getBytes();
			byte[] fileBytes;
			try {
				fileBytes = FileUtils.readFileToByteArray(file);
			} catch (IOException e) {
				System.err.println("Error while reading " + file.getPath());
				return false;
			}
			byte[] encryptedBytes = encryptBytes(fileBytes, passBytes);
			try {
				FileUtils.writeByteArrayToFile(file, encryptedBytes);
			} catch (IOException e) {
				System.err.println("Error while writing to " + file.getPath());
				return false;
			}
			if (line.hasOption("o")) {
				File obfuscated = new File(file.getParent(), obfuscateName(file.getName()));
				try {
					FileUtils.moveFile(file, obfuscated);
				} catch (IOException e) {
					System.err.println("Error while moving " + file.getPath() + " to " + obfuscated.getPath());
					return false;
				}
				if (line.hasOption("v")) {
					System.out.println("Encrypted " + file.getPath() + " to " + obfuscated.getPath());
				}
			} else if (line.hasOption("v")) {
				System.out.println("Encrypted " + file.getPath());
			}
		}
		return true;
	}
	
	private static String obfuscateName(String path) {
		char[] chars = path.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if ((65 <= chars[i] && chars[i] <= 77) || (97 <= chars[i] && chars[i] <= 109)) {
				chars[i] = (char)(chars[i] + 13);
			} else if ((78 <= chars[i] && chars[i] <= 90) || (110 <= chars[i] && chars[i] <= 122)) {
				chars[i] = (char)(chars[i] - 13);
			}
		}
		return new String(chars);
	}
	
	private static void encryptDirectory(File dir, String passphrase) {
		for (File file : dir.listFiles()) {
			encryptFile(file, passphrase);
		}
	}
	
	private static byte[] encryptBytes(byte[] fileBytes, byte[] passBytes) {
		if (fileBytes.length == 0 || passBytes.length == 0)
			return fileBytes;
		byte[] encryptedBytes = new byte[fileBytes.length];
		for (int i = 0; i < encryptedBytes.length; i++) {
			encryptedBytes[i] = (byte) (fileBytes[i] ^ passBytes[i % passBytes.length]);
		}
		return encryptedBytes;
	}
	
	private static void printHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("Encryptor [OPTION]... [FILE]...", options);
	}

}
