import java.util.*;	

public class RTree {
	private ArrayList<Rectangle> rtrnList = new ArrayList<Rectangle>();
	private double limit = 0.0;
	private Rectangle head;
	private ArrayList<Rectangle> rectList1 = new ArrayList<Rectangle>();
	private ArrayList<Rectangle> rectList2= new ArrayList<Rectangle>();
	private ArrayList<Rectangle> tempStorage= new ArrayList<Rectangle>();
	private ArrayList<Rectangle> rectsToSplit= new ArrayList<Rectangle>();
	private ArrayList<Rectangle> nodesToDelete= new ArrayList<Rectangle>();
	private ArrayList<Rectangle> nodesToDelete1 = new ArrayList<Rectangle>();
	private ArrayList<Rectangle> descendants = new ArrayList<Rectangle>();
	private Queue<Rectangle> rectQ = new LinkedList<Rectangle>();
	private Rectangle MBR1 = null;
	private Rectangle MBR2 = null;
	
	public RTree(int cap){
		limit = cap;
	}
	public ArrayList<Rectangle> getRTree(){
		return rtrnList;
	}

	public void insert(Rectangle rect3){
		if(search(rect3).isEmpty()){
			rtrnList.add(rect3);
			Rectangle rectParent = chooseLeaf(rect3,head);
			rectParent.addChild(rect3); 
			rect3.setParent(rectParent);

			if(rectParent.getChildren().size()> limit)
				split(rectParent);
			else
				adjustTree(rect3);	
		}
		else 
			search(rect3).get(0).addDuplicate(new Point(rect3.getP1().getX(), rect3.getP2().getY()));

	}

	public Rectangle chooseLeaf(Rectangle prect, Rectangle r){
		if(r.isLeaf()){
			return r;
		}

		Rectangle minRect = null;	
		int minArea = Integer.MAX_VALUE;
		for(Rectangle p: r.getChildren())
		{
			Rectangle temp = minimumBoundingRectangle(prect,p);	
			if(temp.getArea()-p.getArea()<minArea)
			{
				minRect = p;
				minArea = temp.getArea()-p.getArea();	
			}
		}	

		return chooseLeaf(prect, minRect);
	}
	
	public void split(Rectangle rect){
		Rectangle parent = rect.getParent();
		if(rectList1.isEmpty()&&rectList2.isEmpty()){	
			tempStorage = rect.getChildren();
			pickSeeds(tempStorage);
		}
		if(tempStorage.isEmpty()){	
			if(parent == null)
			{
				parent = new Rectangle(minimumBoundingRectangle(MBR1,MBR2), head.getDepth()+1,  new ArrayList<Rectangle>(), null);
				rtrnList.remove(head);
				rtrnList.add(parent);
				head = parent;

			}

			Rectangle r = new Rectangle(MBR1, parent.getDepth()-1, new ArrayList<Rectangle>(rectList1), parent);
			Rectangle b = new Rectangle(MBR2, parent.getDepth()-1,new ArrayList<Rectangle>(rectList2), parent);

			rectList1.clear();
			rectList2.clear();
			tempStorage.clear();		
			MBR1 = MBR2 = null;
			parent.addChild(r);
			parent.addChild(b);
			for(Rectangle c: r.getChildren())
				c.setParent(r);
			for(Rectangle c: b.getChildren())
				c.setParent(b);
			parent.removeChild(rect);
			rtrnList.remove(rect);	
			rtrnList.add(r);
			rtrnList.add(b);

			rectsToSplit.add(r);	
			adjustTree(r);
			return;

		}
		pickNext();
		split(rect);
	}
	
	public void pickSeeds(ArrayList<Rectangle> rect){
		int maxArea = minimumBoundingRectangle(rect.get(0),rect.get(1)).getArea()-rect.get(0).getArea()-rect.get(1).getArea();
		Rectangle rect1=null;
		Rectangle rect2=null;
		int temp = 0;
		
		for(Rectangle x:rect)				
		{
			for(Rectangle y: rect)
			{
				if(x.contains(y))continue;
				temp = minimumBoundingRectangle(x,y).getArea()-x.getArea()-y.getArea();	

				if(temp>=maxArea){
					maxArea = temp;
					rect1 = x;
					rect2 = y;
				}
			}
		}

		rectList1.add(rect1);
		MBR1 = rect1;				
		rectList2.add(rect2);
		MBR2 = rect2;
		tempStorage.remove(rect1);
		tempStorage.remove(rect2);
	}

