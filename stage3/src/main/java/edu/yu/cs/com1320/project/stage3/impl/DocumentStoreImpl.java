package edu.yu.cs.com1320.project.stage3.impl;

import edu.yu.cs.com1320.project.*;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage3.Document;
import edu.yu.cs.com1320.project.stage3.DocumentStore;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Function;

public class DocumentStoreImpl implements DocumentStore{


	private HashTableImpl<URI , DocumentImpl> store = new HashTableImpl<>();
	private StackImpl<Undoable> commandStack = new StackImpl<>();
	private StackImpl<Undoable> helperStack = new StackImpl<>();
	private TrieImpl<DocumentImpl> trie = new TrieImpl<>(); //new

	@Override
	public int putDocument(InputStream input, URI uri, DocumentFormat format) {
		//If URI or format is null, throw IAE
		if(uri == null || format == null){
			throw new IllegalArgumentException("URI cannot be null");
		}
		//If input is null, delete
		if (input == null){
			//if uri exists return the deleted documents text hashcode
			if(store.get(uri) != null){
				int documentTextHashcode = store.get(uri).getDocumentTextHashCode();
				addCommandRegularDelete(uri , store.get(uri)); //NEW (Stage 2)
				deleteDocument(uri);
				return documentTextHashcode; //NOT SURE
			}
			//If uri doesnt exist return 0
			else{
				addNoOpCommand(uri); //NOOP
				return 0;
			}
		}

		byte[] byteArray;
		byteArray = toByteArray(input);

		if(format == DocumentFormat.TXT){
			return txtFormatOperations(uri , byteArray , input, format);
		}else{
			return pdfFormatOperations(uri, byteArray);

		}

	}

	private int txtFormatOperations(URI uri , byte[] byteArray, InputStream input , DocumentFormat format){
		//Create String
		String txtAsString = new String(byteArray);
		//Get Hashcode
		int txtStringHashCode = txtAsString.hashCode();
		//Check that this exact document (same URI and Hashcode) doesnt already exist
		if(store.get(uri) != null && store.get(uri).getDocumentTextHashCode() == txtStringHashCode){
			addNoOpCommand(uri);
			return txtStringHashCode;//NOT SURE
		}
		//Create Document obj and either (a) insert the new Doc (b) replace old Doc with same URI (but dif Hashcode)
		DocumentImpl document = new DocumentImpl(uri , txtAsString, txtStringHashCode);
		if(store.get(uri) == null){
			store.put(uri , document);
			putIntoTrie(document);
			addCommandRegularPut(uri , document);
			return 0;
		}else{ //If same URI but different text, replace the text
			int oldHashCode = store.get(uri).getDocumentTextHashCode();
			addCommandReplace( uri , store.get(uri) , document , store.get(uri).getDocumentAsTxt(), txtAsString); //CHECK <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
			replaceInTrie(txtAsString , store.get(uri).getDocumentAsTxt() , store.get(uri) , document);
			store.put(uri , document);
			return oldHashCode;
		}
	}

	private void putIntoTrie(DocumentImpl doc){
		String fullDocText = doc.getDocumentAsTxt();
		String array[]= fullDocText.split(" ");
		for (String word: array){
			trie.put(word, doc);
		}
	}
	private void deleteFromEntireTrie(DocumentImpl doc){
		String fullDocText = doc.getDocumentAsTxt();
		String array[]= fullDocText.split(" ");
		for (String word: array){
			trie.delete(word, doc);
		}
	}
	private void replaceInTrie(String newText , String oldText , DocumentImpl oldDoc , DocumentImpl newDoc){
		String array[]= oldText.split(" ");
		for (String word: array){
			trie.delete(word, oldDoc);
		}
		String array2[]= newText.split(" ");
		for (String word: array2){
			trie.put(word, newDoc);
		}
	}

