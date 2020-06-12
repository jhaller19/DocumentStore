package edu.yu.cs.com1320.project.stage3.impl;

import edu.yu.cs.com1320.project.stage3.DocumentStore;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static org.junit.Assert.*;

public class DocumentStoreUndoTest {

	DocumentStoreImpl store = new DocumentStoreImpl();
	List<String> list = new ArrayList<>();
	List<String> allEmptyList = new ArrayList<>();

	@Test
	public void deleteAllUndoURI() throws URISyntaxException {
		String text = "this is cool";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri = new URI("1");
		//
		String text2 = "I am is cool";
		InputStream input2 = new ByteArrayInputStream(text2.getBytes());
		URI uri2 = new URI("2");
		//
		store.putDocument(input, uri, DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2, uri2, DocumentStore.DocumentFormat.TXT);

		Set<URI> set = new HashSet<>();
		set.add(uri);
		set.add(uri2);
		assertEquals(set, store.deleteAll("cool"));
		List<String> emptySet = new ArrayList<>();
		assertEquals(emptySet, store.search("is"));
		assertEquals(emptySet, store.search("this"));
		assertEquals(emptySet, store.search("i"));
		assertEquals(emptySet, store.search("I"));
		assertEquals(emptySet, store.search("am"));
		assertEquals(null, store.getDocumentAsTxt(uri));
		assertEquals(null, store.getDocumentAsTxt(uri2));

		store.undo(uri);
		//System.out.println(store.commandStack.stackSize);
		list.add("this is cool");
		assertEquals(list, store.search("this"));
		assertEquals(emptySet, store.search("am"));
		store.undo(uri2);
		//System.out.println(store.commandStack.stackSize);

		List<String> list2 = new ArrayList<>();
		list2.add("I am is cool");
		assertEquals(list2, store.search("am"));



	}

	@Test
	public void deleteAllPrefixUndoURI() throws URISyntaxException {
		String text = "this is cool";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri = new URI("1");
		//
		String text2 = "I am is cool";
		InputStream input2 = new ByteArrayInputStream(text2.getBytes());
		URI uri2 = new URI("2");
		//
		store.putDocument(input, uri, DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2, uri2, DocumentStore.DocumentFormat.TXT);

		Set<URI> set = new HashSet<>();
		set.add(uri);
		set.add(uri2);
		assertEquals(set, store.deleteAllWithPrefix("co"));
		List<String> emptySet = new ArrayList<>();
		assertEquals(emptySet, store.search("is"));
		assertEquals(emptySet, store.search("this"));
		assertEquals(emptySet, store.search("i"));
		assertEquals(emptySet, store.search("I"));
		assertEquals(emptySet, store.search("am"));
		assertEquals(null, store.getDocumentAsTxt(uri));
		assertEquals(null, store.getDocumentAsTxt(uri2));

		store.undo(uri);
		//System.out.println(store.commandStack.stackSize);
		list.add("this is cool");
		assertEquals(list, store.search("this"));
		assertEquals(emptySet, store.search("am"));
		store.undo(uri2);
		//System.out.println(store.commandStack.stackSize);

		List<String> list2 = new ArrayList<>();
		list2.add("I am is cool");
		assertEquals(list2, store.search("am"));



	}



	////////////////////////////////////////Undo()//////////////////////////////////////////


	@Test
	public void deleteAllUndo() throws URISyntaxException {
		String text = "this is cool";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("1");
		//
		String text2 = "I am is cool";
		InputStream input2 = new ByteArrayInputStream(text2.getBytes());
		URI uri2 =  new URI("2");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);

		Set<URI> set = new HashSet<>();
		set.add(uri);
		set.add(uri2);
		assertEquals(set , store.deleteAll("cool"));
		List<String> emptySet = new ArrayList<>();
		assertEquals(emptySet , store.search("is"));
		assertEquals(emptySet , store.search("this"));
		assertEquals(emptySet , store.search("i"));
		assertEquals(emptySet , store.search("I"));
		assertEquals(emptySet , store.search("am"));
		assertEquals(null, store.getDocumentAsTxt(uri));
		assertEquals(null , store.getDocumentAsTxt(uri2));
		store.undo();
		assertEquals("this is cool" , store.getDocumentAsTxt(uri));
		assertEquals("I am is cool" , store.getDocumentAsTxt(uri2));

