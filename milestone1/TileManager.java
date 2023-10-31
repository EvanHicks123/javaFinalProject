import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;
import java.util.ArrayList;
public class TileManager{
	protected ArrayList<Tile> tileData = new ArrayList<Tile>(); 
	private Tile[] tileType;
	private Game game;
	private int[][] worldData;
	public TileManager(Game g){
		this.game = g;
		tileType = new Tile[2];
		worldData = new int [g.screenRow][g.screenCol];
				
		
		getTileImage();
	}
	public void setMap(String map) {
		try{
			
			InputStream is = getClass().getResourceAsStream("maps/map1.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			for(int row = 0; row < game.screenCol;row++) {
				String line = br.readLine();
				
				for (int col= 0; col < game.screenRow;col++) {
					String nums[] = line.split(" ");
					
					int num = Integer.parseInt(nums[col]);
					worldData[row][col] = num;
				}
			}
			// print world data
			// remember to get rid of this
			for(int i = 0; i< worldData.length; i++) {
				for(int j = 0; j< worldData.length; j++) {
					System.out.print(worldData[i][j] + " ");
				}
				System.out.println();
			}
			br.close();
			
		}catch(Exception e) {
			
		}
	}
	public void getTileImage() {
		try {
			tileType[0] = new Tile();
			tileType[0].setImage((SpriteStore.get()).getSprite("sprites/gravel.png"));
			
			
			tileType[1] = new Tile();
			tileType[1].setImage((SpriteStore.get()).getSprite("sprites/grass.png"));
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void draw(Graphics g) {
		
		for(int row = 0; row < worldData.length;row++) {
			for(int col = 0; col < worldData.length;col++) {
				if(worldData[col][row] == 0) {
					tileType[0].draw(g, row * game.tileSize, col * game.tileSize, game.tileSize, game.tileSize);
					tileData.add(new Tile(row * game.tileSize,col * game.tileSize, true, tileType[0].getImage(), game.tileSize));
				}
				if(worldData[col][row] == 1) {
					tileType[1].draw(g, row * game.tileSize, col * game.tileSize, game.tileSize, game.tileSize);
					tileData.add(new Tile(row * game.tileSize,col * game.tileSize, false, tileType[1].getImage(), game.tileSize));
				}
			}
		}
		
		
	}
	
}
