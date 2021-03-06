package com.algortihms.linklist;

/*
Two linked lists (simple link, not double link) heads are given: headA, and headB;
it is also given that the two lists intersect, thus after the intersection they have the
same elements to the end. Find the first common element, without modifying the lists
elements or using additional datastructures
 */
public class LinkedList {
	
    private Node header;
    private int listSize;
    public LinkedList() {
		header=new Node(0);
		listSize=0;
	}
    //to add new node at the end of the list
    public void addNode(int data)
    {
    	Node currentNode = header;
    	Node tempNode=new Node(data);
    	//travel till end of list
    	
    	while(currentNode.getNext()!=null)
    	{
    		currentNode=currentNode.getNext();
    	}
    	
    	//insert new node at the last of the list
    	currentNode.setNext(tempNode);
    	
    	//keep track of list size
    	listSize++;
    }
    
    //to intersect two list
    //Node n is inserted at the end of the current list
    public void intersectNode(Node n)
    {
    	Node currentNode = this.header;
    	//traverse till the end of the list
    	while(currentNode.getNext()!=null)
    	{
    		currentNode=currentNode.getNext();
    	}
    	//insert node at the end of this list
    	currentNode.setNext(n);
    	//accordingly add the current list size
    	while(n.getNext()!=null)
    	{
    		this.listSize++;
    		n=n.getNext();
    	}
    	this.listSize++;
    }
    
    //return size of the list
    public int getSize()
    {
    	return this.listSize;
    }
    
    //return Node of the desired Value
  	private  Node getNode(int i) {
  		Node currentNode = header.getNext();
      	while(currentNode.getData()!=i)
      	{
      		currentNode=currentNode.getNext();
      	}
  		return  currentNode==null?null:currentNode;
  	}
  	
  	//to display contents of the object
  	@Override
  	public String toString() {
  		StringBuilder display=new StringBuilder();
  		display.append("[");
  		Node current=header.getNext();
  		while (current.getNext()!=null)
  		{
  			display.append(" "+current.getData());
  			current=current.getNext();
  		}
  		display.append(" "+current.getData()+" ]");
  		
  		return display.toString();
  	}
  	
	public static void main(String[] args) {
       LinkedList A = new LinkedList();
       A.addNode(11);
       A.addNode(12);
      A.addNode(13);
      A.addNode(14);
      A.addNode(15);
       Node intersectingNode = A.getNode(12);
       LinkedList B = new LinkedList();
//      B.addNode(30);
//       B.addNode(40);
//       B.addNode(50);
//       B.addNode(60);
       B.addNode(70);
       //List B intersect with list A
       if(intersectingNode != null)
       {
    	   B.intersectNode(intersectingNode);
       }
      System.out.println("List A : "+A);
      System.out.println("List B : "+B);
      Node intersection=null;
      intersection=checkIntersection(A,B);
      if(intersection!=null)
      {
    	  System.out.println("Two List intersects at :"+intersection.getData());
      }
      else
      {
    	  System.out.println("Two Lists are not intersecting!!");
      }
       
	}
	
	//Returns intersecting node, if two list intersects
	// else null is returned
	private static Node checkIntersection(LinkedList a, LinkedList b) {
	
		Node currentOfB;
		Node currentOfA;
		int difference = b.listSize-a.listSize;
		//if size of list b is greater than equal to list a 
		//traverse the list until both list are of same length
		if(difference>=0)
		 {
			 currentOfB=b.header.getNext();
			 currentOfA=a.header.getNext();
			 while(difference!=0 && currentOfB.getNext()!=null)
				{
					currentOfB=currentOfB.getNext();
					difference--;
				}
		 }
		//list a is greater than list b
		else
		{
			 currentOfB=a.header.getNext();
			 currentOfA=b.header.getNext();
			 while(difference!=0 && currentOfB.getNext()!=null)
				{
					currentOfB=currentOfB.getNext();
					difference++;
				}
		}
		
		//traverse both lists and check for each element whether both is same
		//if same element found, return the Node
		while(currentOfB!=null)
		{
			if (currentOfB.getData()==currentOfA.getData())
			{
				return currentOfB;
			}
				
			currentOfA=currentOfA.getNext();
			currentOfB=currentOfB.getNext();
		}
		return null;
	}
	
	

}

//Node represents one element of the list
class Node{
	private int data;
	private Node next;
	
	
	public Node(int data) {
		super();
		this.data = data;
		this.next = null;
	}
	public int getData() {
		return this.data;
	}
	public void setData(int data) {
		this.data = data;
	}
	public Node getNext() {
		return this.next;
	}
	public void setNext(Node next) {
		this.next = next;
	}
	
}