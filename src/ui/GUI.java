package ui;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;


import commandLogic.CommandProcessor;

public class GUI extends JPanel implements ActionListener {
	private static final String ERROR_COULD_NOT_ACCESS_STORAGE = "Error: Could not access storage.";
	private static final String WELCOME_MESSAGE = "Welcome to Jimi, your friendly neighbourhood task manager.\n";
	private static final long serialVersionUID = 1L;
	protected JTextArea textArea;
	protected JTextField textField;
	private JLayeredPane layeredPane;
	private CommandProcessor commandProcessor;
	private final static String NEW_LINE = "\n";

	public GUI() {
		ImageIcon icon = createImageIcon("images/bg.gif");
		JLabel bgLabel = new JLabel(icon);
		bgLabel.setSize(bgLabel.getPreferredSize());
		
		// Create and set up the layered pane.
		JPanel forgroundPanel = new JPanel(new GridBagLayout());
        forgroundPanel.setOpaque(false);
       
        textField = new JTextField(30);
        textField.addActionListener(this);
        
        forgroundPanel.add(textField);
        forgroundPanel.setSize(bgLabel.getPreferredSize());

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(bgLabel.getPreferredSize());
        layeredPane.add(bgLabel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(forgroundPanel, JLayeredPane.PALETTE_LAYER);

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        
        add(createControlPanel());
		add(Box.createRigidArea(new Dimension(0, 10)));
        add(layeredPane);
		//add(bgLabel);
		//add(textField);
		
		try{
			commandProcessor = new CommandProcessor();
			textArea.append(commandProcessor.getCurrentListOfTasks());
		}catch(IOException e){
			textArea.append(NEW_LINE + ERROR_COULD_NOT_ACCESS_STORAGE);
		}
		
	}

	private Component createControlPanel() {
		textArea = new JTextArea(7, 35);
		textArea.setEditable(false);
		textArea.setText(WELCOME_MESSAGE);
		JScrollPane scrollPane = new JScrollPane(textArea);

		JPanel controls = new JPanel();
		controls.add(scrollPane);

		controls.setBorder(BorderFactory.createTitledBorder("Jimi - Task Manager"));
		return controls;
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = GUI.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	public void actionPerformed(ActionEvent evt) {
		String command = textField.getText();
		String output = commandProcessor.processCommand(command);
		textArea.append(NEW_LINE + output);
		textField.selectAll();

		// Make sure the new text is visible, even if there
		// was a selection in the text area.
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 */
	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("Jimi");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Create and set up the content pane.
		frame.getContentPane().add(new GUI());
		
        // Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Schedule a job for the event dispatch thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}