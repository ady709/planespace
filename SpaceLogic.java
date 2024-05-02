package orbit;



import java.util.ArrayList;
//import java.util.LinkedList;


public class SpaceLogic extends Thread{
	private double timePerSec = 60*60;
	private double[] timePerSecOptions = {0, 1, 2, 5, 10, 30, 60, 2*60, 5*60, 10*60, 30*60, 60*60, 2*60*60, 5*60*60, 10*60*60, 
										  24*60*60, 2*24*60*60, 5*24*60*60, 10*24*60*60, 20*24*60*60, 50*24*60*60, 100*24*60*60,
										  200*24*60*60, 365.25*24*60*60, 2*365.25*24*60*60};
	private int timePerSecOptionsIndex = 11;
	private double timePerFrame=0;
	private int framesPerSecCounter=0;
	private float maxTimePerFrame = 300;//0.5f;
	private boolean runs = false;
	private boolean feedRequested = false;
	private boolean requestSelectNearestBody = false;
	private boolean requestSelectNext = false;
	private boolean requestSelectPrev = false;
    private boolean requestPickUpBody = false;
    private boolean requestControl = false;
    private boolean requestClear = false;
    private double reqNearestBodyX = 0, reqNearestBodyY = 0;

    private boolean requestDropDragged = false;
    private DoubleVector dropSpeed = new DoubleVector (0, 0);
    
	private int reset=0; boolean keepstate;
	//private LinkedList <Body> bodies = new LinkedList<Body>(); //changed for arraylist, should be faster.
	private ArrayList <Body> bodies = new ArrayList<Body>();
	private ArrayList <Body> frozenBodies = new ArrayList<Body>();
	private ArrayList <Body> toBeRemoved = new ArrayList<Body>();
	private ArrayList <Body> groupSelection = new ArrayList<Body>();

	private int spawn = 0;
	private ArrayList <Body> draggedBodies = new ArrayList<Body>();
	public boolean spawning = false, getSpawns = false, spawnListPopulated = false;
	public boolean dragging = false;

	private double avgTimePerFrame=0, avgTimePerFrameTtl=0, timePerRealSec=0;
	private double spaceElapsedTime = 0;

	private Body selectedBody;
	private Body controlledBody;
	private DoubleVector controlAcc;
	
	private boolean requestAddSingleBodyToSelection = false;
	private DoubleVector requestAddSingleBodyToSelectionXY;
	
	private boolean requestAddGroupToSelection = false;
	private DoubleVector addGroupToSelectionStart, addGroupToSelectionEnd;
	
	private boolean requestRemoveSelected = false;
	


	private PaintHandler paintHandler;
	private Input input;

	long systemElapsedTime;
	
	public SpaceLogic() {
	}



	public void run() {
		System.out.println("Starting space " );
		runs = true;

		long lastTime = System.nanoTime();

		
		long counter = System.nanoTime();
		int framesPerSecCounter=0;

		while(runs) {
			if(draggedBodies.size() > 0) spawnListPopulated = true; 
			else spawnListPopulated = false; 


			interactBodies();

			speedUpBodies();

			moveBodies();

			//clone bodies frozen in time for drawing
			if (feedRequested) feedBodiesToPaintHandler();//all together, existing and spawning

			//clear acc of all bodies for next frame
			clearAccAll();

			checkEvents();

			framesPerSecCounter++;   	//Count cycles per second
			avgTimePerFrameTtl += timePerFrame;   	//count average time per cycle	

			//1 second timer
			if(System.nanoTime()-counter >= 1e9){
				this.framesPerSecCounter = framesPerSecCounter;
				this.avgTimePerFrame = avgTimePerFrameTtl / framesPerSecCounter;
				framesPerSecCounter = 0;
				timePerRealSec = avgTimePerFrameTtl;
				avgTimePerFrameTtl = 0;
				counter = System.nanoTime();
			}
			//determine time for next frame
			systemElapsedTime = System.nanoTime()-lastTime;
			lastTime = System.nanoTime();
			timePerFrame = (systemElapsedTime*1e-9) * timePerSec;
			if(timePerFrame > maxTimePerFrame) {
				timePerFrame=maxTimePerFrame;
			}
			spaceElapsedTime += timePerFrame;

		} //game loop end





	}



