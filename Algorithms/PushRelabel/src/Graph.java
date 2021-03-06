import java.util.ArrayList;

//Edge Class
class Edge {
	int flow;
	int capacity;
	int u;
	int v;
	
	Edge(int flow, int capacity, int u, int v)
    {
        this.flow = flow;
        this.capacity = capacity;
        this.u = u;
        this.v = v;
    }
}


//Vertex class
class Vertex {
	int height, excessFlow;
	 
    Vertex(int h, int flow)
    {
        this.height = h;
        this.excessFlow = flow;
    }
}

public class Graph {
	int V;
	ArrayList<Vertex> vertexList;
	ArrayList<Edge> edgeList;
	
	Graph(int V)
	{
	    this.V = V;
	    vertexList =new ArrayList<Vertex>();
	    edgeList=new ArrayList<Edge>();
	    
	    //Initialise each vertex with 0 height and 0 excess flow
	    for (int i = 0; i < V; i++){
	    	vertexList.add(new Vertex(0,0));
	    }
	        
	}
	
	//calculates preflow : makes height of vertex s equal to number of vertices
		void calculatePreFlow(int s)
		{
		     vertexList.get(s).height = vertexList.size();
		 
	       for (int i = 0; i < edgeList.size(); i++) 
		    {
		        // If current edge goes from source
		        if (edgeList.get(i).u == s)
		        {
		            // Flow is equal to capacity
		        	edgeList.get(i).flow = edgeList.get(i).capacity;
		 
		            // Initialize excess flow for adjacent v
		            vertexList.get(edgeList.get(i).v).excessFlow += edgeList.get(i).flow;
		 
		            // Add an edge from v to s in residual graph with capacity = 0
		            edgeList.add(new Edge(-edgeList.get(i).flow, 0, edgeList.get(i).v, s));
		        }
		    }
		}
	
	//adds an edge with 0 flow
	void addEdge(int u, int v, int capacity)
	{
	    edgeList.add(new Edge(0, capacity, u, v));
	}
	
	// returns index of overflowing Vertex
	int getOverFlowIndex(ArrayList<Vertex> vertexList)
	{
	    for (int i = 1; i < vertexList.size() - 1; i++)
	       if (vertexList.get(i).excessFlow > 0)
	            return i;
	 
	    // -1 if no overflowing Vertex
	    return -1;
	}
	
	
	boolean push(int u)
	{
		//Iterate all edges to find adjacent of u to which flow can be pushed
		
	    for (int i = 0; i < edgeList.size(); i++)
	    {
	        // Checks u of current edge is same as given
	        // overflowing vertex
	        if (edgeList.get(i).u == u)
	        {
	            // if flow is equal to capacity then no push
	            // is possible
	            if (edgeList.get(i).flow == edgeList.get(i).capacity)
	                continue;
	 
	            // Push is only possible if height of adjacent
	            // is smaller than height of overflowing vertex
	            if (vertexList.get(u).height > vertexList.get(edgeList.get(i).v).height)
	            {
	                // Flow to be pushed is equal to minimum of
	                // remaining flow on edge and excess flow.
	                int flow = getMinimum(edgeList.get(i).capacity - edgeList.get(i).flow,
	                               vertexList.get(u).excessFlow);
	 
	                // Reduce excess flow for overflowing vertex
	                vertexList.get(u).excessFlow -= flow;
	 
	                // Increase excess flow for adjacent
	                vertexList.get(edgeList.get(i).v).excessFlow += flow;
	 
	                // Add residual flow (With capacity 0 and negative flow)
	                edgeList.get(i).flow += flow;
	 
	                updateReverseEdgeFlow(i, flow);
	 
	                return true;
	            }
	        }
	    }
	    return false;
	}

	
	//updates reverse edge flow
	void updateReverseEdgeFlow(int i, int flow)
	{
	    int u = edgeList.get(i).v, v = edgeList.get(i).u;
	    for (int j = 0; j < edgeList.size(); j++)
	    {
	        if (edgeList.get(j).v == v && edgeList.get(j).u == u)
	        {
	            edgeList.get(j).flow -= flow;
	            return;
	        }
	    }
	    // adding reverse Edge in residual graph
	    edgeList.add(new Edge(0, flow, u, v));
	}
	
	
	//follows the relabelling procedure
	// This operation raises the height of an overflowing tank that has no other tanks downhill from it
	//The height of the vertex u is increased by 1 more than the minimum height of its neighbor to which u has  edge.
	void relabel(int u)
	{
	    // Initialize minimum height of an adjacent
	    int minHeight = Integer.MAX_VALUE;
	 
	    // Find the adjacent with minimum height
	    for (int i = 0; i < edgeList.size(); i++)
	    {
	        if (edgeList.get(i).u == u)
	        {
	            // if flow is equal to capacity then no
	            // relabeling
	            if (edgeList.get(i).flow == edgeList.get(i).capacity)
	                continue;
	 
	            // Update minimum height
	            if (vertexList.get(edgeList.get(i).v).height < minHeight)
	            {
	                minHeight = vertexList.get(edgeList.get(i).v).height;
	 
	                // updating height of u to minHeight + 1
	                vertexList.get(u).height = minHeight + 1;
	            }
	        }
	    }
	}
	
	//return maximum flow in the network
	int getMaxFlow(int s, int t)
	{
		//calculate preflow from s
	    calculatePreFlow(s);
	 
	    // until none of the Vertex is in overflow
	    while (getOverFlowIndex(vertexList) != -1)
	    {
	        int u = getOverFlowIndex(vertexList);
	        if (!push(u))
	            relabel(u);
	    }
	 
	    // vertexList.back() returns last Vertex, whose excessFlow will be final maximum flow
	    return vertexList.get(vertexList.size()-1).excessFlow;
	}
	
	//returns minimum of a and b
	int getMinimum(int a,int b){
		if(a>=b){
			return b;
		}
		else{
			return a;
		}
	}
	
	//Reference : Taking example of CLRS page 726
	public static void main(String args[])
	{
	    int V = 6;
	    Graph graph = new Graph(V);
	 
	    // Creating above shown flow network
	  
	    graph.addEdge(0, 1, 16);
	    graph.addEdge(0, 2, 13);
	    graph.addEdge(2, 1, 4);
	    graph.addEdge(1, 3, 12);
	    graph.addEdge(2, 4, 14);
	    graph.addEdge(3, 2, 9);
	    graph.addEdge(3, 5, 20);
	    graph.addEdge(4, 3, 7);
	    graph.addEdge(4, 5, 4);
	 
	    // Initialize source and sink
	    int s = 0, t = 5;
	    System.out.println( "Maximum flow of the Network is :  " + graph.getMaxFlow(s, t));
	}
}
