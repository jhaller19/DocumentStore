package edu.yu.cs.com1320.project.impl;


import edu.yu.cs.com1320.project.Trie;
import java.util.*;

public class TrieImpl<Value> implements Trie<Value> {

	private final int alphabetSize = 91; // extended ASCII <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	private Node root; // root of trie

	public TrieImpl(){};

	class Node<Value>
	{
		protected List<Value> valueList = new ArrayList<>();
		protected Node[] links = new Node[alphabetSize];

	}


	@Override
	public void put(String key, Value val) {
		key = key.replaceAll("[^a-zA-Z0-9 ]", ""); //alpha
		key = key.toUpperCase();
		//deleteAll the value from this key
		if (val == null || key == null)
		{
			return;
		}
		else
		{
			this.root = put(this.root, key, val, 0);
		}
	}


	private Node put(Node x, String key, Value val, int d)
	{
		//create a new node
		if (x == null)
		{
			x = new Node();
		}
		//we've reached the last node in the key,
		//set the value for the key and return the node
		if (d == key.length())
		{
			if(!x.valueList.contains(val)){
				x.valueList.add(val);
			}
			return x;
		}
		//proceed to the next node in the chain of nodes that
		//forms the desired key
		char c = key.charAt(d);
		x.links[c] = this.put(x.links[c], key, val, d + 1);
		return x;
	}

	@Override
	public List<Value> getAllSorted(String key, Comparator<Value> comparator) {
		key = key.replaceAll("[^a-zA-Z0-9 ]", "");
		key = key.toUpperCase();

		Node x = this.get(this.root, key, 0);
		if (x == null)
		{
			List <Value> emptyList = new ArrayList<Value>();
			return emptyList;
		}

		Collections.sort(x.valueList , comparator);

		return x.valueList;//<<<<<<<<<<
	}

	private Node get(Node x, String key, int d)
	{
		//link was null - return null, indicating a miss
		if (x == null)
		{
			return null;
		}
		//we've reached the last node in the key,
		//return the node
		if (d == key.length())
		{
			return x;
		}
		//proceed to the next node in the chain of nodes that
		//forms the desired key
		char c = key.charAt(d);
		return this.get(x.links[c], key, d + 1);
	}

	@Override
	public List<Value> getAllWithPrefixSorted(String prefix , Comparator<Value> comparator) {
		prefix = prefix.replaceAll("[^a-zA-Z0-9 ]", ""); //alpha
		prefix = prefix.toUpperCase();
		Node x = this.get(this.root, prefix, 0);
		List<Value> list = new ArrayList<>();
		if(x == null){
			return list;
		}
		list.addAll(x.valueList); //add values of end of prefix (ie. "c" of "abc")
		List<Value> returnList = getAfterPrefix(x, list);
		returnList.sort(comparator);
		return returnList;
	}

	private List<Value> getAfterPrefix(Node x , List list){
		for (int c = 0; c <alphabetSize; c++) {
			if (x.links[c] != null)
			{
				list.addAll(x.links[c].valueList);
				getAfterPrefix(x.links[c] , list);
			}
		}
		//Get rid of duplicates
		Set<Value> set = new HashSet<>(list);
		list = new ArrayList(set);

		return list;
	}


	/*@Override
	public Set<Value> deleteAllWithPrefix(String prefix) {
		Node x = this.get(this.root, prefix, 0);
		Set<Value> set = new HashSet<>();
		return deleteAfterPrefix(x, set);
	}*/

	@Override
	public Set<Value> deleteAllWithPrefix(String prefix) {
		prefix = prefix.replaceAll("[^a-zA-Z0-9 ]", ""); //alpha
		prefix = prefix.toUpperCase();
		Set <Value> set = new HashSet<>();
		this.root = deletePrefixExperiment(this.root , prefix , 0 , set);
		return set;
	}

