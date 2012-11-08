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

/**
 * Terminal window implemented in Swing API.
 *
 * @author Jakub Danek
 */
public class SwingTerminal extends InOutDevice {

	public static final char KEY_UP = 0x16;
	private JFrame frame;
	private JTextArea historyArea;
	private JTextField inputField;
	private Thread messageListener;
	private JLabel promptLabel;
	private IInputDevice stdout;
	private IOutputDevice stdin;

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
		this.inputField.setText(text);
	}
	
	public String getText() {
		return this.inputField.getText();
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

		outer.add(createTopPanel(), BorderLayout.NORTH);
		outer.add(createBottomPanel(), BorderLayout.SOUTH);

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
                                                if(s != null) {
                                                    historyArea.append(s + "\n");
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

	/**
	 * Create msg output panel.
	 *
	 * @return
	 */
	private JPanel createTopPanel() {
		JPanel topPanel = new JPanel();
		topPanel.setPreferredSize(new Dimension(640, 450));

		historyArea = new JTextArea();
		historyArea.setEditable(false);
		 historyArea.setFont(new Font("Monospaced",Font.PLAIN,15));

		JScrollPane scroll = new JScrollPane(historyArea);
		scroll.setPreferredSize(new Dimension(640, 450));

		scroll.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				e.getAdjustable().setValue(e.getAdjustable().getMaximum());
			}
		});

		topPanel.add(scroll);
		return topPanel;
	}

	/**
	 * Create prompt line.
	 *
	 * @return
	 */
	private JPanel createBottomPanel() {
		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setPreferredSize(new Dimension(640, 30));

		//TODO dummy data
		promptLabel = new JLabel("uzivatel  /path/to/dest/ $");
		bottomPanel.add(promptLabel, BorderLayout.WEST);
		inputField = new JTextField();
		inputField.setFont(new Font("Monospaced",Font.PLAIN,15));
		inputField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					SwingTerminal.this.stdin.writeLine(inputField.getText());
					inputField.setText("");
				} catch (Exception ex) {
					//TODO handle exception
					Logger.getLogger(SwingTerminal.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		});

		inputField.addKeyListener(new KeyAdapter() {
			private boolean ctrlDown = false;
			private boolean cDown = false;
			private boolean dDown = false;

			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_C:
						this.cDown = true;
						break;
					case KeyEvent.VK_D:
						this.dDown = true;
						break;
					case KeyEvent.VK_CONTROL:
						this.ctrlDown = true;
						break;

				}

				if (this.ctrlDown && this.cDown) {
//					Utilities.log("ctrl c pressed");
					Core.getInstance().getServices().dispatchSystemSignal(Signals.SIGTERM);
					e.consume();
				} else if (this.ctrlDown && this.dDown) {
//					Utilities.log("ctrl d pressed");
					Core.getInstance().getServices().dispatchSystemSignal(Signals.SIGQUIT);
					e.consume();
				} else if (e.getKeyCode() == KeyEvent.VK_UP) {
					Core.getInstance().getServices().dispatchKeyboardEvent(KeyboardEvent.ARROW_UP);
//					Utilities.log("up pressed");
					e.consume();
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					Core.getInstance().getServices().dispatchKeyboardEvent(KeyboardEvent.ARROW_DOWN);
//					Utilities.log("down pressed");

					e.consume();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_C:
						this.cDown = false;
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

		bottomPanel.add(inputField);
		return bottomPanel;
	}
}
