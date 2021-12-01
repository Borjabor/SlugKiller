package com.dedecogames.world;

public class Camera {
	
	public static int x = 0;
	public static int y = 0;
	
	public static int clamp(int Current, int Min, int Max) {
		if(Current < Min) {
			Current = Min;
		}
		
		if(Current > Max) {
			Current = Max;
		}
		
		return Current;
	}

}
