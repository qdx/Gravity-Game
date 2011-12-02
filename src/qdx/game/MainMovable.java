package qdx.game;

import java.util.concurrent.Semaphore;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class MainMovable extends Movable{

	double timeX;
	double timeY;
	
	boolean moving = false;
	boolean menuOpen = false;

		
	float touchX = 0;
	float touchY = 0;
	
	Bitmap arrowUp = null;
	Bitmap arrowDown = null;
	
	public MainMovable(int x,int y, int shape, Bitmap bitmap, int mass, GameView father){
		super(x, y, shape, bitmap, mass, father);
		
	}
	
	@Override
	public void drawSelf(Canvas canvas){
		try{
			semaphore.acquire();
			//mt.isRunning = false;
			if(menuOpen == true){//绘制调整速度大小和方向的菜单				
				Paint p = new Paint();			
				/* 以下一段代码中，t,k,q均为中间临时变量
				 * 这段代码的作用是计算在手指与圆心的连线
				 * 的方向上，距离圆心定长的点的坐标。根据
				 * 这个点的坐标来绘制调整速度方向的速度线
				 */
				float t = 0;
				float k = 0;
				float L = 100;
				if(touchY == currentY){
					k = 0;
					t = L*L;
				}
				else{
					float q = Math.abs(touchX - currentX)/Math.abs(touchY-currentY);
					t = (q*q*L*L)/(q*q+1);
					k = (L*L)/(q*q+1);
				}
				float m = 0;
				float n = 0;
				if(touchX - currentX >= 0 && touchY - currentY >= 0){
					m = (float) (Math.sqrt(t) + currentX);
					n = (float) (Math.sqrt(k) + currentY);
				}
				else if(touchX - currentX < 0 && touchY - currentY < 0){
					m = (float) (currentX - Math.sqrt(t));
					n = (float) (currentY - Math.sqrt(k));
				}
				else if(touchX - currentX < 0 && touchY - currentY >= 0){
					m = (float) (currentX - Math.sqrt(t));
					n = (float) (Math.sqrt(k) + currentY);
				}
				else if(touchX - currentX >= 0 && touchY - currentY < 0){
					m = (float) (Math.sqrt(t) + currentX);
					n = (float) (currentY - Math.sqrt(k));
				}
				else{
					canvas.drawText("error: " + m +" " + n, 150, 20, p);
				}
				
				p.setColor(Color.GREEN);
				p.setAntiAlias(true);
				p.setStrokeWidth(2);
				canvas.drawLine(currentX, currentY, m, n, p);//画出方向线	
				
				p.setColor(Color.BLUE);
				p.setStrokeWidth(3);
				p.setTextSize(30);
				
				p.setAntiAlias(true);
				canvas.drawBitmap(arrowUp, currentX + 15, currentY - 45 , null);//画出调整速度的增大箭头
				canvas.drawText(""+speedLv, currentX + 25, currentY + 10, p);//画出当前的速度Lv
				canvas.drawBitmap(arrowDown, currentX + 15, currentY + 15 , null);//画出调整速度的减小箭头
			}
			Paint paint = new Paint();
			paint.setColor(Color.RED);
			if(shape.getShape() == GameShape.CIRCLE_SHAPE){//画出物件本体
				canvas.drawBitmap(shape.bitmap, currentX - shape.radius, currentY - shape.radius, null);
				//canvas.drawCircle(currentX, currentY, 1, paint);
				/*paint.setColor(Color.WHITE);
				canvas.drawRect(400, 0, 480, 80, paint);
				paint.setColor(Color.BLUE);
				canvas.drawText("fvx: "+firstVX, 400, 20, paint);
				canvas.drawText("fvy: "+firstVY, 400, 30, paint);
				canvas.drawText("cvx: "+currentVX, 400, 40, paint);
				canvas.drawText("cvy: "+currentVY, 400, 50, paint);	*/			
			}
			semaphore.release();
		}
		catch(Exception e){
			Log.println(0, "MainMovable", "Outer Exception");
			e.printStackTrace();
		}
		
	}
	
	public void getMenuBitmap(Bitmap up, Bitmap down){//得到菜单的箭头图片用来绘制
		arrowUp = up;
		arrowDown = down;
	}
	
	@Override
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