	public void pickNext(){
		int maxDifference = 0;
		Rectangle maxDiff=null; 
		int maxDiffGroup = 0;

		for(Rectangle t:tempStorage)
		{
			int d1,d2;
			d1 = minimumBoundingRectangle(MBR1,t).getArea()-MBR1.getArea();
			d2 = minimumBoundingRectangle(MBR2,t).getArea()-MBR2.getArea();
			int diff = Math.abs(d1-d2);
			if(diff>=maxDifference)
			{
				maxDifference = diff;
				maxDiff = t;
				if(d1>d2)
					maxDiffGroup = 2;
				else
					maxDiffGroup = 1;
			}
		}

		if((rectList1.size()==1||rectList2.size()==1)&&rectList1.size()!=rectList2.size()){
			if(rectList1.size()>rectList2.size())
				maxDiffGroup = 2;
			else
				maxDiffGroup = 1;
		}

		if(maxDiffGroup==1)
		{
			MBR1 = minimumBoundingRectangle(MBR1,maxDiff);
			rectList1.add(maxDiff);
		}
		else{
			MBR2 = minimumBoundingRectangle(MBR2,maxDiff);
			rectList2.add(maxDiff);
		}
		tempStorage.remove(maxDiff);
	}
	
	public void adjustTree(Rectangle r){
		if(r.equals(head))
			rectsToSplit.clear();
		else
		{
			Rectangle p = r.getParent();

			if(rectsToSplit.contains(r)&&p.getChildren().size()>limit)
				split(r.getParent());
			else
			{
				p.adjustMBR();
				adjustTree(p);
			}

		}
	}
	
	public Rectangle minimumBoundingRectangle(Rectangle a, Rectangle b){
		int newMaxX = Math.max(a.getP2().getX(),b.getP2().getX());
		int newMinX =  Math.min(a.getP1().getX(),b.getP1().getX());
		int newMaxY = Math.max(a.getP1().getY(),b.getP1().getY());
		int newMinY = Math.min(a.getP2().getY(),b.getP2().getY());
		return new Rectangle(new Point(newMinX,newMaxY),  new Point(newMaxX, newMinY));
	}

	public void delete(Rectangle r){
		if(r.duplicate.size()==0){
			Rectangle leaf = findLeaf(r);
			leaf.removeChild(r);							
			rtrnList.remove(r);
			condenseTree(leaf);
			if(head.getChildren().size()==1){
				Rectangle newRoot = head.getChildren().get(0);
				newRoot.setParent(null);
				rtrnList.remove(head);
				head = newRoot;
			}
		}
		else r.removeDuplicate();
	}
	
	public Rectangle findLeaf(Rectangle p){
		rectQ.clear();
		rectQ.add(head);
		while(!rectQ.isEmpty())
		{
			Rectangle next = rectQ.poll();
			if(next.isLeaf())
			{
				if(next.getChildren().contains(p))
				{
					rectQ.clear();
					return next;
				}
			}
			else
				for(Rectangle c: next.getChildren())
					if(c.contains(p))
						rectQ.add(c);
		}
		return null;								
	}
	
	public void condenseTree(Rectangle r){
		if(r.equals(head))								
		{
			if(nodesToDelete.size()>0)
			{
				for(Rectangle rect:nodesToDelete)
					descendants(rect);
				for(Rectangle e:nodesToDelete){	
					for(Rectangle c : e.getChildren())
						c.setParent(null);
					rtrnList.remove(e);
				}
				for(Rectangle e:nodesToDelete1)
				{
					for(Rectangle c : e.getChildren())
						c.setParent(null);
					rtrnList.remove(e);
				}

				for(Rectangle c:descendants)
				{
					rtrnList.remove(c);
					insert(c);
				}
				nodesToDelete.get(nodesToDelete.size()-1).getParent().removeChild(nodesToDelete.get(nodesToDelete.size()-1)); 

				nodesToDelete.clear();
				nodesToDelete1.clear();
				descendants.clear();
			}
			r.adjustMBR();

		}
		else
		{
			Rectangle p = r.getParent();
			if(r.getChildren().size()<2){
				p.removeChild(r);
				nodesToDelete.add(r);
			}
			else
				r.adjustMBR();	
			
			condenseTree(p);								
		}
	}

