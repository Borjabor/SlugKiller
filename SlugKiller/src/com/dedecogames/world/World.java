package com.dedecogames.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.dedecogames.entities.Arrow;
import com.dedecogames.entities.Bow;
import com.dedecogames.entities.Enemy;
import com.dedecogames.entities.Entity;
import com.dedecogames.entities.Heart;
import com.dedecogames.entities.Particle;
import com.dedecogames.entities.Player;
import com.dedecogames.graphics.Spritesheet;
import com.dedecogames.main.Game;

public class World {
	
	public static Tile[] tiles;
	public static int WIDTH, HEIGHT;
	public static final int TILE_SIZE = 16;
	
	//World constructor with set maps
	public World(String path){
		try {
			BufferedImage map = ImageIO.read(getClass().getResource(path));
			int[] pixels = new int[map.getWidth() * map.getHeight()];
			WIDTH = map.getWidth();
			HEIGHT = map.getHeight();
			tiles = new Tile[map.getWidth() * map.getHeight()];
			map.getRGB(0, 0, map.getWidth(), map.getHeight(), pixels, 0, map.getWidth());
			for(int xx = 0; xx < map.getWidth(); xx++) {
				for(int yy = 0; yy < map.getHeight(); yy++) {
					int currPixel = pixels[xx + yy * map.getWidth()];
					tiles[xx + yy * WIDTH] = new FloorTile(xx * 16, yy * 16, Tile.TILE_FLOOR);
					
					if(currPixel == 0xFF000000) {
						//Floor
						tiles[xx + yy * WIDTH] = new FloorTile(xx * 16, yy * 16, Tile.TILE_FLOOR);
					}else if(currPixel == 0xFFffffff) {
						//Wall
						tiles[xx + yy * WIDTH] = new WallTile(xx * 16, yy * 16, Tile.TILE_WALL);
					}else if(currPixel == 0xFF0000FE) {
						//Player
						Game.player.setX(xx*16);
						Game.player.setY(yy*16);
					}else if(currPixel == 0xFFfe0000) {
						//Enemy
						Enemy en = new Enemy(xx*16, yy*16, 16, 16, Entity.ENEMY_EN);
						Game.entities.add(en);
						Game.enemies.add(en);
					}else if(currPixel == 0xFFffff00) {
						//Bow
						Game.entities.add(new Bow(xx*16, yy*16, 16, 16, Entity.BOW_EN));
					}else if(currPixel == 0xFFff00fe) {
						//Arrow
						Game.entities.add(new Arrow(xx*16, yy*16, 16, 16, Entity.ARROW_EN));
					}else if(currPixel == 0xFF7bff30) {
						//Heart
						Game.entities.add(new Heart(xx*16, yy*16, 16, 16, Entity.HEART_EN));
					}else {
						tiles[xx + yy * WIDTH] = new FloorTile(xx * 16, yy * 16, Tile.TILE_FLOOR);
					}
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	//World constructor with random maps
	/*public World(String path) {
		Game.player.setX(0);
		Game.player.setY(0);
		WIDTH = 100;
		HEIGHT = 100;
		tiles = new Tile[WIDTH*HEIGHT];
		
		for(int xx = 0; xx < WIDTH; xx++) {
			for(int yy = 0; yy < HEIGHT; yy++) {
				tiles[xx + yy * WIDTH] = new WallTile(xx*16, yy*16, Tile.TILE_WALL);
			}
		}
		
		int dir = 0;
		int xx = 0, yy = 0;
		
		for(int i = 0; i < 200; i++) {
			tiles[xx + yy * WIDTH] = new FloorTile(xx*16, yy*16, Tile.TILE_FLOOR);
			if(dir == 0) {
				//right
				if(xx < WIDTH) {
					xx++;
				}
			}else if(dir == 1) {
				//left
				if(xx > 0) {
					xx--;
				}
			}else if(dir == 2) {
				//down
				if(yy < HEIGHT) {
					yy++;
				}
			}else if(dir == 3) {
				//up
				if(yy > 0) {
					yy--;
				}
			}
			
			if(Game.rand.nextInt(100) < 30) {
				dir = Game.rand.nextInt(4);
			}
			
		}
	}
	*/
	
	public static void generateParticles(int amount, int x, int y) {
		for(int i = 0; i < amount; i++) {
			Game.entities.add(new Particle(x, y, 1, 1, null));
		}
		
	}
	
	public static boolean isFree(int xnext, int ynext){
		int x1 = xnext/TILE_SIZE;
		int y1 = ynext/TILE_SIZE;
		
		int x2 = (xnext+TILE_SIZE-1)/TILE_SIZE;
		int y2 = ynext/TILE_SIZE;
		
		int x3 = xnext/TILE_SIZE;
		int y3 = (ynext+TILE_SIZE-1)/TILE_SIZE;
		
		int x4 = (xnext+TILE_SIZE-1)/TILE_SIZE;
		int y4 = (ynext+TILE_SIZE-1)/TILE_SIZE;
		
		return !((tiles[x1 + y1 * World.WIDTH] instanceof WallTile) ||
				 (tiles[x2 + y2 * World.WIDTH] instanceof WallTile) ||
				 (tiles[x3 + y3 * World.WIDTH] instanceof WallTile) ||
				 (tiles[x4 + y4 * World.WIDTH] instanceof WallTile));
	}
	
	public static void restartGame(String level) {
		Game.entities.clear();
		Game.enemies.clear();
		Game.entities = new ArrayList<Entity>();
		Game.enemies = new ArrayList<Enemy>();
		Game.spritesheet = new Spritesheet("/SpriteSheet.png");
		Game.player = new Player(70, 64, 16, 16, Game.spritesheet.getSprite(32,  32,  16,  16));
		Game.entities.add(Game.player);
		Game.world = new World("/" + level);
		return;
	}
	
	public void render(Graphics g) {
		int xstart = Camera.x >> 4;
		int ystart = Camera.y >> 4;
		int xfinal = xstart + (Game.WIDTH >> 4);
		int yfinal = ystart + (Game.HEIGHT >> 4);
		for(int xx = xstart; xx <= xfinal; xx++) {
			for(int yy = ystart; yy <= yfinal; yy++) {
				if(xx < 0 || yy < 0 || xx >= WIDTH || yy>= HEIGHT) {
					continue;
				}
				Tile tile = tiles[xx + yy * WIDTH];
				tile.render(g);
			}
		}
		
	}
	
	public static void renderMinimap() {
		for(int i = 0; i < Game.minimapPixels.length; i++) {
			Game.minimapPixels[i] = 0;
		}
		for(int xx = 0; xx < WIDTH; xx++) {
			for(int yy = 0; yy < HEIGHT; yy++) {
				if(tiles[xx + yy * WIDTH] instanceof WallTile) {
					Game.minimapPixels[xx + yy * WIDTH] = 0x282c3c;
				}else if(tiles[xx + yy * WIDTH] instanceof FloorTile) {
					Game.minimapPixels[xx + yy * WIDTH] = 0x61a53f;
				}
			}
		}
		
		int xPlayer = Game.player.getX()/16;
		int yPlayer = Game.player.getY()/16;
		
		Game.minimapPixels[xPlayer + yPlayer * WIDTH] = 0xff0000;
		
	}

}
