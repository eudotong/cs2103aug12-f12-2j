package ui;
//TODO Organise the codes in (FRAMES - PANEL - LABELS)
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import commandLogic.CommandProcessor;
import ui.HintFieldUI;

/**
 * 
 * @author A0092052N
 * 
 */
public class GUI extends JPanel implements ActionListener {
	
	private static final String EMPTY_STRING = "";
	private static final String BORDER_TITLE = "Jimi - Task Manager";
	private static final String FRAME_NAME = "Jimi";
	private static final String HDR_IMAGE_SRC = "images/hdr.PNG";
	private static final long serialVersionUID = 1L;
	
	// TODO Set dimensions for GUI
	private static final int MIN_FRAME_WIDTH = 400;
	private static final int MIN_FRAME_HEIGHT = 350;
	private static final int FOREGROUND_PANEL_WIDTH = 400;
	private static final int FOREGROUND_PANEL_HEIGHT = 50;
	private static final int HDR_IMAGE_WIDTH = 180;
	private static final int HDR_IMAGE_HEIGHT = 70;
	
	private static final String TASKLIST_FONT_TYPE = "Serif";
	private static final int TASKLIST_FONT_STYLE = Font.BOLD;
	private static final int TASKLIST_FONT_SIZE = 12;
	
	private static final int COMMAND_OUTPUT_LABEL_WIDTH = 100;
	private static final int COMMAND_OUTPUT_LABEL_HEIGHT = 10;
	private static final String COMMAND_OUTPUT_FONT_TYPE = "Courier";
	private static final int COMMAND_OUTPUT_FONT_STYLE = Font.BOLD;
	private static final int COMMAND_OUTPUT_FONT_SIZE = 12;
	private static final Color COMMAND_OUTPUT_FONT_COLOR = Color.red;
	
	
	private static final Dimension MIN_FRAME_DIMENSION = new Dimension(MIN_FRAME_WIDTH,MIN_FRAME_HEIGHT) ;
	private static final Dimension FOREGROUND_PANEL_DIMENSION = new Dimension(FOREGROUND_PANEL_WIDTH,FOREGROUND_PANEL_HEIGHT) ;
	private static final Dimension HDR_IMAGE_DIMENSION = new Dimension(HDR_IMAGE_WIDTH, HDR_IMAGE_HEIGHT);
	private static final Dimension COMMAND_OUTPUT_LABEL_DIMENSION = new Dimension(COMMAND_OUTPUT_LABEL_WIDTH, COMMAND_OUTPUT_LABEL_HEIGHT);
	
	private static final Font TASKLIST_FONT = new Font(TASKLIST_FONT_TYPE,TASKLIST_FONT_STYLE,TASKLIST_FONT_SIZE);
	private static final Font COMMAND_OUTPUT_FONT = new Font(COMMAND_OUTPUT_FONT_TYPE,COMMAND_OUTPUT_FONT_STYLE,COMMAND_OUTPUT_FONT_SIZE);
	
	private static Logger logger = Logger.getLogger("JIMI");

	private Border empty = BorderFactory.createEmptyBorder();
	JList<String> tasklist;
	JScrollPane listPane;
	JTextField textField = new JTextField(32);
	CommandProcessor commandProcessor;
	JLabel commandOutputLabel;
	Box verticalBox;

