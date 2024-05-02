package orbit;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

//DOES NOT WORK!


public class CanvasView extends Canvas implements Painter{
	private static final long serialVersionUID = 1L;
	
	private PaintHandler paintHandler; 

	public CanvasView(PaintHandler paintHandler) {
		setBackground(Color.BLACK);
		this.setPreferredSize(new Dimension(1000, 800));
		this.paintHandler = paintHandler;
		setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
	}


	public void redraw() {
		
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			bs = this.getBufferStrategy();
		}	
		
		do {
			do {	

				Graphics g = bs.getDrawGraphics();
				//rendering takes place here
				g.setColor(getBackground());
				g.fillRect(0,0,getWidth(),getHeight());

				paintHandler.redraw((Graphics2D)g);

				g.dispose();
			} while (bs.contentsRestored());
			bs.show(); 
		} while(bs.contentsLost());

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
