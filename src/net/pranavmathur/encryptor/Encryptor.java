package net.pranavmathur.encryptor;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class Encryptor {
	
	/**
	 * Encrypts the given File. If the file is a directory, calls {@link #encryptDirectory(File, String, boolean, boolean)}.
	 * @param file the file to encrypt
	 * @param passphrase the passphrase with which to encrypt the file
	 * @param obfuscate whether or not the filename should be obfuscated
	 * @param verbose whether or not to output extra information
	 * @return the success of the operation
	 */
	public static boolean encryptFile(File file, String passphrase, boolean obfuscate, boolean verbose) {
		if (file.isDirectory()) {
			encryptDirectory(file, passphrase, obfuscate, verbose);
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
			if (obfuscate) {
				File obfuscated = new File(file.getParent(), obfuscateName(file.getName()));
				try {
					FileUtils.moveFile(file, obfuscated);
				} catch (IOException e) {
					System.err.println("Error while moving " + file.getPath() + " to " + obfuscated.getPath());
					return false;
				}
				if (verbose) {
					System.out.println("Encrypted " + file.getPath() + " to " + obfuscated.getPath());
				}
			} else if (verbose) {
				System.out.println("Encrypted " + file.getPath());
			}
		}
		return true;
	}
	
	/**
	 * Obfuscates the given name with a Caesar cipher with shift 13.
	 * @param path the path to be obfuscated
	 * @return the obfuscated path
	 */
	private static String obfuscateName(String path) {
		char[] chars = path.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if ((65 <= chars[i] && chars[i] <= 77) || (97 <= chars[i] && chars[i] <= 109)) {
				chars[i] = (char)(chars[i] + 13);
			} else if ((78 <= chars[i] && chars[i] <= 90) || (110 <= chars[i] && chars[i] <= 122)) {
				chars[i] = (char)(chars[i] - 13);
			} else if (48 <= chars[i] && chars[i] <= 52) {
				chars[i] = (char)(chars[i] + 5);
			} else if (53 <= chars[i] && chars[i] <= 57) {
				chars[i] = (char)(chars[i] - 5);
			}
		}
		return new String(chars);
	}
	
	/**
	 * Recursively encrypts all files in a directory.
	 * @param dir the directory to encrypt
	 * @param passphrase the passphrase with which to encrypt the directory
	 * @param obfuscate whether or not the filename should be obfuscated
	 * @param verbose whether or not to output extra information
	 */
	private static void encryptDirectory(File dir, String passphrase, boolean obfuscate, boolean verbose) {
		for (File file : dir.listFiles()) {
			encryptFile(file, passphrase, obfuscate, verbose);
		}
	}
	
	/**
	 * Encrypts the given bytes using XOR encryption.
	 * @param fileBytes the bytes of the file to be encrypted
	 * @param passBytes the bytes of the passphrase with which to encrypt
	 * @return the encrypted bytes
	 */
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
