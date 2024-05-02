package orbit;


public class Body {
	private double massKg=0;
	private double diameterM=0;
	private DoubleVector vector;
	private DoubleVector position;
	private DoubleVector acc;
	private boolean drawVectors = false;

	private String name;
	private final double gc = 6.67408e-11;

	private boolean collided = false;

	private DoubleVector relativePosition;
	private boolean beingDragged;
	
	private boolean groupSelection; //used only in paintHandler

	//basic constructor
	//@ConstructorProperties(value= {"name","mass","diameter","posX","posY","speed"})
	public Body(String name, double massKg, long diameterM, double posXm, double posYm, double speed   ) {
		vector = new DoubleVector(speed, (double)0);
		position = new DoubleVector(posXm, posYm);
		relativePosition = new DoubleVector(posXm, posYm);
		acc = new DoubleVector(0,0);
		this.name = name;
		this.massKg = massKg;
		this.diameterM = diameterM;
		this.beingDragged = false;
	}


	//constructor for cloner
	public Body(Body otherBody) {
		this.name = otherBody.getName();
		this.massKg = otherBody.getWeight();
		this.diameterM = otherBody.getDiameterM();
		this.vector = new DoubleVector(otherBody.getVector());
		this.position = new DoubleVector(otherBody.getPosition());
		this.relativePosition = new DoubleVector(otherBody.getRelativePosition());
		this.acc = new DoubleVector(otherBody.getAcc());
		this.drawVectors = otherBody.drawVectors;
		this.beingDragged = otherBody.beingDragged;

	}

	//constructor (for spawner)
	public Body(String name, double massKg, long diameterM, DoubleVector position, DoubleVector speed, boolean draw, boolean dragged   ) {
		this.vector = new DoubleVector(speed);
		this.position = new DoubleVector(position);
		this.relativePosition = new DoubleVector(position);
		this.acc = new DoubleVector(0,0);
		this.name = name;
		this.massKg = massKg;
		this.diameterM = diameterM;
		this.drawVectors = draw;
		this.beingDragged = dragged;
	}

	public double getWeight() {
		return this.massKg;

	}

	public String getName() {
		return this.name;
	}

	public double getXm() {
		return position.getX();
	}

	public double getYm() {
		return position.getY();
	}

	public double getDiameterM() {
		return this.diameterM;
	}

	public DoubleVector getVector() {
		return this.vector;
	}
	public DoubleVector getPosition() {
		return this.position;
	}
	public void setPosition(DoubleVector position) {
		this.position.set(position);
	}

	public DoubleVector getRelativePosition() {
		return this.relativePosition;
	}
	public void setRelativePosition(DoubleVector position) {
		this.relativePosition.set(position);
	}

	public void setAcc(DoubleVector acc) {
		this.acc.set(acc);
	}

	public DoubleVector getAcc() {
		return this.acc;
	}

	public void setSpeed(DoubleVector speed) {
		this.vector.set(speed);
	}

	public DoubleVector getSpeed() {
		return this.vector;
	}
	public void addSpeed (DoubleVector speed) {
		this.vector.addOther(speed);
	}

	public double getDistanceToXY (double x, double y) {
		DoubleVector diff = new DoubleVector(x,y);
		diff.subtractOther(this.position);
		double dist = diff.getDistance();
		return dist;
	}

	public boolean collidesWithOther(Body otherBody) {
		DoubleVector relativePosition = new DoubleVector(otherBody.getPosition());
		relativePosition.subtractOther(this.getPosition());
		double distance =  relativePosition.getDistance();
		boolean collides;
		if(this.getDiameterM()/2+otherBody.getDiameterM()/2 >= distance) collides = true; else collides = false;

		return collides;
	}

	public void setCollided (boolean collided) {
		this.collided = collided;
	}

	public boolean getCollided() {
		return collided;
	}

	public void collision(Body otherBody) {
		this.massKg += otherBody.getWeight(); //adds mass
		//momentum transef tbc
		//size change tbc
	}

	public void toggleDrawVectors() {
		this.drawVectors = !this.drawVectors;
	}

	public boolean isDrawVetors() {
		return this.drawVectors;
	}

	public void setDrawVectors(Boolean draw) {
		this.drawVectors = draw;
	}

	public boolean beingDragged() {
		return beingDragged;
	}
	public void setDragged (boolean dragged) {
		this.beingDragged = dragged;
	}

	public boolean getGroupSelection() {
		return groupSelection;
	}
	public void setGroupSelection(boolean set) {
		groupSelection = set;
	}

	public void interact (Body otherBody) {
		DoubleVector distancexy;
		
		if(!this.beingDragged() && otherBody.beingDragged()) {
			distancexy = new DoubleVector(otherBody.getPosition());
			distancexy.subtractOther(this.getPosition());
		} else {
			distancexy = new DoubleVector(otherBody.getRelativePosition());
			distancexy.subtractOther(this.getRelativePosition()); 
		}

		double mass = this.massKg * otherBody.getWeight();
		double distance = distancexy.getDistance();
		double force = gc*mass/(distance*distance);
		double acc = force / this.massKg;
		double ratio = acc / distance;

		DoubleVector accxy = new DoubleVector(distancexy);
		accxy.multiplyBy(ratio);
		this.acc.addOther(accxy); //in m/s^2

	}

	public void resetAcc() {
		acc.reset();


	}

	public void speedUpBody(double timePerFrame) {
		DoubleVector tempAcc = new DoubleVector(this.acc);
		tempAcc.multiplyBy(timePerFrame);
		this.vector.addOther(tempAcc);
	}


	public void moveBody(double timePerFrame) {
		DoubleVector tempSpeed = new DoubleVector(this.vector);
		tempSpeed.multiplyBy(timePerFrame);
		this.position.addOther(tempSpeed);
		this.relativePosition.addOther(tempSpeed);
	}
	
	public boolean isWithin(DoubleVector start, DoubleVector end) {
		if(this.position.getX() >= start.getX() && this.position.getY() >= start.getY()
				&& this.position.getX() <= end.getX() && this.position.getY() <= end.getY() ) {
			return true;
		} else return false;
		
	}

}










