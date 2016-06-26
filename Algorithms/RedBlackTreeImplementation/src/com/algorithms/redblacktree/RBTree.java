package com.algorithms.redblacktree;

//Author : Yogiraj Awati
//NOTE : Please create input.txt and give comma separated values as an input (positive values)
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class RBTree {
	// define color of the node
	static final int BLACK = 1;
	static final int RED = 0;
	private RBNode root; // This is the root of the Red Black tree
	private static RBNode nullNode; // The Leaf Node which is null in RB Tree

	// initialize null node using static block
	static {
		nullNode = new RBNode(0);
		nullNode.left = nullNode;
		nullNode.right = nullNode;
	}

	// Constructors
	public RBTree() {
		root = new RBNode(Integer.MIN_VALUE); // set node to minimum value
		root.left = nullNode;
		root.right = nullNode;
	}

	// Inorder traversal of the RB Tree - prints out in sorted form
	public void inorder() {
		// System.out.println(root.right.data);
		inorder(root.right);
	}

	// Traverse in left child - parent child - right child form
	private void inorder(RBNode node) {
		// until valid node is present to traverse
		if (node != nullNode) {
			inorder(node.left);
			char color = 'B';
			if (node.color == 0)
				color = 'R';
			// Print: data + color of the node
			System.out.print(node.data + "" + color + " ");
			inorder(node.right);
		}
	}

	// Following function inserts data into node and rearranges tree to maintain
	// property of RB Tree
	public void insert(int data)

	{
		RBNode greatNode = null;
		RBNode grandNode = root;
		RBNode parentNode = root;
		RBNode currentNode = root;

		nullNode.data = data;
		while (currentNode.data != data) {

			// store node values
			greatNode = grandNode;
			grandNode = parentNode;
			parentNode = currentNode;

			// Traverse tree downwards
			if (currentNode.data > data)
				currentNode = currentNode.left;
			else
				currentNode = currentNode.right;

			// If 2 child are red,rearrange the tree to maintain the property
			if (currentNode.right.color == RED && currentNode.left.color == RED)
				rearrange(data, currentNode, parentNode, grandNode, greatNode);
		}

		// check - if the node is already present in the tree return
		if (currentNode != nullNode)
			return;

		// create new node and insert into
		currentNode = new RBNode(data, nullNode, nullNode);

		// Insert new node either left or right child
		if (data < parentNode.data)
			parentNode.left = currentNode;
		else
			parentNode.right = currentNode;

		// rearrange the RB tree to maintain the property
		rearrange(data, currentNode, parentNode, grandNode, greatNode);

	}

	//rearrange : Fixes the tree and maintains the property of the red black tree
	private void rearrange(int data, RBNode currentNode, RBNode parentNode, RBNode grandNode, RBNode greatNode) {
		// reverse the colors.
		currentNode.color = RED;
		currentNode.left.color = BLACK;
		currentNode.right.color = BLACK;

		// check for rotation requirements
		if (parentNode.color == RED) {
			grandNode.color = RED;

			// Double rotation check :
			if (data < grandNode.data != data < parentNode.data)
				parentNode = rotate(data, grandNode);

			currentNode = rotate(data, greatNode);

			currentNode.color = BLACK;
		}
		// Make root black
		root.right.color = BLACK;

	}

	// Rotate function : Rotation is performed if needed and new node is returned
	private RBNode rotate(int data, RBNode parentNode) {
		if (parentNode.data > data) {
			if (data < parentNode.left.data) {
				// rotate left child
				return parentNode.left = leftChildRotation(parentNode.left);

			} else {

				return parentNode.left = rightChildRotation(parentNode.left);
			}
		} else {
			if (data < parentNode.right.data) {
				// rotate right child
				return parentNode.right = leftChildRotation(parentNode.right);
			} else {
				return parentNode.right = rightChildRotation(parentNode.right);
			}
		}
	}

	// left child of node is rotated
	private RBNode leftChildRotation(RBNode node) {
		RBNode l = node.left;
		node.left = l.right;
		l.right = node;
		return l;
	}

	// right child of node is rotated
	private RBNode rightChildRotation(RBNode node) {
		RBNode r = node.right;
		node.right = r.left;
		r.left = node;
		return r;
	}

	// finds the data in the tree
	public void search(int data) {
		if (searchHelper(root.right, data))
			System.out.println(" Found");
		else
			System.out.println(" Not Found");
	}

	// returns true if the data is found in the tree otherwise false
	private boolean searchHelper(RBNode node, int val) {
		boolean result = false;
		// perform binary search to find the node

		while ((node != nullNode) && !result) {
			int rightData = node.data;

			if (val < rightData)
				node = node.left;
			else if (val > rightData)
				node = node.right;
			else {
				result = true;
				break;
			}
			result = searchHelper(node, val);
		}
		return result;
	}

	//////////////////////////////////
	// finds the data in the tree
		public RBNode search1(int data) {
			return( searchHelper1(root.right, data));
			
		}

		// returns true if the data is found in the tree otherwise false
		private RBNode searchHelper1(RBNode node, int val) {
			RBNode result = null;
			// perform binary search to find the node

			while ((node != nullNode) && result==null) {
				int rightData = node.data;

				if (val < rightData)
					node = node.left;
				else if (val > rightData)
					node = node.right;
				else {
					return node;
				}
				result = searchHelper1(node, val);
			}
			return result;
		}

	
	/////////////////////////////////
	
	// returns height of the tree
	public int getHeight() {
		return getHeightHelper(root);
	}

	// returns height of the tree from the given node
	public int getHeightHelper(RBNode n) {
		if (n == nullNode)
			return 0;
		return 1 + Math.max(getHeightHelper(n.left), getHeightHelper(n.right));
	}

	// returns minimum node in the tree by traversing entire left side
	public int getMinimum() {
		return getMinimumHelper(root.right);
	}

	public int getMinimumHelper(RBNode n) {
		// traverse left
		while (n.left != nullNode)
			n = n.left;
		return n.data;
	}

	// Returns maximum of the tree buy traversing entire right side
	public int getMaximum() {
		return getMaximumHelper(root.right);
	}

	public int getMaximumHelper(RBNode n) {
		// traverse right
		while (n.right != nullNode)
			n = n.right;
		return n.data;
	}

	// returns inorder successor of the node
	private int successor(RBNode node) {
		RBNode successor = null;
		RBNode tempNode = root;
		if (node.right != nullNode)
			return getMinimumHelper(node.right);

		// Start from root and search for successor down the tree
		while (tempNode != nullNode) {
			// check left
			if (node.data < tempNode.data) {
				successor = tempNode;
				tempNode = tempNode.left;
			}
			// check right
			else if (node.data > tempNode.data)
				tempNode = tempNode.right;
			else
				break;
		}

		return (successor!=null? successor.data : -1);
	}

	// Returns inorder predecessor of the given node
	private int predecessor(RBNode node) {
		if (node.left != nullNode)
			return getMaximumHelper(node.left);

		RBNode predecessor = null;
		RBNode tempNode = root;

		while (tempNode != nullNode) {
			// check right
			if (node.data > tempNode.data) {
				predecessor = tempNode;
				tempNode = tempNode.right;
			}
			// check left
			else if (node.data < tempNode.data)
				tempNode = tempNode.left;
			else
				break;
		}
		return (predecessor!=null? predecessor.data : -1);
	}

	// Input file is space separated
	public static void main(String[] args) throws IOException {
		String inputLine = null;
		Scanner scanner = new Scanner(System.in);
		RBTree rbTree = new RBTree();
        
		// Reading input file
		BufferedReader readFile = null;
		try {
			readFile = new BufferedReader(new FileReader("input.txt"));
			inputLine = readFile.readLine();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String data[] = inputLine.split(" ");

		for (String val : data) {
			rbTree.insert(Integer.parseInt(val));
		}
		readFile.close();

		// RedBlackTree is created
		System.out.println("Tree (Inorder): ");
		// print the inorder
		rbTree.inorder();

		do {
			System.out.println("\nPlease Enter choice::");
			System.out.println("1) Insert ");
			System.out.println("2) Maximum Element");
			System.out.println("3) Minimum Element");
			System.out.println("4) Sort");
			System.out.println("5) Search");
			System.out.println("6) Predecessor");
			System.out.println("7) Sucessor");
			System.out.println("8) Exit");

			int ch = scanner.nextInt();
			switch (ch) {
			case 1:
				System.out.print("\nEnter Element to Insert :- ");
				rbTree.insert(scanner.nextInt());
				break;
			case 2:
				System.out.println("Maximum element in the tree is :- " + rbTree.getMaximum());
				break;
			case 3:
				System.out.println("Minimum element in the tree is :- " + rbTree.getMinimum());
				break;
			case 4:
				System.out.println("Sorted data :- ");
				rbTree.inorder();
				break;
			case 5:
				System.out.print("\nEnter Element to Search :- ");
				rbTree.search(scanner.nextInt());
				break;
			case 6:
				System.out.print("\nEnter Element for Predecessor :- ");
				int i = scanner.nextInt();
				RBNode r = rbTree.search1(i);
				int k;
				if(r!=null && (k=rbTree.predecessor(r))>0)
				{
					System.out.println("Predecessor of "+i+" is :- "+k);
				}
				else
				{
					System.out.println("Data not found in the tree");
				}
				break;
				
			case 7 :
				System.out.print("\nEnter Element for Successor :- ");
				 i = scanner.nextInt();
				 r = rbTree.search1(i);
				if(r!=null && (k=rbTree.successor(r))!=-1)
				{
					System.out.println("Successor of "+i+" is :- "+k);
				}
				else
				{
					System.out.println("Data not found in the tree");
				}
				break;
			case 8:
				scanner.close();
				System.exit(0);
			default:
				System.out.println("Wrong Input\n ");
				break;
			}

			System.out.print("\nPresent Height : " + (rbTree.getHeight() - 1));

		} while (true);

	}

}

//Data structure for the Node
class RBNode {
	RBNode left, right;
	int data;
	int color;

	public RBNode(int data) {
		this(data, null, null);
	}

	public RBNode(int data, RBNode left, RBNode right) {
		this.left = left;
		this.right = right;
		this.data = data;
		// default color of node is black
		color = RBTree.BLACK;
	}
}
