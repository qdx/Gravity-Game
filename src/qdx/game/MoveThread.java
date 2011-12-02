package qdx.game;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.util.Log;


public class MoveThread extends Thread{
	
	Movable father;
	
	boolean isRunning = false;
	boolean selected = false;
	int sleepSpan = 40;
	
	float a_X = 0;
	float a_Y = 0;

	
	posVector bottomL = null;
	posVector bottomR = null;
	posVector topL = null;
	posVector topR = null;
	
	boolean collisonFlag = true;
	
	double currentTime;
	
	public MoveThread(Movable m){
		father = m;
		//isRunning = true;
	}
	
	
	
	public void run(){
		//Lock lock = new ReentrantLock();
		try{
			while(true){
				//TODO:此处用来完成对运动的计算
				try{
					father.semaphore.acquire();
				}
				catch(Exception e){
					Log.println(0, "MoveThread", "Inner Exception");
					e.printStackTrace();
				}
				finally{
					father.semaphore.release();
				}	
					if(isRunning){

						currentTime = System.nanoTime();
						
						if(father.currentX < -father.shape.radius 
							|| father.currentX > father.father.screenWidth + father.shape.radius
							|| father.currentY < -father.shape.radius
							|| father.currentY > father.father.screenHeight + father.shape.radius){
							father.father.win = false;
							father.father.lose = true;
							isRunning = false;
						}
						
						if(Math.abs(father.currentVX) < 2 && a_X == 0){
							father.currentVX = 0;
						}
						if(Math.abs(father.currentVY) < 2 && a_Y == 0){
							father.currentVY = 0;
						}		
						father.currentVX = (float) (father.firstVX + a_X * ((currentTime - father.lastMoveTimeX)/1000/1000/1000));
						father.currentVY = (float) (father.firstVY + a_Y * ((currentTime - father.lastMoveTimeY)/1000/1000/1000));
						if(father.currentVX != 0){
							father.currentX = (int) (father.startX 
													+ ((currentTime - father.lastMoveTimeX)/1000/1000/1000)*father.firstVX
													+ a_X*Math.pow((currentTime - father.lastMoveTimeX)/1000/1000/1000,2)/2);							
						}
						if(father.currentVY != 0){
							father.currentY = (int) (father.startY 
													+ ((currentTime - father.lastMoveTimeY)/1000/1000/1000)*father.firstVY
													+ a_Y*Math.pow((currentTime - father.lastMoveTimeY)/1000/1000/1000,2)/2);							
						}
						
						/*if(father.currentVX == 0
							&& father.currentVY == 0
							&& a_X == 0
							&& a_Y == 0){
							father.father.win = false;
							father.father.lose = true;
							isRunning = false;
						}*/
						
						for(Fixed f:father.father.fixedObjects){
							
							if(f == null) continue;
							if(f.isTarget == true){
								if(f.contains(father.currentX, father.currentY)){
									father.father.win = true;
									father.father.lose = false;
									isRunning = false;
								}
							}
							if(f instanceof gravityField){
								if(((gravityField)f).inField && !f.contains(father.currentX, father.currentY)){
									
									((gravityField)f).inField = false;
									double currentTime = System.nanoTime();
									a_X = father.lastaX.pop().floatValue();
									a_Y = father.lastaY.pop().floatValue();
									father.startX = father.currentX;
									father.startY = father.currentY;
									father.firstVX = father.currentVX;
									father.firstVY = father.currentVY;
									father.lastMoveTimeX = currentTime;
									father.lastMoveTimeY = currentTime;						
								}
								else if(!((gravityField)f).inField && f.contains(father.currentX, father.currentY)){
									
									double currentTime = System.nanoTime();
									((gravityField)f).inField = true;
									Float Fa_X = new Float(a_X);
									Float Fa_Y = new Float(a_Y);
									father.lastaX.push(Fa_X);
									father.lastaY.push(Fa_Y);
									a_X += ((gravityField)f).gravityX;
									//System.exit(0);
									a_Y += ((gravityField)f).gravityY;
									//father.currentY = (int) a_X;
									father.startX = father.currentX;
									father.startY = father.currentY;
									father.firstVX = father.currentVX;
									father.firstVY = father.currentVY;
									father.lastMoveTimeX = currentTime;
									father.lastMoveTimeY = currentTime;
								}
							}
							else if(f.hasMass == true && collisonFlag){								
								if(f.shape.getShape() == GameShape.RECT_SHAPE){
									
									double degree = Math.PI*(f.degree/180);
									posVector fixedPos = new posVector((int)f.startX,(int)f.startY);
									posVector cpos = fixedPos.posRotate(new posVector((int)father.currentX,(int)father.currentY),f.degree);
														
									bottomL = fixedPos.posRotate(f.startX- f.shape.width/2,f.startY+ f.shape.height/2, -f.degree);
									bottomR = fixedPos.posRotate(f.startX+f.shape.width/2,f.startY+f.shape.height/2, -f.degree);
									topL = fixedPos.posRotate(f.startX- f.shape.width/2, f.startY- f.shape.height/2, -f.degree);
									topR = fixedPos.posRotate(f.startX+f.shape.width/2, f.startY- f.shape.height/2, -f.degree);
									
									
									//Log.println(0, "MT_bottomR", ""+bottomR.x+"."+bottomR.y);
									//System.exit(0);
									double xc = cpos.x;
									double yc = cpos.y;
										
									if(yc >= f.shape.height/2
										&& yc <= f.shape.height/2 + father.shape.radius
										&& xc >= -f.shape.width/2
										&& xc <= f.shape.width/2){//down
										
										currentTime = System.nanoTime();										
										father.lastMoveTimeX = currentTime;
										father.lastMoveTimeY = currentTime;
										float tmpVX = father.currentVX;
										float tmpVY = father.currentVY;
										father.currentVX = (float) (tmpVX*Math.cos(degree)*Math.cos(degree) 
																	+ tmpVY*Math.sin(degree)*Math.cos(degree)
																	- tmpVX*Math.sin(degree)*Math.sin(degree)*(1-f.impactFactor)
																	+ tmpVY*Math.sin(degree)*Math.cos(degree)*(1-f.impactFactor));
										father.currentVY = (float) (tmpVX*Math.sin(degree)*Math.cos(degree)
																	+ tmpVY*Math.sin(degree)*Math.sin(degree)
																	+ tmpVX*Math.sin(degree)*Math.cos(degree)*(1-f.impactFactor)
																	- tmpVY*Math.cos(degree)*Math.cos(degree)*(1-f.impactFactor));										
										
										yc = f.shape.height/2+father.shape.radius+3;										
										posVector reverse = fixedPos.reverse((int)xc,(int)yc,f.degree);
										father.currentX = reverse.x;
										father.currentY = reverse.y;
										father.startX = father.currentX;
										father.startY = father.currentY;
										father.firstVX = father.currentVX;
										father.firstVY = father.currentVY;
										
										//collisonFlag = false;//调试用
									}
									else if(yc >= -f.shape.height/2 - father.shape.radius
											&& yc <= f.shape.height/2
											&& xc >= -f.shape.width/2
											&& xc <= f.shape.width/2){//up
										
										currentTime = System.nanoTime();
										father.lastMoveTimeX = currentTime;
										father.lastMoveTimeY = currentTime;
										float tmpVX = father.currentVX;
										float tmpVY = father.currentVY;
										father.currentVX = (float) (tmpVX*Math.cos(degree)*Math.cos(degree) 
												+ tmpVY*Math.sin(degree)*Math.cos(degree)
												- tmpVX*Math.sin(degree)*Math.sin(degree)*(1-f.impactFactor)
												+ tmpVY*Math.sin(degree)*Math.cos(degree)*(1-f.impactFactor));
										father.currentVY = (float) (tmpVX*Math.sin(degree)*Math.cos(degree)
												+ tmpVY*Math.sin(degree)*Math.sin(degree)
												+ tmpVX*Math.sin(degree)*Math.cos(degree)*(1-f.impactFactor)
												- tmpVY*Math.cos(degree)*Math.cos(degree)*(1-f.impactFactor));
										yc = -f.shape.height/2-father.shape.radius-3;
										posVector reverse = fixedPos.reverse((int)xc,(int)yc,f.degree);
										father.currentX = reverse.x;
										father.currentY = reverse.y;
										father.startX = father.currentX;
										father.startY = father.currentY;
										father.firstVX = father.currentVX;
										father.firstVY = father.currentVY;
										father.firstVX = father.currentVX;
										father.firstVY = father.currentVY;
										//collisonFlag = false;//调试用
									}
									else if(xc >= -f.shape.width/2 - father.shape.radius
											&& xc <= -f.shape.width/2
											&& yc >= -f.shape.height/2
											&& yc <= f.shape.height/2){//left
										father.startX = father.currentX;
										father.startY = father.currentY;
										father.lastMoveTimeX = currentTime;
										father.lastMoveTimeY = currentTime;
										float tmpVX = father.currentVX;
										float tmpVY = father.currentVY;
										double tmpDeg = degree;
										if(degree > 0) tmpDeg = degree-Math.PI/2;
										else tmpDeg = degree + Math.PI/2;
										father.currentVX = (float) (tmpVX*Math.cos(tmpDeg)*Math.cos(tmpDeg) 
												+ tmpVY*Math.sin(tmpDeg)*Math.cos(tmpDeg)
												- tmpVX*Math.sin(tmpDeg)*Math.sin(tmpDeg)*(1-f.impactFactor)
												+ tmpVY*Math.sin(tmpDeg)*Math.cos(tmpDeg)*(1-f.impactFactor));
										father.currentVY = (float) (tmpVX*Math.sin(tmpDeg)*Math.cos(tmpDeg)
												+ tmpVY*Math.sin(tmpDeg)*Math.sin(tmpDeg)
												+ tmpVX*Math.sin(tmpDeg)*Math.cos(tmpDeg)*(1-f.impactFactor)
												- tmpVY*Math.cos(tmpDeg)*Math.cos(tmpDeg)*(1-f.impactFactor));
										xc = -f.shape.width/2-father.shape.radius-3;										
										posVector reverse = fixedPos.reverse((int)xc,(int)yc,f.degree);
										father.currentX = reverse.x;
										father.currentY = reverse.y;
										father.startX = father.currentX;
										father.startY = father.currentY;
										father.firstVX = father.currentVX;
										father.firstVY = father.currentVY;
										//collisonFlag = false;//调试用
									}
									else if(xc >= f.shape.width/2
											&& xc <= f.shape.width/2 + father.shape.radius
											&& yc >= -f.shape.height/2
											&& yc <= f.shape.height/2){//right
										father.startX = father.currentX;
										father.startY = father.currentY;
										father.lastMoveTimeX = currentTime;
										father.lastMoveTimeY = currentTime;
										float tmpVX = father.currentVX;
										float tmpVY = father.currentVY;
										double tmpDeg = degree;
										if(degree > 0) tmpDeg = degree-Math.PI/2;
										else tmpDeg = degree + Math.PI/2;
										father.currentVX = (float) (tmpVX*Math.cos(tmpDeg)*Math.cos(tmpDeg) 
												+ tmpVY*Math.sin(tmpDeg)*Math.cos(tmpDeg)
												- tmpVX*Math.sin(tmpDeg)*Math.sin(tmpDeg)*(1-f.impactFactor)
												+ tmpVY*Math.sin(tmpDeg)*Math.cos(tmpDeg)*(1-f.impactFactor));
										father.currentVY = (float) (tmpVX*Math.sin(tmpDeg)*Math.cos(tmpDeg)
												+ tmpVY*Math.sin(tmpDeg)*Math.sin(tmpDeg)
												+ tmpVX*Math.sin(tmpDeg)*Math.cos(tmpDeg)*(1-f.impactFactor)
												- tmpVY*Math.cos(tmpDeg)*Math.cos(tmpDeg)*(1-f.impactFactor));
										xc = f.shape.width/2+father.shape.radius+3;										
										posVector reverse = fixedPos.reverse((int)xc,(int)yc,f.degree);
										father.currentX = reverse.x;
										father.currentY = reverse.y;
										father.startX = father.currentX;
										father.startY = father.currentY;
										father.firstVX = father.currentVX;
										father.firstVY = father.currentVY;
										//collisonFlag = false;//调试用
									}
									else if(xc >= f.shape.width/2//A
											&& xc <= f.shape.width/2 + father.shape.radius
											&& yc >= f.shape.height/2
											&& yc <= f.shape.height/2 + father.shape.radius){
										double distance = Math.sqrt((xc-f.shape.width/2)*(xc-f.shape.width/2) 
												+ (yc-f.shape.height/2)*(yc-f.shape.height/2));
										//father.currentVX = (float) distance;
										
										if(distance <= father.shape.radius){
											//System.exit(0);
											father.startX = father.currentX;
											father.startY = father.currentY;
											father.lastMoveTimeX = currentTime;
											father.lastMoveTimeY = currentTime;
											float tmpVX = father.currentVX;
											float tmpVY = father.currentVY;
											double tmpDeg = degree;											
											try{
												tmpDeg = Math.atan(((bottomR.x + f.startX) - (father.currentX))/((father.currentY)-(bottomR.y + f.startY)));
											}
											catch(ArithmeticException e){
												tmpDeg = Math.PI/2;
											}
											father.currentVX = (float) (tmpVX*Math.cos(tmpDeg)*Math.cos(tmpDeg) 
													+ tmpVY*Math.sin(tmpDeg)*Math.cos(tmpDeg)
													- tmpVX*Math.sin(tmpDeg)*Math.sin(tmpDeg)*(1-f.impactFactor)
													+ tmpVY*Math.sin(tmpDeg)*Math.cos(tmpDeg)*(1-f.impactFactor));
											father.currentVY = (float) (tmpVX*Math.sin(tmpDeg)*Math.cos(tmpDeg)
													+ tmpVY*Math.sin(tmpDeg)*Math.sin(tmpDeg)
													+ tmpVX*Math.sin(tmpDeg)*Math.cos(tmpDeg)*(1-f.impactFactor)
													- tmpVY*Math.cos(tmpDeg)*Math.cos(tmpDeg)*(1-f.impactFactor));
											double tDeg = Math.asin((yc-f.shape.height/2)/distance);
											Log.v("xcb:", ""+xc);
											Log.v("ycb:", ""+yc);
											Log.v("tDeg:",""+tDeg);
											xc = f.shape.width/2+Math.cos(tDeg)*father.shape.radius + 2;
											yc = f.shape.height/2+Math.sin(tDeg)*father.shape.radius + 2;
										
											//xc = f.shape.width/2+f.shape.radius;
											//yc = f.shape.height/2 +f.shape.radius;
											Log.v("w:", ""+f.shape.width);
											Log.v("h:", ""+f.shape.height);
											Log.v("r:", ""+f.shape.radius);
											
											Log.v("xc:", ""+xc);
											Log.v("yc:", ""+yc);
											posVector reverse = fixedPos.reverse((int)xc,(int)yc,f.degree);
											Log.v("x:", ""+reverse.x);
											Log.v("y:", ""+reverse.y);
											father.currentX = reverse.x;
											father.currentY = reverse.y;
											father.startX = father.currentX;
											father.startY = father.currentY;
											father.firstVX = father.currentVX;
											father.firstVY = father.currentVY;
											
											//father.currentVX = 0;
											//father.currentVY = 0;
											//collisonFlag = false;//调试用
										}
									}
									else if(xc >= -f.shape.width/2 - father.shape.radius//B
											&& xc <= -f.shape.width/2
											&& yc >= f.shape.height/2
											&& yc <= f.shape.height/2 + father.shape.radius){
										double distance = Math.sqrt((xc+f.shape.width/2)*(xc+f.shape.width/2) 
												+ (yc-f.shape.height/2)*(yc-f.shape.height/2));
										
										if(distance <= father.shape.radius){
											father.startX = father.currentX;
											father.startY = father.currentY;
											father.lastMoveTimeX = currentTime;
											father.lastMoveTimeY = currentTime;
											float tmpVX = father.currentVX;
											float tmpVY = father.currentVY;
											double tmpDeg = degree;
											try{
												tmpDeg = Math.atan(((bottomL.x + f.startX) - (father.currentX))/((father.currentY)-(bottomL.y + f.startY)));
											}
											catch(ArithmeticException e){
												tmpDeg = Math.PI/2;
											}
											father.currentVX = (float) (tmpVX*Math.cos(tmpDeg)*Math.cos(tmpDeg) 
													+ tmpVY*Math.sin(tmpDeg)*Math.cos(tmpDeg)
													- tmpVX*Math.sin(tmpDeg)*Math.sin(tmpDeg)*(1-f.impactFactor)
													+ tmpVY*Math.sin(tmpDeg)*Math.cos(tmpDeg)*(1-f.impactFactor));
											father.currentVY = (float) (tmpVX*Math.sin(tmpDeg)*Math.cos(tmpDeg)
													+ tmpVY*Math.sin(tmpDeg)*Math.sin(tmpDeg)
													+ tmpVX*Math.sin(tmpDeg)*Math.cos(tmpDeg)*(1-f.impactFactor)
													- tmpVY*Math.cos(tmpDeg)*Math.cos(tmpDeg)*(1-f.impactFactor));
											double tDeg = Math.asin((yc-f.shape.height/2)/distance);
											xc = -f.shape.width/2-Math.cos(tDeg)*father.shape.radius - 2;
											yc = f.shape.height/2+Math.sin(tDeg)*father.shape.radius + 2;
											posVector reverse = fixedPos.reverse((int)xc,(int)yc,f.degree);
											father.currentX = reverse.x;
											father.currentY = reverse.y;
											father.startX = father.currentX;
											father.startY = father.currentY;
											father.firstVX = father.currentVX;
											father.firstVY = father.currentVY;
											//father.currentVX = 0;
											//father.currentVY = 0;
											//collisonFlag = false;//调试用
										}
									}
									else if(xc >= -f.shape.width/2 - father.shape.radius//C
											&& xc <= -f.shape.width/2
											&& yc >= -f.shape.height/2 - father.shape.radius
											&& yc <= -f.shape.height/2){
										double distance = Math.sqrt((xc+f.shape.width/2)*(xc+f.shape.width/2) 
												+ (yc+f.shape.height/2)*(yc+f.shape.height/2));
										
										if(distance <= father.shape.radius){
											father.startX = father.currentX;
											father.startY = father.currentY;
											father.lastMoveTimeX = currentTime;
											father.lastMoveTimeY = currentTime;
											float tmpVX = father.currentVX;
											float tmpVY = father.currentVY;
											double tmpDeg = degree;
											try{
												tmpDeg = Math.atan(((topL.x + f.startX) - (father.currentX))/((father.currentY)-(topL.y + f.startY)));
											}
											catch(ArithmeticException e){
												tmpDeg = Math.PI/2;
											}
											father.currentVX = (float) (tmpVX*Math.cos(tmpDeg)*Math.cos(tmpDeg) 
													+ tmpVY*Math.sin(tmpDeg)*Math.cos(tmpDeg)
													- tmpVX*Math.sin(tmpDeg)*Math.sin(tmpDeg)*(1-f.impactFactor)
													+ tmpVY*Math.sin(tmpDeg)*Math.cos(tmpDeg)*(1-f.impactFactor));
											father.currentVY = (float) (tmpVX*Math.sin(tmpDeg)*Math.cos(tmpDeg)
													+ tmpVY*Math.sin(tmpDeg)*Math.sin(tmpDeg)
													+ tmpVX*Math.sin(tmpDeg)*Math.cos(tmpDeg)*(1-f.impactFactor)
													- tmpVY*Math.cos(tmpDeg)*Math.cos(tmpDeg)*(1-f.impactFactor));
											double tDeg = Math.asin((-yc-f.shape.height/2)/distance);
											xc = -f.shape.width/2-Math.cos(tDeg)*father.shape.radius - 2;
											yc = -f.shape.height/2-Math.sin(tDeg)*father.shape.radius - 2;
											posVector reverse = fixedPos.reverse((int)xc,(int)yc,f.degree);
											father.currentX = reverse.x;
											father.currentY = reverse.y;
											father.startX = father.currentX;
											father.startY = father.currentY;
											father.firstVX = father.currentVX;
											father.firstVY = father.currentVY;
											//father.currentVX = 0;
											//father.currentVY = 0;
											//collisonFlag = false;//调试用
										}
									}
									else if(xc >= f.shape.width/2//D
											&& xc <= f.shape.width/2 + father.shape.radius
											&& yc >= -f.shape.height/2 - father.shape.radius
											&& yc <= -f.shape.height/2){
										double distance = Math.sqrt((xc-f.shape.width/2)*(xc-f.shape.width/2) 
												+ (yc+f.shape.height/2)*(yc+f.shape.height/2));
										
										if(distance <= father.shape.radius){
											father.startX = father.currentX;
											father.startY = father.currentY;
											father.lastMoveTimeX = currentTime;
											father.lastMoveTimeY = currentTime;
											float tmpVX = father.currentVX;
											float tmpVY = father.currentVY;
											double tmpDeg = degree;
											try{
												tmpDeg = Math.atan(((topR.x + f.startX) - (father.currentX))/((father.currentY)-(topR.y + f.startY)));
											}
											catch(ArithmeticException e){
												tmpDeg = Math.PI/2;
											}
											father.currentVX = (float) (tmpVX*Math.cos(tmpDeg)*Math.cos(tmpDeg) 
													+ tmpVY*Math.sin(tmpDeg)*Math.cos(tmpDeg)
													- tmpVX*Math.sin(tmpDeg)*Math.sin(tmpDeg)*(1-f.impactFactor)
													+ tmpVY*Math.sin(tmpDeg)*Math.cos(tmpDeg)*(1-f.impactFactor));
											father.currentVY = (float) (tmpVX*Math.sin(tmpDeg)*Math.cos(tmpDeg)
													+ tmpVY*Math.sin(tmpDeg)*Math.sin(tmpDeg)
													+ tmpVX*Math.sin(tmpDeg)*Math.cos(tmpDeg)*(1-f.impactFactor)
													- tmpVY*Math.cos(tmpDeg)*Math.cos(tmpDeg)*(1-f.impactFactor));
											double tDeg = Math.asin((-yc-f.shape.height/2)/distance);
											xc = +f.shape.width/2+Math.cos(tDeg)*father.shape.radius + 2;
											yc = -f.shape.height/2-Math.sin(tDeg)*father.shape.radius - 2;
											posVector reverse = fixedPos.reverse((int)xc,(int)yc,f.degree);
											father.currentX = reverse.x;
											father.currentY = reverse.y;
											father.startX = father.currentX;
											father.startY = father.currentY;
											father.firstVX = father.currentVX;
											father.firstVY = father.currentVY;
											//father.currentVX = 0;
											//father.currentVY = 0;
											//collisonFlag = false;//调试用
										}
									}
								}
								
							}
						}
						if(father.currentX < father.father.screenWidth/8 + father.shape.radius){//对菜单栏的碰撞检测和处理
							father.startX = father.father.screenWidth/8 + father.shape.radius;
							father.startY = father.currentY;
							father.lastMoveTimeX = currentTime;
							father.lastMoveTimeY = currentTime;							
							father.currentVX = -father.currentVX * (1-father.impactFactor);
							father.currentVY = father.currentVY * (1-father.impactFactor);
							father.firstVX = father.currentVX;
							father.firstVY = father.currentVY;
						}
						
					}
					Thread.sleep(30);
				}						
							//休眠一段时间
		}
		catch(Exception e){
			Log.println(0, "MoveThread", "Outer Exception");
			e.printStackTrace();
		}
	}
	
	
}