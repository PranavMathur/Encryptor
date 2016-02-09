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
		Option passphraseOption = Option.builder("p")
				.longOpt("passphrase")
				.desc("uses the given passphrase")
				.numberOfArgs(1)
				.build();
		Options options = new Options();
		options.addOption(passphraseOption);
		CommandLineParser parser = new DefaultParser();
		CommandLine line;
		try {
			line = parser.parse(options,  args);
			interpretArguments(line);
		} catch (ParseException e) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("Encryptor [OPTION]... [FILE]...", options);
		}
	}
	
	private static void interpretArguments(CommandLine line) {
		
	}

}
