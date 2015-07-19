package test;

import desktopView.ColorImg;
import sprite.Img;
import world.SimpleObject;
import world.SimpleSolid;

public class testObject extends SimpleSolid {

	// Move in 1 of 4 random directions initially
	private int move = (int) (Math.random() * 4);
	// 2D vectors, one for each possible initial direction.
	private static final int[][] direction = { { 0, 2 }, { 2, 0 }, { 0, -2 },
			{ -2, 0 } };
	// Keep track of the number of instances created modulo 256.
	private static int population = 0;

	// The instantiated image from blue to bluish brown depending on the
	// population.
	private final Img blue = new ColorImg(0xFF666600 + population, 20, 30);

	/**
	 * The test object moves in random directions with a single non-translucent
	 * image of a rectangle. The color of the image depends on the number of
	 * times this class has been instantiated, ranging from blue to bluish
	 * brown.
	 */
	public testObject() {
		this.setImage(blue);
		population = (population + 1) % 256;
	}

	@Override
	public void collision(SimpleObject s) {
		switch (s.id()) {
		case 0:
			// If we have collided with ourselves, then pick a new direction to
			// move in.
			move = (int) (Math.random() * 4);
		}

	}

	@Override
	public void update() {
		// Use our current direction (in the variable move) to move according to
		// the direction vector.
		this.move(direction[move][0], direction[move][1], true);
	}

	@Override
	public int id() {
		return 0;
	}

}
