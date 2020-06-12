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
	@Test
	public void testGetMiss() {
		assertEquals(null,this.table.get("Key20"));
	}
	@Test
	public void testPutReturnValue() {
		assertEquals("Value3",this.table.put("Key3","Value3+1"));
		assertEquals("Value6",this.table.put("Key6", "Value6+1"));
		assertEquals(null,this.table.put("Key7","Value7"));
	}
	@Test
	public void testGetChangedValue () {
		HashTableImpl<String, String> table = new HashTableImpl<String, String>();
		String key1 = "hello";
		String value1 = "how are you today?";
		String value2 = "HI!!!";
		table.put(key1, value1);
		assertEquals(value1,table.get(key1));
		table.put(key1, value2);
		assertEquals(value2,table.get(key1));
	}
	@Test
	public void testDeleteViaPutNull() {
		HashTableImpl<String, String> table = new HashTableImpl<String, String>();
		String key1 = "hello";
		String value1 = "how are you today?";
		String value2 = null;
		table.put(key1, value1);
		table.put(key1, value2);
		assertEquals(value2,table.get(key1));
	}
	@Test
	public void testSeparateChaining () {
		HashTableImpl<Integer, String> table = new HashTableImpl<Integer, String>();
		for(int i = 0; i <= 23; i++) {
			table.put(i, "entry " + i);
		}
		assertEquals("entry 12",table.put(12, "entry 12+1"));
		assertEquals("entry 12+1",table.get(12));
		assertEquals("entry 23",table.get(23));
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
	public void putNullReturn() {
		HashTableImpl<Integer, String> test = new HashTableImpl<>();
		test.put(0, "test");
		test.put(5, "hi");
		assertEquals("hi", test.put( 5 , null));
	}
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
