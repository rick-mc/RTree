import java.util.*;
import java.io.*;

public class Main{
	public static void main(String[] args){
		int option, u, x, y, i = 0;
		u=x=y=i;
		String valuesInString[] = new String[2];
		String xString[] = new String[2];
		String yString[] = new String[2];
		Integer xMinMax[] = new Integer[2];
		Integer values[][], yMinMax[] = new Integer[2];
		String line, parts[][];
		Scanner scanner;
		RTree rTree = new RTree(3);
		ArrayList<Rectangle> rectList = new ArrayList<Rectangle>();
		ArrayList<Rectangle> initRects = new ArrayList<Rectangle>();
		ArrayList<Point> pointList = new ArrayList<Point> ();
		Point p1, p2,p3;
		Rectangle searchArea;
		
		try{
			BufferedReader reader = new BufferedReader(new FileReader("project3dataset30K.txt"));
			line = null;

			while((line = reader.readLine()) != null)
				i++;

			parts = new String[2][i];
			values = new Integer[i][2];
			reader = new BufferedReader(new FileReader("project3dataset30K.txt"));
			i = 0;
			while((line = reader.readLine()) != null){
				valuesInString = line.split(",");
				parts[0][i]= valuesInString[0];
				parts[1][i]= valuesInString[1];
				i++;
			}
			for(int j = 0; j <i; j++){
				for(int k = 0; k <=1; k++){
					values[j][k]= Integer.parseInt(parts[k][j]);
					if(k==1){
						p1 = new Point(values[j][k-1], values[j][k]);
						pointList.add(p1);
					}	
				}
			}	

			for(Point p: pointList)
				rectList.add(new Rectangle(p,p,0,null,null));
			initRects.add(rectList.get(0));
			initRects.add(rectList.get(1));
			rTree.makeRTree(initRects,1);
			pointList=null;
			for(u = 2; u <rectList.size(); u++)
				rTree.insert(rectList.get(u));
		} catch(IOException e){}

		while(true){
			scanner = new Scanner(System.in);
			option  = 1;
			line = null;
			try{
				while(!(option==0)){
					System.out.println("\n0 to exit\n1 to search\n2 to insert\n3 to delete\n");
					System.out.print("Pick an option: ");
					option = scanner.nextInt();		

					switch(option){
					case 1:
						System.out.println("\nSearch for points satisfying: ");
						System.out.print("Min x: ");
						line = scanner.next();
						xString[0] = line;
						System.out.print("Max x: ");
						line = scanner.next();
						xString[1] = line;
						xMinMax[0] = Integer.parseInt(xString[0]);
						xMinMax[1] = Integer.parseInt(xString[1]);
	
						System.out.print("Min y: ");
						line = scanner.next();
						yString[0] = line;
						System.out.print("Max y: ");		
						line= scanner.next();
						yString[1] = line;
						yMinMax[0] = Integer.parseInt(yString[0]);
						yMinMax[1] = Integer.parseInt(yString[1]);

						p1 = new Point(xMinMax[0],yMinMax[1]);
						p2 = new Point(xMinMax[1],yMinMax[0]);
			
						searchArea = new Rectangle(p1, p2);		
						rectList = rTree.search(searchArea);

						System.out.print("\nRESULTS\n");
						System.out.print("--------\n");
	
						u=0;
						for(Rectangle r : rectList){
							i = r.duplicate.size();
							while(i != -1){
								u++;
								System.out.println(u + ". " + r.getP1().getX() + "," + r.getP1().getY());
								i--;
							}
						}					
						break;
			
					case 2 : 
						System.out.print("\nx value to insert: ");
						x = scanner.nextInt();
						System.out.print("y value to insert: ");
						y = scanner.nextInt();
						p1 = new Point(x, y);
						rTree.insert(new Rectangle(p1, p1, 0, null, null));
						break;
					case 3:
						System.out.print("\nx value to delete: ");
						x = scanner.nextInt();
						System.out.print("y walue to delete: ");
						y = scanner.nextInt();
						p1 = new Point(x,y);
						rectList = rTree.getRTree();
						for(Rectangle r : rectList){
							if(r.isPoint()&&r.getP1().getX()==p1.x &&r.getP2().getY()==p1.y){
								rTree.delete(r);
								rectList=rTree.getRTree();
								break;
							}
						}
						break;
					case 0 : continue;
		
					default : System.out.println("Please enter a valid character");
				}
			}
			break;
			}catch(InputMismatchException e){continue;
			}catch(NumberFormatException e){continue;}		
	
		}
	}
}
