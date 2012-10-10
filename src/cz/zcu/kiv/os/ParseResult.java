/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os;

import java.util.Arrays;

/**
 *
 * @author bydga
 */
public class ParseResult {

	public String[] args;
	public String stdIn;
	public String stdOut;
	public boolean stdOutAppend;
	public String stdErr;
	public boolean stdErrAppend;
	public boolean isBackgroundTask;
	public ParseResult pipeline;

	public ParseResult() {
	}

	public ParseResult(String[] args, String stdIn, String stdOut, boolean stdOutAppend, String stdErr, boolean stdErrAppend, boolean isBackgroundTask, ParseResult pipeline) {
		this.args = args;
		this.stdIn = stdIn;
		this.stdOut = stdOut;
		this.stdOutAppend = stdOutAppend;
		this.stdErr = stdErr;
		this.stdErrAppend = stdErrAppend;
		this.isBackgroundTask = isBackgroundTask;
		this.pipeline = pipeline;
	}

	@Override
	public String toString() {
		return "\tArgs: " + Arrays.toString(args) + "\n\tstdIn: " + stdIn + "\n\tstdOut: " + stdOut + " Append: " + (stdOutAppend ? "yes" : "no")
				+ "\n\tstdErr: " + stdErr + " Append: " + (stdErrAppend ? "yes" : "no"
				+ "\n\tBg: " + (isBackgroundTask ? "yes" : "no") + (pipeline != null ? "\n\tPIPE: " + pipeline.toString() : ""));
	}
	
	public String toTestString()
	{
		return this.toString().replaceAll("\t", "").replaceAll("\n", "");
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ParseResult) {
			ParseResult pr = (ParseResult) obj;
			return this.isBackgroundTask == pr.isBackgroundTask && this.stdErrAppend == pr.stdErrAppend && this.stdOutAppend == pr.stdOutAppend
					&& this.stdErr.equals(pr.stdErr) && this.stdIn.equals(pr.stdIn) && this.stdOut.equals(pr.stdOut) && this.pipeline.equals(pr.pipeline)
					&& Arrays.equals(this.args, pr.args);


		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 71 * hash + Arrays.deepHashCode(this.args);
		hash = 71 * hash + (this.stdIn != null ? this.stdIn.hashCode() : 0);
		hash = 71 * hash + (this.stdOut != null ? this.stdOut.hashCode() : 0);
		hash = 71 * hash + (this.stdOutAppend ? 1 : 0);
		hash = 71 * hash + (this.stdErr != null ? this.stdErr.hashCode() : 0);
		hash = 71 * hash + (this.stdErrAppend ? 1 : 0);
		hash = 71 * hash + (this.isBackgroundTask ? 1 : 0);
		hash = 71 * hash + (this.pipeline != null ? this.pipeline.hashCode() : 0);
		return hash;
	}
}
