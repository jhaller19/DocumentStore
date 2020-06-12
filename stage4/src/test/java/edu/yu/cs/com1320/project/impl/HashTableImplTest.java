package edu.yu.cs.com1320.project.impl;


import edu.yu.cs.com1320.project.HashTable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

public class HashTableImplTest {

	/*//JUDAH
	private HashTable<String,String> table;

	@Before
	public void initTable(){
		this.table = new HashTableImpl<>();
		this.table.put("Key1", "Value1");
		this.table.put("Key2","Value2");
		this.table.put("Key3","Value3");
		this.table.put("Key4","Value4");
		this.table.put("Key5","Value5");
		this.table.put("Key6","Value6");
	}
	@Test
	public void testGet() {
		assertEquals("Value1",this.table.get("Key1"));
		assertEquals("Value2",this.table.get("Key2"));
		assertEquals("Value3",this.table.get("Key3"));
		assertEquals("Value4",this.table.get("Key4"));
		assertEquals("Value5",this.table.get("Key5"));
	}
	@Test
	public void testGetChained() {
		//second node in chain
		assertEquals("Value6",this.table.get("Key6"));
		//second node in chain after being modified
		this.table.put("Key6","Value6+1");
		assertEquals("Value6+1",this.table.get("Key6"));
		//check that other values still come back correctly
		testGet();
	}
	//JUDAH*/
	/*@Test
	public void arrayDoublingTest(){
		HashTableImpl table = new HashTableImpl();
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
		assertEquals(10 , table);


	}*/



	///**************************************OLD TESTS*****************************************\\

	@Test
	public void getKeyExists() {
		HashTableImpl<String, String> test = new HashTableImpl<>();
		test.put("hello", "test");
		assertEquals("test", test.get("hello"));
	}
	@Test
	public void getKeyDoesntExists() {
		HashTableImpl<String, String> test = new HashTableImpl<>();
		assertEquals( null , test.get("hello"));
	}
	@Test
	public void firstPut() {
		HashTableImpl<String, String> test = new HashTableImpl<>();
		assertEquals( null , test.put("k","v"));
	}
	@Test
	public void deleteLast() {
		HashTableImpl<Integer, String> test = new HashTableImpl<>();
		test.put(1 , "a");
		test.put(2 , "b");
		test.put(3 , "c");
		test.put(4 , "d");
		test.put(5 , "e");
		test.put(6 , "f");
		test.put(7, "g");
		test.put(8 , "h");
		test.put(9 , "i");
		test.put(10 , "j");
		test.put(11 , "k");

		test.put(10,null);

		assertEquals( null , test.get(10));
	}

	@Test
	public void getMultiplePuts() {
		HashTableImpl<String, String> test = new HashTableImpl<>();
		test.put("1" , "a");
		test.put("2" , "b");
		test.put("3" , "c");
		test.put("4" , "d");
		test.put("5" , "e");
		test.put("6" , "f");
		test.put("7" , "g");
		test.put("8" , "h");
		test.put("9" , "i");
		test.put("10" , "j");
		test.put("11" , "k");
		test.put("10" , "j");
		test.put("11" , "k");

		assertEquals( "k" , test.get("11"));
	}

	@Test
	public void putAlreadyExists() {
		HashTableImpl<String, String> table = new HashTableImpl<>();
		table.put("hi" , "old value");
		assertEquals("old value" , table.put("hi","new value"));
		assertEquals("new value" , table.get("hi"));
	}
	@Test
	public void putDoesntAlreadyExists() {
		HashTableImpl<String, String> table = new HashTableImpl<>();
		table.put("hi" , "old value");
		assertEquals( null , table.put("hili","new value"));
	}
	@Test
	public void putDelete() {
		HashTableImpl<String, String> table = new HashTableImpl<>();
		table.put("hi" , "delete");
		table.put("hi",null);
		assertEquals( null , table.get("hi"));
	}

}
