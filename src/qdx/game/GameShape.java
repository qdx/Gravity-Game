package qdx.game;

import android.graphics.Bitmap;

public class GameShape{
	public static final int RECT_SHAPE = 1;
	public static final int CIRCLE_SHAPE = 2;
	public static final int OVAL_SHAPE = 3;
	public static final int LINE = 4;
	
	private int shape;
	Bitmap bitmap;
	int width;
	int height;
	int radius;
	
	public GameShape(int shape, Bitmap bitmap){
		this.shape = shape;
		this.bitmap = bitmap;
		if(this.shape == RECT_SHAPE){
			width = bitmap.getWidth();
			height = bitmap.getHeight();
			radius = -1;
		}
		else if(this.shape == CIRCLE_SHAPE){
			width = bitmap.getWidth();
			height = bitmap.getHeight();
			radius = width/2;
		}
		else{
			
		}
	}
	public int getShape(){
		return shape;
	}
}