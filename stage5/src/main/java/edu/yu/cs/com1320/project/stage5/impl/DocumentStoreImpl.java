package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.*;
import edu.yu.cs.com1320.project.impl.BTreeImpl;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.DocumentStore;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Function;

public class DocumentStoreImpl implements DocumentStore{


	private BTreeImpl<URI , DocumentImpl> btree = new BTreeImpl<>();	//Main storage structure
	private StackImpl<Undoable> commandStack = new StackImpl<>();		//Keeps track of actions for undo purposes
	private StackImpl<Undoable> helperStack = new StackImpl<>();		//Helper stack for undo purposes
	private TrieImpl<URI> trie = new TrieImpl<>(); 						//Allows dictionary (and prefix) lookup
	private MinHeapImpl<URIobj> heap = new MinHeapImpl<>();				//Tracks least-recently-used documents
	private int maxDocumentCount;
	private int maxDocumentBytes;
	private int bytesUsed;
	private int documentsUsed;
	private File baseDir;


	public DocumentStoreImpl(){
		this.baseDir = new File(System.getProperty("user.dir"));
		btree.setPersistenceManager((PersistenceManager) new DocumentPersistenceManager(baseDir));

		this.putSentinel();
	}

	public DocumentStoreImpl(File baseDir){
		if(baseDir == null){
			this.baseDir = new File(System.getProperty("user.dir"));
		}else{
			this.baseDir = baseDir;
		}
		btree.setPersistenceManager((PersistenceManager) new DocumentPersistenceManager(baseDir));

		this.putSentinel();
	}

