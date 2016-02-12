package net.pranavmathur.encryptor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
	
	private static void interpretArguments(CommandLine line) throws ParseException {
		String passphrase = (String) line.getParsedOptionValue("p");
		if (passphrase == null)
			passphrase = new String(System.console().readPassword("Enter a passphrase: "));
		List<File> files = getFiles(line.getArgs());
	}
	
	private static List<File> getFiles(String[] args) {
		List<File> files = new ArrayList<File>();
		for (String s : args) {
			files.add(new File(s));
		}
		return files;
	}

}
