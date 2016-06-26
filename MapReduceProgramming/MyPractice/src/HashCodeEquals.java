import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class HashCodeEquals implements Comparable<HashCodeEquals> {
    int age;
    String name;
    
    public HashCodeEquals(int age,String name) {
		// TODO Auto-generated constructor stub
    	this.age=age;
    	this.name=name;
	}
	@Override
	public int hashCode() {
		return this.age;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HashCodeEquals other = (HashCodeEquals) obj;
		if (age != other.age)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
   
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Age :"+this.age+" Name: "+this.name;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HashSet<HashCodeEquals> h =new HashSet<>();
		h.add(new HashCodeEquals(44,"z"));
		h.add(new HashCodeEquals(1,"a"));
		h.add(new HashCodeEquals(66,"d"));
		h.add(new HashCodeEquals(77,"b"));
		h.add(new HashCodeEquals(90,"j"));
		Iterator<HashCodeEquals> i =h.iterator();
		while(i.hasNext())
		{
			System.out.println(i.next());
		} 
		
        TreeSet<HashCodeEquals> t =new TreeSet<HashCodeEquals>();
        t.addAll(h);
        System.out.println("Sorted by Id : "+t);
        Iterator<HashCodeEquals> j =t.iterator();
		while(j.hasNext())
		{
			System.out.println(j.next());
		} 
        
        
	}
	@Override
	public int compareTo(HashCodeEquals b) {
		// TODO Auto-generated method stub
		return this.name.compareToIgnoreCase(b.name);
	}

}
