/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.Utilities;
import cz.zcu.kiv.os.core.Core;
import cz.zcu.kiv.os.core.ProcessInfo;
import java.util.List;

/**
 *
 * @author bydga
 */
public class Ps extends cz.zcu.kiv.os.core.Process {

	@Override
	protected void run(String[] args) throws Exception {

		StringBuilder builder = new StringBuilder();
		int maxLength = 3; //PID.length
		List<ProcessInfo> list = Core.getInstance().getServices().getProcessTableData();
		for (ProcessInfo info : list) {
			String s = "" + info.pid;
			maxLength = s.length() > maxLength ? s.length() : maxLength;
		}
		String header = String.format("%" + maxLength + "s CMD", "PID");
		for (ProcessInfo info : list) {
			builder.append(String.format("%" + maxLength + "s", "" +info.pid)).append(" ").append(info.processName).append("\n");
		}
		
		this.getOutputStream().writeLine(header);
		this.getOutputStream().writeLine(builder.toString());
	}
}
