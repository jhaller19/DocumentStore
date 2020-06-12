package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.HashTable;

public class HashTableImpl <Key,Value> implements HashTable<Key,Value> {

	private int arraySize;
	private Entry[] entryArray;
	private double loadFactor;
	private double nEntriesPut;


    public HashTableImpl(){
        arraySize = 5;
        entryArray = new Entry[arraySize];
        loadFactor = 4;
    }

	class Entry <Key , Value>{
		Key key;
		Value value;
		Entry<Key,Value> next;

		Entry(Key key , Value value){
			if(key == null || value == null){
				throw new IllegalArgumentException("Key or Value cannot be null");
			}
			this.key = key;
			this.value = value;
			this.next = null;
		}
	}

	@Override
	public Value get(Key k) {
		//get bucket
		int keyHashCode = k.hashCode();
		int index = this.hashFunction(keyHashCode);

		//traverse the bucket list until find that key
		Entry currentEntry = entryArray[index];
		while(currentEntry != null){
			if(currentEntry.key.equals(k)){
				return (Value) currentEntry.value;
			}else{
				currentEntry = currentEntry.next;
			}
		}
		//key doesnt exist in table
		return null;
	}

	private int hashFunction(int hashCode){
		int index = (hashCode & 0x7fffffff) % arraySize;
		return index;//Should this be abs
	}

	@Override
	public Value put(Key k, Value v) {
		//get bucket index
		int keyHashCode = k.hashCode();
		int index = this.hashFunction(keyHashCode);
		//If Value == null, delete...
		if(v == null){
			return delete(k , index);
		}
		//
		if(entryArray[index]==null){
			entryArray[index] = new Entry<Key , Value>(k,v);
			nEntriesPut++;
			//IF(LOADFACTOR) REHASH
			if(nEntriesPut/arraySize > loadFactor){////////<<<<<<<<<<
				doubleAndRehash();
			}
			return null;
		}
		//traverse until either (a) find existing key (b) get to the end
		Entry currentEntry = entryArray[index];
		//For first element, check if it the same
		if(k.equals(currentEntry.key)) {
			Value temp = (Value) currentEntry.value;
			currentEntry.value = v;
			return temp;
		}
		while(currentEntry.next != null){
			//if key matches: replace old value with new value, return old value
			if(k.equals(currentEntry.key)){
				Value temp = (Value) currentEntry.value;
				currentEntry.value = v;
				return temp;
			}else{
				currentEntry = currentEntry.next;
				if(k.equals(currentEntry.key)) {
					Value temp = (Value) currentEntry.value;
					currentEntry.value = v;
					return temp;
				}
			}
		}

		//put entry at the end
		currentEntry.next = new Entry<Key,Value>(k,v);
		nEntriesPut++;
		//REHASH
		if(nEntriesPut/arraySize > loadFactor){//<<<<<<<<<<
			doubleAndRehash();
		}
		return null;
	}

	private Value delete(Key k, int index) {
		//If
		if(k.equals(entryArray[index].key)){
			Value temp = (Value) entryArray[index].value;
			entryArray[index] = entryArray[index].next;
			nEntriesPut--;
			return temp;
		}else{
			Entry currentEntry = entryArray[index];
			while(currentEntry.next != null && currentEntry.next.key != k) {
				currentEntry = currentEntry.next;
			}
			if(currentEntry.next != null) {
				//I return the deleted value here
				Value temp = (Value)currentEntry.next.value;
				currentEntry.next = currentEntry.next.next;
				nEntriesPut--;
				return temp;
			}
		}
		return null; //NOT SURE
	}

	private void doubleAndRehash(){
		this.nEntriesPut = 0;
    	Entry[] tempArray = this.entryArray;
		this.arraySize = 2*arraySize;
		this.entryArray = new Entry[arraySize];
		for(int i = 0 ; i < tempArray.length ; i++){
			Entry current = tempArray[i];
			while(current != null){
				this.put((Key) current.key  , (Value) current.value );
				//nEntriesPut--;
				current = current.next;
			}

		}

	}
	private void printTable(){
		for(int i =0 ; i < arraySize ; i++){
			System.out.print("["+i+"] ");
			Entry current = entryArray[i];
			while(current != null){
				System.out.print(current.key + ", ");
				current = current.next;
			}
			System.out.println();
		}
		System.out.println("Number Current Entries: "+ nEntriesPut);
		System.out.println("Array size: "+ arraySize);
		System.out.println("Load Factor: "+ loadFactor);
		System.out.println("Entries/size "+ nEntriesPut/arraySize);



	}

   /* public static void main(String[] args) {
        HashTableImpl<Integer,String> table = new HashTableImpl();
        table.put(0, "hi");
        table.put(1, "hi");
        table.put(2, "hi");
        table.put(3, "hi");
        table.put(4, "hi");
        table.put(5, "hi");
        table.put(6, "hi");
        table.put(7, "hi");
        table.put(8, "hi");
		table.put(9, "hi");
		table.put(10, "hi");
		table.put(11, "hi");
		table.put(12, "hi");
		table.put(13, "hi");
		table.put(14, "hi");
		table.put(15, "hi");
		table.put(16, "hi");
		table.put(17, "hi");
		table.put(18, "hi");
		table.put(19, "hi");
		table.put(20, "hi");
		table.put(21, "hi");
		table.put(22, "hi");
		table.put(23, "hi");
		table.put(24, "hi");
		table.put(25, "hi");
		table.put(26, "hi");
		table.put(27, "hi");
		table.put(28, "hi");
		table.put(29, "hi");
		table.put(30, "hi");
		table.put(31, "hi");
		table.put(32, "hi");
		table.put(33, "hi");
		table.put(34, "hi");
		table.put(35, "hi");
		table.put(36, "hi");
		table.put(37, "hi");
		table.put(38, "hi");
		table.put(39, "hi");
		//table.put(40, "hi");



		table.printTable();

    }*/


}
