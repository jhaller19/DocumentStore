package edu.yu.cs.com1320.project.stage2.impl;

import edu.yu.cs.com1320.project.impl.StackImpl;
import org.junit.Test;

import static org.junit.Assert.*;

public class StackImplTest {

	@Test
	public void pushNpop1() {
		StackImpl<String> stack = new StackImpl<>();
		stack.push("1");
		stack.push("2");
		stack.push("3");
		assertEquals("3",stack.peek());
		assertEquals("3",stack.pop());
		assertEquals("2",stack.peek());
		assertEquals("2",stack.pop());
		assertEquals("1",stack.peek());
		assertEquals("1",stack.pop());
		assertEquals(null , stack.pop());
		assertEquals(null , stack.peek());

	}
	@Test
	public void sizeTest() {
		StackImpl<String> stack = new StackImpl<>();
		assertEquals(0,stack.size());
		stack.push("1");
		stack.push("2");
		stack.push("3");
		assertEquals(3,stack.size());
		stack.pop();
		assertEquals(2,stack.size());
		stack.pop();
		assertEquals(1,stack.size());
		stack.pop();
		assertEquals(0,stack.size());

	}

}