	private void interactBodies() {

		for (Body thisBody : bodies) { // interaction loop

			if(thisBody.beingDragged()) {		//
				thisBody.setPosition(thisBody.getRelativePosition().getSum(paintHandler.getMousePointM()));
			}

			for (Body thatBody : bodies) { // interact with all other bodies

				if (thisBody != thatBody && !thatBody.getCollided() // don't interact with self or collided bodies
						&& !(thisBody.beingDragged() && !thatBody.beingDragged())   //don't interact dragged bodies with existing bodies
						&& !(!thisBody.beingDragged() && thatBody.beingDragged() && !input.middleMouse)	) { //don't interact existing body with dragged body unless middle mouse button pressed
					if(thatBody.beingDragged()) {
						thatBody.setPosition(thatBody.getRelativePosition().getSum(paintHandler.getMousePointM()));
					}

					if (thisBody.collidesWithOther(thatBody) && !thatBody.getCollided() 
							&& !thisBody.getCollided()  // check for collision, don't collide with bodies that have collided already
							&& !(thisBody.beingDragged() && !thatBody.beingDragged()) ) { // don't collide dragged bodies with existing ones

						if (thisBody.getWeight() <= thatBody.getWeight()) { // lighter body will be removed, it will not interact with others
							thisBody.setCollided(true);
							this.toBeRemoved.add(thisBody);
							thatBody.collision(thisBody);
							System.out.println(new FormattedTime(getSpaceElapsedTime()).timeText + ": Removing " + thisBody.getName() + " due to collision with " + thatBody.getName());
						} else if (!thatBody.beingDragged()){
							thatBody.setCollided(true);
							this.toBeRemoved.add(thatBody);
							thisBody.collision(thatBody);
							System.out.println(new FormattedTime(getSpaceElapsedTime()).timeText + ": Removing " + thatBody.getName() + " due to collision with " + thisBody.getName());
						} 

					} else {
						thisBody.interact(thatBody);  //gravitational interaction only if not colliding 
					}

				}
			} // thisBody cycled through other bodies



		}//interaction loop end

		removeBodies();					//remove bodies that collided



	}

	private void speedUpBodies(){
		//speed up bodies
		for(Body thisBody : bodies) {
			thisBody.speedUpBody(this.timePerFrame);

		}
		//add speed to controlled body
		if (controlAcc != null && controlledBody != null) {
			controlledBody.addSpeed(controlAcc);
			for(Body body : groupSelection) {
				if(body==controlledBody) continue;
				body.addSpeed(controlAcc);
			}
			controlAcc = null;
		}
	}

	private void moveBodies() {
		for (Body thisBody : bodies) { //move loop
			thisBody.moveBody(this.timePerFrame);	  		//move existing bodies
			if(thisBody.getPosition().getDistance()>1e15){	//check if body is getting too far
				this.toBeRemoved.add(thisBody);				//and remove it
				System.out.println(new FormattedTime(getSpaceElapsedTime()).timeText + ": Removing " + thisBody.getName() + " due to long distance from 0,0" + "(" + String.valueOf(thisBody.getPosition().getDistance()) + ")" );
			}
		}

		removeBodies();
	}//moveBodies end


	private void feedBodiesToPaintHandler(){

		if(frozenBodies!=null)frozenBodies.clear();



		for (Body thisBody : bodies) { //copy existing bodies
			Body frozenBody = new Body(thisBody);
			frozenBodies.add(frozenBody);
			if(thisBody==selectedBody)paintHandler.setSelectedBody(frozenBody);
			if(thisBody==controlledBody)paintHandler.setControlledBody(frozenBody);
			if(groupSelection.contains(thisBody)) {
				frozenBody.setGroupSelection(true); 
			} else {
				frozenBody.setGroupSelection(false);
			}

			if(selectedBody == null) {
				paintHandler.setSelectedBody(null);
			}

			if(controlledBody == null) {
				paintHandler.setControlledBody(null);

			}
		}
		this.paintHandler.setBodies(frozenBodies, groupSelection.size());
		feedRequested=false;
	}


	private void checkEvents() {
		if(requestSelectNearestBody) {
			selectBody(getNearestBody(reqNearestBodyX, reqNearestBodyY));
			requestSelectNearestBody = false;
		}
		
		if(requestPickUpBody) {
			pickUpBody(getNearestBody(reqNearestBodyX, reqNearestBodyY));
			requestPickUpBody = false;
		}
		
		if(requestDropDragged) {
			dropDragged(dropSpeed);
			requestDropDragged = false;
		}
		
		if(requestSelectNext) {
			selectionUp();
			requestSelectNext = false;
		}
		
		if(requestSelectPrev) {
			selectionDown();
			requestSelectPrev = false;
		}

		//reset
		if(reset>0) {
			clearEvents();
			InitState.initState(reset,keepstate, this, paintHandler);
			reset=0;
			if(!keepstate) this.spaceElapsedTime = 0;
			if (spawning && !keepstate)getSpawns = true; //re-acquire spawn after bodies list was cleared
		}

		//get bodies from spawner
		if (getSpawns) {
			bodies.removeAll(draggedBodies);
			draggedBodies.clear();
			draggedBodies = Spawner.getBodies(spawn);
			bodies.addAll(draggedBodies);
			getSpawns = false;
		} 
		if (!spawning && draggedBodies.size() > 0  && !dragging) {
			bodies.removeAll(draggedBodies);
			draggedBodies.clear();
		}

		if(requestControl) {
			requestControl = false;
			controlBodies();
		}
		
		if(requestClear) {
			requestClear = false;
			this.clearEvents();
		}
		
		if(requestAddSingleBodyToSelection) {
			addSingleBodyToSelection();
			requestAddSingleBodyToSelection = false;	
		}
		
		if(requestAddGroupToSelection) {
			addGroupToSelection();
		}
		
		if(requestRemoveSelected) {
			removeSelected();
		}
		
	}

