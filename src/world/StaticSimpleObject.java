package world;

/**
 * This empty class cannot be removed once added to a map, which means
 * it cannot be added again. It also cannot be moved or collided with.
 * @author Brian Nakayama
 * @version 1.1
 */
public class StaticSimpleObject extends SimpleObject{

	public StaticSimpleObject(){
		super(NO_UPDATES_NO_COLLIDES);
	}
	
	@Override
	public void collision(SimpleObject s) {
	}

	@Override
	public void update() {
	}

	@Override
	public int id() {
		return -1;
	}
	
	public boolean removeSelf(){
		return false;
	}
	
}
