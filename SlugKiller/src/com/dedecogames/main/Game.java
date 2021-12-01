package com.dedecogames.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.dedecogames.entities.ArrowShot;
import com.dedecogames.entities.Enemy;
import com.dedecogames.entities.Entity;
import com.dedecogames.entities.Player;
import com.dedecogames.graphics.Spritesheet;
import com.dedecogames.graphics.UI;
import com.dedecogames.world.Camera;
import com.dedecogames.world.World;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener, MouseMotionListener{
	
	
	private static final long serialVersionUID = 1L;
	public static JFrame frame;
	private Thread thread;
	private boolean isRunning = true;
	public static final int WIDTH = 160;
	public static final int HEIGHT = 160;
	public static final int SCALE = 4;
	
	
	private int curLevel = 1, maxLevel = 2;
	private BufferedImage image;
	public BufferedImage lightmap;
	public static BufferedImage minimap;
	
	public static List<Entity> entities;
	public static List<Enemy> enemies;
	public static List<ArrowShot> arrows;
	public static Spritesheet spritesheet;
	
	public static World world;
	
	public static Player player;
	
	public static Random rand;
	
	public UI ui;
	
	public InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("pixelart.ttf");
	public Font newFont;
	
	public static String gameState = "MENU";
	private boolean showMessageGameOver = true;
	private int framesOver = 0;
	private int alpha = 100;
	private boolean restartGame = false;
	
	
	public Menu menu;
	
	public boolean saveGame = false;
	
	public int[] pixels;
	public int[] lightMapPixels;  
	public static int[] minimapPixels;
	
	public static int mx, my;
	public int xx, yy;
	
	public Game() {
		//Sound.musicBg.loop();
		int m = 4 << 10;
		System.out.println(m);
		rand = new Random();
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		//setPreferredSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize())); //Fullscreen
		setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));  //Windowed
		initFrame();
		
		//Initialize Objects
		
		ui = new UI();
		image = new BufferedImage(WIDTH, HEIGHT,BufferedImage.TYPE_INT_RGB);
		try {
			lightmap = ImageIO.read(getClass().getResource("/lightmap.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		lightMapPixels = new int[lightmap.getWidth() * lightmap.getHeight()];
		lightmap.getRGB(0, 0, lightmap.getWidth(), lightmap.getHeight(), lightMapPixels, 0, lightmap.getWidth());
		pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		arrows = new ArrayList<ArrowShot>();
		
		spritesheet = new Spritesheet("/SpriteSheet.png");
		player = new Player(70, 64, 16, 16, spritesheet.getSprite(32,  32,  16,  16));
		entities.add(player);
		world = new World("/level1.png");
		
		minimap = new BufferedImage(World.WIDTH, World.HEIGHT, BufferedImage.TYPE_INT_RGB);
		minimapPixels = ((DataBufferInt)minimap.getRaster().getDataBuffer()).getData();
		
		try {
			newFont = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(20f);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		menu = new Menu();
	}
	
	public void initFrame() {
		frame = new JFrame("Slug Killer");
		frame.add(this);
		//frame.setUndecorated(true);
		frame.setResizable(false);
		frame.pack();
		Image image = null;
		try {
			image = ImageIO.read(getClass().getResource("/icon.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image image2 = toolkit.getImage(getClass().getResource("/cursor.png"));
		Cursor c = toolkit.createCustomCursor(image2,  new Point(0,0), "img");
		frame.setCursor(c);
		frame.setIconImage(image);
		frame.setAlwaysOnTop(true);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
	}
	
	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}
	
	public synchronized void stop() {
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String args[]) {
		Game game = new Game();
		game.start();
		
	}
	
	public void tick() {
		if(gameState == "NORMAL") {
			//xx++;
			//yy++;
			if(this.saveGame) {
				this.saveGame = false;
				String[] opt1 = {"level"};
				int[] opt2 = {this.curLevel};
				Menu.saveGame(opt1, opt2, 10);
			}
			this.restartGame = false;
			
			//if(state_scene == playing) {
			for(int i = 0; i < entities.size(); i++) {
				Entity e = entities.get(i);
				e.tick();
			}
			
			for(int i = 0; i < arrows.size(); i++) {
				arrows.get(i).tick();
			}
			/*}else {
				if(state_scene == intro) {
					if(player.getX() < 100) {
						player.x++;
					}else {
						state_scene = start;
					}
				}else if (state_scene == start) {
					timeScene++;
					if(timeScene == maxTimeScene) {
						state_scene = playing;
					}
				}
				
			}*/
			
			
			//This part let's you add more levels to src folder int the format "level" + number ".png": eg.: level1.png.
			
			if(enemies.size() == 0){
				curLevel++;
				if(curLevel > maxLevel) {
					curLevel = 1;
				}
				String newWorld = "level" + curLevel + ".png";
				World.restartGame(newWorld);
			}
			
		}else if(gameState == "GAME_OVER") {
			this.framesOver++;
			if(this.framesOver == 30) {
				this.framesOver = 0;
				this.alpha += 20;
				if(this.alpha >= 255) {
					this.alpha = 255;
				}
				if(this.showMessageGameOver) {
					this.showMessageGameOver = false;
				}else {
					this.showMessageGameOver = true;
				}
				
			}
			if(restartGame) {
				this.restartGame = false;
				Game.gameState = "NORMAL";
				String newWorld = "level" + curLevel + ".png";
				World.restartGame(newWorld);
			}
		}else if (gameState == "MENU") {
			menu.tick();
			
		}
		
	}
	
	/*public void drawRectangleExample(int xoff, int yoff) {
		for(int xx = 0; xx < 32; xx++) {
			for(int yy = 0; yy < 32; yy++) {
				int xOff = xx + xoff;
				int yOff = yy + yoff;   //Offsets pra mover forma
				if(xOff < 0 || yOff < 0 || xOff >= WIDTH || yOff >= HEIGHT)
					continue; //evita crash/erro caso o offset saia da area do jogo
				pixels[xOff + (yOff*WIDTH)] = 0xff0000; //pixels ficou unidimensional, essa formula converte em multidimensional
			}
		}
	}*/
	
	public void applyLight() {
		for(int xx = 0; xx < WIDTH; xx++) {
			for(int yy = 0; yy < HEIGHT; yy++) {
				if(lightMapPixels[xx + yy * WIDTH] == 0xffffffff) { // 4 valores pq alpha aparece no arquivo de imagem
					int pixel = Pixel.getLightBlend(pixels[xx + yy * WIDTH], 0x808080, 0);
					pixels[xx + yy * WIDTH] = pixel;
				}
			}
		}
	}
	
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = image.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0,0,WIDTH, HEIGHT);
		
		/* Rendering game*/
		//Graphics2D g2 = (Graphics2D) g;
		world.render(g);
		Collections.sort(entities, Entity.nodeSorter);
		for(int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.render(g);
		}
		
		for(int i = 0; i < arrows.size(); i++) {
			arrows.get(i).render(g);
		}
		
		applyLight();
		ui.render(g);
		
		g.dispose();
		g = bs.getDrawGraphics();
		//drawRectangleExample(xx, yy); //exemplo de manipulacao de pixel
		g.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null); // Windowed
		//g.drawImage(image, 0, 0, Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height, null); //Fullscreen  --  tem que mudar umas coisas pq o jogo nao foi feito pensado nisso. Ideal seria isso ser considerado desde o inicio
		g.setColor(Color.white);
		g.setFont(new Font("arial", Font.BOLD, 20));
		g.drawString("Arrows: " + player.arrow, 18, 67);
		//g.setFont(newFont);
		//g.drawString("New Font Test", 20, 150);
		World.renderMinimap();
		g.drawImage(minimap, 535, 5,World.WIDTH*2, World.HEIGHT*2, null);
		g.setColor(Color.white);
		g.setFont(new Font("arial", Font.BOLD, 20));
		g.drawString("Level: " + curLevel, 545, 125);
		if(gameState == "GAME_OVER") {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(0, 0, 0, alpha));
			g2.fillRect(0,  0,  WIDTH*SCALE, HEIGHT*SCALE);
			g.setColor(Color.RED);
			g.setFont(new Font("arial", Font.BOLD, 60));
			g.drawString("GAME OVER", 35*SCALE, 80*SCALE);
			g.setColor(Color.WHITE);
			g.setFont(new Font("arial", Font.BOLD, 50));
			if(showMessageGameOver) {
				g.drawString(">Press 'Enter' to restart<", 6*SCALE, 95*SCALE);
			}
		}else if(gameState == "MENU") {
			menu.render(g);
		}
		
		bs.show();
		
	}
	
	public void run() {
		requestFocus();
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();
		while(isRunning) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			if(delta >= 1) {
				tick();
				render();
				frames++;
				delta--;
			}
			
			if(System.currentTimeMillis() - timer >= 1000) {
				//System.out.println("FPS: " + frames);
				frames = 0;
				timer += 1000;
			}
		}
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		if(e.getKeyCode() == KeyEvent.VK_F) {
			player.jump = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			player.right = true;
		}else if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			player.left = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			player.up = true;
			
			if(gameState == "MENU") {
				menu.up = true;
			}
			
		}else if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			player.down = true;
			
			if(gameState == "MENU") {
				menu.down = true;
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			this.restartGame = true; 
			if(gameState == "MENU") {
				menu.enter = true;
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			gameState = "MENU";
			Menu.pause = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_F5) {
			if(gameState == "NORMAL") {
				this.saveGame = true;				
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			player.right = false;
		}else if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			player.left = false;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			player.up = false;
		}else if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			player.down = false;
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		player.mouseShoot = true;
		player.mx = e.getX()/4;
		player.my = e.getY()/4;
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mx = e.getX()/4;
		my = e.getY()/4;
		
	}

}
