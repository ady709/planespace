package orbit;

import java.awt.Point;

public class DoubleVector {

	private double x,y;

	public DoubleVector() {	
	}
	public DoubleVector(DoubleVector otherVector) {
		this.x = otherVector.getX();
		this.y = otherVector.getY();
	}
	public DoubleVector(double x, double y) {
		this.x = x;
		this.y = y;
	}
	public DoubleVector(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	
	
	//
	public void reset() {
		this.x = 0;
		this.y = 0;
	}
	public double getX() {
		return this.x;
	}
	public double getY() {
		return this.y;
	}
	public void set(DoubleVector otherVector) {
		this.x = otherVector.getX();
		this.y = otherVector.getY();
	}
	public void set(Point otherVector) {
		this.x = otherVector.getX();
		this.y = otherVector.getY();
	}	
	
	public DoubleVector get() {
		return this;
	}
	public double getDistance() {
		return Math.sqrt(this.x*this.x+this.y*this.y);		
	}
	//
	public void addOther(DoubleVector otherVector) {
		this.x += otherVector.getX();
		this.y += otherVector.getY();
	}
	public void subtractOther (DoubleVector otherVector) {
		this.x -= otherVector.getX();
		this.y -= otherVector.getY();
	}
	public DoubleVector getSum(DoubleVector otherVector) {
		return new DoubleVector(this.x+otherVector.x, this.y+otherVector.y);
	}
	
	public DoubleVector getMultiply(double ratio) {
		return new DoubleVector(this.x*ratio, this.y*ratio);
	}
	
	public void multiplyBy(double ratio) {
		this.x *= ratio;
		this.y *= ratio;
	}






}
