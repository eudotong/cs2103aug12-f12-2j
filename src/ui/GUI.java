package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import commandLogic.CommandProcessor;

public class GUI extends JPanel implements ActionListener {
	private static final String EMPTY_STRING = "";
	private static final String BORDER_TITLE = "Jimi - Task Manager";
	private static final String FRAME_NAME = "Jimi";
	private static final String BACKGROUND_IMG = "images/bg.gif";
	private static final String HDR_IMG = "images/hdr.png";
	private static final long serialVersionUID = 1L;

	  
	        
	private Border empty = BorderFactory.createEmptyBorder();
	JList<String> jlist;
	JScrollPane listPane;
	JTextField textField = new JTextField(32);
	CommandProcessor commandProcessor;
	JLabel commandOutputLabel = null;
	Box verticalBox;

	public GUI() {
		try {
			commandProcessor = new CommandProcessor();
			jlist = new JList<String>(commandProcessor.getCurrentListModelOfTasks());
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		jlist.setVisibleRowCount(4);
		Font displayFont = new Font("Serif", Font.BOLD, 14);
		jlist.setFont(displayFont);
		listPane = new JScrollPane(jlist);
		MyCellRenderer cellRenderer = new MyCellRenderer();
	    jlist.setCellRenderer(cellRenderer);
	    jlist.setFixedCellHeight(30);
		
		ImageIcon icon = createImageIcon(BACKGROUND_IMG);
		JLabel bgLabel = new JLabel(icon);
		bgLabel.setSize(bgLabel.getPreferredSize());

		// Create and set up the layered pane.
		JPanel forgroundPanel = new JPanel(new GridBagLayout());
		forgroundPanel.setOpaque(false);
		forgroundPanel.setPreferredSize(new Dimension(400, 50));

		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setPreferredSize(bgLabel.getPreferredSize());
		layeredPane.setAlignmentX(Component.CENTER_ALIGNMENT);
		layeredPane.add(forgroundPanel, JLayeredPane.PALETTE_LAYER);

		textField.setBackground(new java.awt.Color(220, 219, 219));
		textField.setBorder(empty);
		textField.addActionListener(this);

		forgroundPanel.add(textField);

		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		add(Box.createRigidArea(new Dimension(0, 10)));

		commandOutputLabel = new JLabel(EMPTY_STRING);
		commandOutputLabel.setFont(new Font("Courier", Font.BOLD, 12));
		commandOutputLabel.setForeground(Color.red);
		//commandOutputLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		commandOutputLabel.setPreferredSize(new Dimension(100, 10));
		add(createControlPanel());
		add(commandOutputLabel);
		add(forgroundPanel);
		// add(layeredPane);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				textField.requestFocus();
			}
		});
	}
//	
//	class LoadFont {
//        URL fontUrl = new URL("http://www.webpagepublicity.com/" +
//                "free-fonts/a/Airacobra%20Condensed.ttf");
//            Font font = Font.createFont(Font.TRUETYPE_FONT, fontUrl.openStream());
//            font = font.deriveFont(Font.PLAIN,20);
//            GraphicsEnvironment ge =  GraphicsEnvironment.getLocalGraphicsEnvironment();
//            ge.registerFont(font);
//
//            JLabel l = new JLabel(
//                "The quick brown fox jumped over the lazy dog. 0123456789");
//            l.setFont(font);
//            JOptionPane.showMessageDialog(null, l);
//        }
	
	class MyCellRenderer extends DefaultListCellRenderer {
		@Override
		   public Component getListCellRendererComponent(JList list, Object value,
		         int index, boolean isSelected, boolean cellHasFocus) {
			   String text = value.toString();
			  // text = HTML_1 + String.valueOf(width) + HTML_2 + value.toString()+ HTML_3;
					   		      
		      return super.getListCellRendererComponent(list, text, index, isSelected,
		            cellHasFocus);
		      
		   }

		}

	
	public Component createControlPanel() {

		ImageIcon hdr = createImageIcon(HDR_IMG);
		JLabel hdrLabel = new JLabel(hdr);
		hdrLabel.setPreferredSize(new Dimension(180, 70));

		verticalBox = Box.createVerticalBox();

		verticalBox.add(hdrLabel);
		verticalBox.add(listPane);
		verticalBox.setPreferredSize(new Dimension(180, 250));
		verticalBox.setBorder(BorderFactory.createTitledBorder(BORDER_TITLE));
		return verticalBox;

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
		jlist.setModel(commandProcessor.getCurrentListModelOfTasks());
		//refreshCurrentList(commandProcessor.getCurrentListModelOfTasks());

		textField.setText(EMPTY_STRING);

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