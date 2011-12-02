package qdx.game;

import java.util.Stack;
import java.util.concurrent.Semaphore;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Movable{
	int currentX = 0;
	int currentY = 0;
	int startVX = 0;
	int startVY = 0;
	int mass = 0;
	int speedLv = 1;
	int[] speed = new int[9];
	
	float startX = 0;
	float startY = 0;
	float currentVX = 0;
	float currentVY = 0;
	float impactFactor = 0.25f;
	float test = 0;
	
	float firstVX = 0;
	float firstVY = 0;
	double lastMoveTimeX = 0;
	double lastMoveTimeY = 0;
	
	Stack<Float> lastaX = new Stack<Float>();
	Stack<Float> lastaY = new Stack<Float>();
	boolean touchAble = true;
	boolean exists = true;
	boolean visible = true;
	boolean canTouch = false;
	boolean canDrag = false;
	boolean isMoving = false;
	boolean dragable = false;
	
	Semaphore semaphore = new Semaphore(1);

	MoveThread mt = null;
	
	GameShape shape = null;
	
	GameView father = null;
	
	public Movable(int x,int y, int shape, Bitmap bitmap, int mass, GameView father){
		this.shape = new GameShape(shape, bitmap);
		this.father = father;
		startX = x;
		startY = y;
		currentX = x;
		currentY = y;
		this.mass = mass;
		for(int i = 1 ; i <= 9 ; i ++){//这个速度为经验值，经过实际测试觉得比较合理。
			speed[i - 1] = i * 50;
		}
		mt = new MoveThread(this);
		mt.start();
	}
	public void drawSelf(Canvas canvas){
		if(shape.getShape() == GameShape.CIRCLE_SHAPE)
			canvas.drawBitmap(shape.bitmap, currentX - shape.radius, currentY - shape.radius, null);	
	}
	
	
	public boolean contains(float x,float y){//判断给定的坐标是否在本组件的范围内
		switch(shape.getShape()){
		case(GameShape.RECT_SHAPE):{
			if(x >= currentX && x <= currentX + shape.width 
					&& y <= currentY && y >= currentY + shape.height)
				return true;
			else 
				return false;
		}
		case(GameShape.CIRCLE_SHAPE):{
			float tmpx = Math.abs(x-currentX)*Math.abs(x-currentX);//计算和圆心的距离
			float tmpy = Math.abs(y-currentY)*Math.abs(y-currentY);
			if(tmpx+tmpy <= shape.radius*shape.radius)
				return true;
			else
				return false;
		}
		default:{
			return false;
		}
		}		
	}
}