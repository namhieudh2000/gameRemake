public abstract class GameObject{
	
	protected Location loc;
	protected String imageFile;
	protected int speed;
	
	abstract String getImageFile();
	
	abstract int getSpeed();
}
	