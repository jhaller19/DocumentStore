package edu.yu.cs.com1320.project.stage4.impl;

import edu.yu.cs.com1320.project.*;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage4.Document;
import edu.yu.cs.com1320.project.stage4.DocumentStore;
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
	private TrieImpl<DocumentImpl> trie = new TrieImpl<>();
	private MinHeapImpl<DocumentImpl> heap = new MinHeapImpl<>() ;
	private int maxDocumentCount;
	private int maxDocumentBytes;
	//
	private int bytesUsed;
	private int documentsUsed;


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
				addCommandRegularDelete(uri , store.get(uri));
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
			store.get(uri).setLastUseTime(System.nanoTime());
			heap.reHeapify(store.get(uri));
			addNoOpCommand(uri);
			return txtStringHashCode;//NOT SURE
		}
		//Create Document obj and either (a) insert the new Doc (b) replace old Doc with same URI (but dif Hashcode)
		DocumentImpl document = new DocumentImpl(uri , txtAsString, txtStringHashCode);
		if(store.get(uri) == null){
			memoryCheckAndInsert(document);
			store.put(uri , document);
			putIntoTrie(document);
			addCommandRegularPut(uri , document);
			return 0;
		}else{ //If same URI but different text, replace the text
			int oldHashCode = store.get(uri).getDocumentTextHashCode();
			addCommandReplace( uri , store.get(uri) , document , store.get(uri).getDocumentAsTxt(), txtAsString); //CHECK <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
			replaceInTrie(txtAsString , store.get(uri).getDocumentAsTxt() , store.get(uri) , document);
			replaceInHeap(store.get(uri) , document);
			document.setLastUseTime(System.nanoTime()); // Set 2
			store.put(uri , document);
			return oldHashCode;
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
			store.get(uri).setLastUseTime(System.nanoTime());
			heap.reHeapify(store.get(uri));
			addNoOpCommand(uri);
			return pdfStringHashCode;
		}
		//Create Document obj and either (a) insert the new Doc (b) replace old Doc with same URI (but dif Hashcode)
		DocumentImpl document = new DocumentImpl( uri,  pdfAsString, pdfStringHashCode, byteArray);
		if(store.get(uri) == null){
			memoryCheckAndInsert(document);
			store.put(uri , document);
			putIntoTrie(document);
			addCommandRegularPut(uri , document);
			return 0;
		}else{
			int oldHashCode = store.get(uri).getDocumentTextHashCode();
			addCommandReplace( uri , store.get(uri) , document , store.get(uri).getDocumentAsTxt(), pdfAsString); //CHECK <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
			replaceInTrie(pdfAsString , store.get(uri).getDocumentAsTxt() , store.get(uri) , document);
			replaceInHeap(store.get(uri) , document);
			store.put(uri , document);
			return oldHashCode;
		}
	}

	private void memoryCheckAndInsert(DocumentImpl doc){ 															//Boolean
		int currentDocBytes = doc.getDocumentAsTxt().getBytes().length + doc.getDocumentAsPdf().length;
		if(maxDocumentCount != 0){
			while(documentsUsed + 1 > maxDocumentCount){
				deleteUntilFree();
			}
		}
		if(maxDocumentBytes != 0){
			while(bytesUsed + currentDocBytes > maxDocumentBytes){
				/*if(currentDocBytes > maxDocumentBytes){
					break;
				}*/
				deleteUntilFree();
			}
		}
		doc.setLastUseTime(System.nanoTime());
		heap.insert(doc);
		documentsUsed++;
		bytesUsed += doc.getDocumentAsTxt().getBytes().length + doc.getDocumentAsPdf().length;
	}

	private void deleteUntilFree(){
		DocumentImpl deletedDoc = heap.removeMin();		//(1)
		store.put(deletedDoc.getKey() , null);		//(2)
		deleteFromUndoStack(deletedDoc);				//(3)
		deleteFromEntireTrie(deletedDoc);				//(4)
		int byteSize = deletedDoc.getDocumentAsTxt().getBytes().length + deletedDoc.getDocumentAsPdf().length;
		bytesUsed -= byteSize;
		documentsUsed--;
	}

	private void deleteFromUndoStack(DocumentImpl doc){
		Undoable command = commandStack.pop();
		Boolean successfulDelete = false;
		while(command!=null) {
			if (command instanceof GenericCommand) {
				successfulDelete = genericCommandDeleteURI((GenericCommand<URI>) command, doc.getKey());
				//If no match, place onto helperstack and pop next
				if (successfulDelete == false) {
					helperStack.push(command);
				}
				command = commandStack.pop();
			} else {
				successfulDelete = commandSetDeleteURI((CommandSet<URI>) command, doc.getKey());
				//If no match, place onto helperstack and pop next
				if (successfulDelete == false) {
					helperStack.push(command);
				}
				command = commandStack.pop();
			}
		}
		Undoable helperCommand = helperStack.pop();
		while(helperCommand != null){
			commandStack.push(helperCommand);
			helperCommand = helperStack.pop();

		}
	}

	private Boolean genericCommandDeleteURI(GenericCommand<URI> command , URI uri){
		if(command.getTarget().equals(uri)){
			return true;
		}
		return false;
	}
	private Boolean commandSetDeleteURI(CommandSet<URI> commandSet , URI uri){
		if(commandSet.containsTarget(uri)){
			//FIXXXXXX
			Iterator<GenericCommand<URI>> set = commandSet.iterator();
			while(set.hasNext()){
				GenericCommand genericCommand = set.next();
				if(genericCommand.getTarget().equals(uri)){
					set.remove();
				}
			}
			//If commandSet is not empty, push the command back onto stack
			if(commandSet.size() != 0){
				commandStack.push(commandSet);
			}
			return true;
		}
		return false;
	}

	private void replaceInHeap(DocumentImpl oldDoc , DocumentImpl newDoc){
		bytesUsed -= oldDoc.getDocumentAsTxt().length() + oldDoc.getDocumentAsPdf().length;
		documentsUsed--;
		memoryCheckAndInsert(newDoc);
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
			store.get(uri).setLastUseTime(System.nanoTime()); //Set 5
			heap.reHeapify(store.get(uri));
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
			store.get(uri).setLastUseTime(System.nanoTime()); //Set 6
			heap.reHeapify(store.get(uri));
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
		deleteFromHeap(store.get(uri));
		deleteFromEntireTrie(store.get(uri));
		store.put(uri,null);
		return true;
	}

	private void deleteFromHeap(DocumentImpl doc){
		doc.setLastUseTime(Long.MIN_VALUE);
		heap.reHeapify(doc);
		heap.removeMin();
		int byteSize = doc.getDocumentAsTxt().getBytes().length + doc.getDocumentAsPdf().length;
		bytesUsed -= byteSize;
		documentsUsed--;
	}

	private void addCommandRegularPut(URI uri , DocumentImpl document) {
		Function<URI , Boolean> undoFunction = (URI uriForLambda) -> {
			deleteFromHeap(document);
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
			memoryCheckAndInsert(document);
			store.put(uriForLambda, document);
			putIntoTrie(document);
			return true;
		};
		GenericCommand<URI> command = new GenericCommand<>(uri , undoFunction);
		commandStack.push(command);
	}
	private void addCommandReplace(URI uri , DocumentImpl oldDocument , DocumentImpl newDocument , String newText , String oldText) {
		Function<URI , Boolean> undoFunction = (URI uriForLambda) -> {
			replaceInHeap(oldDocument , newDocument);
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

	private Boolean genericCommandUndoURI(GenericCommand<URI> command , URI uri){
		if(command.getTarget().equals(uri)){
			command.undo();
			return true;
		}
		return false;
	}
	private Boolean commandSetUndoURI(CommandSet<URI> command , URI uri){
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
				successfulUndo = genericCommandUndoURI((GenericCommand<URI>) command, uri);
				//If no match, place onto helperstack and pop next
				if (successfulUndo == false) {
					helperStack.push(command);
					command = commandStack.pop();
				}
			} else {
				successfulUndo = commandSetUndoURI((CommandSet<URI>) command, uri);
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
		/*
		Set Document Usage NOT SURE
		*/
		return store.get(uri);
	} //MAKE SURE THIS CAN BE HERE

	//////NEW///////
	@Override
	public List<String> search(String keyword) {

		List<DocumentImpl> listOfDocs = trie.getAllSorted(keyword , new sortDocs(keyword));
		List<String> listOfDocTexts = new ArrayList<>();
		Long time = System.nanoTime();
		for(DocumentImpl doc : listOfDocs){
			doc.setLastUseTime(time); //Set 7
			heap.reHeapify(doc);
			listOfDocTexts.add(doc.getDocumentAsTxt());
		}
		return listOfDocTexts;
	}

	@Override
	public List<byte[]> searchPDFs(String keyword) {
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
		Long time = System.nanoTime();
		for(DocumentImpl doc : listOfDocs){
			doc.setLastUseTime(time); //Set 8
			heap.reHeapify(doc);
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
		List<DocumentImpl> listOfDocs = trie.getAllWithPrefixSorted(prefix , new sortPrefix(prefix)); //Fill IN
		List<String> listOfDocTexts = new ArrayList<>();
		Long time = System.nanoTime();
		for(DocumentImpl doc : listOfDocs){
			doc.setLastUseTime(time); //Set 9
			heap.reHeapify(doc);
			listOfDocTexts.add(doc.getDocumentAsTxt());
		}
		return listOfDocTexts;
	}

	@Override
	public List<byte[]> searchPDFsByPrefix(String prefix) {
		List<DocumentImpl> listOfDocs = trie.getAllWithPrefixSorted(prefix , new sortPrefix(prefix)); //Fill in
		List<byte []> listOfPDFs = new ArrayList<>();
		Long time = System.nanoTime();
		for(DocumentImpl doc : listOfDocs){
			doc.setLastUseTime(time); //Set 10
			heap.reHeapify(doc);
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
		CommandSet<URI> commandSet = new CommandSet<>(); //Create CommandSet
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
			//Obliterate from heap
			deleteFromHeap(doc);

		}
		commandStack.push(commandSet);
		return uriSet;
	}

	private GenericCommand<URI>createDeleteAllGenericCommand(URI uri , DocumentImpl document){
		Function<URI , Boolean> undoFunction = (URI uriForLambda) -> {
			memoryCheckAndInsert(document);
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
		CommandSet<URI> commandSet = new CommandSet<>(); //Create CommandSet
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
			//Obliterate from heap
			deleteFromHeap(doc);
		}
		commandStack.push(commandSet);
		return uriSet;

	}

	@Override
	public void setMaxDocumentCount(int limit) {
		this.maxDocumentCount = limit;
		if(documentsUsed > maxDocumentCount){
			while(documentsUsed > maxDocumentCount){
				deleteUntilFree();
			}
		}
	}

	@Override
	public void setMaxDocumentBytes(int limit) {
		this.maxDocumentBytes = limit;
		if(bytesUsed > maxDocumentBytes){
			while(bytesUsed > maxDocumentBytes){
				deleteUntilFree();
			}
		}
	}




}
