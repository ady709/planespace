package orbit;

import java.util.ArrayList;

public class Spawner {

	private static int spawnIndex = 0;
	private static String[] spawnObject = {"null","Sun","Earth","Moon","Earth+Moon","Jupiter+Moons","Moon Array"}; 
	
	private static ArrayList <Body> bodies = new ArrayList<Body>();
	
	public static int spawnIndexUp(){
		spawnIndex++;
		if(spawnIndex>spawnObject.length-1) {
			spawnIndex=0;
		}
		return spawnIndex;
	}
	
	public static int spawnIndexDown(){
		spawnIndex--;
		if(spawnIndex<0) {
			spawnIndex=spawnObject.length-1;
		}
		return spawnIndex;
	}
	
	public static int getSpawnIndex() {
		return spawnIndex;
	}
	
	public static ArrayList<Body> getBodies(int spawnIndex) {
		
		bodies.clear();
		
		switch(spawnIndex) {
							
		case 1: //sun
			bodies.add(new Body("Sun", 1.989e30, (long)2*(696340000), new DoubleVector(0,0), new DoubleVector(0,0), false, true));
			break;
		
		
		case 2: //Earth
			bodies.add(new Body("Earth", 5.972e24,(long)(1.2742e7), new DoubleVector(0,0), new DoubleVector(0,0), false, true));
			break;
		case 3: //Moon
			bodies.add(new Body("Moon", 7.34767309e22,(long)(3474200), new DoubleVector(0,0), new DoubleVector(0,0), false, true));
			break;
		case 4: //Earth + Moon
			bodies.add(new Body("Earth", 5.972e24,(long)(1.2742e7), new DoubleVector(0,0), new DoubleVector(0,0), false, true));
			bodies.add(new Body("Moon", 7.34767309e22,(long)(3474200), new DoubleVector(0,384.4e6), new DoubleVector(1022.0,0), false, true));
			break;
		case 5 : // Jupiter+moons
			bodies.add(new Body("Jupiter", 1898.19e24, (long)(2*71492000), new DoubleVector(0,0),    new DoubleVector(0,0),       false, true));
			bodies.add(new Body("IO"     , 89.3e21,    (long)(2*3643000),  new DoubleVector(0,422e6), new DoubleVector(17.3e3,0), false, true ));
			bodies.add(new Body("Europa",  48e21,      (long) 2*3121600,   new DoubleVector(0,671e6), new DoubleVector(13.7e3,0), false, true ));
			bodies.add(new Body("Ganymede",148.2e21,   (long) 2*5262000,   new DoubleVector(0,1068e6), new DoubleVector(10.9e3,0), false, true ));
			bodies.add(new Body("Callisto",107.6e21,   (long) 2*4821000,   new DoubleVector(0,1883e6), new DoubleVector(8.2e3,0), false, true ));
			break;
		case 6: //Moon Array
			int count = 0;
			for(int x=-3;x<=3;x++) {
				for(int y=-3;y<=3;y++) {
					bodies.add(new Body("Array body " + String.valueOf(count), 7.34767309e23, (long)3474200, new DoubleVector(x*100000000.0,y*100000000.0), new DoubleVector(0,0), false, true));
					count++;
				}
			}
			
			
		}	
		return bodies;
		
	}
	
}
