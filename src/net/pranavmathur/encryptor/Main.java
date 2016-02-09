package net.pranavmathur.encryptor;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {

	public static void main(String[] args) {
		Option passphrase = new Option("p", "passphrase", true, "use the given passphrase");
		Options options = new Options();
		options.addOption(passphrase);
		CommandLineParser parser = new DefaultParser();
		CommandLine line;
		try {
			line = parser.parse(options,  args);
			System.out.println(line.hasOption("p"));
		} catch (ParseException e) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("Encryptor", options);
		}
	}

}
