package net.pranavmathur.encryptor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
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

public class Main {
	
	private static CommandLine line;

	public static void main(String[] args) {
		Option passphraseOption = Option.builder("p")
				.longOpt("passphrase")
				.desc("uses the given passphrase")
				.numberOfArgs(1)
				.build();
		Option visualOption = Option.builder("v")
				.longOpt("visual")
				.desc("prompt for password and files using GUI")
				.build();
		Options options = new Options();
		OptionGroup passphraseGroup = new OptionGroup();
		passphraseGroup.addOption(passphraseOption);
		passphraseGroup.addOption(visualOption);
		options.addOptionGroup(passphraseGroup);
		CommandLineParser parser = new DefaultParser();
		try {
			line = parser.parse(options,  args);
			interpretArguments();
		} catch (ParseException e) {
			HelpFormatter formatter = new HelpFormatter();
			System.out.println(e.getMessage());
			formatter.printHelp("Encryptor [OPTION]... [FILE]...", options);
		}
	}
	
	private static void interpretArguments() throws ParseException {
		if (line.getArgs().length == 0) return;
		String optionalPassphrase = (String) line.getParsedOptionValue("p");
		final String passphrase = optionalPassphrase != null ? 
				optionalPassphrase : 
					new String(System.console().readPassword("Enter a passphrase: "));
		List<File> files = getFiles(line.getArgs());
		files.stream().forEach(file -> encryptFile(file, passphrase));
	}
	
	private static String getPasswordVisual() {
		JPasswordField pf = new JPasswordField();
		int okCxl = JOptionPane.showConfirmDialog(null, pf, "Enter Passphrase", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		return okCxl == JOptionPane.OK_OPTION ? new String(pf.getPassword()) : null;
	}
	
	private static List<File> getFiles(String[] args) {
		List<File> files = new ArrayList<File>();
		for (String s : args) {
			files.add(new File(s));
		}
		return files;
	}
	
	private static boolean encryptFile(File file, String passphrase) {
		if (file.isDirectory()) {
			encryptDirectory(file, passphrase);
		} else {
			byte[] passBytes = passphrase.getBytes();
			byte[] fileBytes;
			try {
				fileBytes = Files.readAllBytes(file.toPath());
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			byte[] encryptedBytes = encryptBytes(fileBytes, passBytes);
			try {
				Files.write(file.toPath(), encryptedBytes);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
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

}
