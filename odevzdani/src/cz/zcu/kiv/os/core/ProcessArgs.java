
package cz.zcu.kiv.os.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
	private String[] args;
	private String[] names;
	private Map<String,ProcessOption> options;
	
	public ProcessArgs(String[] args) {
		this(args, new ProcessDefinedOptions(), null);
	}
	
	public ProcessArgs(String[] args, ProcessDefinedOptions definedOptions) {
		this(args, definedOptions, null);
	}
	
	public ProcessArgs(String[] args, ProcessDefinedOptions definedOptions, String optionPrefix) {
		
		this.args = args;
		this.processName = args[0];
		
		this.options = new HashMap<String, ProcessOption>();
		ArrayList<String> namesList = new ArrayList<String>();
		
		String arg;
		for (int i = 1; i < args.length; i++) {
			arg = args[i];
			boolean defined = definedOptions.isDefined(arg);
			
			// defined option
			if(defined == true) {
				int optionArgsCount = definedOptions.getOptionArgsCount(arg);
				
				String[] optionArgs = new String[optionArgsCount];
				int missingArgPos = 0;
				
				if(defined == true && optionArgsCount > 0) {
					for (int j = 0; j < optionArgsCount; j++) {
						i++;
						if(i >= args.length ) {
							missingArgPos = j+1;
							break;
						}
						if(definedOptions.isDefined(args[i])){
							i--;
							missingArgPos = j+1;
							break;
						}
						optionArgs[j] = args[i];
					}
				}
				ProcessOption option = new ProcessOption(arg, optionArgs, defined, missingArgPos);
				this.options.put(arg, option);
			}
			else {
				// undefined option
				if(optionPrefix != null && arg.startsWith(optionPrefix) && arg.length()>optionPrefix.length()) {
					ProcessOption option = new ProcessOption(arg, new String[0], defined, 0);
					this.options.put(arg, option);
				}
				// name
				else {
					namesList.add(arg);
				}
			}
		}
		
		this.names = new String[ namesList.size() ];
		namesList.toArray( this.names );
	}
	
	public String getProcessName() {
		return this.processName;
	}
	
	public ProcessOption getOption(String optionName) {
		return this.options.get(optionName);
	}
	
	public String[] getAllArgs() {
		return this.args;
	}
	public String[] getAllNames() {
		return this.names;
	}
	
	public String[] getAllOptionNames() {
		
		ArrayList<String> allOptionNames = new ArrayList<String>();
		for (String key : this.options.keySet()) {
			allOptionNames.add(key);
		}
		
		String[] namesArray = new String[ allOptionNames.size() ];
		allOptionNames.toArray( namesArray );
		
		return namesArray;
	}
	
	public ProcessOption[] getAllOptions() {
		
		ProcessOption[] optionsArray = new ProcessOption[this.options.size()];
		int i = 0;
		for (String key : this.options.keySet()) {
			optionsArray[i] = this.options.get(key);
			i++;
		}
		return optionsArray;
	}
}
