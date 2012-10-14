/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package ui;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;

import commandLogic.CommandProcessor;

public class GUI extends JPanel implements ActionListener {
	private static final String ERROR_COULD_NOT_ACCESS_STORAGE = "Error: Could not access storage.";
	private static final String WELCOME_MESSAGE = "Welcome to Jimi, your friendly neighbourhood task manager.";
	private static final long serialVersionUID = 1L;
	protected JTextArea textArea;
	protected JTextField textField = new JTextField(30);
	private CommandProcessor commandProcessor;
	private final static String NEW_LINE = "\n";

	public GUI() {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		// Create and set up the layered pane.
		textField.addActionListener(this);
		JPanel layeredPane1 = new JPanel();
		layeredPane1.setBorder(BorderFactory.createTitledBorder("input"));
		layeredPane1.add(textField);
		layeredPane1.setOpaque(true);

		JPanel layeredPane = new JPanel();
		JLabel bgLabel = new JLabel();
		ImageIcon icon = createImageIcon("images/bg.gif");

		// Create and add the Duke label to the layered pane.
		bgLabel = new JLabel(icon);
		if (icon == null) {
			System.err.println("Picture not found; using white rectangle instead.");
			bgLabel.setOpaque(true);
			bgLabel.setBackground(Color.cyan);
		}
		layeredPane.add(bgLabel);
		// layeredPane.moveToBack(bgLabel);

		// Add control pane and layered pane to this JPanel.
		add(Box.createRigidArea(new Dimension(0, 10)));
		add(createControlPanel());
		add(layeredPane1);
		add(layeredPane);
		try{
			commandProcessor = new CommandProcessor();
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

		controls.setBorder(BorderFactory.createTitledBorder("output"));
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

		// Add contents to the window.
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