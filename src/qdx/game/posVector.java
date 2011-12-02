package qdx.game;

public class posVector{
	public int x;
	public int y;
	public posVector(int x,int y){
		this.x = x;
		this.y = y;
	}	
	public posVector posRotate(posVector originalPos, float deg){
		double degree = Math.PI*(deg/180);
		//double r = Math.sqrt((originalPos.x-this.x)*(originalPos.x-this.x) 
		//					+ (originalPos.y-this.y)*(originalPos.y-this.y));
		double newX = originalPos.x - this.x;
		double newY = originalPos.y - this.y;
		
		double xc = Math.cos(degree)*newX + Math.sin(degree)*newY;
		double yc = -newX*Math.sin(degree)+newY*Math.cos(degree);
		posVector result = new posVector((int)xc,(int)yc);
		return result;
	}
	
	public posVector posRotate(int x, int y, float deg){
		posVector tmp = new posVector(x,y);
		return posRotate(tmp,deg);
	}
	
	public posVector reverse(int x, int y, float deg){//此处的deg应该给fixed.degree
		double degree = -Math.PI*(deg/180);
		double rx = Math.cos(degree)*x + Math.sin(degree)*y;
		double ry = -x*Math.sin(degree)+y*Math.cos(degree);
		
		posVector result = new posVector((int)(rx+this.x),(int)(ry+this.y));
		return result;
	}
	/*public static posVector posRotate(posVector axis,posVector originalPos, float deg){//degree应该小于90大于-90
		double degree = Math.PI*(deg/180);
		double r = Math.sqrt((originalPos.x-axis.x)*(originalPos.x-axis.x) 
							+ (originalPos.y-axis.y)*(originalPos.y-axis.y));
		double omiga = 0;
		omiga = Math.asin((originalPos.y-axis.y)/r);
		if(originalPos.x-axis.x>=0){			
				omiga = Math.asin((originalPos.y-axis.y)/r);			
		}
		else{			
				omiga = -Math.asin((originalPos.y-axis.y)/r);
		}
		double degreec = omiga - degree;//TODO:此处计算坐标的返回值有错！！！！
		if(originalPos.x-axis.x < 0)
			degreec = omiga + degree;
		double xc = Math.cos(degreec)*r;
		double yc = Math.sin(degreec)*r;
		posVector result = new posVector((int)xc,(int)yc);
		return result;
	}*/
}