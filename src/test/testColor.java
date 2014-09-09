package test;

import sprite.ColorImg;
import sprite.Img;
import world.SimpleObject;

public class testColor extends SimpleObject{

	private int move = (int)(Math.random() * 4);
	private static final int[][] direction = {{0,6},{6,0},{0,-6},{-6,0}};
	private final Img random = new ColorImg(0x99000000 | (int)(Math.random() * 0xFFFFFF), 20, 30);
	
	public testColor(){
		this.setImage(random);
	}
	
	@Override
	public void collision(SimpleObject s) {
		move = (move + 1) % 4;
	}

	@Override
	public void update() {
		this.move(direction[move][0], direction[move][1], true);
	}

	@Override
	public int id() {
		return 1;
	}

}
