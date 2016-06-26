/*
 * Given two string Str1 and Str2, Find whether any anagram of Str2 is a sub-string of string Str1 (Case Insensitive) then return True otherwise False.
Test case :if Str1 = Amazon and Str2 = omaz, Output: True


 */
public class Solution {
	public static void main(String[] args) {
		String str1="Amazon";
		String str2="omz";
		Boolean result = cehckAnagram(str1,str2);
		System.out.println(result);
			
	}

	private static Boolean cehckAnagram(String str1, String str2) {
		for (int i = 0; i < str2. length(); i++) {
			String temp = str2.substring(i);
			if(str1.toLowerCase().contains(temp.toLowerCase()))
				return true;
		}
		return false;
	}

}
