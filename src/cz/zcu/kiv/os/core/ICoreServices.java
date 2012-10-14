/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.core;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;

/**
 *
 * @author bydga
 */
public interface ICoreServices {

	public PipedInputStream openFile(Process caller, String fileName, String rights);
	public void closeFile(Process caller, PipedInputStream stream);
	public Process createProcess(Process parent, String processName, String[] args, InputStream stdIn, OutputStream stdOut, OutputStream stdErr) throws Exception;
	public Process createProcess(Process parent, String processName, String[] args) throws Exception;
}