	public void descendants(Rectangle r){
		if(r.isPoint()&&!descendants.contains(r))
			descendants.add(r);
		else
			for(Rectangle c: r.getChildren())
			{
				if(!nodesToDelete.contains(c)&&!nodesToDelete1.contains(c)&&!c.isPoint())
					nodesToDelete1.add(c);
				descendants(c);
			}
	}

	public ArrayList<Rectangle> makeRTree(ArrayList<Rectangle> rec, int depth)
	{
		rtrnList.addAll(rec);
		
		if(rec.size()==1){	
			head = rec.get(0);
			return rtrnList;
		}
		for(int x =0; x< rec.size();x++){
			for(int y=0;y<rec.size()-1;y++){
				if(rec.get(y).getPCenter().getX()>rec.get(y+1).getPCenter().getX())
				{
					Rectangle temp = rec.get(y);
					rec.remove(y);
					rec.add(y+1,temp);
				}
			}
		}

		ArrayList<ArrayList<Rectangle>> pArray = new ArrayList<ArrayList<Rectangle>>();
		ArrayList<ArrayList<Rectangle>> pArray2 = new ArrayList<ArrayList<Rectangle>>();
		pArray.add(new ArrayList<Rectangle>());
		int y = 0;
		int c = 0;
		for(int x=0;x<rec.size();x++)
		{
			if(y == (int)(Math.ceil(Math.sqrt(rec.size()/limit))) &&x<rec.size()-1)
			{
				pArray.add(new ArrayList<Rectangle>());
				c++;
				y=0;
			}
			pArray.get(c).add(rec.get(x));
			y++;
		}
		for(ArrayList<Rectangle> arr:pArray){
			for(int x =0; x<arr.size();x++){
				for(int z=0;z<arr.size()-1;z++){
					if(arr.get(z).getPCenter().getY()>arr.get(z+1).getPCenter().getY())
					{
						Rectangle temp = arr.get(z);
						arr.remove(z);
						arr.add(z+1,temp);
					}
				}
			}	
		}

		int c2=-1;
		int y2;
		for(ArrayList<Rectangle> arr:pArray){
			y2=0;
			pArray2.add(new ArrayList<Rectangle>());
			c2++;
			for(int x=0;x<arr.size();x++)
			{
				if(y2 == limit){
					pArray2.add(new ArrayList<Rectangle>());
					c2++;
					y2=0;
				}
				pArray2.get(c2).add(arr.get(x));
				y2++;
			}
		}

		ArrayList<Rectangle> rtrn = new ArrayList<Rectangle>();

		for(ArrayList<Rectangle> arr:pArray2)
		{
			int newMaxX = 0;
			int newMinX =Integer.MAX_VALUE;
			int newMaxY =0;
			int newMinY =Integer.MAX_VALUE;
			for(Rectangle r:arr){
				newMaxX = Math.max(r.getP2().getX(),newMaxX);
				newMinX =  Math.min(r.getP1().getX(),newMinX);
				newMaxY = Math.max(r.getP1().getY(),newMaxY);
				newMinY = Math.min(r.getP2().getY(),newMinY);

			}


			rtrn.add(new Rectangle(new Point(newMinX,newMaxY),new Point( newMaxX,newMinY), depth,arr,null));
			for(Rectangle r:arr)
				r.setParent(rtrn.get(rtrn.size()-1));
		}
		return makeRTree(rtrn, depth + 1);

	}

	public ArrayList<Rectangle> search(Rectangle r){
		ArrayList<Rectangle> searchList = new ArrayList<Rectangle>();
		rectQ.clear();
		rectQ.add(head);
		while(!rectQ.isEmpty())
		{
			Rectangle next = rectQ.poll();
			if(next.isPoint())
			{
				if(r.contains(next)){
					searchList.add(next);
				}
			}
			else
				for(Rectangle c: next.getChildren())
					if(r.overlaps(c))
						rectQ.add(c);
		}
		return searchList;
	}
	
}