	private void clearAccAll() {
		for (Body body : bodies) {
			body.resetAcc();
		}
	}

	public void clearList() {
		if (this.bodies!=null&&this.bodies.size()>0) {
			bodies.clear();
		}
	}
	

	private void removeBodies() {
		this.bodies.removeAll(toBeRemoved);
		if(toBeRemoved.contains(selectedBody)) selectedBody = null;
		this.groupSelection.removeAll(toBeRemoved);
		if(toBeRemoved.contains(controlledBody)) {
			controlledBody = null;
			//this.groupSelection.clear();
		}
		
		this.toBeRemoved.clear();
	}

	private void selectionUp() {
		selectedBody = getNextBody(selectedBody);
	}

	private void selectionDown() {
		selectedBody = getPrevBody(selectedBody);
	}

	public void requestSelectNext() {
		requestSelectNext = true;
	}
	public void requestSelectPrev() {
		requestSelectPrev = true;
	}
	
	public Body getSelectedBody() {
		return selectedBody;
	}

	private Body getNearestBody(double x, double y) {
		double distance = 0;
		Body nearestBody = null;
		for (Body body : bodies) {
			if(1/body.getDistanceToXY(x, y)>distance){
				nearestBody = body;
				distance = 1/body.getDistanceToXY(x, y);
			}
		}
		return nearestBody;
	}

	private Body getHeaviestBody(ArrayList<Body> list) {
		Body heaviestBody = null;
		Double massKg = 0.0;
		for (Body body : list) {
			if(body.getWeight() > massKg) {
				heaviestBody = body;
				massKg = body.getWeight();
			}
		}
		return heaviestBody;
	}
	
	public void requestSelectNearestBody(double x, double y) {
		reqNearestBodyX = x;
		reqNearestBodyY = y;
		requestSelectNearestBody = true;
	}

	public void selectBody (Body body) {
		if(selectedBody == body) {
			selectedBody = null;
		} else selectedBody = body;
	}

	private Body getNextBody(Body body) {
		if(!bodies.isEmpty()) {
			if(bodies.indexOf(body)+1 < bodies.size() && body != null) {
				return bodies.get(bodies.indexOf(body)+1);
			} else {
				return bodies.get(0);
			}
		} else return null;
	}

	private Body getPrevBody(Body body) {
		if(!bodies.isEmpty()) {
			if(bodies.indexOf(body) > 0 && body != null) {
				return bodies.get(bodies.indexOf(body) - 1);
			} else {
				return bodies.get(bodies.size()-1);
			}
		} else return null;
	}

	public void addBody(Body body) {
		if (bodies.isEmpty()) selectedBody = body;
		bodies.add(body);

	}

	public void addPaintHandler (PaintHandler ph) {
		this.paintHandler = ph;
	}
	
	public void addInput(Input input) {
		this.input = input;
	}

	public double getAvgTimePerFrame() {
		return avgTimePerFrame;
	}
	public double getTimePerSec() {
		return timePerSec;
	}

	public double getSpaceElapsedTime() {
		return spaceElapsedTime;
	}


	public void timePerSecUp(){
		if( !(timePerSecOptionsIndex+1 >= timePerSecOptions.length)
				&& timePerFrame<maxTimePerFrame) {
			timePerSecOptionsIndex++;
		}
		timePerSec = timePerSecOptions[timePerSecOptionsIndex];
		
	}
	public void timePerSecDown(){
		if( !(timePerSecOptionsIndex-1 < 0) ) {
			timePerSecOptionsIndex--;
		}
		timePerSec = timePerSecOptions[timePerSecOptionsIndex];
	}
	
	public int getCyclesPerFrame() {
		return (int)framesPerSecCounter;
	}
	

	public void setRunning() {
		this.runs=true;
	}
	public void notRunning() {
		this.runs=false;
	}
	public void resetState(int state,boolean keep) {
		this.reset = state;
		this.keepstate = keep;
	}

	public double timePerRealSec() {
		return timePerRealSec;
	}

	public void requestFeed() {
		this.feedRequested = true;
	}

