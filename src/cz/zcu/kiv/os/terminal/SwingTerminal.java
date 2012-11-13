package cz.zcu.kiv.os.terminal;

import cz.zcu.kiv.os.Utilities;
import cz.zcu.kiv.os.core.Core;
import cz.zcu.kiv.os.core.interrupts.KeyboardEvent;
import cz.zcu.kiv.os.core.interrupts.Signals;
import cz.zcu.kiv.os.core.device.IInputDevice;
import cz.zcu.kiv.os.core.device.InOutDevice;
import cz.zcu.kiv.os.core.device.IOutputDevice;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.text.BadLocationException;

/**
 * Terminal window implemented in Swing API.
 *
 * @author Jakub Danek
 */
public class SwingTerminal extends InOutDevice {

	public static final char KEY_UP = 0x16;
	private JFrame frame;
	private JTextArea historyArea;
	private Thread messageListener;
	private IInputDevice stdout;
	private IOutputDevice stdin;
	private int charsWrittenOnLine = 0;

	/**
	 * Default constructor.
	 */
	public SwingTerminal(IInputDevice stdout, IOutputDevice stdin) {
		super(stdout, stdin, false);
		this.stdin = stdin;
		this.stdout = stdout;
		runGui();
	}

	public void setText(String text) {
		this.historyArea.append(text);
		this.charsWrittenOnLine = text.length();
	}

	public String getText() {
		try {
			int start = this.historyArea.getLineStartOffset(this.historyArea.getLineCount() - 1) + this.charsWrittenOnLine;
			int end = this.historyArea.getLineEndOffset(this.historyArea.getLineCount() - 1);
			String lineText = this.historyArea.getText(start, end - start);
			return lineText;
		} catch (BadLocationException ex) {
			return "";
		}
	}

	private void runGui() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				initFrame();
			}
		});
	}

	/**
	 * Initialize frame parameters.
	 */
	private void initFrame() {
		frame = new JFrame("OS simulation");
		initComponents();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.pack();
	}

	/**
	 * Create form components.
	 */
	private void initComponents() {
		JPanel outer = new JPanel(new BorderLayout());
		outer.setPreferredSize(new Dimension(640, 480));

		outer.add(createTopPanel(), BorderLayout.CENTER);

		frame.getContentPane().add(outer);
		startListening();
	}

	private void startListening() {
		messageListener = new Thread(new Runnable() {
			@Override
			public void run() {
				while (stdout.isOpen()) {
					try {
						String s = stdout.readLine();
						if (s != null) {
							historyArea.append(s + "\n");
							SwingTerminal.this.setCaretToEnd();
						
						}
					} catch (Exception ex) {
						//TODO handle exception
						Logger.getLogger(SwingTerminal.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}
		});
		messageListener.start();
	}

	private void setCaretToEnd() {
		this.historyArea.setCaretPosition(SwingTerminal.this.historyArea.getText().length());

	}

	/**
	 * Create msg output panel.
	 *
	 * @return
	 */
	private JPanel createTopPanel() {
		JPanel topPanel = new JPanel();
		topPanel.setPreferredSize(new Dimension(640, 450));

		historyArea = new JTextArea();
		historyArea.setFont(new Font("Monospaced", Font.PLAIN, 15));

		historyArea.addKeyListener(new KeyAdapter() {
			private boolean ctrlDown = false;
			private boolean cDown = false;
			private boolean dDown = false;
			private boolean zDown = false;

			@Override
			public void keyPressed(KeyEvent e) {
				SwingTerminal.this.setCaretToEnd();
				switch (e.getKeyCode()) {

					case KeyEvent.VK_ENTER:
						try {
							SwingTerminal.this.stdin.writeLine(SwingTerminal.this.getText());
							SwingTerminal.this.setText("");
						} catch (Exception ex) {
							//TODO handle exception
							Logger.getLogger(SwingTerminal.class.getName()).log(Level.SEVERE, null, ex);
						}
						break;
					case KeyEvent.VK_C:
						this.cDown = true;
						break;
					case KeyEvent.VK_Z:
						this.zDown = true;
						break;
					case KeyEvent.VK_D:
						this.dDown = true;
						break;
					case KeyEvent.VK_CONTROL:
						this.ctrlDown = true;
						break;

				}

				if (this.ctrlDown && this.cDown) {
					Core.getInstance().getServices().dispatchSystemSignal(Signals.SIGTERM);
					e.consume();
				} else if (this.ctrlDown && this.dDown) {
					Core.getInstance().getServices().dispatchSystemSignal(Signals.SIGQUIT);
					e.consume();
				} else if (this.ctrlDown && this.zDown) {
					Core.getInstance().getServices().dispatchSystemSignal(Signals.SIGPAUSE);
					e.consume();
				} else if (e.getKeyCode() == KeyEvent.VK_UP) {
					Core.getInstance().getServices().dispatchKeyboardEvent(KeyboardEvent.ARROW_UP);
					e.consume();
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					Core.getInstance().getServices().dispatchKeyboardEvent(KeyboardEvent.ARROW_DOWN);
					e.consume();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_C:
						this.cDown = false;
						break;
					case KeyEvent.VK_Z:
						this.zDown = false;
						break;
					case KeyEvent.VK_D:
						this.dDown = false;
						break;
					case KeyEvent.VK_CONTROL:
						this.ctrlDown = false;
						break;
				}
			}
		});


		JScrollPane scroll = new JScrollPane(historyArea);
		scroll.setPreferredSize(new Dimension(640, 450));

		topPanel.add(scroll);
		return topPanel;
	}
}
