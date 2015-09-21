import java.util.*;

public class Point {
	int x=0;
	int y=0;
	char[] ch=new char[500];
	public Point(int a, int b){
		x=a;
		y=b;
//		Random r = new Random();
		
//		for(int i = 0; i<500; i++)
//			ch[i]=(char)r.nextInt(28);
	}
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}	
}
