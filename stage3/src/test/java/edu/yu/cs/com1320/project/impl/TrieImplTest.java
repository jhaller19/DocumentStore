package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.stage3.impl.DocumentImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class TrieImplTest {

	TrieImpl<String> trie = new TrieImpl<>();
	List<String> list = new ArrayList<>();
	Set<String> set = new HashSet<>();


	@Test
	public void complexDeleteAllPrefix() {
		trie.put("abc%de", "1");
		trie.put("abcdf", "2");
		trie.put("abcgh", "3");
		trie.put("abc", "4");
		set.add("1");
		set.add("2");
		set.add("3");
		set.add("4");
		assertEquals(set, trie.deleteAllWithPrefix("a%bc"));


	}

	@Test
	public void complexDeleteAll() {
		trie.put("a*bcd", "1");
		trie.put("a()b", "2");
		set.add("1");
		assertEquals(set, trie.deleteAll("abc()d"));
		List<String> list = new ArrayList<>();
		list.add("2");
		assertEquals(list, trie.getAllSorted("a*b", new doNothing()));
	}

	@Test
	public void simpleDeleteAllPrefix() {
		list.add("1");
		list.add("2");
		trie.put("hello", "1");
		trie.put("hello", "2");
		assertEquals(list, trie.getAllWithPrefixSorted("h*e", new doNothing()));
		Set<String> set = new HashSet<String>(list);
		assertEquals(set, trie.deleteAllWithPrefix("h()e"));
		list.remove("1");
		list.remove("2");
		assertEquals(list, trie.getAllWithPrefixSorted("h%^&e", new doNothing()));

	}

	@Test
	public void deleteAllPrefixFullWord() {
		list.add("1");
		list.add("2");
		trie.put("hello", "1");
		trie.put("hello", "2");
		assertEquals(list, trie.getAllWithPrefixSorted("he", new doNothing()));
		Set<String> set = new HashSet<String>(list);
		assertEquals(set, trie.deleteAllWithPrefix("hello"));
		list.remove("1");
		list.remove("2");
		assertEquals(list, trie.getAllWithPrefixSorted("he", new doNothing()));

	}

	@Test
	public void simpleGetAllPrefix() {
		list.add("1");
		list.add("2");
		trie.put("hello", "1");
		trie.put("hello", "2");
		assertEquals(list, trie.getAllWithPrefixSorted("h()", new doNothing()));
	}

	@Test
	public void getAllPrefixNoDuplicates() {
		list.add("1");
		//list.add("2");
		trie.put("hello", "1");
		trie.put("he", "1");
		assertEquals(list, trie.getAllWithPrefixSorted("h", new doNothing()));
	}

	@Test
	public void getAllNoDuplicates() {
		list.add("1");
		//list.add("2");
		trie.put("hello", "1");
		trie.put("hello", "1");
		assertEquals(list, trie.getAllSorted("hello", new doNothing()));
	}

	@Test
	public void simpleDeleteAll() {
		list.add("1");
		list.add("2");
		trie.put("hello", "1");
		trie.put("hello", "2");
		assertEquals(list, trie.getAllSorted("hello", new doNothing()));
		Set<String> set = new HashSet<>(list);
		assertEquals(set, trie.deleteAll("hello"));
		list.remove("1");
		list.remove("2");
		assertEquals(list, trie.getAllSorted("hello", new doNothing()));

	}

	@Test
	public void simpleDelete() {
		list.add("1");
		trie.put("hello", "1");
		assertEquals(list, trie.getAllSorted("hello", new doNothing()));
		assertEquals(null, trie.delete("hello", "new doNothing"));
		assertEquals("1", trie.delete("hello", "1"));
		list.remove("1");
		assertEquals(list, trie.getAllSorted("hello", new doNothing()));

	}

	@Test
	public void simplePutAndGet() {
		list.add("1");
		trie.put("hello", "1");
		assertEquals(list, trie.getAllSorted("hello", new doNothing()));
	}

	@Test
	public void simplePutAndGet2() {
		list.add("1");
		trie.put("he-llo", "1");
		assertEquals(list, trie.getAllSorted("hell-o", new doNothing()));
	}

	@Test
	public void fullAlphabetNumbers() {
		list.add("aAzZ09");
		trie.put("zaAzZ09", "aAzZ09");
		assertEquals(list, trie.getAllSorted("zaAzZ09", new doNothing()));
	}

	@Test
	public void AlphaNumeric() {
		list.add("aAzZ09");
		trie.put("zaAz%Z09", "aAzZ09");
		assertEquals(list, trie.getAllSorted("zaAzZ09", new doNothing()));
	}
	@Test
	public void putDuplicate() {
		//list.add("1");
		trie.put("hello", "1");
		trie.put("hello", "1");
		assertEquals(1, trie.getAllSorted("hello", new doNothing()).size());
	}

	class doNothing implements Comparator<String> {
		@Override
		public int compare(String o1, String o2) {
			return 0;
		}
	}

	///////////////////////Case Insensitive/////////////////////////////////
	@Test
	public void getAllCaseIncesitive() {
		list.add("1");
		trie.put("hElLo", "1");
		assertEquals(list, trie.getAllSorted("HellO", new doNothing()));
	}
	@Test
	public void getAllPrefixCaseIncesitive() {
		list.add("1");
		trie.put("hElLo", "1");
		assertEquals(list, trie.getAllWithPrefixSorted("HELl", new doNothing()));
	}

	@Test
	public void deleteIncesitive() {
		list.add("1");
		trie.put("hEllO", "1");
		assertEquals(list, trie.getAllSorted("Hello", new doNothing()));
		assertEquals(null, trie.delete("hellO", "new doNothing"));
		assertEquals("1", trie.delete("heLLo", "1"));
		list.remove("1");
		assertEquals(list, trie.getAllSorted("hEllo", new doNothing()));

	}

	@Test
	public void deleteAllInsensitive() {
		list.add("1");
		list.add("2");
		trie.put("hELLo", "1");
		trie.put("Hello", "2");
		assertEquals(list, trie.getAllSorted("hellO", new doNothing()));
		Set<String> set = new HashSet<>(list);
		assertEquals(set, trie.deleteAll("HEllo"));
		list.remove("1");
		list.remove("2");
		assertEquals(list, trie.getAllSorted("hELLO", new doNothing()));

	}

	@Test
	public void deleteAllPrefixInsensitive() {
		list.add("1");
		list.add("2");
		trie.put("heLLo", "1");
		trie.put("Hello", "2");
		assertEquals(list, trie.getAllSorted("hellO", new doNothing()));
		Set<String> set = new HashSet<>(list);
		assertEquals(set, trie.deleteAllWithPrefix("HEl"));
		list.remove("1");
		list.remove("2");
		assertEquals(list, trie.getAllSorted("hELLO", new doNothing()));

	}

}