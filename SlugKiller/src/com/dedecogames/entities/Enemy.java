package com.dedecogames.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.dedecogames.main.Game;
import com.dedecogames.main.Sound;
import com.dedecogames.world.AStar;
import com.dedecogames.world.Camera;
import com.dedecogames.world.Vector2i;
import com.dedecogames.world.World;

public class Enemy extends Entity{
	
	private double speed = 0.6;
	private int frames = 0, maxFrames = 10, index = 0, maxIndex = 2;
	
	//private BufferedImage[] sprites;
	private BufferedImage[] right_Enemy;
	private BufferedImage[] left_Enemy;
	
	private int life = 3;
	private boolean isDamaged = false;
	private int damageFrames = 10, damageCurrent = 0;

	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, null);
		/*sprites = new BufferedImage[3];
		sprites[0] = Game.spritesheet.getSprite(112, 0, 16, 16);
		sprites[1] = Game.spritesheet.getSprite(128, 0, 16, 16);
		sprites[2] = Game.spritesheet.getSprite(144, 0, 16, 16);*/
		right_Enemy = new BufferedImage[3];
		right_Enemy[0] = Game.spritesheet.getSprite(112, 16, 16, 16);
		right_Enemy[1] = Game.spritesheet.getSprite(128, 16, 16, 16);
		right_Enemy[2] = Game.spritesheet.getSprite(144, 16, 16, 16);
		
		left_Enemy = new BufferedImage[3];
		left_Enemy[0] = Game.spritesheet.getSprite(112, 0, 16, 16);
		left_Enemy[1] = Game.spritesheet.getSprite(128, 0, 16, 16);
		left_Enemy[2] = Game.spritesheet.getSprite(144, 0, 16, 16);
		
		
	}
	
	public void tick() {
		
		//Commented block was AI path finding before A*
		
		// if(Game.rand.nextInt(100) < 50) { <coloca o resto do if/else aqui> } - metodo mais simples de simular uma especie de colisao entre o inimigos
		/*
		if(this.calculateDistance(this.getX(),  this.getY(), Game.player.getX(), Game.player.getY()) < 50) { //Inimigo so se move quando jogador chegar nessa distancia dele
		if(this.isCollidingWithPlayer() == false) {
		if((int)x < Game.player.getX() && World.isFree((int)(x+speed),  this.getY()) && !isColliding((int)(x+speed),  this.getY())) {
			x += speed;
		}else if((int)x > Game.player.getX() && World.isFree((int)(x-speed),  this.getY()) && !isColliding((int)(x-speed),  this.getY())) {
			x -= speed;
		}
		if((int)y < Game.player.getY() && World.isFree(this.getX(), (int)(y+speed)) && !isColliding(this.getX(), (int)(y+speed))) {
			y += speed;
		}else if((int)y > Game.player.getY() && World.isFree(this.getX(), (int)(y-speed)) && !isColliding(this.getX(), (int)(y-speed))) {
			y -= speed;
		}
		}else {
			if(Game.rand.nextInt(100) < 10) {
				//Sound.hurtEffect.play();
				Game.player.life -= Game.rand.nextInt(3);
				Game.player.isDamaged = true;
			}
			
		}
		}else {
			
		}*/
		
		depth = 0;
		
		if(frames < 120) {
			frames++;
		}else if(frames >= 120) {
			if(!isCollidingWithPlayer()) {
				if(path == null || path.size() == 0) {
					Vector2i start = new Vector2i((int)(x/16), (int)(y/16));
					Vector2i end = new Vector2i((int)(Game.player.x/16), (int)(Game.player.y/16));
					path = AStar.findPath(Game.world, start, end);
				}
			}else {
				if(new Random().nextInt(100) < 5) {
					Game.player.life -= Game.rand.nextInt(3);
					Game.player.isDamaged = true;
				}
			}
		}
		
		if(new Random().nextInt(100) < 60)
			followPath(path);
		
		/*frames++;
		if(frames == maxFrames) {
			frames = 0;
			index++;
			if(index > maxIndex) {
				index = 0;
				}
			}*/
		
		collideArrow();
		
		if(life <= 0) {
			destroySelf();
			return;
		}
		
		if(isDamaged) {
			this.damageCurrent++;
			if(this.damageCurrent == this.damageFrames) {
				this.damageCurrent = 0;
				this.isDamaged = false;
			}
		}
		
	}
	
	public void destroySelf() {
		Game.enemies.remove(this);
		Game.entities.remove(this);
	}
	
	public void collideArrow() {
		for(int i = 0; i < Game.arrows.size(); i++) {
			Entity e = Game.arrows.get(i);
			if(e instanceof ArrowShot) {
				if(Entity.isColliding(this,  e)) {
					isDamaged = true;
					life--;
					Game.arrows.remove(i);
					return;
				}
			}
		}
	}
	
	public boolean isCollidingWithPlayer() {
		Rectangle enemyCurrent = new Rectangle(this.getX() + maskx, this.getY() + masky, mwidth, mheight);
		Rectangle player =  new Rectangle(Game.player.getX(), Game.player.getY(), 16, 16);
		return enemyCurrent.intersects(player);
	}
	
	
	
	public void render(Graphics g) {
		if(!isDamaged) {
			if(x <= Game.player.getX()) {
				g.drawImage(right_Enemy[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
			}else if(x >= Game.player.getX()){
				g.drawImage(left_Enemy[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
			}
		}else {
			g.drawImage(Entity.ENEMY_DAMAGED, this.getX() - Camera.x, this.getY() - Camera.y, null);
		}
		//g.drawImage(sprites[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
		
		//super.render(g);
		//Color myColour = new Color(0, 0, 0, 0); // R, G, B, Alpha
		//g.setColor(myColour);
		//g.fillRect(this.getX() + maskx - Camera.x,  this.getY() + masky - Camera.y, maskw, maskh);
		
	}

}
