package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.core.Process;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author Jiri Zikmund
 */
public class Echo extends Process {

	public static void main(String[] args) {
		Vytvor v = new Vytvor();
		v.vytvor();
	}
	
	public Echo(int pid, int ppid, InputStream stdIn, OutputStream stdOut, OutputStream stdErr, Observer observer){
		super(pid, ppid, stdIn, stdOut, stdErr, observer);
	}
	
	@Override
	public void run() {
		try {
			for (int i = 0; i < 100; i++) {
				this.stdOut.write(("PID "+(this.pid)+"\n").getBytes());
				this.workingThread.sleep(this.ppid);
			}
		}
		catch(Exception e) {
			
		}
	}

}

class Vytvor implements Observer {

	public  void vytvor() {
		Echo echo = new Echo(1, 1000, System.in, System.out, System.out, this);
		Echo echo2 = new Echo(2, 500, System.in, System.out, System.out, this);
		echo.start();
		echo2.start();
		echo.stop();
	}
	
	@Override
	public void update(Observable o, Object arg) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
}