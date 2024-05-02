package orbit;

import java.awt.Color;

public class BodyGraphics {
	
	public boolean isVisible=false;
	public int posX=0,posY=0,diameter=0;
	public Color color;
	
	public int speedVectorX=0, speedVectorY=0, accVectorX=0, accVectorY=0;
	public double speedVectorScaleSTD = 30e3/90;
	public double accVectorScaleSTD = 5e-3/30;

	BodyGraphics(Body body, double viewX, double viewY, double scale, int width, int height){
		posX = (int) (width/2 + (-viewX + body.getPosition().getX())/scale );
		posY = (int) (height/2 + (-viewY + body.getPosition().getY())/scale);
		diameter = (int) (body.getDiameterM() / scale);
		if (diameter < 6) diameter = 6;
		isVisible = !(posX-diameter > width || posX+diameter < 0 
				         || posY-diameter > height || posY+diameter < 0);
		
		//set color by name
		color = (Color.GRAY); //default color
		String name = body.getName();
		if(name.matches("Earth"))color = (Color.cyan);
		else if(name.matches("ISS"))color = (Color.WHITE);
		else if(name.matches("Sun"))color = (Color.yellow);
		else if(name.matches("Mars"))color = (Color.red);
		else if(name.matches("Jupiter"))color = (Color.decode("#FF8C00"));
		else if(name.matches("Saturn"))color = (Color.decode("#FFA500"));
		else if(name.matches("Some asteroid"))color = (Color.decode("#696969"));
 
		
		if (body.isDrawVetors()) {
			//speed Vector
			//length of speedVector
			double speedVectorLength = (body.getVector().getDistance() / speedVectorScaleSTD);
			//minimum speed vector length
			//if(speedVectorLength<1) speedVectorLength = 1;
			//else if(speedVectorLength<24) speedVectorLength = 24;
			//maximum speed vector length
			if(speedVectorLength>width/2) speedVectorLength = width/2; //was 120
			//recalculate speed vector scale after length restrictions
			double speedVectorScale = (body.getVector().getDistance() / speedVectorLength);
			//position of painted vector x,y
			speedVectorX = posX + (int) (body.getVector().getX() / speedVectorScale);
			speedVectorY = posY + (int) (body.getVector().getY() / speedVectorScale);
			
			//acc vector
			//length of acc vector
			int accVectorLength = (int) (body.getAcc().getDistance() / accVectorScaleSTD);
			//minimum acc vector length
			//if(accVectorLength<1) accVectorLength = 1;
			//else if (accVectorLength<10)accVectorLength = 10;
			//maximum acc vector length
			if(accVectorLength>width/2) accVectorLength = width/2; //was 60
			//recalculate acc vector scale after length restrictions
			double accVectorScale = body.getAcc().getDistance() / accVectorLength;
			//position of painted vector x,y
			accVectorX = posX + (int) (body.getAcc().getX() / accVectorScale);
			accVectorY = posY + (int) (body.getAcc().getY() / accVectorScale);
		} else {
			speedVectorX = posX;
			speedVectorY = posY;
			accVectorX = posX;
			accVectorY = posY;
		}
		
		
	}
	
	public double getaccVectorScaleSTD() {
		return accVectorScaleSTD;
	}
	
	
	
}
