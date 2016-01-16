package ehallmar_CSCI201L_Assignment1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Scanner;

public class Controller {
	private static ArrayList<String> sort(HashSet<String> words, String comp) {
		ArrayList<SortHelper> sortedWords = new ArrayList<SortHelper>();
		Integer current_score;
		for(String w: words) {
			current_score = 0;
			current_score += w.length()-comp.length();
			for(int i = 0; i < comp.length(); i++) {
				if (!(comp.charAt(i)==w.charAt(i))) {
					current_score += 1;
				}
			}
			sortedWords.add(new SortHelper(current_score,w));
		}
		// Sort
		CompareScore comparator = new CompareScore();
		sortedWords.sort(comparator);
		ArrayList<String> to_return = new ArrayList<String>();
		for(SortHelper get_string: sortedWords) {
			to_return.add(get_string.word);
		}
		return to_return;
	}
	public static void main(String [] args){
		String word_file = null;
		String keyboard_file = null;
		String text_file = null;
		
		// Try to get args
		for(String s: args) {
			if(s.endsWith(".txt")) {
				text_file = s;
			} else if (s.endsWith(".wl")) {
				word_file = s;
			} else if (s.endsWith(".kb")) {
				keyboard_file = s;
			}
		}
		
		// Make sure we get all the input required
		Scanner scan = new Scanner(System.in);
		while (text_file == null || !text_file.endsWith(".txt")) {
			System.out.println("Please enter text file: ");
			text_file = scan.next().trim();			
		}
		while (word_file == null || !word_file.endsWith(".wl")) {
			System.out.println("Please enter word list file: ");
			word_file = scan.next().trim();
		}
		while (keyboard_file == null || !keyboard_file.endsWith(".kb")) {
			System.out.println("Please enter keyboard file: ");
			keyboard_file = scan.next().trim();
		}
		// Get words and setup Word Hash and Prefix Trie
		HashSet<String> allWords = new HashSet<String>();
		PrefixTrie prefixWords = new PrefixTrie();
		try {
			BufferedReader words_in = new BufferedReader(new FileReader(word_file));
		    String line = null;
		    while ((line = words_in.readLine()) != null) {
		    	for (String w: line.split("\\ ")) {
		    		w = w.trim().replaceAll("[^a-zA-Z\\s]", "").toLowerCase();
		    		if(w.length() > 0) {
		    			allWords.add(w);
		    			prefixWords.insert(w);
		    		}
		    	}
		    }
		    words_in.close();
		} catch (FileNotFoundException e) {
			System.out.println("Word list file not found");
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Error reading Word list file");
			System.exit(1);
		}
		
		// Next, we build our keyboard config Hash
		Hashtable<String,ArrayList<String>> keyboardHash = new Hashtable<String,ArrayList<String>>();
		try {
			BufferedReader keyboard_in = new BufferedReader(new FileReader(keyboard_file));
		    String line = null;
		    while ((line = keyboard_in.readLine()) != null) {
		    	ArrayList<String> neighbors = new ArrayList<String>();
		    	for (String w: line.split("\\,")[1].toLowerCase().split("")) {
		    		if(!w.equals(line.split("\\,"))) {
		    			// No self loops
			    		neighbors.add(w);
		    		}
		    	}			
		    	keyboardHash.put(line.split("\\,")[0], neighbors);
		    }
		    keyboard_in.close();
		} catch (FileNotFoundException e) {
			System.out.println("Keyboard file not found");
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Error reading Keyboard file");
			System.exit(1);
		}
		
		// Now, we can examine the text file
		Hashtable<String,HashSet<String>> corrections = new Hashtable<String,HashSet<String>>();
		try {
			BufferedReader text_in = new BufferedReader(new FileReader(text_file));
		    String line = null;
			ArrayList<String> similar_words = new ArrayList<String>();
		    while ((line = text_in.readLine()) != null) {
		    	for (String w: line.split("\\ ")) {
		    		w = w.trim().replaceAll("[^a-zA-Z\\s]", "").toLowerCase();
		    		if(w.length() > 0) {
		    			// w is now a formatted word
		    			if(!allWords.contains(w)) {
		    				// Not a correct word
		    				//	Find corrections and completions
		    				similar_words.add(w);
		    				corrections.put(w, new HashSet<String>());
							// Pluck potential completed words out of prefix tree
							similar_words.addAll(prefixWords.getPrefixWords(w));
		    				String current;
		    				while(similar_words.isEmpty() == false) {
		    					current = similar_words.get(0);
		    					if(allWords.contains(current)) {
		    						corrections.get(w).add(current);	
		    					}
		    					similar_words.remove(0);
								if(current.length()==w.length()) {
			    					for(int i = 0; i < current.length(); i++) {
		    							Character c = current.charAt(i);
		    							if(w.charAt(i)==c.charValue() && prefixWords.isPrefix(current.substring(0,i))) {
			    							for(String s: keyboardHash.get(c.toString())) {
			    								String to_add;
			    								if (current.length()==1) {
					    							to_add = s;
					    						} else {
					    							to_add = current.substring(0,i)+s+current.substring(i+1);
					    						}
				    							// Pluck potential completed words out of prefix tree
			    								similar_words.add(to_add);
			    								similar_words.addAll(prefixWords.getPrefixWords(to_add));
			    							}
				    							
		    							}
	    							}
		    					}
		    				} 
		    			}
		    		}
		    	}
		    }
		    text_in.close();
		} catch (FileNotFoundException e) {
			System.out.println("Text file not found");
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Error reading Text file");
			System.exit(1);
		}
		
		Hashtable<String,ArrayList<String>> clean_corrections = new Hashtable<String,ArrayList<String>>();
		// Rank the suggestions
		for(String to_sort: corrections.keySet()) {
			clean_corrections.put(to_sort, sort(corrections.get(to_sort),to_sort));
		}
	    System.out.println(clean_corrections.toString());

		// Output results
		
	}
}
