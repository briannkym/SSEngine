package test;

import desktopView.ColorImg;

import sprite.Img;
import world.SimpleObject;

public class testColor extends SimpleObject {


	// Move in 1 of 4 random directions initially
	private int move = (int) (Math.random() * 4);
	// 2D vectors, one for each possible initial direction.
	private static final int[][] direction = { { 0, 6 }, { 6, 0 }, { 0, -6 },
			{ -6, 0 } };

	// Generate a randomly colored semi-translucent image.
	private final Img random = new ColorImg(
			0x99000000 | (int) (Math.random() * 0xFFFFFF), 20, 30);

	/**
	 * The test color object creates a random color that moves at a speed of 6
	 * pixels in a random direction.
	 */
	public testColor() {
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
