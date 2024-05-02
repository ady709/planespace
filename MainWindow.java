package orbit;

import java.awt.Component;

import java.awt.event.WindowAdapter;


//import java.awt.EventQueue;

import javax.swing.JFrame;

public class MainWindow extends JFrame {
	
	private static final long serialVersionUID = 1L;

	MainWindow(String title,Component plane, Input input){
		setTitle(title);
		add(plane);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		this.setLocationRelativeTo(null);
		addKeyListener(input);
		addMouseListener(input);
		addMouseMotionListener(input);
		
		setVisible(true);
		requestFocus();

		
		
		addWindowListener(new WindowAdapter() {
			@Override 
			public void windowActivated(java.awt.event.WindowEvent arg0) {
				MainWindow.this.requestFocus();
			}
			
			@Override
			public void windowClosing(java.awt.event.WindowEvent arg0) {
				System.out.println("Good bye!");
			}
		});
		
	}


	
}
