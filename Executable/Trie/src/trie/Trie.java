package trie;

import java.util.ArrayList;

/**
 * This class implements a Trie. 
 * 
 * @author Sesh Venugopal
 *
 */
public class Trie {
	
	// prevent instantiation
	private Trie() { }
	
	/**
	 * Builds a trie by inserting all words in the input array, one at a time,
	 * in sequence FROM FIRST TO LAST. (The sequence is IMPORTANT!)
	 * The words in the input array are all lower case.
	 * 
	 * @param allWords Input array of words (lowercase) to be inserted.
	 * @return Root of trie with all words inserted from the input array
	 */
	public static TrieNode buildTrie(String[] allWords) {
		
		// This is how a node looks:  [ (Word Number, Start Index, End Index) | Child | Sibling ]
		
		// Initialized root
		TrieNode root = new TrieNode(null, null, null);
		
		for(int index = 0; index < allWords.length; index++) {	
		
			if(root.firstChild == null) { // First child of root, may change, should only execute once
				
				String tempString = allWords[index];
				short start = 0,
				      end = (short)(tempString.length() - 1);
				
				Indexes value = new Indexes(index, start, end);
				TrieNode node = new TrieNode(value, null, null);
				
				root.firstChild = node;
				
			}
			
			
			else { // Going to have to traverse the tree from now on
				
				TrieNode tempNode = root.firstChild; // Used to find the position to add
				
				String prefix = "", lastFoundPrefix = "", word1, word2;
				Short start, end;
				
				if(index == 1) {
					start = tempNode.substr.startIndex; // Start of the substring
	
					word1 = allWords[tempNode.substr.wordIndex].substring(start);
					word2 = allWords[index];
					
					prefix = findPrefix(word1,word2);
				}
				while(tempNode.firstChild != null || tempNode.sibling != null) {
					
					start = tempNode.substr.startIndex; // Start of the substring
					end = tempNode.substr.endIndex; // End of the substring

					word1 = allWords[tempNode.substr.wordIndex].substring(start, end+1);
					word2 = allWords[index].substring(lastFoundPrefix.length());
						
					prefix = findPrefix(word1,word2);
					
					if(prefix == "") { // No matching prefix
						
						if (tempNode.sibling != null) { // Has more siblings to check 
							
							tempNode = tempNode.sibling;
						}
						else { // No more siblings
							
							break; 
						}
					}
					
					else { // If matching prefix found
						
						if(prefix!="") {
							lastFoundPrefix = prefix; // Holds this in case we no longer have matching prefixes 
						}
							
						if(tempNode.firstChild == null) { // Add prefix node outside loop
							break;
						}
						
						else { // If there are more matching prefixes to find
							
							if(word1.length()>prefix.length()) {
								break;
							}
							else {
								tempNode = tempNode.firstChild;
							}
						}
					}
				}
				
				if(tempNode.firstChild == null && tempNode.sibling == null) {
					
					start = tempNode.substr.startIndex; // Start of the substring
					end = tempNode.substr.endIndex; // End of the substring

					word1 = allWords[tempNode.substr.wordIndex].substring(start, end+1);
					word2 = allWords[index].substring(lastFoundPrefix.length());
					
					prefix = findPrefix(word1,word2);
					
				}
				
				if(prefix == "" && tempNode.sibling == null) { // To add a sibling **Works
					
					start = (short)(lastFoundPrefix.length());
					end = (short)(allWords[index].length()-1);
					
					Indexes value = new Indexes(index, start, end);
					TrieNode sibling = new TrieNode(value, null, null);
					
					tempNode.sibling = sibling;
					
				}
				
				else { // To add prefix node
				
					int oldWordIndex = tempNode.substr.wordIndex;
					short oldWordEnd = tempNode.substr.endIndex;
					
					short prefixEnd = (short)(tempNode.substr.startIndex+ prefix.length()-1);
					
					if(prefixEnd < tempNode.substr.startIndex) {
						
						prefixEnd = tempNode.substr.startIndex;
					}
					
					tempNode.substr.endIndex = prefixEnd; // Changing the old child into a prefix node
					
					Indexes newChildIndex = new Indexes(oldWordIndex, (short)(prefixEnd+1), oldWordEnd);
					TrieNode newChildNode = new TrieNode(newChildIndex, tempNode.firstChild, null); // Creating a new child and keeping old node
					
					tempNode.firstChild = newChildNode; // Connecting prefix and child
					
					start = (short)(prefixEnd+1);
					end = (short)(allWords[index].length()-1);
					
					Indexes value = new Indexes(index, start, end);
					TrieNode sibling = new TrieNode(value, null, null);
					
					newChildNode.sibling = sibling;	
				}
			}
		}
		return root;
	}
	
