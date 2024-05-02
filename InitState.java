package orbit;

public  class InitState {


	public static void initState(int scenario, boolean keep, SpaceLogic space, PaintHandler paintHandler) {
		//Body                 (String name, double weightKg,    long diameterM, double posXm, double posYm,     double speed   ) 

		if(!keep) {
			space.clearList();
			paintHandler.setView(0,0);
			
		}
		double viewX = paintHandler.getViewX(), viewY = paintHandler.getViewY();
		
		if(scenario==2) {
			int count = 0;
			for(int x=-3;x<=3;x++) {
				for(int y=-3;y<=3;y++) {
					space.addBody(new Body("Array body " + String.valueOf(count),  7.34767309e23,     (long)3474200,    (double) (x*100000000)+viewX, (double)(y*100000000)+viewY,(double)0.001));
					count++;
				}
			}
		}


		if(scenario==1) {
			space.addBody(new Body("Sun",   1.989e30,      (long)2*(696340000),   	 (double) (0)+viewX, (double)(0)+viewY,(double)					0));
			space.addBody(new Body("Mercury",0.33011e24,     (long)2*(2439700),      (double) (0)+viewX, 46e9+viewY, (double)     					38860));
			space.addBody(new Body("Venus", 4.867e24,     (long)2*   (6051800),      (double) (0)+viewX, 107.64e9+viewY, (double)     				35.02e3));

			//space.addBody(new Body("Some asteroid", 5.972e8, (long)  (1274),      (double) (0), 120.6e9,(double)				36e3));

			space.addBody(new Body("Earth", 5.972e24,       (long)  (12742000),      (double) (0)+viewX, 149.6e9+viewY,(double)				2.978473e4));
			space.addBody(new Body("Moon",7.34767309e22,   (long)    (3474200),      (double) (0)+viewX, 149.6e9+384.4e6+viewY,  			2.978473e4+1022	));



			space.addBody(new Body("Mars",   6.4171e23,   (long)    (2*3396200),  (double) (0)+viewX, 206.62e9+viewY,   26.50e3  			));

			space.addBody(new Body("Jupiter", 1898.19e24, (long)(2*71492000), (double) (0)+viewX, 740.52e9+viewY,       13.72e3));
			space.addBody(new Body("IO"     , 89.3e21,    (long)(2*3643000),  (double) (0)+viewX, 740.52e9+422e6+viewY, 13.72e3+17.3e3));
			space.addBody(new Body("Europa",  48e21,      (long) 2*3121600,   (double) (0)+viewX, 740.52e9+671e6+viewY, 13.72e3+13.7e3));
			space.addBody(new Body("Ganymede",148.2e21,   (long) 2*5262000,   (double) (0)+viewX, 740.52e9+1068e6+viewY, 13.72e3+10.9e3));
			space.addBody(new Body("Callisto",107.6e21,   (long) 2*4821000,   (double) (0)+viewX, 740.52e9+1883e6+viewY, 13.72e3+8.2e3));

			//space.addBody(new Body("Voyager1",2e3,   (long) 12,   (double) (0), 740.52e9+2488e6, 3.72e3+8.2e3));
			//space.addBody(new Body("Voyager2",2e3,   (long) 12,   (double) (0), 740.52e9+2000e6, 2.72e3+8.2e3));

			space.addBody(new Body("Saturn", 568.34e24, (long)(2*60268000), (double) (0)+viewX, 1514.50e9+viewY,       9.09e3));


			//space.addBody(new Body("Halley's Comet", 2.2e14, (long)(15000), (double) (0), 2514.50e9,       0.09e3));
		}	

		if(scenario==3) {
			//Body           (String name, double weightKg,    long diameterM, double posXm, double posYm,     				double speed   ) 
			space.addBody(new Body("Earth",      5.972e24,         (long)12742000, (double)0+viewX,    (double)0+viewY,       			 (double)0)); //13 060 km/s
			space.addBody(new Body("ISS",      (double)10000,      (long)100,      (double)0+viewX,    (double)(12742000/2+40000000)+viewY,   (double)3000));
			space.addBody(new Body("ISS",     (double)10000,      (long)100,      (double)0+viewX,    (double)(12742000/2+4000000)+viewY,   (double)7800));
			space.addBody(new Body("ISS",     (double)10000,      (long)100,      (double)0+viewX,    (double)(12742000/2+320000000)+viewY,   (double)1060));

			space.addBody(new Body("Moon",     7.34767309e22,     (long)3474200,  (double)0+viewX,    (double)(-12742000/2-200000000)+viewY,   (double)-810));
			space.addBody(new Body("Moon",     7.34767309e22,     (long)3474200,  (double)0+viewX,    (double)(12742000/2+200000000)+viewY,   (double)820));
			space.addBody(new Body("Moon",     7.34767309e22,     (long)3474200,  (double)0+viewX,    (double)(12742000/2+150000000)+viewY,    (double)810));
			space.addBody(new Body("Moon",     7.34767309e22,     (long)3474200,  (double)0+viewX,    (double)(-12742000/2-150000000)+viewY,     (double)-820));




		}
		
		if(scenario==4) {
			//Body           (String name, double weightKg,    long diameterM, double posXm, double posYm,     		double speed   ) 
			space.addBody(new Body("Earth", 5.972e24,       (long)  (12742000),      (double) (0)+viewX, viewY,(double)				0));
			space.addBody(new Body("Moon",7.34767309e22,   (long)    (3474200),      (double) (0)+viewX, 384.4e6+viewY,  			1022	)); //1022
		}
		
		if(scenario==5) {
			space.addBody(new Body("Sun",   1.989e30,      (long)2*(696340000),   	 (double) (0)+viewX, (double)(0)+viewY,(double)					0));
		}

	}	


}
