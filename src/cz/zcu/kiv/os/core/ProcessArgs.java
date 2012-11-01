
package cz.zcu.kiv.os.core;

import java.util.ArrayList;
import java.util.List;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jiri Zikmund
 */
public final class ProcessArgs {

	private String processName;
	private String[] names;
	private String[] options;
	private String[] definedOptions;
	
	public ProcessArgs(String[] args, String[] definedOptions) {
		
		this.definedOptions = definedOptions;
		
		List<String> namesList = new ArrayList<String>();
		List<String> optionsList = new ArrayList<String>();
		
		this.processName = args[0];
		
		String arg;
		for (int i = 1; i < args.length; i++) {
			arg = args[i];
			if( arg.charAt(0) == '-' && arg.length() > 1 ) {
				optionsList.add(arg);
			}
			else {
				namesList.add(arg);
			}
		}
		
		this.names = new String[ namesList.size() ];
		namesList.toArray( this.names );
		
		this.options = new String[ optionsList.size() ];
		optionsList.toArray( this.options );
	}
	
	public ProcessArgs(String[] args) {
		this(args, new String[0]);
	}
	
	public void setDefinedOptions(String[] definedOptions) {
		this.definedOptions = definedOptions;
	}
	
	public String getProcessName() {
		return this.processName;
	}
	
	public String[] getNames() {
		return this.names;
	}
	
	public String[] getOptions() {
		return this.options;
	}
	
	public boolean validOption(String option) {
		return this.inOptions(this.definedOptions, option);
	}
	
	private boolean inOptions( String[] haystack, String needle ) {
		for (int i = 0; i < haystack.length; i++) {
			if( haystack[i].equals(needle) ) {
				return true;
			}
		}
		return false;
	}
	
}