	private static String findPrefix(String word1, String word2) {
		//If this returns an empty string then, there is no match
		//Else we have to divide the tree using the returned prefix
		
		//System.out.println("Words: " + word1 + " " + word2);
		
		String small, large, prefix = "";
		
		if (word1.length() > word2.length()) {
			small = word2; large = word1;
		}
		else {
			small = word1; large = word2;
		}
		
		int length = small.length();
		
		for(int i = 0; i < length; i++) {
			
			if(small.charAt(i) == large.charAt(i)) {
				prefix = prefix + Character.toString(small.charAt(i));
			}
			
			else { //If either no match or char no longer match
				
				break;
			}
		}
		return prefix;
	}
	
	/**
	 * Given a trie, returns the "completion list" for a prefix, i.e. all the leaf nodes in the 
	 * trie whose words start with this prefix. 
	 * For instance, if the trie had the words "bear", "bull", "stock", and "bell",
	 * the completion list for prefix "b" would be the leaf nodes that hold "bear", "bull", and "bell"; 
	 * for prefix "be", the completion would be the leaf nodes that hold "bear" and "bell", 
	 * and for prefix "bell", completion would be the leaf node that holds "bell". 
	 * (The last example shows that an input prefix can be an entire word.) 
	 * The order of returned leaf nodes DOES NOT MATTER. So, for prefix "be",
	 * the returned list of leaf nodes can be either hold [bear,bell] or [bell,bear].
	 *
	 * @param root Root of Trie that stores all words to search on for completion lists
	 * @param allWords Array of words that have been inserted into the trie
	 * @param prefix Prefix to be completed with words in trie
	 * @return List of all leaf nodes in trie that hold words that start with the prefix, 
	 * 			order of leaf nodes does not matter.
	 *         If there is no word in the tree that has this prefix, null is returned.
	 */
	public static ArrayList<TrieNode> completionList(TrieNode root,
										String[] allWords, String prefix) {

		ArrayList<TrieNode> trieArray = new ArrayList<TrieNode>();
		
		TrieNode tempNode = root.firstChild;
		
		short start, end;
		String word1 = "", word2 = "", newPrefix = "", lastPrefixFound = "";
		
		while(tempNode!=null) {
			
			start = tempNode.substr.startIndex; // Start of the substring
			end = tempNode.substr.endIndex; // End of the substring
			
			word1 = allWords[tempNode.substr.wordIndex].substring(start, end+1);
			word2 = prefix.substring(start);
			
			//System.out.println("Word1: " + word1 + " Word2: " + word2);
			
			newPrefix = findPrefix(word1,word2);
			
			//System.out.println("New Prefix: " + newPrefix + " VS " + " Prefix: " + prefix);
			
			if(newPrefix.length() > word1.length()) { // Dont really remember why I wrote this, but I know I put it here for a reason ;)
				newPrefix = "";
			}
			if(newPrefix!=""&&tempNode.firstChild==null&&!word2.equals(newPrefix)) { // If what I put in doesnt come back out then it doesnt match
				return null;
			}
			
			//System.out.println("Prefix: " + newPrefix);
			//System.out.println("Word2: " + word2 + " Prefix: " + newPrefix);
			
			if(word2.equals(newPrefix)) {
				//System.out.println("I run");
				break;
			}
			else {
				
				if(!newPrefix.equals("") && tempNode.firstChild != null) {
					tempNode = tempNode.firstChild;
				}
				else if(!newPrefix.equals("") && tempNode.firstChild == null) {
					break;
				}
				else if(tempNode.sibling != null){
					tempNode = tempNode.sibling;
				}
				else {
					break;
				}
			}
		}
		
		if(newPrefix.equals("") && tempNode.sibling==null) {
			return null;
		}
		else {
			if(newPrefix != "" && tempNode.firstChild == null && tempNode.sibling != null) {
				trieArray.add(tempNode);
				return trieArray;
			}
			if(tempNode.firstChild == null && tempNode.sibling == null ) {
				trieArray.add(tempNode);
				return trieArray;
			}
			else {
				return addToList(tempNode.firstChild, trieArray, allWords);
			}
		}
	}
	
	private static ArrayList<TrieNode> addToList(TrieNode root, ArrayList<TrieNode> trieArray, String[] allWords) {
	
		if(root.firstChild != null) {
			addToList(root.firstChild, trieArray, allWords);
		}
		if(root.sibling != null) {
			addToList(root.sibling, trieArray, allWords);
		}
		
		short endOfWord = (short)(allWords[root.substr.wordIndex].length()-1);
		
		if(root.substr.endIndex == endOfWord) {
			trieArray.add(root);
		}
		
		return trieArray;
	}
	
	public static void print(TrieNode root, String[] allWords) {
		System.out.println("\nTRIE\n");
		print(root, 1, allWords);
	}
	
	private static void print(TrieNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			String pre = words[root.substr.wordIndex]
							.substring(0, root.substr.endIndex+1);
			System.out.println("      " + pre);
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
 }
