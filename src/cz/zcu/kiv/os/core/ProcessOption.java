/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.core;

/**
 *
 * @author Jiri Zikmund
 */
public final class ProcessOption {
	
	private String optionName;
	private String[] optionArgs;
	private boolean defined;
	private int missingArgPos;
	
	public ProcessOption(String option, String[] args, boolean defined, int missingArgPos) {
		this.optionName = option;
		this.optionArgs = args;
		this.defined = defined;
		this.missingArgPos = (missingArgPos > 0) ? missingArgPos : 0;
	}
	
	public ProcessOption(String option, String[] names, boolean defined) {
		this(option, names, defined, 0);
	}
	
	public String getOptionName() {
		return this.optionName;
	}
	
	public String[] getOptionArgs() {
		return this.optionArgs;
	}
	
	public boolean isDefined() {
		return this.defined;
	}
	
	public boolean notRecognized() {
		return !this.defined;
	}
	
	public boolean isArgMissing() {
		return (this.missingArgPos > 0) ? true : false;
	}
	
	public int getMissingArgPos() {
		return this.missingArgPos;
	}
}
