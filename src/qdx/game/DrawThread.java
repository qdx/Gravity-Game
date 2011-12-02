package qdx.game;

import android.graphics.Canvas;
import android.view.SurfaceHolder;
import java.util.concurrent.locks.*;

public class DrawThread extends Thread{
	GameView father;
	SurfaceHolder surfaceHolder;
	
	int count = 0;
	boolean run = true;
	
	public DrawThread(GameView gv, SurfaceHolder surfaceholder){
		father = gv;
		surfaceHolder = surfaceholder;
	}
	
	public void run(){
		Canvas c = null;
		try{
			while(true){
				if(run){
				try{
					c = surfaceHolder.lockCanvas(null);
					synchronized(surfaceHolder){
						father.doDraw(c);					
					}				
				}
				catch(Exception e){
					e.printStackTrace();
				}
				finally{
					if(c != null)
						surfaceHolder.unlockCanvasAndPost(c);
				}
					Thread.sleep(30);			
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}