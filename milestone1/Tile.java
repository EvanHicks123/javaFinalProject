import java.awt.Graphics;

public class Tile {
	private boolean collidable ;
	private Sprite image;
	private int x;
	private int y;
	private int tileSize;
	
	Tile(){
		
	}
	Tile(int x, int y, boolean collidable, Sprite image, int tileSize){
		this.x = x;
		this.y = y;	
		this.collidable  = collidable ;
		this.image = image;
		this.tileSize = tileSize;
	}
	
	
	public void draw(Graphics g,int x, int y,int xSize, int ySize) {
		g.drawImage(image.image,x,y,xSize,ySize,null);
	}
	public void setImage(Sprite img) {
		image = img;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public Sprite getImage() {
		return image;
	}
	public boolean getCollidable() {
		return collidable;
	}
	public int getHeight() {
		return tileSize;
	}
	
	
}
