package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Stack;

public class StackImpl <T> implements Stack<T> {

	private Entry top;
	private int stackSize;

	public StackImpl(){					// NOT SURE

	}

	class Entry<T>{
		T data;
		Entry next;

		Entry(T data){
			this.data = data;
		}
	}
	@Override
	public void push(T element) {
		Entry newEntry = new Entry(element);
		if(top==null){
			top = newEntry;
			stackSize++;
		} else{
			newEntry.next = top;
			top = newEntry;
			stackSize++;
		}

	}

	@Override
	public T pop() {
		if(top==null){
			return null;
		}
		Entry poppedEntry = top;
		top = top.next;
		stackSize--;
		return (T) poppedEntry.data;
	}

	@Override
	public T peek() {
		if(top==null){
			return null;
		}else{
			return (T) top.data;
		}
	}

	@Override
	public int size() {
		return stackSize;
	}

	private void printStack(){
		Entry entry = top;
		while(entry != null){
			System.out.println(entry.data);
			entry = entry.next;

		}
	}


}
