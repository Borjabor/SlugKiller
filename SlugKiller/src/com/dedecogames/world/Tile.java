package com.dedecogames.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.dedecogames.main.Game;

public class Tile {
	
	public static BufferedImage TILE_FLOOR = Game.spritesheet.getSprite(0, 0, 16, 16);
	public static BufferedImage TILE_WALL = Game.spritesheet.getSprite(16, 0, 16, 16);
	
	public boolean show = true; //-- FogOfWar (voltei pra true pra desativar; pra voltar e mexer na fog, so iniciar em false) --NOTA:  velocidade influenciou na revelacao de mapa dele. Por estar em double, afetou algo que fez a parede de baixo e direito nao serem reveladas
	
	private BufferedImage sprite;
	private int x, y;
	
	public Tile(int x, int y, BufferedImage sprite) {
		this.x = x;
		this.y = y;
		this.sprite = sprite;
	}
	
	public void render(Graphics g) {
		if(show) {
			g.drawImage(sprite,  x - Camera.x,  y - Camera.y,  null);
		}
	}

}
