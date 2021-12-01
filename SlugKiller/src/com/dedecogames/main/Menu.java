package com.dedecogames.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.dedecogames.world.World;

public class Menu {
	
	public String[] options = {"New Game", "Load Game", "Exit"};
	
	public int currentOption = 0;
	public int maxOption = options.length  - 1;
	
	private boolean select = true;
	private int frames = 0;
	
	public boolean up, down, enter;
	public static boolean pause = false;
	
	public static boolean saveExists = false;
	public static boolean saveGame = false;
	
	public void tick() {
		File file = new File("save.txt");
		if(file.exists()) {
			saveExists = true;
		}else {
			saveExists = false;
		}
		
		if(up) {
			up = false;
			currentOption--;
			if(currentOption < 0) {
				currentOption = maxOption;
			}
		}
		
		if(down) {
			down = false;
			currentOption++;
			if(currentOption > maxOption) {
				currentOption = 0;
			}
		}
		
		if (enter) {
			//Sound.music.loop();
			enter = false;
			if(options[currentOption] == "New Game") {
				Game.gameState = "NORMAL";
				file = new File("save.txt");
				file.delete();
			}else if(options[currentOption] == "Load Game") {
				file = new File("save.txt");
				if(file.exists()) {
					String saver = loadGame(10);
					applySave(saver);
				}
			}
			
			if(options[currentOption] == "Exit") {
				System.exit(0);
			}
		}
		
		this.frames++;
		if(this.frames == 25) {
			this.frames = 0;
			if(this.select) {
				this.select = false;
			}else {
				this.select = true;
			}
		}
		
	}
	
	public static void applySave(String str) {
		String[] spl = str.split("/");
		for(int i = 0; i < spl.length; i++) {
			String[] spl2 = spl[i].split(":");
			switch(spl2[0]) {
				case"level":
					World.restartGame("level" + spl2[1] + ".png");
					Game.gameState = "NORMAL";
					pause = false;
					break;
			}
		}
	}
	
	
	public static String loadGame(int encode) {
		String line = "";
		File file = new File("save.txt");
		if(file.exists()) {
			try {
				String singleLine = null;
				BufferedReader reader = new BufferedReader(new FileReader("save.txt"));
				try {
					while((singleLine = reader.readLine()) != null) {
						String[] trans = singleLine.split(":");
						char[] val = trans[1].toCharArray();
						trans[1] = "";
						for(int i = 0; i < val.length; i++) {
							val[i] -= encode;
							trans[1] += val[i];
						}
						line += trans[0];
						line += ":";
						line += trans[1];
						line += "/";
					}
				}catch(IOException e) {}
			}catch(FileNotFoundException e) {}
		}
		
		return line;
	}
	
	public static void saveGame(String[] val1, int[] val2, int encode) {
		BufferedWriter write = null;
		try {
			write = new BufferedWriter(new FileWriter("save.txt"));
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		for(int i = 0; i < val1.length; i++) {
			String current = val1[i];
			current += ":";
			char[] value = Integer.toString(val2[i]).toCharArray();
			for(int n = 0; n < value.length; n++) {
				value[n] += encode;
				current += value[n];
			}
			try {
				write.write(current);
				if(i < val1.length -1) {
					write.newLine();
				}
			}catch(IOException e) {}
		}
		try {
			write.flush();
			write.close();
		}catch(IOException e) {}
	}
	
	public void render(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		if(pause) {
			g2.setColor(new Color(0, 0, 0, 190));
		}else if(pause == false) {
			g.setColor(new Color(25,25, 30));
		}
		g.fillRect(0, 0, Game.WIDTH*Game.SCALE, Game.HEIGHT*Game.SCALE);
		g.setColor(new Color(148, 0, 211));
		g.setFont(new Font("arial", Font.BOLD, 70));
		g.drawString(">Slug Killer<", Game.WIDTH*Game.SCALE/2 - 200, Game.HEIGHT*Game.SCALE/2 - 180);
		
		g.setColor(Color.white);
		g.setFont(new Font("arial", Font.BOLD, 40));
		if(pause) {
			g.drawString("Continue", Game.WIDTH*Game.SCALE/2 - 90, Game.HEIGHT*Game.SCALE/2 - 90);
		}else if(pause == false) {
			g.drawString("New Game", Game.WIDTH*Game.SCALE/2 - 90, Game.HEIGHT*Game.SCALE/2 - 90);
		}
		g.drawString("Load Game", Game.WIDTH*Game.SCALE/2 - 90, Game.HEIGHT*Game.SCALE/2 + 10);
		g.drawString("Exit", Game.WIDTH*Game.SCALE/2 - 90, Game.HEIGHT*Game.SCALE/2 + 110);
		
		if(select) {
			if(options[currentOption] == "New Game") {
				g.drawString(">", Game.WIDTH*Game.SCALE/2 - 120, Game.HEIGHT*Game.SCALE/2 - 90);
			}else if(options[currentOption] == "Load Game") {
				g.drawString(">", Game.WIDTH*Game.SCALE/2 - 120, Game.HEIGHT*Game.SCALE/2 + 10);
			}else if(options[currentOption] == "Exit") {
				g.drawString(">", Game.WIDTH*Game.SCALE/2 - 120, Game.HEIGHT*Game.SCALE/2 + 110);
			}
		}
	}

}
