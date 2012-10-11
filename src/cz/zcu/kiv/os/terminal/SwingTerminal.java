package cz.zcu.kiv.os.terminal;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import javax.swing.*;

/**
 * Terminal window implemented in Swing API.
 *
 * @author Jakub Danek
 */
public class SwingTerminal extends JFrame {

    private JTextArea historyArea;
    private JLabel promptLabel;
    

    /**
     * Default constructor.
     * @throws HeadlessException
     */
    public SwingTerminal() throws HeadlessException {
        initComponents();
        initFrame();
    }

    /**
     * Initialize frame parameters.
     */
    private void initFrame() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.pack();
    }

    /**
     * Create form components.
     */
    private void initComponents() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setPreferredSize(new Dimension(1024, 800));

        outer.add(createTopPanel(), BorderLayout.NORTH);
        outer.add(createBottomPanel(), BorderLayout.SOUTH);

        this.getContentPane().add(outer);
    }

    /**
     * Create msg output panel.
     * @return
     */
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(1024, 768));

        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setPreferredSize(new Dimension(1024, 768));

        JScrollPane scroll = new JScrollPane(historyArea);

        topPanel.add(scroll);
        return topPanel;
    }

    /**
     * Create prompt line.
     * @return
     */
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setPreferredSize(new Dimension(1024, 33));

        //TODO dummy data
        promptLabel = new JLabel("uzivatel  /path/to/dest/ $");
        bottomPanel.add(promptLabel, BorderLayout.WEST);

        JTextField inputField = new JTextField();
        bottomPanel.add(inputField);
        


        return bottomPanel;
    }

}
