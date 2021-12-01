package com.dedecogames.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.dedecogames.graphics.Spritesheet;
import com.dedecogames.main.Game;
import com.dedecogames.world.Camera;
import com.dedecogames.world.World;

public class Player extends Entity{
	
	public boolean right, left, up, down;
	public int right_dir = 0, left_dir = 1, up_dir = 2, down_dir = 3;
	public int dir;
	public double speed = 1.2;
	
	private int frames = 0, maxFrames = 8, index = 0, maxIndex = 2;
	private boolean moved = false;
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	private BufferedImage[] downPlayer;
	private BufferedImage[] upPlayer;
	
	private BufferedImage playerDamage;
	
	public static boolean hasBow = false;
	
	public int arrow = 0;
	public boolean shoot = false, mouseShoot = false;
	
	public boolean isDamaged = false;
	private int damageFrames = 0;
	
	public double life = 100, maxlife = 100;
	public int mx, my;
	
	public boolean jump = false;
	public static boolean isJumping = false;
	public boolean jumpUp = false, jumpDown  =false;
	public int jumpSpd = 2;
	public int z = 0;
	public int jumpFrames = 30, jumpCur = 0;

	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		rightPlayer = new BufferedImage[3];
		leftPlayer = new BufferedImage[3];
		downPlayer = new BufferedImage[3];
		upPlayer = new BufferedImage[3];
		playerDamage = Game.spritesheet.getSprite(16, 16, 16, 16);
		
