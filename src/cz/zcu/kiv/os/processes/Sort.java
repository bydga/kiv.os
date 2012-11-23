/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.Utilities;
import cz.zcu.kiv.os.core.Core;
import cz.zcu.kiv.os.core.device.IInputDevice;
import cz.zcu.kiv.os.core.filesystem.InvalidPathCharactersException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;

/**
 *
 * @author Jiri Zikmund
 */
public class Sort extends cz.zcu.kiv.os.core.Process {
	
	private boolean optionBlanks = false;
	private boolean optionReverse = false;
	
	private List<String> names;
	private List<String> lines;
	
	private static final String helpText =
		"\nUsage: sort [OPTION]... [FILE]...\n" +
		"Write sorted concatenation of all specified FILE(s)\n"+
		"to standard output.\n"+
		"OPTIONS:\n"+
		"  -r, --reverse                 descending order\n"+
		"  -b, --ignore-leading-blanks   ignore leading blanks\n"+
		"      --help                  display this help and exit\n"+
		"With no FILE, or when FILE is -, read standard input.\n";	
	
	public static String getManualPage() {
		return helpText; 
	}
	
	public Sort() {
		this.names = new ArrayList<String>();
		this.lines = new ArrayList<String>();
	}
	
	@Override
	public void run(String[] args) throws Exception {
		
		if( this.getNamesAndOptinos(args) == false) {
			return;
		}
		try {
			this.getLines();
		} catch(InvalidPathCharactersException ex) {
			this.getOutputStream().writeLine("Following characters cannot be used as filename: " + InvalidPathCharactersException.invalidCharsList());
			return;
		}
		this.sortLines();
		this.echoLines();
	}
	
	private boolean getNamesAndOptinos(String[] args) throws Exception {
		for (int i = 1; i < args.length; i++) {
			
			String arg = args[i];
			// options
			if(arg.startsWith("-") && arg.length() > 1) { 
				
				if(arg.equals("-b") || arg.equals("--ignore-leading-blanks")) {
					this.optionBlanks = true;
				}
				else if(arg.equals("-r") || arg.equals("--reverse")) {
					this.optionReverse = true;
				}
				else if(arg.equals("--help")) {
					this.getOutputStream().writeLine(Sort.getManualPage());
					return false; //exit
				}
				else {
					StringBuilder bf = new StringBuilder();
					bf.append("sort: invalid option ");
					bf.append(arg);
					bf.append("\n");
					bf.append("Try 'sort --help' for more information.");
					this.getOutputStream().writeLine(bf.toString());
					return false; //exit
				}
			}
			// names
			else {
				this.names.add(arg);
			}	
		}
		return true;
	}

	private void getLines() throws Exception {
		if(this.names.isEmpty()) {
			this.readStandardInput();
		}
		else {
			for(String name : this.names) {
				if(name.equals("-")) {
					this.readStandardInput();
				}
				else {
					this.readFile(name);
				}
			}
		}
	}
	
	private void sortLines() {
		
		IdentityHashMap<Integer, String> originalLinesMap = new IdentityHashMap<Integer, String>();
		IdentityHashMap<String, Integer> editedLinesMap = new IdentityHashMap<String, Integer>();
		List<String> editedLines = new ArrayList<String>();
		
		for(int i = 0; i < this.lines.size(); i++) {
			String line = this.lines.get(i);
			String editedLine = this.editLineByOptions(line);
			originalLinesMap.put(i, line);
			editedLinesMap.put(editedLine, i);
			editedLines.add(editedLine);
		}
		
		if(this.optionBlanks == true) {
			Collections.sort(editedLines, Collections.reverseOrder());
		}
		else {
			Collections.sort(editedLines);
		}
		
		this.lines = new ArrayList<String>();
		
		for( String line : editedLines ) {
			int index = editedLinesMap.get(line);
			this.lines.add(originalLinesMap.get(index));
		}
		
	}
	
	private void echoLines() throws Exception {
		for(String line : this.lines) {
			this.getOutputStream().writeLine(line);
		}
	}
	
	private String editLineByOptions(String line) {
		if(this.optionBlanks == true) {
			line = Utilities.trimLeft(line);
		}
		return line;
	}
	
	private void readFile(String fileName) throws Exception {
		IInputDevice file = Core.getInstance().getServices().openFileForRead(this, fileName);
		String line;
		while ((line = file.readLine()) != null) {
			this.lines.add(line);
		}
		file.detach();
	}
	
	private void readStandardInput() throws Exception {
		String line;
		while((line = this.getInputStream().readLine()) != null) {
			this.lines.add(line);
		}
	}

}