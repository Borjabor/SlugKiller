package com.dedecogames.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.dedecogames.entities.Player;
import com.dedecogames.main.Game;

public class UI {
	
	public void render(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(4, 3, 52, 9);
		g.setColor(Color.red);
		g.fillRect(5, 4, 50, 7);
		g.setColor(new Color(0,200,80));
		g.fillRect(5, 4, (int)((Game.player.life/Game.player.maxlife)*50), 7);
		g.setColor(Color.white);
		g.setFont(new Font("arial", Font.BOLD, 9));
		g.drawString((int)Game.player.life + " / " + (int)Game.player.maxlife, 16, 11);
		
	}

}
