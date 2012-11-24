package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.core.Core;
import cz.zcu.kiv.os.core.ProcessArgs;
import cz.zcu.kiv.os.core.ProcessDefinedOptions;
import cz.zcu.kiv.os.core.ProcessOption;
import cz.zcu.kiv.os.core.device.IInputDevice;
import cz.zcu.kiv.os.core.filesystem.InvalidPathCharactersException;
import java.io.FileNotFoundException;

/**
 * Process that counts lines, words and characters from files and standard input
 * and writes them to the standard output.
 * 
 * @author Jiri Zikmund
 */
public class Wc extends cz.zcu.kiv.os.core.Process {
	
	private boolean optionChars = false;
	private boolean optionLines = false;
	private boolean optionMaxLineLength = false;
	private boolean optionWords = false;

	private int countChars = 0;
	private int countLines = 0;
	private int countMaxLineLength = 0;
	private int countWords = 0;
	
	private int totalCountChars = 0;
	private int totalCountLines = 0;
	private int totalCountMaxLineLength = 0;
	private int totalCountWords = 0;
	
	private static final String helpText =
		"\nUsage: wc [OPTION]... [FILE]...\n"+
		"Print line, word and byte counts for each specified FILE.\n"+
		"If more than one FILE is specified, print also total of counts.\n"+
		"OPTIONS:\n"+
		"  -m, --chars            print the character counts\n"+
		"  -l, --lines            print the newline counts\n"+
		"  -L, --max-line-length  print the length of the longest line\n"+
		"  -w, --words            print the word counts\n"+
		"      --help             display this help and exit\n"+
		"With no FILE, or when FILE is -, read standard input.\n";
	
	/**
	 * Returns manual page for Wc process
	 * 
	 * @return string with manual page
	 */
	public static String getManualPage() {
		return helpText;
	}
	
	@Override
	public void run(String[] args) throws Exception {
			
		// set defined options
		ProcessDefinedOptions definedOptions = new ProcessDefinedOptions();
		definedOptions.addOption("-m", 0);
		definedOptions.addOption("--chars", 0);
		definedOptions.addOption("-l", 0);
		definedOptions.addOption("--lines", 0);
		definedOptions.addOption("-L", 0);
		definedOptions.addOption("--max-line-length", 0);
		definedOptions.addOption("-w", 0);
		definedOptions.addOption("--words", 0);
		definedOptions.addOption("--help", 0);
		ProcessArgs processArgs = new ProcessArgs(args, definedOptions, "-");
		
		ProcessOption[] options = processArgs.getAllOptions();
		// apply options
		for (int i = 0; i < options.length; i++) {
			if(options[i].notRecognized()) {
				StringBuilder bf = new StringBuilder();
				bf.append("wc: invalid option ");
				bf.append(options[i].getOptionName());
				bf.append("\n");
				bf.append("Try 'wc --help' for more information.");
				this.getOutputStream().writeLine(bf.toString());
				return; //exit
			}
			else if(options[i].getOptionName().equals("--help")) {
				this.getOutputStream().writeLine(Wc.getManualPage());
				return; //exit
			}
			else if(options[i].getOptionName().equals("-m") || options[i].getOptionName().equals("--chars")) {
				this.optionChars = true;
			}
			else if(options[i].getOptionName().equals("-l") || options[i].getOptionName().equals("--lines")) {
				this.optionLines = true;
			}
			else if(options[i].getOptionName().equals("-L") || options[i].getOptionName().equals("--max-line-length")) {
				this.optionMaxLineLength = true;
			}
			else if(options[i].getOptionName().equals("-w") || options[i].getOptionName().equals("--words")) {
				this.optionWords = true;
			}
		}
		
		// count all files
		String[] names = processArgs.getAllNames();
		
		// read from standard input or pipe
		if(names.length == 0) {
			this.readPipe();
			this.echoCounts();
			return;
		}
		
		for (int i = 0; i < names.length; i++) {
			
			if( names[i].equals("-") ) {
				this.readStandardInput();
				this.echoCounts(names[i]);
				this.resetCounts();
			}
			else {
				try {
					try {
						this.readFile(names[i]);
					} catch (InvalidPathCharactersException ex) {
						this.getOutputStream().writeLine("Following characters cannot be used as filename: " + InvalidPathCharactersException.invalidCharsList());
						return;
					}
					this.echoCounts(names[i]);
					this.resetCounts();
				} catch (FileNotFoundException e) {
					this.getOutputStream().writeLine("wc: " + names[i] + ": No such file");
				}
			}
		}
		
		if(names.length>1) {
			this.echoTotalCounts();
		}
	}
	
	/**
	 * Reads lines from file
	 *
	 * @param fileName file to read
	 * @throws Exception when error occurs while reading file
	 */
	private void readFile(String fileName) throws Exception {
		
		IInputDevice file = Core.getInstance().getServices().openFileForRead(this, fileName);
		String line;
		while ((line = file.readLine()) != null) {
			this.getCountsFromLine(line);
		}
		file.detach();
	}
	
