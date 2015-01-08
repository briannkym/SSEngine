package test;

import control.DesktopControl;
import desktopView.DesktopCanvas;
import world.SimpleMap;
import world.SimpleWorld;
import world.SimpleWorldFactory;

public class testMain {
	public static void main(String[] args) {
		SimpleMap m = new SimpleMap(160, 120, 20, 20);
		testObject cam = new testObject();
		m.addSimpleObject(cam, 790, 590);
		for(int x = 0; x < 160; x ++){
			for(int y = 0; y < 120; y ++){
				double d = Math.random();
				if(d<0.4){
					m.addSimpleObject(new testObject(), x*20, y*20);
				} else if (d < 0.6){
					int z = (int)(Math.random()*16);
					m.addSimpleObject(new testColor(), x*20, y*20, z);
				}
			}
		}
		
		//Note the new code below:
		DesktopCanvas dc = new DesktopCanvas(800, 600, "Test");
		DesktopControl dv = DesktopControl.getInstance();
		dv.setCanvas(dc);
		//End of new code.
		SimpleWorld w = new SimpleWorld(m, dv);
		w.setCameraStalk(cam);
		w.start(true);
	}
}
