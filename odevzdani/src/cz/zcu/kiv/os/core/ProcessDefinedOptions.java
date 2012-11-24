/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.core;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jiri Zikmund
 */
public final class ProcessDefinedOptions {
	
	private Map<String,Integer> definedOptions;
	
	public ProcessDefinedOptions() {
		this.definedOptions = new HashMap<String, Integer>();
	}
	
	public void addOption(String optionName, int optionArgsCount) {
		this.definedOptions.put(optionName, optionArgsCount);
	}
	
	public void addOption(String optionName) {		
		this.addOption(optionName, 0);
	}
	
	public int getOptionArgsCount(String option) {
		Integer optionNameCount = this.definedOptions.get(option);
		return (optionNameCount == null) ? 0 : (int)optionNameCount;
	}
	
	public boolean isDefined(String option) {
		return this.definedOptions.containsKey(option);
	}
	
}