	private void putSentinel(){
		try {
			btree.put(new URI("sentinel") , null);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	//Inner Class used in heap to link URIs to their lastTimeUsed without a direct reference to their corresponding documents
	private class URIobj implements Comparable{
		private URI uri;
		URIobj(URI uri){
			this.uri = uri;
		}
		public URI getURI(){
			return this.uri;
		}
		@Override
		public int compareTo(Object o) {
			URIobj urIobj = (URIobj) o;
			return Long.compare(btree.get(uri).getLastUseTime() , btree.get(urIobj.getURI()).getLastUseTime());
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			URIobj urIobj = (URIobj) o;
			return uri.equals(urIobj.uri);
		}

		@Override
		public int hashCode() {
			return Objects.hash(uri);
		}
	}


	@Override
	public int putDocument(InputStream input, URI uri, DocumentFormat format) {
		//If URI or format is null, throw IAE
		if(uri == null || format == null){
			throw new IllegalArgumentException("URI cannot be null");
		}
		//If input is null, delete
		if (input == null){
			return inputNullDeleteActions(uri);
		}

		byte[] byteArray;
		byteArray = toByteArray(input);

		if(format == DocumentFormat.TXT){
			return txtFormatOperations(uri , byteArray);
		}else{
			return pdfFormatOperations(uri, byteArray);

		}


	}

	private int inputNullDeleteActions(URI uri){
		//if uri exists int the tree, return the deleted document's text hashcode
		if(btree.get(uri) != null){
			int documentTextHashcode = btree.get(uri).getDocumentTextHashCode();
			addCommandRegularDelete(uri , btree.get(uri));
			deleteDocument(uri);
			return documentTextHashcode;
		}
		//If uri doesnt exist, return 0
		else{
			addNoOpCommand(uri);
			return 0;
		}
	}

	private void exactDocumentExistsOperations(URI uri){
		//If this Doc was just Deserialized due to this call, add it to memory
		if(btree.get(uri).getJustDeserialized()){
			justDeserializedAddMemory(uri);
		}
		//Else it was already in memory, just reheapify
		else{
			btree.get(uri).setLastUseTime(System.nanoTime());
			heap.reHeapify(new URIobj(uri));
			addNoOpCommand(uri);
		}
	}

	private void insertNewDocToStore(DocumentImpl document , URI uri){
		memoryCheckAndInsert(document);
		putIntoTrie(document);
		addCommandRegularPut(uri , document);
	}

	private int replaceDocInStore(DocumentImpl document , URI uri , String txtAsString){
		int oldHashCode = btree.get(uri).getDocumentTextHashCode();
		DocumentImpl oldDoc = btree.get(uri);
		//If URI was on disk, add to memory
		if(btree.get(uri).getJustDeserialized()){
			memoryCheckAndInsert(document);
			btree.get(uri).setJustDeserialized(false);
			addCommandReplace( uri , oldDoc , document , oldDoc.getDocumentAsTxt(), txtAsString);
			replaceInTrie(txtAsString , oldDoc.getDocumentAsTxt() , oldDoc , document);
		}
		else{
			addCommandReplace( uri , btree.get(uri) , document , btree.get(uri).getDocumentAsTxt(), txtAsString);
			replaceInTrie(txtAsString , btree.get(uri).getDocumentAsTxt() , btree.get(uri) , document);
			replaceInHeap(document , btree.get(uri) );
		}
		return oldHashCode;
	}


		private int txtFormatOperations(URI uri , byte[] byteArray){
		String txtAsString = new String(byteArray);
		int txtStringHashCode = txtAsString.hashCode();

		//Check that this exact document (same URI and Hashcode) doesnt already exist
		if(btree.get(uri) != null && btree.get(uri).getDocumentTextHashCode() == txtStringHashCode){
			exactDocumentExistsOperations(uri);
			return txtStringHashCode;
		}

		//Create Document obj and either (a) insert the new Doc (b) replace old Doc with same URI (but dif Hashcode)
		DocumentImpl document = new DocumentImpl(uri , txtAsString, txtStringHashCode);
		//This is a new doc
		if(btree.get(uri) == null){
			insertNewDocToStore(document , uri);
			return 0;
		}
		//If same URI but different text, replace the text
		else{
			return replaceDocInStore(document , uri , txtAsString);
		}
	}

	private int pdfFormatOperations(URI uri , byte[] byteArray){
		String pdfAsString = pdfToString(byteArray);
		int pdfStringHashCode = pdfAsString.hashCode();
		//Check that this exact document (same URI and Hashcode) doesnt already exist
		if(btree.get(uri) != null && btree.get(uri).getDocumentTextHashCode() == pdfStringHashCode){
			exactDocumentExistsOperations(uri);
			return pdfStringHashCode;
		}

		//Create Document obj and either (a) insert the new Doc (b) replace old Doc with same URI (but dif Hashcode)
		DocumentImpl document = new DocumentImpl(uri , pdfAsString, pdfStringHashCode);
		//This is a new doc
		if(btree.get(uri) == null){
			insertNewDocToStore(document , uri);
			return 0;
		}
		//If same URI but different text, replace the text
		else{
			return replaceDocInStore(document , uri , pdfAsString);
		}
	}


	private void memoryCheckAndInsert(DocumentImpl doc){
		int currentDocBytes = doc.getDocumentAsTxt().getBytes().length + doc.getDocumentAsPdf().length;
		//free up space if needed
		if(maxDocumentCount != 0){
			while(documentsUsed + 1 > maxDocumentCount){
				deleteUntilFree();
			}
		}
		if(maxDocumentBytes != 0){
			while(bytesUsed + currentDocBytes > maxDocumentBytes){
				deleteUntilFree();
			}
		}
		btree.put(doc.getKey() , doc);
		doc.setLastUseTime(System.nanoTime());
		heap.insert(new URIobj(doc.getKey()));
		documentsUsed++;
		bytesUsed += doc.getDocumentAsTxt().getBytes().length + doc.getDocumentAsPdf().length;
	}

	private void deleteUntilFree(){
		DocumentImpl deletedDoc = btree.get(heap.removeMin().uri);
		try {
			btree.moveToDisk(deletedDoc.getKey());
		} catch (Exception e) {
			e.printStackTrace();
		}
		int byteSize = deletedDoc.getDocumentAsTxt().getBytes().length + deletedDoc.getDocumentAsPdf().length;
		bytesUsed -= byteSize;
		documentsUsed--;
	}


	private void replaceInHeap(DocumentImpl oldDoc , DocumentImpl newDoc){
		newDoc.setLastUseTime(Long.MIN_VALUE);
		heap.reHeapify(new URIobj(newDoc.getKey()));
		heap.removeMin();
		bytesUsed -= newDoc.getDocumentAsTxt().length() + newDoc.getDocumentAsPdf().length;
		documentsUsed--;
		memoryCheckAndInsert(oldDoc);
	}

	private void putIntoTrie(DocumentImpl doc){
		String fullDocText = doc.getDocumentAsTxt();
		String array[]= fullDocText.split(" ");
		for (String word: array){
			trie.put(word, doc.getKey());
		}
	}
	private void deleteFromEntireTrie(DocumentImpl doc){
		String fullDocText = doc.getDocumentAsTxt();
		String array[]= fullDocText.split(" ");
		for (String word: array){
			trie.delete(word, doc.getKey());
		}
	}
	private void replaceInTrie(String newText , String oldText , DocumentImpl oldDoc , DocumentImpl newDoc){
		String array[]= oldText.split(" ");
		for (String word: array){
			trie.delete(word, oldDoc.getKey());
		}
		String array2[]= newText.split(" ");
		for (String word: array2){
			trie.put(word, newDoc.getKey());
		}
	}


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
		byte[] buf = new byte[1024];
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

	@Override
	public byte[] getDocumentAsPdf(URI uri) {
		//If document doesnt exist
		if(btree.get(uri) == null){
			return null;
		}
		//if document exists
		else{
			//If this will deserialize the doc, add it to memory
			if(btree.get(uri).getJustDeserialized()){
				justDeserializedAddMemory(uri);
			}
			//Else this already exist in memory, reheapify
			else{
				btree.get(uri).setLastUseTime(System.nanoTime());
				heap.reHeapify(new URIobj(uri));
			}
			return btree.get(uri).getDocumentAsPdf();
		}
	}

	@Override
	public String getDocumentAsTxt(URI uri) {
		//If document doesnt exist
		if(btree.get(uri) == null){
			return null;
		}
		//Else document exists...
		else{
			//If this his will deserialize the doc, add it to memory
			if(btree.get(uri).getJustDeserialized()){
				justDeserializedAddMemory(uri);
			}
			//Else this already exist in memory, reheapify
			else{
				btree.get(uri).setLastUseTime(System.nanoTime());
				heap.reHeapify(new URIobj(uri));
			}
			return btree.get(uri).getDocumentAsTxt();
		}
	}

	private void justDeserializedAddMemory(URI uri){
		DocumentImpl doc = btree.get(uri);
		memoryCheckAndInsert(doc);
		doc.setJustDeserialized(false);
	}


	@Override
	public boolean deleteDocument(URI uri) {
		//if document doesnt exist
		if(btree.get(uri) == null){
			addNoOpCommand(uri);
			return false;
		}
		//If document exists
		if(btree.get(uri).getJustDeserialized()){
			btree.get(uri).setJustDeserialized(false);
			btree.put(uri , null);
			return true;
		}
		addCommandRegularDelete(uri , btree.get(uri));
		deleteFromHeap(btree.get(uri));
		deleteFromEntireTrie(btree.get(uri));
		btree.put(uri,null);
		return true;
	}

	private void deleteFromHeap(DocumentImpl doc){
		doc.setLastUseTime(Long.MIN_VALUE);
		heap.reHeapify(new URIobj(doc.getKey()));
		heap.removeMin();
		int byteSize = doc.getDocumentAsTxt().getBytes().length + doc.getDocumentAsPdf().length;
		bytesUsed -= byteSize;
		documentsUsed--;
	}

	private void addCommandRegularPut(URI uri , DocumentImpl document) {
		Function<URI , Boolean> undoFunction = (URI uriForLambda) -> {
			deleteFromHeap(document);
			btree.put(uriForLambda, null);
			String [] array = document.getDocumentAsTxt().split(" ");
			for(String word : array){
				trie.delete(word , document.getKey());
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
			btree.put(uriForLambda, document);
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
			btree.put(uriForLambda, oldDocument);
			return true;
		};
		GenericCommand<URI> command = new GenericCommand<>(uri , undoFunction);
		commandStack.push(command);
	}

	@Override
	public void undo() throws IllegalStateException {
		Undoable command = commandStack.pop();
		if(command == null){
			throw new IllegalStateException("Stack is empty");
		}
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
		File file = new File(baseDir + File.separator + uri.getHost() + uri.getPath().replace("/" , File.separator) + ".json");
		//Check if doc is on disk
		if(file.exists()){
			return null;
		}
		return btree.get(uri);
	}

	@Override
	public List<String> search(String keyword) {
		List<URI> listOfURI = trie.getAllSorted(keyword , new sortDocs(keyword));
		List<String> listOfDocTexts = new ArrayList<>();
		Long time = System.nanoTime();
		for(URI uri : listOfURI){
			//If this call causes doc to be deserialized, add to memory
			if(btree.get(uri).getJustDeserialized()){
				justDeserializedAddMemory(uri);
			}
			//Else this doc already exists, reheapify it
			else {
				btree.get(uri).setLastUseTime(time);
				heap.reHeapify(new URIobj(uri));
			}
			listOfDocTexts.add(btree.get(uri).getDocumentAsTxt());
		}
		return listOfDocTexts;
	}


	@Override
	public List<byte[]> searchPDFs(String keyword) {
		List<URI> listOfURI = trie.getAllSorted(keyword , new sortDocs(keyword));
		List<byte []> listOfPDFs = new ArrayList<>();
		Long time = System.nanoTime();
		for(URI uri : listOfURI){
			//If this call causes doc to be deserialized, add to memory
			if(btree.get(uri).getJustDeserialized()){
				justDeserializedAddMemory(uri);
			}
			//Else this doc already exists, reheapify it
			else {
				btree.get(uri).setLastUseTime(time);
				heap.reHeapify(new URIobj(uri));
			}
			listOfPDFs.add(btree.get(uri).getDocumentAsPdf());
		}
		return listOfPDFs;
	}

	class sortDocs implements Comparator<URI> {
		String word;

		sortDocs(String word){
			this.word = word;
		}
		@Override
		public int compare(URI o1, URI o2) {
			word = word.toUpperCase();// NOT SURE
			if(o1.equals(o2)){
				return 0;
			}
			int o1WordCount  = btree.get(o1).wordCount(word);
			int o2WordCount  = btree.get(o2).wordCount(word);
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
		List<URI> listOfURI = trie.getAllWithPrefixSorted(prefix , new sortPrefix(prefix));
		List<String> listOfDocTexts = new ArrayList<>();
		Long time = System.nanoTime();
		for(URI uri : listOfURI){
			//If this call causes doc to be deserialized, add to memory
			if(btree.get(uri).getJustDeserialized()){
				justDeserializedAddMemory(uri);
			}
			//Else this doc already exists, reheapify it
			else {
				btree.get(uri).setLastUseTime(time);
				heap.reHeapify(new URIobj(uri));
			}
			listOfDocTexts.add(btree.get(uri).getDocumentAsTxt());
		}
		return listOfDocTexts;
	}

	@Override
	public List<byte[]> searchPDFsByPrefix(String prefix) {
		List<URI> listOfURI = trie.getAllWithPrefixSorted(prefix , new sortPrefix(prefix));
		List<byte []> listOfPDFs = new ArrayList<>();
		Long time = System.nanoTime();
		for(URI uri : listOfURI){
			//If this call causes doc to be deserialized, add to memory
			if(btree.get(uri).getJustDeserialized()){
				justDeserializedAddMemory(uri);
			}
			//Else this doc already exists, reheapify it
			else {
				btree.get(uri).setLastUseTime(time); //Set 7
				heap.reHeapify(new URIobj(uri));
			}
			listOfPDFs.add(btree.get(uri).getDocumentAsPdf());
		}
		return listOfPDFs;
	}

	class sortPrefix implements Comparator<URI> {
		String prefix;

		sortPrefix(String prefix){
			this.prefix = prefix;
		}
		@Override
		public int compare(URI o1, URI o2) {
			prefix = prefix.toUpperCase();
			int o1WordCount  = prefixCount(btree.get(o1) , prefix);
			int o2WordCount  = prefixCount(btree.get(o2) , prefix);
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
		Set <URI> deletedURISet = trie.deleteAll(key);
		for(URI uri : deletedURISet){
			GenericCommand<URI> genericCommand = createDeleteAllGenericCommand(uri , btree.get(uri));
			commandSet.addCommand(genericCommand);
			//Obliterate doc from rest of trie
			deleteFromEntireTrie(btree.get(uri));
			//Obliterate from heap
			deleteFromHeap(btree.get(uri));
			//Obliterate Doc from Hashtable (NOT SURE ABOUT COMMANDS HERE)
			btree.put(uri , null);

		}
		commandStack.push(commandSet);
		return deletedURISet;
	}

	private GenericCommand<URI>createDeleteAllGenericCommand(URI uri , DocumentImpl document){
		Function<URI , Boolean> undoFunction = (URI uriForLambda) -> {
			memoryCheckAndInsert(document);
			btree.put(uriForLambda, document);
			putIntoTrie(document);
			return true;
		};
		GenericCommand<URI> command = new GenericCommand<>(uri , undoFunction);
		return command;
	}


	@Override
	public Set<URI> deleteAllWithPrefix(String prefix) {
		CommandSet<URI> commandSet = new CommandSet<>(); //Create CommandSet
		prefix = prefix.toUpperCase();
		//Get deleted docs
		Set <URI> deletedPrefixURISet = trie.deleteAllWithPrefix(prefix);
		//Iterate over set of deleted Docs
		for(URI uri : deletedPrefixURISet){
			GenericCommand<URI> genericCommand = createDeleteAllGenericCommand(uri , btree.get(uri));
			commandSet.addCommand(genericCommand);
			//Obliterate doc from rest of trie
			deleteFromEntireTrie(btree.get(uri));
			//Obliterate from heap
			deleteFromHeap(btree.get(uri));
			//Obliterate Doc from Hashtable (NOT SURE ABOUT COMMANDS HERE)
			btree.put(uri, null);
		}
		commandStack.push(commandSet);
		return deletedPrefixURISet;

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

	protected int getBytesUsed(){
		return bytesUsed;
	}

	protected int getDocsUsed(){ return documentsUsed;}




}
