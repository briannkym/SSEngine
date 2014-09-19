package test;

import java.awt.image.BufferedImage;

import sprite.ColorImg;
import sprite.Img;
import world.SimpleObject;
import world.SimpleSolid;

public class testObject extends SimpleSolid{

	private int move = (int)(Math.random() * 4);
	private static final int[][] direction = {{0,2},{2,0},{0,-2},{-2,0}};
	private static int population = 0;
	private final Img<BufferedImage> blue = new ColorImg(0xFF666600 + population, 20, 30);
	
	public testObject(){
		this.setImage(blue);
		population = (population +1) % 256;
	}
	
	@Override
	public void collision(SimpleObject s) {
		switch(s.id()){
		case 0:
			move = (int)(Math.random() * 4);
		}
		
	}

	@Override
	public void update() {
		this.move(direction[move][0], direction[move][1], true);
	}

	@Override
	public int id() {
		return 0;
	}

}
