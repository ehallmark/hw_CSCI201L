package ehallmar_CSCI201L_Assignment1;

import java.util.ArrayList;

public class test {
	public static void main(String [] args){
		PrefixTrie test = new PrefixTrie();
		test.insert("hell");
		test.insert("hello");
		test.insert("");
		String[] new_string = new String[1000];
		new_string[0] = "a";
		for(int i = 1; i < 1000; i++) {
			new_string[i] = new_string[i-1]+(char)(i%26+(int)'a');
			test.insert(new_string[i]);
		}
		test.insert("helloworld");
		test.insert("helloworlrd");
		ArrayList<String> words = test.getPrefixWords("he");
		for(int i = 0; i < words.size(); i++) {
			System.out.println(words.get(i));
		}
	}
}