	public GUI(){

		try {
			commandProcessor = new CommandProcessor();
			tasklist = new JList<String>(
					commandProcessor.getCurrentListModelOfTasks());
		} catch (IOException e) {
			e.printStackTrace();
		}

		tasklist.setVisibleRowCount(4);
		tasklist.setFont(TASKLIST_FONT);
		listPane = new JScrollPane(tasklist);
		MyCellRenderer cellRenderer = new MyCellRenderer();
		tasklist.setCellRenderer(cellRenderer);
		tasklist.setFixedCellHeight(30);

		// Create and set up the layered pane.
		JPanel foregroundPanel = new JPanel(new GridBagLayout());
		foregroundPanel.setOpaque(false); 
		foregroundPanel.setPreferredSize(FOREGROUND_PANEL_DIMENSION);

		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		layeredPane.add(foregroundPanel, JLayeredPane.PALETTE_LAYER);

		// textField.setBackground(new java.awt.Color(220, 219, 219));
		// textField.setBorder(empty);
		textField.setUI(new HintFieldUI("Type /? for help", true));
		textField.addActionListener(this);
		textField.getInputMap().put(KeyStroke.getKeyStroke("UP"), "upKey");
		textField.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "downKey");
		textField.getActionMap().put("upKey", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String previouslyIssuedCommand = commandProcessor
						.getPreviouslyIssued();
				if (!previouslyIssuedCommand.isEmpty()) {
					textField.setText(previouslyIssuedCommand);
				}
			}
		});
		
		//TODO EC - Not working?
		textField.getActionMap().put("downKey", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String laterIssuedCommand = commandProcessor.getLaterIssued();
				if (!laterIssuedCommand.isEmpty()) {
					textField.setText(laterIssuedCommand);
				}
			}
		});

		foregroundPanel.add(textField);

		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		//TODO EC asks.. What is this for?
		add(Box.createRigidArea(new Dimension(0, 10)));

		commandOutputLabel = new JLabel(EMPTY_STRING, JLabel.LEFT); 
		commandOutputLabel.setFont(COMMAND_OUTPUT_FONT);
		commandOutputLabel.setForeground(COMMAND_OUTPUT_FONT_COLOR);
		//commandOutputLabel.setAlignmentX(Component.LEFT_ALIGNMENT);//TODO Not working
		commandOutputLabel.setPreferredSize(COMMAND_OUTPUT_LABEL_DIMENSION);
		add(createControlPanel());
		add(commandOutputLabel);
		add(foregroundPanel);
		// add(layeredPane);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				textField.requestFocus();
			}
		});
	}

	class MyCellRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			String text = value.toString();
			// text = HTML_1 + String.valueOf(width) + HTML_2 +
			// value.toString()+ HTML_3;

			return super.getListCellRendererComponent(list, text, index,
					isSelected, cellHasFocus);

		}

	}

	public Component createControlPanel() {
		JLabel hdrLabel = new JLabel();
		try{
			ImageIcon hdr = new ImageIcon(this.getClass().getResource(HDR_IMAGE_SRC));
			hdrLabel = new JLabel(hdr); //TODO hdrLabel Dimension
			hdrLabel.setPreferredSize(HDR_IMAGE_DIMENSION);
			hdrLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		}catch(NullPointerException e){
			logger.log(Level.WARNING, "Could not load image");
		}
		verticalBox = Box.createVerticalBox();

		verticalBox.add(hdrLabel);
		verticalBox.add(listPane);
		verticalBox.setPreferredSize(new Dimension(180, 250));
		verticalBox.setBorder(BorderFactory.createTitledBorder(BORDER_TITLE));
		return verticalBox;

	}

	public void actionPerformed(ActionEvent evt) {
		String command = textField.getText();

		String output = commandProcessor.processCommand(command);
		output = output.replaceAll("null|",""); //TODO TEMP SOLUTION
		commandOutputLabel.setText(output);
		tasklist.setModel(commandProcessor.getCurrentListModelOfTasks());
		// refreshCurrentList(commandProcessor.getCurrentListModelOfTasks());

		textField.setText(EMPTY_STRING);
		System.out.println("The alignment for commandOutput is " + commandOutputLabel.getHorizontalAlignment());

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
		frame.setMinimumSize(MIN_FRAME_DIMENSION);

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
					FileHandler fileHandler = new FileHandler("app.log", true);
					logger.addHandler(fileHandler);
					logger.log(Level.INFO, "Starting GUI");
					createAndShowGUI();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}