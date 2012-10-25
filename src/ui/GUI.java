package ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;


import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import commandLogic.CommandProcessor;

public class GUI extends JPanel implements ActionListener {
	private static final String BORDER_TITLE = "Jimi - Task Manager";
	private static final String FRAME_NAME = "Jimi";
	private static final String BACKGROUND_IMG = "images/bg.gif";
	private static final String HDR_IMG = "images/hdr.png";
	private static final String ERROR_COULD_NOT_ACCESS_STORAGE = "Error: Could not access storage.";
	private static final long serialVersionUID = 1L;
		
	private Border empty = BorderFactory.createEmptyBorder();
	JList <Object> jlist = new JList<Object>();
	JScrollPane listPane;
	JTextField textField = new JTextField(32);
	CommandProcessor commandProcessor;
	JLabel commandOutputLabel = null;
	Box verticalBox;

	public GUI() {
		jlist.setVisibleRowCount(4);
		Font displayFont = new Font("Serif", Font.BOLD, 18);
		jlist.setFont(displayFont);
		
		ImageIcon icon = createImageIcon(BACKGROUND_IMG);
		JLabel bgLabel = new JLabel(icon);
		bgLabel.setSize(bgLabel.getPreferredSize());

		// Create and set up the layered pane.
		JPanel forgroundPanel = new JPanel(new GridBagLayout());
		forgroundPanel.setOpaque(false);
		forgroundPanel.setPreferredSize(new Dimension(400, 50));

		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setPreferredSize(bgLabel.getPreferredSize());
		// layeredPane.add(bgLabel, JLayeredPane.DEFAULT_LAYER);
		layeredPane.add(forgroundPanel, JLayeredPane.PALETTE_LAYER);

		textField.setBackground(new java.awt.Color(220, 219, 219));
		textField.setBorder(empty);
		textField.addActionListener(this);
		

		forgroundPanel.add(textField);

		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));


		add(Box.createRigidArea(new Dimension(0, 10)));

			try {
				commandProcessor = new CommandProcessor();
				String string= commandProcessor.getCurrentListOfTasks();
				getCurrentList(string);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		commandOutputLabel = new JLabel("");
		commandOutputLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(createControlPanel());
		add(forgroundPanel);
		add(commandOutputLabel);
		add(layeredPane);
		
	}
	
	
	
	private JScrollPane getCurrentList(String string) {
		String[] arr= string.split("<br>");
		jlist = new JList(arr);
		listPane = new JScrollPane(jlist);
		return listPane;
		}
		
	public Component createControlPanel() {

		ImageIcon hdr = createImageIcon(HDR_IMG);
		JLabel hdrLabel = new JLabel(hdr);
		hdrLabel.setSize(150, 150);
		
		verticalBox = Box.createVerticalBox();
		
		verticalBox.add(hdrLabel);
		verticalBox.add(listPane);
		verticalBox.setPreferredSize(new Dimension(180, 200));
		verticalBox.setBorder(BorderFactory.createTitledBorder(BORDER_TITLE));
		return verticalBox;

	}

	private void setListPane(Box verticalBox) {
		
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
		commandOutputLabel.setText(output);
		
		String editedText= commandProcessor.getCurrentListOfTasks();
		String[] arr= editedText.split("<br>");
		jlist = new JList(arr);
		listPane = new JScrollPane(jlist);
		verticalBox.remove(1);
		verticalBox.add(listPane);
		
				
		textField.selectAll();
		//textArea.setText(commandProcessor.getCurrentListOfTasks());
		

		// Make sure the new text is visible, even if there was a selection in the text area.
		//textArea.setCaretPosition(textArea.getDocument().getLength());
		textField.setText("");

	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 * 
	 * @throws FileNotFoundException
	 */
	private static void createAndShowGUI() throws FileNotFoundException {
		// Create and set up the window.
		JFrame frame = new JFrame(FRAME_NAME);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
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
				try {
					createAndShowGUI();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}