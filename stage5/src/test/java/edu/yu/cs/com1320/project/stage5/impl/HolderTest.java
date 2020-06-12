package edu.yu.cs.com1320.project.stage5.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

public class HolderTest {

	public static void main(String[] args) throws URISyntaxException {


		URI uri = new URI("sentinel://hello/hi/not/pi/a/b/c/pizza.json");
		URI uri2 = new URI("sentinel");
		URI uri3 = new URI("");


		System.out.println(uri2.compareTo(uri));
		String a = "hi";
		System.out.println(a.replaceAll("/" , "a"));




	}
}