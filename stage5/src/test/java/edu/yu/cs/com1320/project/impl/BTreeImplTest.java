package edu.yu.cs.com1320.project.impl;

import org.junit.Test;

import static org.junit.Assert.*;

public class BTreeImplTest {
	@Test
	public void test(){
		BTreeImpl<String , String> tree = new BTreeImpl<>();
		assertEquals(tree.put("a" , "1"), null);
		tree.put("b" , "2");
		tree.put("c" , "3");
		tree.put("d" , "4");
		tree.put("e" , "5");
		assertEquals("1" , tree.get("a"));
		assertEquals("2" , tree.get("b"));
		assertEquals("3" , tree.get("c"));
		assertEquals("4" , tree.get("d"));
		assertEquals("5" , tree.get("e"));
	}
	@Test
	public void test2(){
		BTreeImpl<String , String> tree = new BTreeImpl<>();
		assertEquals(tree.put("a" , "1"), null);
		assertEquals(tree.put("a" , "2"), "1");

		tree.put("b" , "2");
		tree.put("c" , "3");
		tree.put("d" , "4");
		tree.put("e" , "5");
		assertEquals("2" , tree.get("a"));
		assertEquals("2" , tree.get("b"));
		assertEquals("3" , tree.get("c"));
		assertEquals("4" , tree.get("d"));
		assertEquals("5" , tree.get("e"));
	}
}