		emptySet.add("this is cool");
		emptySet.add("I am is cool");
		assertEquals(emptySet , store.search("cool"));

	}

	@Test
	public void deleteAllPrefixUndo() throws URISyntaxException {
		String text = "this is cool";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("1");
		//
		String text2 = "I am is cool";
		InputStream input2 = new ByteArrayInputStream(text2.getBytes());
		URI uri2 =  new URI("2");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);

		Set<URI> set = new HashSet<>();
		set.add(uri);
		set.add(uri2);
		assertEquals(set , store.deleteAllWithPrefix("co"));
		List<String> emptySet = new ArrayList<>();
		assertEquals(emptySet , store.search("is"));
		assertEquals(emptySet , store.search("this"));
		assertEquals(emptySet , store.search("i"));
		assertEquals(emptySet , store.search("I"));
		assertEquals(emptySet , store.search("am"));
		assertEquals(null, store.getDocumentAsTxt(uri));
		assertEquals(null , store.getDocumentAsTxt(uri2));
		store.undo();
		assertEquals("this is cool" , store.getDocumentAsTxt(uri));
		assertEquals("I am is cool" , store.getDocumentAsTxt(uri2));

		emptySet.add("this is cool");
		emptySet.add("I am is cool");
		assertEquals(emptySet , store.search("cool"));
	}


	@Test
	public void genericUndoSimplePut() throws URISyntaxException {
		String text = "1";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("1");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		assertEquals("1" , store.getDocumentAsTxt(uri));
		list.add("1");
		assertEquals(list , store.search("1"));
		store.undo();
		list.remove("1");
		assertEquals(null , store.getDocumentAsTxt(uri));
	}

	@Test
	public void genericUndoSimpleDelete() throws URISyntaxException {
		String text = "1";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("1");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		store.deleteDocument(uri);
		assertEquals(null , store.getDocument(uri));
		assertEquals(list , store.search("1"));
		store.undo();
		assertEquals("1" , store.getDocumentAsTxt(uri));
		list.add("1");
		assertEquals(list , store.search("1"));
	}

	@Test
	public void genericUndoSimplePutNull() throws URISyntaxException {
		String text = "1";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("1");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		store.putDocument(null , uri , DocumentStore.DocumentFormat.TXT);
		assertEquals(null , store.getDocument(uri));
		assertEquals(list , store.search("1"));
		store.undo();
		assertEquals("1" , store.getDocumentAsTxt(uri));
		list.add("1");
		assertEquals(list , store.search("1"));
	}

	@Test
	public void replaceUndo() throws URISyntaxException {
		String text = "1";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("1");
		//
		String text2 = "2";
		InputStream input2 = new ByteArrayInputStream(text2.getBytes());
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		assertEquals("1" , store.getDocumentAsTxt(uri));
		list.add("1");
		assertEquals(list , store.search("1"));
		//
		store.putDocument(input2 , uri , DocumentStore.DocumentFormat.TXT);
		assertEquals("2" , store.getDocumentAsTxt(uri));
		list.remove("1");
		list.add("2");
		assertEquals(list , store.search("2"));
		List <String> emptyList = new ArrayList<>();
		assertEquals(emptyList , store.search("1"));
		//
		store.undo();
		//
		assertEquals("1" , store.getDocumentAsTxt(uri));
		list.remove("2");
		list.add("1");
		assertEquals(list , store.search("1"));
		assertEquals(emptyList , store.search("2"));

	}

	@Test
	public void replaceUndoPDF() throws URISyntaxException, IOException {
		String text = "1";
		InputStream input = createPDFinput(text);
		URI uri =  new URI("1");
		//
		String text2 = "2";
		InputStream input2 = createPDFinput(text2);
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.PDF);
		assertEquals("1" , store.getDocumentAsTxt(uri));
		list.add("1");
		assertEquals(list , store.search("1"));
		//
		store.putDocument(input2 , uri , DocumentStore.DocumentFormat.PDF);
		assertEquals("2" , store.getDocumentAsTxt(uri));
		list.remove("1");
		list.add("2");
		assertEquals(list , store.search("2"));
		List <String> emptyList = new ArrayList<>();
		assertEquals(emptyList , store.search("1"));
		//
		store.undo();
		//
		assertEquals("1" , store.getDocumentAsTxt(uri));
		list.remove("2");
		list.add("1");
		assertEquals(list , store.search("1"));
		assertEquals("1" , store.getDocumentAsTxt(uri));
		assertEquals(emptyList , store.search("2"));

	}

	private InputStream createPDFinput(String text) throws IOException {
		byte[] byteArray = null;
		PDDocument doc = new PDDocument();
		PDPage page = new PDPage();
		doc.addPage(page);
		PDFont font = PDType1Font.HELVETICA_BOLD;
		try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
			contents.beginText();
			contents.setFont(font, 12);
			contents.newLineAtOffset(100, 700);
			contents.showText(text);
			contents.endText();
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		doc.save(out);
		doc.close();
		byteArray = out.toByteArray();
		InputStream input = new ByteArrayInputStream(byteArray);
		return input;
	}

	private String pdfToString(byte[] array){
		InputStream input = new ByteArrayInputStream(array);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];//NOT SURE
		int length = 0;
		while(true) {
			try {
				if (!((length = input.read(buf)) != -1)) break;
			} catch (IOException e) {
				e.printStackTrace();
			}
			baos.write(buf , 0 , length);
		}
		byte [] a = baos.toByteArray();

		PDDocument document = null;
		try {
			document = PDDocument.load(a);
		} catch (IOException e) {
			e.printStackTrace();
		}
		PDFTextStripper stripper = null;
		try {
			stripper = new PDFTextStripper();
		} catch (IOException e) {
			e.printStackTrace();
		}
		stripper.setSortByPosition(true);
		String text = null;
		for (int p = 1; p <= document.getNumberOfPages(); ++p) {
            /*stripper.setStartPage(p);
            stripper.setEndPage(p);*/
			try {
				text = stripper.getText(document);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return text;
	}

	@Test
	public void boredComplexUndo() throws URISyntaxException {
 		String text = "1";
		InputStream input1 = new ByteArrayInputStream(text.getBytes());
		URI uri1 =  new URI("1");
		//
		String text2 = "2";
		InputStream input2 = new ByteArrayInputStream(text2.getBytes());
		URI uri2 =  new URI("2");
		//
		String text3 = "3";
		InputStream input3 = new ByteArrayInputStream(text3.getBytes());
		String text3and1 = "3";
		InputStream input3and1 = new ByteArrayInputStream(text3and1.getBytes());
		URI uri3 =  new URI("1");
		//
		String text4 = "4 a1";
		InputStream input4= new ByteArrayInputStream(text4.getBytes());
		URI uri4 =  new URI("4");
		//
		//
		String text5 = "4 a2";
		InputStream input5 = new ByteArrayInputStream(text5.getBytes());
		URI uri5 =  new URI("5");
		//
		// put 1
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		list.add("1");
		assertEquals(store.search("1") , list);
		// put 2
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		List<String> list2 = new ArrayList<>();
		list2.add("2");
		assertEquals(store.search("2") , list2);
		// replace 1-->3
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		list.remove("1");
		list.add("3");
		assertEquals(list , store.search("3"));
		assertEquals(allEmptyList , store.search("1"));
		// NooP
		store.putDocument(input3and1 , uri3 , DocumentStore.DocumentFormat.TXT);
		assertEquals(list , store.search("3"));
		//put 4
		store.putDocument(input4 , uri4 , DocumentStore.DocumentFormat.TXT);
		//put 5
		store.putDocument(input5 , uri5 , DocumentStore.DocumentFormat.TXT);
		List<String> list3 = new ArrayList<>();
		list3.add("4 a1");
		list3.add("4 a2");
		assertEquals(list3 , store.search("4"));
		store.deleteAll("4");
		assertEquals(allEmptyList , store.search("4"));

		///
		store.undo();
		assertEquals(list3 , store.search("4"));
		store.undo();
		store.undo();
		assertEquals(allEmptyList , store.search("4"));
		store.undo(); //noop
		store.undo();
		assertEquals(allEmptyList , store.search("3"));
		list.remove("3");
		list.add("1");
		assertEquals(list , store.search("1"));
		store.undo();
		assertEquals(allEmptyList,  store.search("2"));
		store.undo();
		assertEquals(allEmptyList,  store.search("1"));

	}

	@Test
	public void boredComplexUndoURI() throws URISyntaxException {
		String text = "1";
		InputStream input1 = new ByteArrayInputStream(text.getBytes());
		URI uri1 =  new URI("1");
		//
		String text2 = "2";
		InputStream input2 = new ByteArrayInputStream(text2.getBytes());
		URI uri2 =  new URI("2");
		//
		String text3 = "3";
		InputStream input3 = new ByteArrayInputStream(text3.getBytes());
		String text3and1 = "3";
		InputStream input3and1 = new ByteArrayInputStream(text3and1.getBytes());
		URI uri3 =  new URI("1");
		//
		String text4 = "4 a1";
		InputStream input4= new ByteArrayInputStream(text4.getBytes());
		URI uri4 =  new URI("4");
		//
		//
		String text5 = "4 a2";
		InputStream input5 = new ByteArrayInputStream(text5.getBytes());
		URI uri5 =  new URI("5");
		//
		// put 1
		store.putDocument(input1 , uri1 , DocumentStore.DocumentFormat.TXT);
		list.add("1");
		assertEquals(store.search("1") , list);
		// put 2
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);
		List<String> list2 = new ArrayList<>();
		list2.add("2");
		assertEquals(store.search("2") , list2);
		// replace 1-->3
		store.putDocument(input3 , uri3 , DocumentStore.DocumentFormat.TXT);
		list.remove("1");
		list.add("3");
		assertEquals(list , store.search("3"));
		assertEquals(allEmptyList , store.search("1"));
		// NooP
		store.putDocument(input3and1 , uri3 , DocumentStore.DocumentFormat.TXT);
		assertEquals(list , store.search("3"));
		//put 4
		store.putDocument(input4 , uri4 , DocumentStore.DocumentFormat.TXT);
		//put 5
		store.putDocument(input5 , uri5 , DocumentStore.DocumentFormat.TXT);
		List<String> list3 = new ArrayList<>();
		list3.add("4 a1");
		list3.add("4 a2");
		assertEquals(list3 , store.search("4"));
		store.deleteAll("4");
		assertEquals(allEmptyList , store.search("4"));

		///
		store.undo();
		assertEquals(list3 , store.search("4"));
		store.undo(uri5);
		store.undo(uri4);
		assertEquals(allEmptyList , store.search("4"));
		store.undo(uri3); //noop
		store.undo(uri3);
		assertEquals(allEmptyList , store.search("3"));
		list.remove("3");
		list.add("1");
		assertEquals(list , store.search("1"));
		store.undo(uri2);
		assertEquals(allEmptyList,  store.search("2"));
		store.undo(uri1);
		assertEquals(allEmptyList,  store.search("1"));
		//stack empty
	}

	@Test
	public void deleteAllNoOp() throws URISyntaxException {
		String text = "this is cool";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri = new URI("1");
		//
		String text2 = "I am is cool";
		InputStream input2 = new ByteArrayInputStream(text2.getBytes());
		URI uri2 = new URI("2");
		//
		store.putDocument(input, uri, DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2, uri2, DocumentStore.DocumentFormat.TXT);

		Set<URI> set = new HashSet<>();
		assertEquals(set, store.deleteAll("pizza"));
		List<String> emptySet = new ArrayList<>();

		store.undo();
		store.undo();
		store.undo();
		//Stack empty

	}

	@Test
	public void deleteAllPrefixNoOp() throws URISyntaxException {
		String text = "this is cool";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri = new URI("1");
		//
		String text2 = "I am is cool";
		InputStream input2 = new ByteArrayInputStream(text2.getBytes());
		URI uri2 = new URI("2");
		//
		store.putDocument(input, uri, DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2, uri2, DocumentStore.DocumentFormat.TXT);

		Set<URI> set = new HashSet<>();
		assertEquals(set, store.deleteAllWithPrefix("p"));
		List<String> emptySet = new ArrayList<>();

		store.undo();
		store.undo();
		store.undo();
		//Stack is empty

	}

	@Test
	public void replaceTest() throws URISyntaxException {
		String text = "replACe this";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri = new URI("1");
		//
		String text2 = "I am is cool";
		InputStream input2 = new ByteArrayInputStream(text2.getBytes());
		URI uri2 = new URI("1");
		//
		store.putDocument(input, uri, DocumentStore.DocumentFormat.TXT);
		list.add("replACe this");
		assertEquals(list , store.search("rEplace") );
		assertEquals(allEmptyList , store.search("Am") );
		store.putDocument(input2, uri2, DocumentStore.DocumentFormat.TXT);
		assertEquals(allEmptyList , store.search("ThIs") );
		list.remove("replACe this");
		list.add("I am is cool");
		assertEquals(list , store.search("AM") );

		store.undo();
		assertEquals(allEmptyList , store.search("AM") );
		list.add("replACe this");
		list.remove("I am is cool");
		assertEquals(list , store.search("ThIs") );

	}

	@Test
	public void deleteAllNoOp2() throws URISyntaxException {
		String text = "this is cool";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri = new URI("1");
		//
		String text2 = "I am is cool";
		InputStream input2 = new ByteArrayInputStream(text2.getBytes());
		URI uri2 = new URI("2");
		//
		store.putDocument(input, uri, DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2, uri2, DocumentStore.DocumentFormat.TXT);

		Set<URI> set = new HashSet<>();
		set.add(uri);
		set.add(uri2);
		store.deleteAll("miss");

		store.undo();
		store.undo();
		store.undo();
	}
	@Test
	public void deleteAllwithPrefixNoOp2() throws URISyntaxException {
		String text = "this is cool";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri = new URI("1");
		//
		String text2 = "I am is cool";
		InputStream input2 = new ByteArrayInputStream(text2.getBytes());
		URI uri2 = new URI("2");
		//
		store.putDocument(input, uri, DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2, uri2, DocumentStore.DocumentFormat.TXT);

		Set<URI> set = new HashSet<>();
		set.add(uri);
		set.add(uri2);
		store.deleteAllWithPrefix("miss");

		store.undo();
		store.undo();
		store.undo();
		//store.undo();
	}

	@Test
	public void hashtableCheck() throws URISyntaxException {
		String text = "1";
		InputStream input1 = new ByteArrayInputStream(text.getBytes());
		URI uri1 = new URI("1");
		//
		store.putDocument(input1, uri1 , DocumentStore.DocumentFormat.TXT);
		assertEquals("1" , store.getDocumentAsTxt(uri1));
		store.deleteAll("1");
		assertEquals(null , store.getDocumentAsTxt(uri1));
		store.undo();
		assertEquals("1" , store.getDocumentAsTxt(uri1));

	}

	@Test
	public void undoURIinCommandSet() throws URISyntaxException {
		String text = "this is cool";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("1");
		//
		String text2 = "I am is cool";
		InputStream input2 = new ByteArrayInputStream(text2.getBytes());
		URI uri2 =  new URI("2");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		store.putDocument(input2 , uri2 , DocumentStore.DocumentFormat.TXT);

		Set<URI> set = new HashSet<>();
		set.add(uri);
		set.add(uri2);
		assertEquals(set , store.deleteAll("cool"));
		List<String> emptySet = new ArrayList<>();


		store.undo(uri);
		assertEquals("this is cool" , store.getDocumentAsTxt(uri));
		assertEquals(null , store.getDocumentAsTxt(uri2));
		store.undo(uri2);
		assertEquals("I am is cool" , store.getDocumentAsTxt(uri2));
		store.undo();
		assertEquals(null , store.getDocumentAsTxt(uri2));
		assertEquals("this is cool" , store.getDocumentAsTxt(uri));
		store.undo();
		assertEquals(null , store.getDocumentAsTxt(uri));

		//store.undo();



		/*emptySet.add("this is cool");
		emptySet.add("I am is cool");
		assertEquals(emptySet , store.search("cool"));*/

	}


	///////////////////////////////////////////////////////////////////////

	@Test(expected = IllegalStateException.class)
	public void URINotFoundUndo() throws URISyntaxException {
		String text = "hello";
		InputStream input = new ByteArrayInputStream(text.getBytes());
		URI uri =  new URI("hi");
		URI uri2  = new URI("not");
		//
		store.putDocument(input , uri , DocumentStore.DocumentFormat.TXT);
		store.undo(uri2);
	}

	@Test(expected = IllegalStateException.class)
	public void commandStackEmptySimpleUndo() {
		store.undo();
	}
	@Test(expected = IllegalStateException.class)
	public void commandStackEmptyURIUndo() throws URISyntaxException {
		URI uri = new URI("hi");
		store.undo(uri);
	}

}