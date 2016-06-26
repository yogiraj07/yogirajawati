
import java.util.Scanner;


//Author : Yogiraj Awati
class BinomialHeap {
	
	
	// Make-heap, Insert, Minimum, ExtractMin,
//	Union, Decrease-Key, Delete

	private BinomialNode root;
	private int treeSize;

	public BinomialHeap() {
		root = null;
		treeSize = 0;
	}
	
	
	public BinomialNode getRoot() {
		return root;
	}


	public int getTreeSize() {
		return treeSize;
	}


	// internal class BinomialHeapNode
	public static class BinomialNode {

		private int key; // data in the Node
		private int degree; // /number of children
		private BinomialNode parent; // pointer from current node to parent
		private BinomialNode sibling; // pointer to next tree in the list
		private BinomialNode child; // points to first child of the node

		public BinomialNode(int data) {
			key = data;
			degree = 0;
			parent = null;
			sibling = null;
			child = null;
		}
		//getters and setters
		public int getDegree() { 
			return degree;
		}

		public int getKey() { 
			return key;
		}

		public BinomialNode getSibling() { 
			return sibling;
		}
		
		public BinomialNode getChild() { 
			return child;
		}

		public BinomialNode getParent() { 
			return parent;
		}


		//reverse tree
		private BinomialNode reverse(BinomialNode s) {
			BinomialNode ret;
			if (sibling != null)
				ret = sibling.reverse(this);
			else
				ret = this;
			sibling = s;
			return ret;
		}
		
		//Reference : http://www.sanfoundry.com/java-program-implement-binomial-heap/
		//Returns Size of the tree
		public int getSizeOfTree() {
			return (1 + ((child == null) ? 0 : child.getSizeOfTree()) + ((sibling == null) ? 0
					: sibling.getSizeOfTree()));
		}

		
        
		//gives Minimum Node present in the tree
		private BinomialNode findMinimumNode() {
			BinomialNode p = this, q = this;
			int minimum = p.key;
			//iterate over all nodes
			while (p != null) {
				//find minimum and keep it updated
				if (p.key < minimum) {
					q = p;
					minimum = p.key;
				}
				p = p.sibling;
			}

			return q;
		}

		// returns Node with given key. If not found returns null
		private BinomialNode returnNodeWithKey(int v) {
			
			BinomialNode tempNode = this, n = null;
			//iterate till last
			while (tempNode != null) {
				//if the value is found
				if (tempNode.key == v) {
					n = tempNode;
					break;
				}
				
				//if no child traverse to siblings
				if (tempNode.child == null)
					tempNode = tempNode.sibling;
				
				//else traverse its child first 
				else {
					n = tempNode.child.returnNodeWithKey(v);
					if (n == null)
						tempNode = tempNode.sibling;
					else
						break;
				}
			}

			return n;
		}

	}
//end of inner class

	

	// returns minimum key in the tree
	public int findMinimum() {
		return root.findMinimumNode().key;
	}

	
	private void mergeTree(BinomialNode b) {
		
		BinomialNode temp1 = root, temp2 = b;
		
		while ((temp1 != null) && (temp2 != null)) {
			//for same degrees
			if (temp1.degree == temp2.degree) {
				BinomialNode t = temp2;
				//manage siblings
				temp2 = temp2.sibling;
				t.sibling = temp1.sibling;
				temp1.sibling = t;
				temp1 = t.sibling;
			} else {
				//else current degree is smaller
				if (temp1.degree < temp2.degree) {
					if ((temp1.sibling == null)	|| (temp1.sibling.degree > temp2.degree)) {
						BinomialNode t = temp2;
						temp2 = temp2.sibling;
						t.sibling = temp1.sibling;
						temp1.sibling = t;
						temp1 = t.sibling;
					} else {
						temp1 = temp1.sibling;
					}
				} else {
					BinomialNode t = temp1;
					temp1 = temp2;
					temp2 = temp2.sibling;
					temp1.sibling = t;
					if (t == root) {
						root = temp1;
					} else {
					}
				}
			}
		}
        //if temp1 is null, insert temp2 as a sibling of temp1
		if (temp1 == null) {
			temp1 = root;
			//traverse till the end of the sibling list
			while (temp1.sibling != null) {
				temp1 = temp1.sibling;
			}
			temp1.sibling = temp2;
		} else {
		}
	}


	//Union Implementation
	private void unionNodes(BinomialNode b) {
		mergeTree(b);
        //create Nodes for implementation
		BinomialNode prevNode = null;
		BinomialNode currentNode = root, nextNode = root.sibling;

		while (nextNode != null) {
			if ((currentNode.degree != nextNode.degree)	|| ((nextNode.sibling != null) 
					&& (nextNode.sibling.degree == currentNode.degree))) {
				prevNode = currentNode;
				currentNode = nextNode;
			} else {
				if (currentNode.key <= nextNode.key) {
					currentNode.sibling = nextNode.sibling;
					nextNode.parent = currentNode;
					nextNode.sibling = currentNode.child;
					currentNode.child = nextNode;
					//maintain degree counter
					currentNode.degree++;
				} else {
					if (prevNode == null) {
						root = nextNode;
					} else {
						prevNode.sibling = nextNode;
					}
					currentNode.parent = nextNode;
					currentNode.sibling = nextNode.child;
					nextNode.child = currentNode;
					nextNode.degree++;
					currentNode = nextNode;
				}
			}

			nextNode = currentNode.sibling;
		}
	}

	// Insert value into the tree
	public void insert(int data) {
		if (data > 0) {
			BinomialNode newNode = new BinomialNode(data);
			//if the tree is empty
			if (root == null) {
				root = newNode;
				treeSize = 1;
			} else {
				
				//add node to tree by maintaining heap property
				unionNodes(newNode);
				treeSize++;
			}
		}
		else
		{
			System.out.println("Enter positive value");
		}
	}

