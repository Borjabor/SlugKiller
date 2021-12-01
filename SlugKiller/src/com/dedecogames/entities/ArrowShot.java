package com.dedecogames.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.dedecogames.main.Game;
import com.dedecogames.world.Camera;
import com.dedecogames.world.World;

public class ArrowShot extends Entity{
	
	private double dx;
	private double dy;
	private double spd = 4;
	private int timer = 40, curtimer = 0;
	private int side;
	private double newy;
	
	public ArrowShot(int x, int y, int width, int height, BufferedImage sprite, double dx, double dy, int side) {
		super(x, y, width, height, sprite);
		this.dx = dx;
		this.dy = dy;
		this.side = side;
	}

	public void tick() {
		x += dx*spd;
		y += dy*spd;
		curtimer++;
		if(curtimer == timer) {
			Game.arrows.remove(this);
			return;
		}
		if(dy > 0) {
			newy = y-16;
		}else {
			newy = y+4;
		}
		
		
		if(dx > 0 && !World.isFree((int) (x-14), (int)newy)) {
			Game.arrows.remove(this);
			World.generateParticles(50,  (int) x, (int) y);
		}
		if(dx < 0 && !World.isFree((int) (x+4), (int)newy)) {
			Game.arrows.remove(this);
			World.generateParticles(50,  (int) x, (int) y);
		}
		
	}
	
	public void render(Graphics g) {
		/*g.setColor(Color.DARK_GRAY);
		g.fillOval(this.getX() - Camera.x, this.getY() - Camera.y, width, height);*/
		if(side == 0) {
			g.drawImage(ARROW_RIGHT, this.getX() - Camera.x, this.getY() - Camera.y, width, height, null);
		}else if(side == 1){
			g.drawImage(ARROW_LEFT, this.getX() - Camera.x, this.getY() - Camera.y, width, height, null);
		}
	}

}
