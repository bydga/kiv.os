/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.processes;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jakub Danek
 */
public class Ls extends cz.zcu.kiv.os.core.Process {

	private List<String> paths;
	private boolean showHidden = false;
	private boolean showImpliedHidden = false;
	private boolean showDetailed = false;

	public Ls() {
		paths = new ArrayList<String>();
	}

	@Override
	protected void run(String[] args) throws Exception {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	private String prepareFileListingOutput(Map<String, List<File>> filesMap) {
		StringBuilder builder = new StringBuilder();
		for(Map.Entry<String, List<File>> entry : filesMap.entrySet()) {
			builder.append(entry.getKey()).append(":\n");


		}
		
		return builder.toString();
	}

	private String prepareSingleDirectoryList(List<File> files) {
		StringBuilder builder = new StringBuilder();
		if(showDetailed) {
			String tmp;
			for(File f : files) {
				
				builder.append(f.length());
			}
		}
	}

	private void processParameters(String[] args) {
		String tmp;
		for(int i = 1; i < args.length; i++) {
			tmp = args[i];
			if(tmp.equals("-A")) {
				showHidden = true;
			} else if(tmp.equals("-a")) {
				showHidden = true;
				showImpliedHidden = true;
			} else if(tmp.equals("-l")) {
				showDetailed = true;
			} else {
				paths.add(tmp);
			}
		}
	}

}