	private Node deletePrefixExperiment(Node x, String key, int d , Set set)
	{
		if (x == null)
		{
			return null;
		}
		//we're at the node to del - set the val to null
		if (d == key.length()-1) //<
		{
			char a = key.charAt(d);
			if(x.links[a] == null){ //<
				return x; //NOT SURE
			}
			collectValues(x.links[key.charAt(d)] , set);
			set.addAll(x.links[key.charAt(d)].valueList);
			x.links[key.charAt(d)] = null;
			//deleteAfterPrefix(x , set);
		}
		//continue down the trie to the target node
		else
		{
			char c = key.charAt(d);
			x.links[c] = this.deletePrefixExperiment(x.links[c], key, d + 1 , set);
		}
		//this node has a val – do nothing, return the node
		if (!x.valueList.isEmpty())
		{
			return x;
		}
		//remove subtrie rooted at x if it is completely empty
		for (int c = 0; c <alphabetSize; c++)
		{
			if (x.links[c] != null)
			{
				return x; //not empty
			}
		}
		//empty - set this link to null in the parent
		return null;
	}

	/*private Set<Value> deleteAfterPrefix(Node x , Set set){
		for (int c = 0; c <alphabetSize; c++) {
			if (x.links[c] != null)
			{
				//System.out.println(c);
				set.addAll(x.links[c].valueList);
				deleteAfterPrefix(x.links[c] , set);
				x.links[c] = null;
			}
		}
		return set;
	}*/
	private Set<Value> collectValues(Node x , Set set){
		for (int c = 0; c <alphabetSize; c++) {
			if (x.links[c] != null)
			{
				//System.out.println(c);
				set.addAll(x.links[c].valueList);
				collectValues(x.links[c] , set);
			}
		}
		return set;
	}


	@Override
	public Set<Value> deleteAll(String key) {
		key = key.replaceAll("[^a-zA-Z0-9 ]", ""); //alpha
		key = key.toUpperCase();
		Set<Value> set = new HashSet<>();
		this.root = deleteAll(this.root, key, 0 , set);
		return set;
	}

	private Node deleteAll(Node x, String key, int d , Set set)
	{
		if (x == null)
		{
			return null;
		}
		//we're at the node to del - set the val to null
		if (d == key.length())
		{
			set.addAll(x.valueList);
			x.valueList.clear();
		}
		//continue down the trie to the target node
		else
		{
			char c = key.charAt(d);
			x.links[c] = this.deleteAll(x.links[c], key, d + 1, set);
		}
		//this node has a val – do nothing, return the node
		if (!x.valueList.isEmpty())
		{
			return x;
		}
		//remove subtrie rooted at x if it is completely empty
		for (int c = 0; c <alphabetSize; c++)
		{
			if (x.links[c] != null)
			{
				return x; //not empty
			}
		}
		//empty - set this link to null in the parent
		return null;
	}

	@Override
	public Value delete(String key, Value val) {				//needs work on return
		Comparator<Value> compareNothing = (Value o1 , Value o2) -> {
			return 0;
		};
		key = key.replaceAll("[^a-zA-Z0-9 ]", ""); //alpha
		key = key.toUpperCase();
		Boolean valExists = false;
		if(getAllSorted(key , compareNothing).contains(val)){
			valExists = true;
		}
		this.root = delete(this.root, key, val,  0);
		if(valExists){
			return val;
		}
		return null;
	}

	private Node delete(Node x, String key, Value val, int d)
	{
		if (x == null)
		{
			return null;
		}
		//we're at the node to del - set the val to null
		if (d == key.length())
		{
			x.valueList.remove(val);
			//val = null;
		}
		//continue down the trie to the target node
		else
		{
			char c = key.charAt(d);
			x.links[c] = this.delete(x.links[c], key, val, d + 1);
		}
		//this node has a val – do nothing, return the node
		if (!x.valueList.isEmpty())
		{
			return x;
		}
		//remove subtrie rooted at x if it is completely empty
		for (int c = 0; c <alphabetSize; c++)
		{
			if (x.links[c] != null)
			{
				return x; //not empty
			}
		}
		//empty - set this link to null in the parent
		return null;
	}

}
