package orbit;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Input implements KeyListener,	MouseListener, MouseMotionListener {
	
	SpaceLogic space;
	PaintHandler paintHandler;
	//keyboard
	public boolean panLeft=false, panRight=false, panUp=false, panDown=false, zoomIn=false, zoomOut=false;
	public boolean shift=false, ctrl=false;
	public boolean noModifier;
	//mouse
	public boolean leftMouse = false, rightMouse=false, middleMouse = false;
	public Point mouseXY = new Point();
	

	
	public Input(SpaceLogic space) {
		this.space = space;
	}
	
	public void addPaintHandler (PaintHandler ph) {
		this.paintHandler = ph;
	}

    @Override
	public void keyPressed(KeyEvent k) {
    	//panning
		if(k.getKeyCode()==KeyEvent.VK_UP && k.getKeyCode()!=KeyEvent.VK_DOWN) panUp = true;
		if(k.getKeyCode()==KeyEvent.VK_DOWN && k.getKeyCode()!=KeyEvent.VK_UP) panDown = true;
		if(k.getKeyCode()==KeyEvent.VK_LEFT && k.getKeyCode()!=KeyEvent.VK_RIGHT) panLeft = true;
		if(k.getKeyCode()==KeyEvent.VK_RIGHT && k.getKeyCode()!=KeyEvent.VK_LEFT) panRight = true;
		//zooming
		if(k.getKeyCode()==KeyEvent.VK_PAGE_UP && k.getKeyCode()!=KeyEvent.VK_PAGE_DOWN) zoomOut = true;
		if(k.getKeyCode()==KeyEvent.VK_PAGE_DOWN && k.getKeyCode()!=KeyEvent.VK_PAGE_UP) zoomIn = true;
		//if(k.getKeyCode()==KeyEvent.VK_SHIFT) shift = true;
		
	}
	@Override
	public void keyReleased(KeyEvent k) {
		//panning
		if(k.getKeyCode()==KeyEvent.VK_UP) panUp = false;
		if(k.getKeyCode()==KeyEvent.VK_DOWN) panDown = false;
		if(k.getKeyCode()==KeyEvent.VK_LEFT) panLeft = false;
		if(k.getKeyCode()==KeyEvent.VK_RIGHT) panRight = false;
		//zooming
		if(k.getKeyCode()==KeyEvent.VK_PAGE_UP) zoomOut = false;
		if(k.getKeyCode()==KeyEvent.VK_PAGE_DOWN) zoomIn = false;
		if(k.getKeyCode()==KeyEvent.VK_DELETE) space.requestRemoveSelected();
	
	}
	
	@Override
	public void keyTyped(KeyEvent k) { 
		//time compression
		if(k.getKeyChar()=='+' || k.getKeyChar()=='=') space.timePerSecUp();
		if(k.getKeyChar()=='-') space.timePerSecDown();
		//selected body
		if(k.getKeyChar()=='[') space.requestSelectPrev();
		if(k.getKeyChar()==']') space.requestSelectNext();
		//lock view
		if(k.getKeyChar()=='l') paintHandler.toggleLockView();
		//reset state
		if(k.getKeyChar()=='1') space.resetState(1,false);
		if(k.getKeyChar()=='2') space.resetState(2,false);
		if(k.getKeyChar()=='3') space.resetState(3,false);
		if(k.getKeyChar()=='4') space.resetState(4,false);
		if(k.getKeyChar()=='5') space.resetState(5,false);
		if(k.getKeyChar()=='!') space.resetState(1,true);
		if(k.getKeyChar()=='@') space.resetState(2,true);
		if(k.getKeyChar()=='#') space.resetState(3,true);
		if(k.getKeyChar()=='$') space.resetState(4,true);
		if(k.getKeyChar()=='%') space.resetState(5,true);
		//toggle drawVectors
		if(k.getKeyChar()=='v') if(space.getSelectedBody() != null) space.getSelectedBody().toggleDrawVectors();
		//select body nearest to view center
		if(k.getKeyChar()=='n') space.requestSelectNearestBody(paintHandler.getMousePointM().getX(), paintHandler.getMousePointM().getY());
		//spawner
		if(space.getControlledBody()==null && !(space.dragging && !space.spawning) ) {
			if(k.getKeyChar()=='.') space.setSpawn(Spawner.spawnIndexUp());
			if(k.getKeyChar()==',') space.setSpawn(Spawner.spawnIndexDown());
			if(k.getKeyChar()=='/') space.setSpawn(Spawner.getSpawnIndex());
		}
		//control
		if(k.getKeyChar()=='c') space.requestControl();
		//clear events
		if(k.getKeyChar()==' ') space.requestClear();
		
	}

	
	private void checkModifiers(MouseEvent e) {
		if( e.isAltDown() ||  e.isControlDown() ||  e.isShiftDown() )noModifier=false; else noModifier=true;
		if(!e.isAltDown() && !e.isControlDown() &&  e.isShiftDown() )shift=true; else shift=false;
		if(!e.isAltDown() &&  e.isControlDown() && !e.isShiftDown() )ctrl=true; else ctrl=false;
		
	}
	
	private boolean LMBFree() {
		boolean LMBFree;
		if(space.dragging) LMBFree = false; else LMBFree = true;
		return LMBFree;
	}
	
	//Mouse
	@Override
	public void mouseClicked(MouseEvent e) {
		checkModifiers(e);
		
		
		
		
		DoubleVector XY = new DoubleVector(paintHandler.getMousePointM());
		//select single body as primary selected body:
		if(noModifier && e.getButton()==MouseEvent.BUTTON1 && LMBFree()) {
			space.requestSelectNearestBody(XY.getX(), XY.getY());
		}
		//add single body to groupSelection:
		if(shift && e.getButton() == MouseEvent.BUTTON1 && LMBFree()) {
			space.requestAddSingleBodyToSelection(XY);
		}
		
		
		//pick up single body for dragging
		if (ctrl && e.getButton() == MouseEvent.BUTTON1 && LMBFree()) {
			space.requestPickUpBody(XY.getX(), XY.getY());
		}

		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		checkModifiers(e);
		if (e.getButton() == MouseEvent.BUTTON1) leftMouse=true;
		if (e.getButton() == MouseEvent.BUTTON2) middleMouse=true;
		if (e.getButton() == MouseEvent.BUTTON3) rightMouse=true;
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
		checkModifiers(e);
		
		if (e.getButton() == MouseEvent.BUTTON1) leftMouse=false;
		if (e.getButton() == MouseEvent.BUTTON2) middleMouse=false;
		if (e.getButton() == MouseEvent.BUTTON3) rightMouse=false;
		
		if (e.getButton() == MouseEvent.BUTTON1 && space.dragging && noModifier) paintHandler.dropDragged(); 
	}
	

	//Mouse movement
	@Override
	public void mouseDragged(MouseEvent e) {
		mouseXY = e.getPoint();
		
		/* does not work, needs different approach
		if(LMBFree() && leftMouse) {
			paintHandler.setView(paintHandler.getMousePointM().getX(), paintHandler.getMousePointM().getY());
		}*/

		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseXY = e.getPoint();

	}
	
	
	
	
	
	
	
	public Point getMousePoint() {
		Point correctedMouseXY = new Point(mouseXY.x-8, mouseXY.y-30); // x-9, y-36  
		return correctedMouseXY;
	}
	


}