	private int pdfFormatOperations(URI uri , byte[] byteArray){
		//Create String
		String pdfAsString = null;
		pdfAsString = pdfToString(byteArray);
		//Get Hashcode
		int pdfStringHashCode = pdfAsString.hashCode();
		//Check that this exact document (same URI and Hashcode) doesnt already exist
		if (store.get(uri) != null && store.get(uri).getDocumentTextHashCode() == pdfStringHashCode){
			addNoOpCommand(uri);
			return pdfStringHashCode;
		}
		//Create Document obj and either (a) insert the new Doc (b) replace old Doc with same URI (but dif Hashcode)
		DocumentImpl document = new DocumentImpl( uri,  pdfAsString, pdfStringHashCode, byteArray);
		if(store.get(uri) == null){
			store.put(uri , document);
			putIntoTrie(document);
			addCommandRegularPut(uri , document);
			return 0;
		}else{
			int oldHashCode = store.get(uri).getDocumentTextHashCode();
			//addCommandRegularDelete(uri , store.get(uri));
			addCommandReplace( uri , store.get(uri) , document , store.get(uri).getDocumentAsTxt(), pdfAsString); //CHECK <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
			replaceInTrie(pdfAsString , store.get(uri).getDocumentAsTxt() , store.get(uri) , document);
			store.put(uri , document);
			//Should there be a trieput() here?   <<<<<<<<<<<<<<<<<<<
			return oldHashCode;
		}
	}


