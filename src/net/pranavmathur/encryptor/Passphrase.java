package net.pranavmathur.encryptor;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

public class Passphrase {
	
	/**
	 * Retrieves the passphrase for encryption without echoing during input.
	 * @return a string representation of the passphrase
	 */
	public static String getPassphrase() {
		return new String(System.console().readPassword("Enter a passphrase: "));
	}
	
	/**
	 * Uses a swing password field to retrieve the passphrase.
	 * @return a string representation of the passphrase
	 */
	public static String getPassphraseVisual() {
		JPasswordField pf = new JPasswordField();
		int okCxl = JOptionPane.showConfirmDialog(
				null, pf, "Enter Passphrase", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		return okCxl == JOptionPane.OK_OPTION ? new String(pf.getPassword()) : null;
	}
	
}