	public void setSpawn(int spawn) {
		this.spawn = spawn;
		if (spawn>0) {
			spawning = true;
			getSpawns = true;
			dragging = true;
		} else {
			//if(spawning)dragging = false;
			dragging = false;
			spawning = false;
			getSpawns = false;
		}
	}

	public void requestDropDragged(DoubleVector speed) {
		dropSpeed.set(speed);
		requestDropDragged = true;
	}
	
	public void dropDragged(DoubleVector speed) {
		for(Body body : bodies) {
			if(body.beingDragged()) {
				body.setDragged(false);
				body.addSpeed(speed);
				body.setRelativePosition(body.getPosition());
			}
		}
		spawning = false;
		getSpawns = false;
		dragging = false;
		spawn = 0;
		draggedBodies.clear();



	}



	public void pickUpBody(Body body) {
		body.setDragged(true);
		body.setRelativePosition(new DoubleVector(0, 0));
		body.setSpeed(new DoubleVector(0, 0));

		for(Body groupBody : groupSelection) {
			groupBody.setDragged(true);
			groupBody.setSpeed(new DoubleVector(0,0));
			DoubleVector grBdRelPos = new DoubleVector(groupBody.getPosition());
			grBdRelPos.subtractOther(body.getPosition());
			groupBody.setRelativePosition(grBdRelPos);
		}
		
		dragging = true;

		//test
		draggedBodies.add(body);
		draggedBodies.addAll(groupSelection);
		
	}
	
	public void requestPickUpBody(double x, double y) {
		reqNearestBodyX = x;
		reqNearestBodyY = y;
		requestPickUpBody = true;
	}

	public void requestControl() {
		requestControl = true;
	}
	
	public void controlBodies() {
		if(!dragging && selectedBody==null) return;
		if(dragging) {
	
			//select heaviest as controlled
			controlledBody = getHeaviestBody(draggedBodies);
			controlledBody.setDrawVectors(true);
			draggedBodies.remove(controlledBody);
			//others to following
			groupSelection.addAll(draggedBodies);
			//drop the bodies to space
			this.dropDragged(new DoubleVector(0,0));
			
			
		} else {
			if(controlledBody==null) {
				controlledBody = selectedBody;
				controlledBody.setDrawVectors(true);
			} else {
				controlledBody.setDrawVectors(false);
				controlledBody = null;
			}
			
		}
		
		

	}
	
	public Body getControlledBody() {
		return controlledBody;
	}
	
	public void addControlAcc(DoubleVector controlAcc) {
		this.controlAcc = new DoubleVector(controlAcc);
	}
	
	public void requestClear() {
		requestClear = true;
	}
	
	private void clearEvents() {
		if(spawn==0 && controlledBody==null)groupSelection.clear();
		//clear dragged bodies and spawning
		spawning = false;
		getSpawns = false;
		dragging = false;
		spawn = 0;
		bodies.removeAll(draggedBodies);
		draggedBodies.clear();
		//clear control
		if(controlledBody!=null) {
			controlledBody.setDrawVectors(false);
			controlledBody = null;
		}
		
		
	}
	
	public void requestAddSingleBodyToSelection(DoubleVector XY) {
		requestAddSingleBodyToSelectionXY = new DoubleVector(XY);
		requestAddSingleBodyToSelection = true;
	}
	
	private void addSingleBodyToSelection() {
		Body body = getNearestBody(requestAddSingleBodyToSelectionXY.getX(), requestAddSingleBodyToSelectionXY.getY());
		//if(body != selectedBody) {
			if(groupSelection.contains(body)) {
				groupSelection.remove(body);
			} else {				
				groupSelection.add(body);
			}
		//}
		
	}
	
	public void requestAddGroupToSelection(DoubleVector start, DoubleVector end) {
		requestAddGroupToSelection = true;
		addGroupToSelectionStart = new DoubleVector(start);
		addGroupToSelectionEnd = new DoubleVector(end);
	}
	
	private void addGroupToSelection() {
		requestAddGroupToSelection = false;
		
		for(Body body : bodies) {
			if( body.beingDragged() )  continue;
			
			if(body.isWithin(addGroupToSelectionStart, addGroupToSelectionEnd) ) {
				if(groupSelection.contains(body))groupSelection.remove(body); else groupSelection.add(body);				
			}
			
			if(selectedBody==null) selectedBody=getHeaviestBody(groupSelection);
			
		}
		
	}
	
	public void requestRemoveSelected() {
		requestRemoveSelected = true;
	}
	
	private void removeSelected() {
		requestRemoveSelected = false;
		if(selectedBody!=null) this.toBeRemoved.add(selectedBody);
		if(!groupSelection.isEmpty()) this.toBeRemoved.addAll(groupSelection);
		removeBodies();
		
	}
}