	//PRIVATE
	private String pdfToString(byte[] byteArray)  {
		PDDocument document = null;
		try {
			document = PDDocument.load(byteArray);
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
				document.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return text.trim();
	}


	private byte[] toByteArray(InputStream is) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];//NOT SURE
		int length = 0;
		while(true) {
			try {
				if (!((length = is.read(buf)) != -1)) break;
			} catch (IOException e) {
				e.printStackTrace();
			}
			baos.write(buf , 0 , length);
		}
		return baos.toByteArray();
	}
	//PRIVATE

	@Override
	public byte[] getDocumentAsPdf(URI uri) {
		//If document doesnt exist
		if(store.get(uri) == null){
			return null;
		}
		//if document exists
		else{
			return store.get(uri).getDocumentAsPdf();
		}
	}

	@Override
	public String getDocumentAsTxt(URI uri) {
		//If document doesnt exist
		if(store.get(uri) == null){
			return null;
		}
		//if document exists
		else{
			return store.get(uri).getDocumentAsTxt();
		}
	}

	@Override
	public boolean deleteDocument(URI uri) {
		//if document doesnt exist
		if(store.get(uri) == null){
			addNoOpCommand(uri);
			return false;
		}
		//If document exists
		addCommandRegularDelete(uri , store.get(uri));
		deleteFromEntireTrie(store.get(uri));
		store.put(uri,null);
		return true;
	}

	private void addCommandRegularPut(URI uri , DocumentImpl document) {
		Function<URI , Boolean> undoFunction = (URI uriForLambda) -> {
			store.put(uriForLambda, null);
			String [] array = document.getDocumentAsTxt().split(" ");
			for(String word : array){
				trie.delete(word , document);
			}
			return true;
		};
		GenericCommand<URI> command = new GenericCommand<>(uri , undoFunction);
		commandStack.push(command);
	}

	private void addNoOpCommand(URI uri) {
		Function<URI , Boolean> undoFunction = (URI uriForLambda) -> {
			return true;
		};
		GenericCommand<URI> command = new GenericCommand<>(uri , undoFunction);
		commandStack.push(command);
	}

	private void addCommandRegularDelete(URI uri , DocumentImpl document) {
		Function<URI , Boolean> undoFunction = (URI uriForLambda) -> {
			store.put(uriForLambda, document);
			putIntoTrie(document);
			return true;
		};
		GenericCommand<URI> command = new GenericCommand<>(uri , undoFunction);
		commandStack.push(command);
	}
	private void addCommandReplace(URI uri , DocumentImpl oldDocument , DocumentImpl newDocument , String newText , String oldText) {
		Function<URI , Boolean> undoFunction = (URI uriForLambda) -> {
			replaceInTrie(newText , oldText, newDocument  ,oldDocument );
			store.put(uriForLambda, oldDocument);
			//putIntoTrie(document);
			return true;
		};
		GenericCommand<URI> command = new GenericCommand<>(uri , undoFunction);
		commandStack.push(command);
	}

	@Override
	public void undo() throws IllegalStateException { //changed something around here not sure if it works right
		Undoable command = commandStack.pop();
		if(command == null){
			throw new IllegalStateException("Stack is empty");
		}
		//command = commandStack.pop();
		command.undo();

	}

	private Boolean genericCommandUndoURI(GenericCommand command , URI uri){
		if(command.getTarget().equals(uri)){
			command.undo();
			return true;
		}
		return false;
	}
	private Boolean commandSetUndoURI(CommandSet command , URI uri){
		if(command.containsTarget(uri)){
			command.undo(uri);
			//If commandSet is not empty, push the command back onto stack
			if(command.size() != 0){
				commandStack.push(command);
			}
			return true;
		}
		return false;
	}

	@Override
	public void undo(URI uri) throws IllegalStateException {
		Undoable command = commandStack.pop();
		Boolean successfulUndo = false;
		while(successfulUndo==false && command!=null) {
			if (command instanceof GenericCommand) {
				successfulUndo = genericCommandUndoURI((GenericCommand) command, uri);
				//If no match, place onto helperstack and pop next
				if (successfulUndo == false) {
					helperStack.push(command);
					command = commandStack.pop();
				}
			} else {
				successfulUndo = commandSetUndoURI((CommandSet) command, uri);
				//If no match, place onto helperstack and pop next
				if (successfulUndo == false) {
					helperStack.push(command);
					command = commandStack.pop();
				}
			}
		}
		Undoable helperCommand = helperStack.pop();
		while(helperCommand != null){
			commandStack.push(helperCommand);
			helperCommand = helperStack.pop();

		}
		//If it comes out of the loop without Success then uri must not exist/stack is empty
		if(command == null){
			throw new IllegalStateException("Uri doesnt exist");
		}

	}

	protected Document getDocument(URI uri){
		return store.get(uri);
	} //MAKE SURE THIS CAN BE HERE

	//////NEW///////
	@Override
	public List<String> search(String keyword) {
		if(keyword == null){
			List<String> empty = new ArrayList<>();
			return empty;
		}

		List<DocumentImpl> listOfDocs = trie.getAllSorted(keyword , new sortDocs(keyword));
		List<String> listOfDocTexts = new ArrayList<>();
		for(DocumentImpl doc : listOfDocs){
			listOfDocTexts.add(doc.getDocumentAsTxt());
		}
		return listOfDocTexts;
	}

	@Override
	public List<byte[]> searchPDFs(String keyword) {
		if(keyword == null){
			List<byte[]> empty = new ArrayList<>();
			return empty;
		}
		Comparator<DocumentImpl> compareIt = (DocumentImpl o1, DocumentImpl o2) ->{
			int o1WordCount  = o1.wordCount(keyword);
			int o2WordCount  = o2.wordCount(keyword);
			if(o1WordCount==o2WordCount){
				return 0;
			}
			if(o1WordCount<o2WordCount){
				return 1;
			}
			return -1;
		};

		List<DocumentImpl> listOfDocs = trie.getAllSorted(keyword , compareIt);
		List<byte []> listOfPDFs = new ArrayList<>();
		for(DocumentImpl doc : listOfDocs){
			listOfPDFs.add(doc.getDocumentAsPdf());
		}
		return listOfPDFs;
	}

	class sortDocs implements Comparator<DocumentImpl> {
		String word;

		sortDocs(String word){
			this.word = word;
		}
		@Override
		public int compare(DocumentImpl o1, DocumentImpl o2) {
			word = word.toUpperCase();// NOT SURE
			if(o1.equals(o2)){
				return 0;
			}
			int o1WordCount  = o1.wordCount(word);
			int o2WordCount  = o2.wordCount(word);
			if(o1WordCount==o2WordCount){
				return 0;
			}
			if(o1WordCount<o2WordCount){
				return 1;
			}
			return -1;
		}
	}

	@Override
	public List<String> searchByPrefix(String prefix) {
		if(prefix == null){
			List<String> empty = new ArrayList<>();
			return empty;
		}
		List<DocumentImpl> listOfDocs = trie.getAllWithPrefixSorted(prefix , new sortPrefix(prefix)); //Fill IN
		List<String> listOfDocTexts = new ArrayList<>();
		for(DocumentImpl doc : listOfDocs){
			listOfDocTexts.add(doc.getDocumentAsTxt());
		}
		return listOfDocTexts;
	}

	@Override
	public List<byte[]> searchPDFsByPrefix(String prefix) {
		if(prefix == null){
			List<byte[]> empty = new ArrayList<>();
			return empty;
		}
		List<DocumentImpl> listOfDocs = trie.getAllWithPrefixSorted(prefix , new sortPrefix(prefix)); //Fill in
		List<byte []> listOfPDFs = new ArrayList<>();
		for(DocumentImpl doc : listOfDocs){
			listOfPDFs.add(doc.getDocumentAsPdf());
		}
		return listOfPDFs;
	}

	class sortPrefix implements Comparator<DocumentImpl> {
		String prefix;

		sortPrefix(String prefix){
			this.prefix = prefix;
		}
		@Override
		public int compare(DocumentImpl o1, DocumentImpl o2) {
			prefix = prefix.toUpperCase();
			int o1WordCount  = prefixCount(o1 , prefix);
			int o2WordCount  = prefixCount(o2 , prefix);
			if(o1WordCount==o2WordCount){
				return 0;
			}
			if(o1WordCount<o2WordCount){
				return 1;
			}
			return -1;
		}
	}

	private int prefixCount(DocumentImpl doc , String prefix){
		String text = doc.getDocumentAsTxt();
		text = text.replaceAll("[^a-zA-Z0-9 ]", "");
		text = text.toUpperCase();
		String[] array = text.split(" ");
		int counter = 0 ;
		for(String word : array){
			if(word.length() >= prefix.length() && word.substring(0 ,prefix.length()).equals(prefix)){
				counter ++;
			}
		}
		return counter;
	}

	@Override
	public Set<URI> deleteAll(String key) {
		if(key == null){
			Set<URI> empty = new HashSet<>();
			return empty;
		}
		CommandSet commandSet = new CommandSet(); //Create CommandSet
		//key = key.toUpperCase();
		Set <DocumentImpl> deletedDocsSet = trie.deleteAll(key);
		Set <URI> uriSet = new HashSet<>();
		for(DocumentImpl doc : deletedDocsSet){
			GenericCommand<URI> genericCommand = createDeleteAllGenericCommand(doc.getKey() , doc);
			commandSet.addCommand(genericCommand);
			//Get URIs of deleted Docs
			uriSet.add(doc.getKey());
			//Obliterate Doc from Hashtable (NOT SURE ABOUT COMMANDS HERE)
			store.put(doc.getKey() , null);
			//Obliterate doc from rest of trie
			deleteFromEntireTrie(doc);

		}
		commandStack.push(commandSet);
		return uriSet;
	}

	private GenericCommand<URI>createDeleteAllGenericCommand(URI uri , DocumentImpl document){
		Function<URI , Boolean> undoFunction = (URI uriForLambda) -> {
			store.put(uriForLambda, document);
			putIntoTrie(document);
			return true;
		};
		GenericCommand<URI> command = new GenericCommand<>(uri , undoFunction);
		return command;
	}

	/*
	1) use trie.deletePrefix() to delete and get the set of Docs just deleted
	2) Now you have a set of the documents you want to obliterate from the rest of the trie
	3) Iterate through the words of all the docs and use trie.delete()
	 */
	@Override
	public Set<URI> deleteAllWithPrefix(String prefix) {
		if(prefix == null){
			Set<URI> empty = new HashSet<>();
			return empty;
		}
		CommandSet commandSet = new CommandSet(); //Create CommandSet
		prefix = prefix.toUpperCase();
		//Get deleted docs
		Set <DocumentImpl> deletedPrefixDocsSet = trie.deleteAllWithPrefix(prefix);
		Set <URI> uriSet = new HashSet<>();
		//Iterate over set of deleted Docs
		for(DocumentImpl doc : deletedPrefixDocsSet){
			GenericCommand<URI> genericCommand = createDeleteAllGenericCommand(doc.getKey() , doc);
			commandSet.addCommand(genericCommand);
			//Get URIs of deleted Docs
			uriSet.add(doc.getKey());
			//Obliterate Doc from Hashtable (NOT SURE ABOUT COMMANDS HERE)
			store.put(doc.getKey() , null);
			//Obliterate doc from rest of trie
			deleteFromEntireTrie(doc);
			/*String textArray[]= doc.getDocumentAsTxt().split(" ");
			for(String key : textArray){
				trie.delete(key , doc);
			}*/
		}
		commandStack.push(commandSet);
		return uriSet;

	}

}