	//  Extract the node with the minimum key from the tree
	public int extractMinimum() {
		//if the tree is empty return -1
		if (root == null)
			return -1;

		BinomialNode prevNode = null;
		BinomialNode currentNode = root;
	
		BinomialNode minimumNode = root.findMinimumNode();
		//traverse until minimum node is not found
		while (currentNode.key != minimumNode.key) {
			prevNode = currentNode;
			currentNode = currentNode.sibling;
		}
        
		
		//check siblings
		if (prevNode == null) {
			root = currentNode.sibling;
		} else {
			prevNode.sibling = currentNode.sibling;
		}
		
		currentNode = currentNode.child;
		
		BinomialNode tempNode = currentNode;
		//traverse parent and siblings
		while (currentNode != null) {
			currentNode.parent = null;
			currentNode = currentNode.sibling;
		}

		if ((root == null) && (tempNode == null)) {
			treeSize = 0;
		} else {
			
			if ((root == null) && (tempNode != null)) {
				//reverse
				root = tempNode.reverse(null);
				treeSize = root.getSizeOfTree();
			} 
			else {
				if ((root != null) && (tempNode == null)) {
					treeSize = root.getSizeOfTree();
				} 
				else {
					unionNodes(tempNode.reverse(null));
					treeSize = root.getSizeOfTree();
				}
			}
		}
		//result
		return minimumNode.key;
	}

	// Decrease a key value
	public void decreaseKey(int val1, int val2) {
		
		 if (val1 < val2) {
	            System.out.println ("Incorrect input");
	            return ;
	        }
	         
		 BinomialNode tempNode = root.returnNodeWithKey(val1);
	        if (tempNode == null){
	            System.out.println ("Value doesnt exist in tree");
	            return ;
	        }           
	        tempNode.key = val2;
	        BinomialNode parentNode = tempNode.parent;
	        while ((parentNode != null) && (tempNode.key < parentNode.key)) 
	        {
	            int t = tempNode.key;
	            tempNode.key = parentNode.key;
	            parentNode.key = t;
	            tempNode = parentNode;
	            parentNode = parentNode.parent;
	        }
	        
		System.out.println("Successfully key decreased");
	}

	// Delete a node
	public void deleteKey(int key) {
		//if the key is valid and present
		if ((root != null) && (root.returnNodeWithKey(key) != null)) {
			//set key to minimum -1 , so that extract minimum will delete the value and heapify once again
			decreaseKey(key, findMinimum() - 1);
			extractMinimum();
		}
	}
	
	public void printHeap(){
        System.out.print("\n Heap is -> : ");
        printHeap(root);
        System.out.println("\n");
    }
    private void printHeap(BinomialNode n)
    {
        if (n != null){
        	//traverse child
            printHeap(n.child);
            System.out.print(n.key +"\t");
            //traverse siblings
            printHeap(n.sibling);
        }
    }    
    

	public static void main(String[] Argv) {
        Scanner s = new Scanner(System.in);
        System.out.println("Binomial Heap Implementation\n");
        BinomialHeap bh = new BinomialHeap();
        char input='y';
        while(input=='y'|| input=='Y'){
            System.out.println("Enter choice :\n");
            System.out.println("1) INSERT ");
            System.out.println("2) DELETE ");
            System.out.println("3) HEAP SIZE");            
            System.out.println("4) DECREASE KEY");
            System.out.println("5) GET MINIMUM");
            System.out.println("6) EXTRACT MINIMUM");
            System.out.println("7) UNION");
            System.out.println("8) EXIT");
            int ch = s.nextInt();
            switch(ch){
                case 1: 
                    System.out.println("Enter the value to be inserted : ");
                    int val = s.nextInt();
                    bh.insert(val);
                    System.out.println("Value " + val + " inserted Successfully !!!");
                    break;
                case 2:
                    System.out.println("Enter the key to be deleted : ");
                    val = s.nextInt();
                    bh.deleteKey(val);
                    System.err.println("Value " + val + "deleted!!");
                    break;
                case 3: 
                    System.out.println("Heap Size is :  " + bh.getTreeSize());
                    break;
                case 4:
                    System.out.println("Enter the key to be decreased : ");
                    int val1 = s.nextInt();
                    System.out.println("Enter the new key value smaller than existing one : ");
                    int val2 = s.nextInt();
                    bh.decreaseKey(val1,val2);
                    break; 

                case 5: 
                    System.out.println("Minimum value present in the tree : " + bh.findMinimum());
                    break;
                case 6:
                    System.out.println("Minimum value extracted : " + bh.extractMinimum());
                    break;
               
                case 7:
                    System.out.println("Merging two Heaps ");
                    BinomialHeap bh1 = new BinomialHeap();
                    bh1.insert(11);
                    bh1.insert(43);
                    bh1.insert(65);
                    bh1.insert(1);
                    bh1.insert(2);
                    System.out.println("First Tree : ");
                    bh.printHeap();
                    System.out.println("Second Tree (default) : ");
                    bh1.printHeap();
                    bh.unionNodes(bh1.getRoot());
                    System.out.println("Merged Binomial Heap is : ");
                    break;
                case 8:
                	System.out.println("Exiting ...");
                    System.exit(0);
                default:
                	System.out.println("Incorrect Choice");
                	System.out.println("Exiting ...");
				System.exit(0);

			}
			bh.printHeap();
			System.out.println("Do you want to continue? Type y or n !!!");
			input = s.next().charAt(0);

		}

	}
}


// end of class