	/**
	 * Reads lines from standard input and writes them to the standard output
	 * 
	 * @throws Exception when error occures while reading from standard input or
	 * writing to standart output
	 */
	private void readStandardInput() throws Exception {
		
		String line;
		while((line = this.getInputStream().readLine()) != null) {
			this.getOutputStream().writeLine(line);
			this.getCountsFromLine(line);
		}
	}
	
	/**
	 * Reads lines from standard pipe (standard input)
	 * 
	 * @throws Exception when error occures while reading from standard input
	 */
	private void readPipe() throws Exception {
		
		String line;
		while((line = this.getInputStream().readLine()) != null) {
			this.getCountsFromLine(line);
		}
	}
	
	/**
	 * Saves count statisctics from one line
	 * 
	 * @param line line to count
	 */
	private void getCountsFromLine(String line) {
		
		int lineLength = line.length();
		int words = this.getCountWords(line);
		
		this.countLines++;
		this.totalCountLines++;
		this.countChars += lineLength + 1;
		this.totalCountChars += lineLength + 1;
		this.countWords += words;
		this.totalCountWords += words;
		
		if(lineLength > this.countMaxLineLength) {
			this.countMaxLineLength = lineLength;
		}
		
		if(line.length() > this.totalCountMaxLineLength) {
			this.totalCountMaxLineLength = lineLength;
		}
	}
	
	/**
	 * Writes statisctics for current input to the standard output
	 * 
	 * @param countLines count of all lines from the input
	 * @param countWords count of all words from the input
	 * @param countChars count of all characters from the input
	 * @param countMaxLineLength length of the longest line in the input
	 * @param inputName name from the input
	 * @throws Exception when error occures while writing to standard output
	 */
	private void echoCounts(int countLines, int countWords, int countChars, int countMaxLineLength, String inputName) throws Exception {
		
		StringBuilder bf = new StringBuilder();
		int padding = 7;
		// default without options
		if(!this.optionChars && !this.optionLines && !this.optionMaxLineLength && !this.optionWords) {
			bf.append(this.padLeft(Integer.toString(countLines),padding));
			bf.append(this.padLeft(Integer.toString(countWords),padding));
			bf.append(this.padLeft(Integer.toString(countChars),padding));
		}
		// with options
		else {
			if( this.optionLines == true ) {
				bf.append(this.padLeft(Integer.toString(countLines),padding));
			}
			if( this.optionWords == true ) {
				bf.append(this.padLeft(Integer.toString(countWords),padding));
			}
			if( this.optionChars == true ) {
				bf.append(this.padLeft(Integer.toString(countChars),padding));
			}
			if( this.optionMaxLineLength == true ) {
				bf.append(this.padLeft(Integer.toString(countMaxLineLength),padding));
			}
		}
		// add name of file
		if(inputName != null) {
			bf.append("    ");
			bf.append(inputName);
		}
		this.getOutputStream().writeLine(bf.toString());
	}
	
	/**
	 * Writes total count statistics to the standard output
	 * 
	 * @throws Exception when error occures while writing to standard output
	 */
	private void echoCounts() throws Exception {
		this.echoCounts(null);
	}
	
	/**
	 * Writes count statistics of the input to the standard output
	 * 
	 * @param inputName name of input
	 * @throws Exception when error occures while writing to standard output
	 */
	private void echoCounts(String inputName) throws Exception {
		this.echoCounts(this.countLines, this.countWords, this.countChars, this.countMaxLineLength, inputName);
	}
	
	/**
	 * Writes total count statisctics to the standard output
	 * 
	 * @throws Exception when error occures while writing to standard output
	 */
	private void echoTotalCounts() throws Exception {
		this.echoCounts(this.totalCountLines, this.totalCountWords, this.totalCountChars, this.totalCountMaxLineLength, "total");
	}
	
	/**
	 * Resets line counter for current input (file or standard input)
	 */
	private void resetCounts() {
		this.countChars = 0;
		this.countLines = 0;
		this.countMaxLineLength = 0;
		this.countWords = 0;
	}
	
	/**
	 * Get count of words from one line
	 *
	 * @param s line to count words from
	 * @return count of words in the line
	 */
	private int getCountWords(String s){

		int counter = 0;
		boolean word = false;
		int endOfLine = s.length() - 1;

		for (int i = 0; i < s.length(); i++) {
			if (Character.isLetter(s.charAt(i)) == true && i != endOfLine) {
				word = true;
			} else if (Character.isLetter(s.charAt(i)) == false && word == true) {
				counter++;
				word = false;
			} else if (Character.isLetter(s.charAt(i)) && i == endOfLine) {
				counter++;
			}
		}
		return counter;
	}
	
	/**
	 * Edits string to be padded to right
	 *
	 * @param s string to pad
	 * @param n number of positions to pad
	 * @return string padded to right
	 */
	private String padRight(String s, int n) {
		return String.format("%1$-" + n + "s", s);  
	}
	
	/**
	 * Edits string to be padded to left
	 *
	 * @param s string to pad
	 * @param n number of positions to pad
	 * @return string padded to left
	 */
	private String padLeft(String s, int n) {
		return String.format("%1$" + n + "s", s);  
	}
	
}
