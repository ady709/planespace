package orbit;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;


public class JPanelView extends JPanel implements Painter { 

	private static final long serialVersionUID = 1L;

	private PaintHandler paintHandler; 



	//Constructor
	public JPanelView(PaintHandler paintHandler){
		setBackground(Color.black);
		setDoubleBuffered(true);
		this.setPreferredSize(new Dimension(1200,800));
		this.paintHandler = paintHandler;
		setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D gg = (Graphics2D)g.create();

		
		paintHandler.redraw(gg);
		gg.dispose();


	}



	public void redraw() {
		repaint();

	}

	public void hideMouse() {
		setCursor(getToolkit().createCustomCursor(
	            new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0),
	            "null"));
	}
	
	public void showMouse() {
		//setCursor(Cursor.getDefaultCursor());
		setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		
	}



}


