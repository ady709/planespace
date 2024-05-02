package orbit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class PaintHandler implements Runnable {

	private SpaceLogic space;
	private Painter painter;
	private Input input;

	Thread drawThread;
	boolean isRunning = true;
	private double viewX = 0, viewY = 0;
	private double scale = 20000000; // 1000000000
	private int target_fps = 60, fps = 0;
	private Body selectedBody;
	private Body controlledBody;
	private int inGroup = 0;
	private boolean lockView = false;
	private double requestedXm = 0, requestedYm = 0;
	private double lockViewClamp = 1e20; // 1e20; decrease to get smooth panning to selected body, if it only was that smooth...
	int width, height;

	final static float[] dash1 = { 5.0f, 5.0f };
	final static float[] dash2 = { 1.0f, 5.0f };
	final static BasicStroke accStroke = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, dash1,	0);
	final static BasicStroke ttlAccStroke = new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0,	dash2, 0);
	final static BasicStroke speedStroke = new BasicStroke(3.0f);
	final static BasicStroke noStroke = new BasicStroke(1.0f);
	private boolean inspectorDisplayed = false;
	private DoubleVector inspectorInitialPosition = new DoubleVector (0,0);

	//cursor movement and speed
	private DoubleVector[] cursorSpeeds = new DoubleVector[15];
	private int cursorSpeedsIndex = 0;
	private DoubleVector cursorXY = new DoubleVector(0,0);
	private DoubleVector lastCursorXY = new DoubleVector();
	private double lastSpaceElapsedTime;
	private DoubleVector fineCursorAvgSpeed = new DoubleVector(0, 0);
	private DoubleVector roughCursorAvgSpeed = new DoubleVector(0, 0);
	private DoubleVector controlAcc = new DoubleVector(0,0);
	
	ArrayList<Body> bodies = new ArrayList<Body>();
	boolean synced = false;
	int bodies_size;
	
	//selection box
	private boolean drawingSelectionBox = false;
	private Body selectionBoxStart, selectionBoxEnd;
	

	// Constructor
	public PaintHandler(SpaceLogic space, Input input) {
		this.space = space;
		this.input = input;
		
		clearCursorSpeed();

	}

	public void setBodies(ArrayList<Body> frozenBodies, int inGroup) {
		this.bodies.clear();
		this.bodies.addAll(frozenBodies);
		this.bodies_size = bodies.size();
		this.inGroup = inGroup;
		synced = true;
	}

	public void addPainter(Component painter) {
		this.painter = (Painter) painter;
	}

	public void startDisplay() {
		System.out.println("Starting display ");
		space.requestFeed();
		drawThread = new Thread(this);
		drawThread.start();

	}


	// draw loop
	@Override
	public void run() {
		long lasttime = System.currentTimeMillis();
		long timeCounter = lasttime;
		long pause;
		int fps = 0;

		while (isRunning) {
			width = painter.getWidth();
			height = painter.getHeight();
			
			
			// handle view panning
			panView();

			//get Graphics object for painting
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					painter.redraw();
				}
			});

			refreshCursorInfo();

			
			
			//fps counter
			fps++;
			if (System.currentTimeMillis() - timeCounter >= 1000) {
				timeCounter = System.currentTimeMillis();
				this.fps = fps;
				fps = 0;
			}
			
			//sleep time
			pause = 1000 / target_fps - ((System.currentTimeMillis() - lasttime));
			if (pause > 0) {
				try {
					Thread.sleep(pause);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			lasttime = System.currentTimeMillis();
		}

	}
	
	
	private void panView() {

		if (input.panUp) {
			panUp(4);
		}
		if (input.panDown) {
			panDown(4);
		}
		if (input.panLeft) {
			panLeft(4);
		}
		if (input.panRight) {
			panRight(4);
		}
		if (input.zoomIn) {
			double screenYm = height * scale;
			double screenYm_new = screenYm - screenYm / 1 / target_fps;
			scale *= screenYm_new / screenYm;
		}
		if (input.zoomOut) {
			double screenYm = height * scale;
			double screenYm_new = screenYm + screenYm / 1 / target_fps;
			scale *= screenYm_new / screenYm;
		}

		// check for viewlock
		if (lockView && selectedBody != null) {
			requestedXm = selectedBody.getXm();
			requestedYm = selectedBody.getYm();

			if (selectedBody.getDistanceToXY(viewX, viewY) >= lockViewClamp) {
				if (requestedXm > viewX)
					panRight(1);
				if (requestedXm < viewX)
					panLeft(1);
				if (requestedYm > viewY)
					panDown(1);
				if (requestedYm < viewY)
					panUp(1);
			} else {
				viewX = requestedXm;
				viewY = requestedYm;
			}

		}

	}

	public void redraw(Graphics2D g) {

		if(synced) {
			drawBodies(g);
			checkEvents(g);
		}
		//display row of information at top
		displayInfo(g);
				
		
		//reset synced and request fresh copy of bodies
		synced = false;
		space.requestFeed();
	}
	
	public void drawBodies(Graphics2D g) {


		for (Body thisBody : bodies) { // draw loop

			BodyGraphics bodyG = new BodyGraphics(thisBody, viewX, viewY, scale, width, height);
			if (bodyG.isVisible) {
				// draw body
				g.setColor(bodyG.color);
				g.fillOval(bodyG.posX - bodyG.diameter / 2, bodyG.posY - bodyG.diameter / 2, bodyG.diameter,
						bodyG.diameter);

				// draw vector indicators
				if (thisBody.isDrawVetors()) {
					// speed vector indicator
					g.setStroke(speedStroke);
					g.setColor(Color.GREEN);
					g.drawLine(bodyG.posX, bodyG.posY, bodyG.speedVectorX, bodyG.speedVectorY);// bodyG.speedVectorX,
																								// bodyG.speedVectorY
					// G vector indicator
					g.setColor(Color.MAGENTA);
					g.setStroke(accStroke);
					g.drawLine(bodyG.posX, bodyG.posY, bodyG.accVectorX, bodyG.accVectorY);// bodyG.speedVectorX,
																							// bodyG.speedVectorY
					g.setStroke(noStroke);

				}

				// draw selection outline
				if (thisBody == selectedBody) {
					g.setColor(Color.red);
					g.drawOval(bodyG.posX - bodyG.diameter / 2 - 3, bodyG.posY - bodyG.diameter / 2 - 3,
							bodyG.diameter + 6, bodyG.diameter + 6);
				}

				//draw control vector and set control acc
				if (thisBody==controlledBody) {
					controlAcc.set(input.getMousePoint());
					controlAcc.subtractOther(new DoubleVector(bodyG.posX, bodyG.posY));
					controlAcc.multiplyBy(10);
					g.setStroke(accStroke);
					g.setColor(Color.CYAN);
					g.drawLine(bodyG.posX, bodyG.posY, (int) input.getMousePoint().getX(), (int) input.getMousePoint().getY());
					g.setStroke(noStroke);
					g.drawOval(bodyG.posX - bodyG.diameter / 2 - 4, bodyG.posY - bodyG.diameter / 2 - 4,
							bodyG.diameter + 8, bodyG.diameter + 8);
				}
				if(controlledBody==null)controlAcc.set(new DoubleVector(0,0)); 
				//draw groupSelection bodies outline
				if(thisBody.getGroupSelection()) {
					g.setColor(Color.BLUE);
					g.drawOval(bodyG.posX - bodyG.diameter / 2 - 4, bodyG.posY - bodyG.diameter / 2 - 4,
							bodyG.diameter + 8, bodyG.diameter + 8);
				}
			
			}

		}

	}



	private void checkEvents(Graphics2D g) {
		// check if inspector should be painted
		if (input.rightMouse) {
			if (!inspectorDisplayed) {
				painter.hideMouse();
				inspectorInitialPosition.set(getMousePointM());
			}
			inspectorDisplayed = true;
			drawInspector(g);
		} else if (inspectorDisplayed) {
			painter.showMouse();
			inspectorDisplayed = false;
		}

		//pass controlAcc to space
		if(controlAcc.getDistance() > 0 && input.middleMouse) {
			space.addControlAcc(controlAcc.getMultiply(1.0/fps));
		}

		//selectionBox
		if(!drawingSelectionBox && input.leftMouse && input.shift) {
			selectionBoxStart = new Body("boxStart", 0, 0, new DoubleVector(this.getMousePointM()), new DoubleVector(0,0), false, false);
			drawingSelectionBox = true;
		}
		
		if(drawingSelectionBox && input.leftMouse && input.shift) {
			selectionBoxEnd = new Body("boxEnd", 0, 0, new DoubleVector(this.getMousePointM()), new DoubleVector(0,0), false, false);
			g.setStroke(accStroke);
			g.setColor(Color.BLUE);
			BodyGraphics boxG1 = new BodyGraphics(selectionBoxStart, viewX, viewY, scale, width, height);
			BodyGraphics boxG2 = new BodyGraphics(selectionBoxEnd, viewX, viewY, scale, width, height);
			int x = Math.min(boxG1.posX, boxG2.posX);
			int y = Math.min(boxG1.posY, boxG2.posY);
			int w = Math.abs(boxG2.posX-boxG1.posX);
			int h = Math.abs(boxG2.posY-boxG1.posY);
			g.drawRect(x, y, w, h);
			g.setStroke(noStroke);
		}
		
		if (drawingSelectionBox && !(input.leftMouse && input.shift) ) {
			double x1 = Math.min(selectionBoxStart.getPosition().getX(), selectionBoxEnd.getPosition().getX());
			double y1 = Math.min(selectionBoxStart.getPosition().getY(), selectionBoxEnd.getPosition().getY());
			double x2 = Math.max(selectionBoxStart.getPosition().getX(), selectionBoxEnd.getPosition().getX());
			double y2 = Math.max(selectionBoxStart.getPosition().getY(), selectionBoxEnd.getPosition().getY());
			space.requestAddGroupToSelection(new DoubleVector(x1,y1), new DoubleVector(x2, y2));
			drawingSelectionBox = false;
			
		}
		
	}



	
	private void displayInfo(Graphics2D g) {
		FormattedTime time =  new FormattedTime(Math.round(space.getTimePerSec()) ); 
		FormattedTime time2 = new FormattedTime(space.getSpaceElapsedTime());
		FormattedTime time3 =  new FormattedTime(Math.round(space.timePerRealSec()) );
		g.setColor(Color.green);
		g.drawString(     "Scale 1:" + String.format("%,d",Math.round(scale)) 
						+ "  Time compression: " + String.format("%,d",(int)space.getTimePerSec())
						+ " (1sec = " + time.timeText2 + ")"
						+ "  Cycle time s: " + String.format("%.7f", space.getAvgTimePerFrame()) 
						+ "  Cycles per second: " + space.getCyclesPerFrame() 
						+ "  FPS: " + fps
						+ "  Bodies: " + bodies_size
						+ "  <<<Elapsed time: " + time2.timeText + ">>>"
						+ "  Real time per 1 sec: " + time3.timeText2
						, 5, 15);

		//selectedBody info
		String name, speed, group;
		
		if (inGroup>0) group = "  " + String.valueOf(inGroup) + " bodies in group selection"; else group = "";
		
		if (selectedBody == null) {
			name = "N/A";
			speed = "";
		} else {
			name = selectedBody.getName();
			speed = String.format("%.2f", (selectedBody.getSpeed().getDistance() / 1000) ) + " km/s" ;
			
			g.drawString(     "Selected: " + name
							+ "  Speed: " + speed
							+ group
							, 5, 30);			
		}
		
		//ControlledBody info
		if(controlledBody != null) {
			g.drawString(     "Controlling: " + controlledBody.getName()
					+ "  speed: " + String.format("%.2f", (controlledBody.getSpeed().getDistance() / 1000) ) + " km/s" 
					+ "  acc: " + String.format("%.2f",controlAcc.getDistance()/1000) + " km/s/s"
					, 5, 45);
		}


	}



	private void drawInspector(Graphics2D g) {

		//refreshCursorInfo(); //done in main loop

		Body inspector = new Body("Inspector", (double) 1, (long) (this.width / 25 * scale), cursorXY.getX(), cursorXY.getY(), 0.0d);
		inspector.setDrawVectors(true);
		// draw inspector
		BodyGraphics bodyG = new BodyGraphics(inspector, viewX, viewY, scale, width, height);
		g.setColor(Color.WHITE);
		g.drawOval(bodyG.posX - bodyG.diameter / 2, bodyG.posY - bodyG.diameter / 2, bodyG.diameter, bodyG.diameter);

		//draw speed vector
		inspector.setSpeed(roughCursorAvgSpeed);
		bodyG = new BodyGraphics(inspector, viewX, viewY, scale, width, height);
		g.setColor(Color.GREEN);
		g.setStroke(speedStroke);
		g.drawLine(bodyG.posX, bodyG.posY, bodyG.speedVectorX, bodyG.speedVectorY);

		// G vector indicator
		DoubleVector ttlAcc = new DoubleVector(0, 0);
		for (Body thatBody : bodies) {
			if(thatBody.beingDragged()) continue;
			inspector.interact(thatBody);
			bodyG = new BodyGraphics(inspector, viewX, viewY, scale, width, height);
			g.setColor(Color.MAGENTA);
			g.setStroke(accStroke);
			g.drawLine(bodyG.posX, bodyG.posY, bodyG.accVectorX, bodyG.accVectorY);
			g.setStroke(noStroke);

			// total acceleration vector
			ttlAcc.addOther(inspector.getAcc());
			inspector.resetAcc();
		}
		// total acceleration vector draw
		inspector.setAcc(ttlAcc);
		bodyG = new BodyGraphics(inspector, viewX, viewY, scale, width, height);
		g.setColor(Color.RED);
		g.setStroke(ttlAccStroke);
		g.drawLine(bodyG.posX, bodyG.posY, bodyG.accVectorX, bodyG.accVectorY);
		g.setStroke(noStroke);

		//draw line connecting the initial and current possition
		Body inspectorStartPosition = new Body("InspectorStart", 1.0, (long) 10, inspectorInitialPosition.getX(), inspectorInitialPosition.getY(), 0.0 );
		BodyGraphics bodyG2 = new BodyGraphics(inspectorStartPosition, viewX, viewY, scale, width, height);
		g.setStroke(accStroke);
		g.setColor(Color.GREEN);
		g.drawLine(bodyG2.posX, bodyG2.posY, bodyG.posX, bodyG.posY);
		g.setStroke(noStroke);
		
		DoubleVector dist = new DoubleVector(inspectorInitialPosition);
		dist.subtractOther(inspector.getPosition());
		
		DoubleVector distTextPos = new DoubleVector((double) bodyG.posX ,(double) bodyG.posY );
		distTextPos.subtractOther(new DoubleVector((double) bodyG2.posX ,(double) bodyG2.posY ));
		distTextPos.multiplyBy(0.5);
		distTextPos.addOther(new DoubleVector((double) bodyG2.posX+20 ,(double) bodyG2.posY+20 ));
		
		if(distTextPos.getX()<10 || distTextPos.getX() > width-50 || distTextPos.getY()<50 || distTextPos.getY()>height-50) {
			distTextPos.set(new DoubleVector(5.0,60.0));
		}
		
		g.drawString(distToText(dist.getDistance()), (int) distTextPos.getX(), (int) distTextPos.getY());
	}

	

	
	private void refreshCursorInfo() {
		cursorXY.set(getMousePointM());
		
		double now = space.getSpaceElapsedTime();
		double spaceElapsedTimeDelta = now - lastSpaceElapsedTime;
		lastSpaceElapsedTime = now;
		if(spaceElapsedTimeDelta==0)spaceElapsedTimeDelta=1;
		
		DoubleVector speedXY = new DoubleVector((cursorXY.getX() - lastCursorXY.getX()) / spaceElapsedTimeDelta,
												(cursorXY.getY() - lastCursorXY.getY()) / spaceElapsedTimeDelta);
		lastCursorXY.set(cursorXY);
		
		//this simple approximation works well for inspector
		roughCursorAvgSpeed.addOther(speedXY);
		roughCursorAvgSpeed.multiplyBy(0.5);
		
		//approximation by array of points would be fine for drag&drop
		cursorSpeeds[cursorSpeedsIndex].set(speedXY);
		cursorSpeedsIndex++;
		if(cursorSpeedsIndex>=cursorSpeeds.length)cursorSpeedsIndex=0;
		//get average
		for(int i=0; i<cursorSpeeds.length; i++) {
			fineCursorAvgSpeed.addOther(cursorSpeeds[i]);
		}
		//int l1 = cursorPath.length;
		//double l2 = 1.0 / l1;
		fineCursorAvgSpeed.multiplyBy(1.0/cursorSpeeds.length);
		
	}
	
	private void clearCursorSpeed() {
		roughCursorAvgSpeed.set(new DoubleVector(0,0));
		fineCursorAvgSpeed.set(new DoubleVector(0,0));
		for(int i=0; i<cursorSpeeds.length; i++) {
			cursorSpeeds[i] = new DoubleVector(0, 0);
		}
		
		
	}
	
	


	
	
	
	
	public DoubleVector getCursorXY() {
		return cursorXY;
	}
	
	private void panUp(int fraction) {
		double screenYm = painter.getHeight() * scale;
		viewY -= (screenYm / fraction / target_fps);
	}

	private void panDown(int fraction) {
		double screenYm = painter.getHeight() * scale;
		viewY += (screenYm / fraction / target_fps);
	}

	private void panLeft(int fraction) {
		double screenXm = painter.getWidth() * scale;
		viewX -= (screenXm / fraction / target_fps);
	}

	private void panRight(int fraction) {
		double screenXm = painter.getWidth() * scale;
		viewX += (screenXm / fraction / target_fps);
	}

	public void toggleLockView() {
		lockView = !lockView;
	}

	public double getViewX() {
		return viewX;
	}

	public void setViewX(long viewX) {
		this.viewX = viewX;
	}

	public double getViewY() {
		return viewY;
	}

	public void setViewY(long viewY) {
		this.viewY = viewY;
	}

	public void setView(double x, double y) {
	this.viewX = x;
	this.viewY = y;
	}
	
	public double getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}
	
	public DoubleVector getMousePointM() {
		double x = (input.getMousePoint().x - width / 2) * this.scale + this.viewX;
		double y = (input.getMousePoint().y - height / 2) * this.scale + this.viewY;
		return new DoubleVector(x, y);
	}
	
	public void setSelectedBody(Body body) {
		this.selectedBody = body;
	}

	public void setControlledBody (Body body) {
		this.controlledBody = body;
	}
	
	public void dropDragged() {
		if(space.dragging)space.requestDropDragged(fineCursorAvgSpeed);
	}
	
	public String distToText(Double dist) {
		String distText;
		//1.496e+11 m = 1 AU
		if(dist > 1.496e11/10) {
			distText = String.format("%.2f", dist / 1.496e11 ) + " AU";
		} else if(dist > 1000) {
			distText = String.format("%,d", Math.round(dist / 1000)) + " Km";
		} else {
			distText = String.format("%,d", Math.round(dist)) + " m";
		}
		
		return distText;
	}
	
}