		for(int i = 0; i < 3; i++) {
			rightPlayer[i] = Game.spritesheet.getSprite(32 + (i*16),  0,  16,  16);
		}
		for(int i = 0; i < 3; i++) {
			leftPlayer[i] = Game.spritesheet.getSprite(32 + (i*16),  16,  16,  16);
		}
		for(int i = 0; i < 3; i++) {
			downPlayer[i] = Game.spritesheet.getSprite(32 + (i*16),  32,  16,  16);
		}
		for(int i = 0; i < 3; i++) {
			upPlayer[i] = Game.spritesheet.getSprite(32 + (i*16),  48,  16,  16);
		}
		
	}
	
	public void revealMap() { // -- FogOfWar
		int xx = (int) x/16;
		int yy = (int) y/16;
		
		World.tiles[xx+yy*World.WIDTH].show = true;
	}
	
	public void tick() {
		
		depth = 1; // da pra colocar no construtor se nao pretende alterar
		
		//revealMap(); -- FogOfWar
		
		if(jump) {
			if(isJumping == false) {
				jump = false;
				isJumping = true;
				jumpUp = true;
			}
		}
		if(isJumping == true) {
				if(jumpUp) {
					jumpCur += jumpSpd;
				}else if(jumpDown) {
					jumpCur -= 2;
					if(jumpCur <= 0) {
						isJumping = false;
						jumpUp = false;
						jumpDown = false;
					}
				}
				z = jumpCur;
				if(jumpCur >= jumpFrames) {
					jumpUp = false;
					jumpDown = true;
				}
		}
		
		moved = false;
		if(right && World.isFree((int)(x+speed), this.getY())) {
			moved = true;
			dir = right_dir;
			x += speed;
		}else if(left && World.isFree((int)(x-speed), this.getY())) {
			moved = true;
			dir = left_dir;
			x -= speed;
		}
		
		if(up && World.isFree(this.getX(), (int)(y-speed))) {
			moved = true;
			//dir = up_dir;
			y -= speed;
		}else if(down && World.isFree(this.getX(), (int)(y+speed))) {
			moved = true;
			//dir = down_dir;
			y += speed;
		}
		
		if(moved) {
			frames++;
			if(frames == maxFrames) {
				frames = 0;
				index++;
				if(index > maxIndex) {
					index = 0;
				}
			}
		}
		
		checkCollisionHeart();
		checkCollisionArrow();
		checkCollisionBow();
		
		if(isDamaged) {
			this.damageFrames++;
			if(this.damageFrames == 8) {
				this.damageFrames = 0;
				isDamaged = false;
			}
		}
		
		if(shoot) {
			shoot = false;
			 if(hasBow && arrow > 0) {
				arrow--;
				int dx = 0;
				int px = 0;
				int py = 9;
				int side = 0;
				if(dir == right_dir) {
					px = 9;
					dx = 1;
					side = 0;
					//ArrowShot arrow = new ArrowShot(this.getX() + px, this.getY() + py, 5, 3, ARROW_RIGHT, dx, 0, side);
					//Game.arrows.add(arrow);
				}else {
					px = 3;
					dx = -1;
					side = 1;
					//ArrowShot arrow = new ArrowShot(this.getX() + px, this.getY() + py, 5, 3, ARROW_LEFT, dx, 0, side);
					//Game.arrows.add(arrow);
				}
				
				ArrowShot arrow = new ArrowShot(this.getX() + px, this.getY() + py, 5, 3, null, dx, 0, side);
				Game.arrows.add(arrow);
			 }			 		 
		}
		
		if(mouseShoot) {
			mouseShoot = false;
			//System.out.println(mx + ";" + my);
			double angle = Math.atan2(my - (this.getY() + 8 - Camera.y), mx - (this.getX() + 8 - Camera.x));
			//System.out.println(angle);
			if(hasBow && arrow > 0) {
				arrow--;				
				double dx = Math.cos(angle);
				double dy = Math.sin(angle);
				int px = 6;
				int py = 8;
				int side = 0;
				if(mx > (this.getX() + 8 - Camera.x)) {
					side = 0;
				}else {
					side = 1;
				}
				
				ArrowShot arrow = new ArrowShot(this.getX() + px, this.getY() + py, 5, 3, null, dx, dy, side);
				Game.arrows.add(arrow);
			 }
		 }
		
		if(life <= 0) {
			life = 0;
			Game.gameState = "GAME_OVER";
		}
		
		Camera.x = Camera.clamp(this.getX() - Game.WIDTH/2, 0, World.WIDTH*16 - Game.WIDTH);
		Camera.y = Camera.clamp(this.getY() - Game.HEIGHT/2, 0, World.HEIGHT*16 - Game.HEIGHT);
	}
	
	public void checkCollisionBow() {
		for(int i = 0; i< Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			if(e instanceof Bow) {
				if(Entity.isColliding(this,  e)) {
					hasBow = true;
					Game.entities.remove(i);
					return;
				}
			}
		}
	}
	
	public void checkCollisionArrow() {
		for(int i = 0; i< Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			if(e instanceof Arrow) {
				if(Entity.isColliding(this,  e)) {
					arrow += 10;
					Game.entities.remove(i);
					return;
				}
			}
		}
	}
	
	public void checkCollisionHeart() {
		for(int i = 0; i< Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			if(e instanceof Heart) {
				if(Entity.isColliding(this,  e)) {
					life += 10;
					if(life >= 100) {
						life = 100;
					}
					Game.entities.remove(i);
					return;
				}
			}
		}
	}
	
	public void render(Graphics g) {
		/*if(dir == right_dir) {
			g.drawImage(rightPlayer[index], this.getX(), this.getY(), null);
		}else if(dir == left_dir) {
			g.drawImage(leftPlayer[index], this.getX(), this.getY(), null);
		}else if(dir == up_dir) {
			g.drawImage(upPlayer[index], this.getX(), this.getY(), null);
		}else if(dir == down_dir) {
			g.drawImage(downPlayer[index], this.getX(), this.getY(), null);
		}*/
		Graphics2D g2 = (Graphics2D) g;
		double angleMouse = Math.toDegrees(Math.atan2((this.getY() + 8 - Camera.y) - Game.my, (this.getX() + 8 - Camera.x) - Game.mx));
		
		if(!isDamaged) {
			if(Game.gameState == "NORMAL") {				
				//System.out.println(angleMouse);
				
				if(angleMouse >= 0 && angleMouse < 45 || angleMouse < 0 && angleMouse > -45) {
					g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y -z, null);
				}else if(angleMouse >= 45 && angleMouse < 135){
					g.drawImage(upPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
				}else if(angleMouse >= 135 || angleMouse < -135){
					g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y -z, null);
				}else {
					g.drawImage(downPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
				}
				/*if(right && up) {
					g.drawImage(upPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
				}else if(right && down) {
					g.drawImage(downPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
				}else if(right) {
					g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y -z, null);
				}else if(moved == false && dir == right_dir) {
					g.drawImage(rightPlayer[0], this.getX() - Camera.x, this.getY() - Camera.y -z, null);
				}
				if(left && up) {
					g.drawImage(upPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
				}else if(left && down) {
					g.drawImage(downPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
				}else if(left) {
					g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y -z, null);
				}else if(moved == false && dir == left_dir) {
					g.drawImage(leftPlayer[0], this.getX() - Camera.x, this.getY() - Camera.y -z, null);
				}
				if(up) {
					g.drawImage(upPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
				}else if(moved == false && dir == up_dir) {
					g.drawImage(upPlayer[0], this.getX() - Camera.x, this.getY() - Camera.y, null);
				}
				if(down) {
					g.drawImage(downPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
				}else if(moved == false && dir == down_dir) {
					g.drawImage(downPlayer[0], this.getX() - Camera.x, this.getY() - Camera.y, null);
				}*/
			}else if(Game.gameState == "MENU") {
				g.drawImage(downPlayer[0], this.getX() - Camera.x, this.getY() - Camera.y, null);
			}
		}else {
			g.drawImage(playerDamage, this.getX() - Camera.x, this.getY() - Camera.y -z, null);
		}
		
		if(hasBow) {
			/*double angleBow = Math.atan2((this.getY() + 8 - Camera.y) - Game.my, (this.getX() + 8 - Camera.x) - Game.mx);
			g2.rotate(angleBow, (this.getX() + 8 - Camera.x), (this.getY() + 8 - Camera.y));
			g.drawImage(Entity.BOW_RIGHT, this.getX() - Camera.x, this.getY() - Camera.y, null);*/
			
			if(angleMouse > 90 || angleMouse < -90) {
				g.drawImage(Entity.BOW_RIGHT, this.getX() - Camera.x, this.getY() - Camera.y -z, null);
			}else if(angleMouse <= 90 || angleMouse >= -90) {
				g.drawImage(Entity.BOW_LEFT, this.getX() - Camera.x, this.getY() - Camera.y -z, null);			
			}
		}
		
		if(isJumping) {
			g.setColor(new Color(19,19,19,130));
			g.fillOval(this.getX() - Camera.x +3, this.getY() - Camera.y +12, 10, 7);
		}
	}

}
