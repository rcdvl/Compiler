package com.virtualmachine;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;

import com.virtualmachine.window.MainWindow;

public class Main {

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}

		MainWindow window = new MainWindow();
		window.setVisible(true);		
	}
}
