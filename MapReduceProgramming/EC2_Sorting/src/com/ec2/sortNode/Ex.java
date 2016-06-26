package com.ec2.sortNode;

import java.util.ArrayList;

public class Ex {
	public static void main(String[] args) {
		String s = "asd-0000 /// \\ ??????? asd 11111,,asd.asd, asd";
		String clean = s.replaceAll("[/,?]", "");
		System.out.println(clean);
	}
}
