package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.MinHeap;

import java.util.HashMap;
import java.util.NoSuchElementException;

public class MinHeapImpl<E extends Comparable> extends MinHeap<E>{

	public MinHeapImpl(){ // PUBLIC?
		this.elements = (E[])new Comparable[10];
		this.elementsToArrayIndex = new HashMap<>();
	};

	@Override
	public void reHeapify(E element) {
		int index = elementsToArrayIndex.get(element);
		/*if(elements[index/2]!= null && (!isGreater(index , index/2))){
			upHeap(index);
		} else if((elements[index*2]!= null && isGreater(index , index*2)) || (elements[index*2+1]!= null && isGreater(index , (index*2) + 1))){
			downHeap(index);
		}*/
		upHeap(index);
		downHeap(index);

	}

	@Override
	protected int getArrayIndex(E element) {
		if(!elementsToArrayIndex.containsKey(element)){
			return -1;
		}
		return elementsToArrayIndex.get(element);
	}

	@Override
	protected void doubleArraySize() {
		this.count = 0;
		E[] temp = elements;
		elements = (E[]) new Comparable[elements.length*2];
		for(int i = 1 ; i < temp.length; i++){
			insert(temp[i]);
		}

	}

	@Override
	protected  void swap(int i, int j)
	{
		E temp = this.elements[i];
		this.elements[i] = this.elements[j];
		this.elements[j] = temp;

		//ME: Map it
		elementsToArrayIndex.put(this.elements[i] , i);
		elementsToArrayIndex.put(this.elements[j] , j);

	}

	@Override
	protected  boolean isGreater(int i, int j)
	{
		return this.elements[i].compareTo(this.elements[j]) > 0;
	}

	@Override
	protected  void upHeap(int k)
	{
		while (k > 1 && this.isGreater(k / 2, k))
		{
			this.swap(k, k / 2);
			k = k / 2;
		}
	}

	@Override
	public void insert(E x)
	{
		// double size of array if necessary
		if (this.count >= this.elements.length - 1)
		{
			this.doubleArraySize();
		}
		//add x to the bottom of the heap
		this.elements[++this.count] = x;
		//ME: map it
		elementsToArrayIndex.put(x , count);
		//percolate it up to maintain heap order property
		this.upHeap(this.count);
	}

	@Override
	public E removeMin()
	{
		if (isEmpty())
		{
			throw new NoSuchElementException("Heap is empty");
		}
		E min = this.elements[1];
		//swap root with last, decrement count
		this.swap(1, this.count--);
		//move new root down as needed
		this.downHeap(1);
		//ME: de-Map it
		elementsToArrayIndex.remove(this.elements[this.count + 1]);
		//
		this.elements[this.count + 1] = null; //null it to prepare for GC

		return min;
	}


	private void print(){
		for (int i = 1; i < elements.length; i++) {
			System.out.println("[" + i + "] " + elements[i]);
		}
	}


}
