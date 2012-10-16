/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.core;

import cz.zcu.kiv.os.terminal.ParseResult;
import java.io.PipedInputStream;

/**
 *
 * @author bydga
 */
public interface ICoreServices {

	public PipedInputStream openFile(Process caller, String fileName, String rights);
	public void closeFile(Process caller, PipedInputStream stream);
	public Process createProcess(Process parent, String processName, String[] args, String stdIn, String stdOut, boolean appendStdOut, String stdErr, boolean appendStdErr, boolean isBackgroundProcess ) throws Exception;
	public Process createProcess(Process parent, String processName, String[] args ) throws Exception;
}
