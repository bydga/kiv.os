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

	@Override
	public String toString() {
		return "\tArgs: " + Arrays.toString(args) + "\n\tIn: " + stdIn + " Bg: " + (isBackgroundTask ? "yes" : "no") + "\n\tOut: " + stdOut + " Append: " + (stdOutAppend ? "yes" : "no") + 
				"\n\tErr: " + stdErr + " Append: " + (stdErrAppend ? "yes" : "no");
	}
}
