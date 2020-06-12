package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.MinHeap;
import org.junit.Test;

import static org.junit.Assert.*;

public class MinHeapImplTest {

	MinHeapImpl<String> heap = new MinHeapImpl<>();

	@Test
	public void first(){
		heap.insert("b");
		heap.insert("a");
		heap.insert("c");

		assertEquals(1 ,heap.getArrayIndex("a"));
		assertEquals(2 ,heap.getArrayIndex("b"));
		assertEquals(3 ,heap.getArrayIndex("c"));

		assertEquals("a", heap.removeMin());
		assertEquals("b", heap.removeMin());
		assertEquals("c", heap.removeMin());

	}

	@Test
	public void doubleit(){
		heap.insert("1");
		heap.insert("2");
		heap.insert("3");
		heap.insert("4");
		heap.insert("5");
		heap.insert("6");
		heap.insert("7");
		heap.insert("8");
		heap.insert("9");
		heap.insert("10");
		heap.insert("11");
		heap.insert("12");




	}

}