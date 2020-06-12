package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.HashTable;


public class HashTableImpl <Key,Value> implements HashTable<Key,Value> {

    private int arraySize = 5;
    private Entry[] entryArray = new Entry[arraySize];


    /*public HashTableImpl(){
        arraySize = 5;
        entryArray = new Entry[arraySize];
    }*/

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
                                                                ///////////FIX//////////
                if(k.equals(currentEntry.key)) {
                    Value temp = (Value) currentEntry.value;
                    currentEntry.value = v;
                    return temp;
                }
                                                                ///////////FIX///////////
            }
        }

        //put entry at the end
        currentEntry.next = new Entry<Key,Value>(k,v);
        return null;
    }

    private Value delete(Key k, int index) {

        if(k.equals(entryArray[index].key)){
            Value temp = (Value) entryArray[index].value;
            entryArray[index] = entryArray[index].next;
            return temp;
        }else{
            Entry currentEntry = entryArray[index];
            while(currentEntry.next != null && currentEntry.next.key != k) {
                currentEntry = currentEntry.next;
            }
            if(currentEntry.next != null) {
                currentEntry.next = currentEntry.next.next;
            }
        }
        return null; //NOT SURE
    }

    /*public static void main(String[] args) {
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

        table.put(4, null);
        for(int i = 0 ; i < 5; i++){
            System.out.println(table.entryArray[i].key);
        }


    }*/


}
