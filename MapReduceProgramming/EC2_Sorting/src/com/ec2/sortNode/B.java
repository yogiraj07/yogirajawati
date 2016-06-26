package com.ec2.sortNode;

public class B {
public static void main(String[] args) {
	instantiateObject("com.ec2.sortNode.A");
}

private static void instantiateObject(String s) {
	// TODO Auto-generated method stub
	try {
		System.out.println("1");
		Class t = Class.forName(s);
		
		System.out.println("2");
		Object o = t.newInstance();
		if(o instanceof A)
		{
			A x = (A) o;
			System.out.println(x.a);
			
		}
		System.out.println("nbhvbgfhk");
		if(o instanceof A.Map)
		{
			A.Map mm = (A.Map) o;
			
			System.out.println(mm.i);
		}
		
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		System.out.println("Error in loading class");
		e.printStackTrace();
	} catch (InstantiationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IllegalAccessException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
}
