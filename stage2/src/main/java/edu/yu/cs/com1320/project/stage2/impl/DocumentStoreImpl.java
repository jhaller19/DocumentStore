package edu.yu.cs.com1320.project.stage2.impl;

import edu.yu.cs.com1320.project.Command;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.stage2.Document;
import edu.yu.cs.com1320.project.stage2.DocumentStore;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Function;

public class DocumentStoreImpl implements DocumentStore {

	private HashTableImpl<URI , DocumentImpl> store = new HashTableImpl<>();
	private StackImpl<Command> commandStack = new StackImpl<>(); //NEW
	private StackImpl<Command> helperStack = new StackImpl<>(); //NEW

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

		//(1)
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
			addCommandRegularPut(uri , document);
			return 0;
		}else{ //If same URI but different text, replace the text
			int oldHashCode = store.get(uri).getDocumentTextHashCode();
			addCommandRegularDelete(uri , store.get(uri));
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
			addNoOpCommand(uri);
			return pdfStringHashCode;
		}
		//Create Document obj and either (a) insert the new Doc (b) replace old Doc with same URI (but dif Hashcode)
		DocumentImpl document = new DocumentImpl( uri,  pdfAsString, pdfStringHashCode, byteArray);
		if(store.get(uri) == null){
			store.put(uri , document);
			addCommandRegularPut(uri , document);
			return 0;
		}else{
			int oldHashCode = store.get(uri).getDocumentTextHashCode();
			addCommandRegularDelete(uri , store.get(uri));
			store.put(uri , document);
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

	////////////////////////////COMMANDS/////////////////////////////////////////

	private void addCommandRegularPut(URI uri , DocumentImpl document) {
		Function<URI , Boolean> undoFunction = (URI uriForLambda) -> {
			store.put(uriForLambda, null);
			return true;
		};
		Command command = new Command(uri , undoFunction);
		commandStack.push(command);
	}

	private void addCommandRegularDelete(URI uri , DocumentImpl document) {
		Function<URI , Boolean> undoFunction = (URI uriForLambda) -> {
			store.put(uriForLambda, document);
			return true;
		};
		Command command = new Command(uri , undoFunction);
		commandStack.push(command);
	}

	private void addNoOpCommand(URI uri) {
		Function<URI , Boolean> undoFunction = (URI uriForLambda) -> {
			return true;
		};
		Command command = new Command(uri , undoFunction);
		commandStack.push(command);
	}

	/////////////////////////////////////////////////////////////////////


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
		store.put(uri,null);
		return true;
	}


	@Override
	public void undo() throws IllegalStateException {
		Command command = commandStack.pop();
		if(command == null){
			throw new IllegalStateException("Stack is empty");
		}
		command.undo();
	}

	@Override
	public void undo(URI uri) throws IllegalStateException {
		Command command = commandStack.pop();
		while(command != null && !command.getUri().equals(uri)){
			helperStack.push(command);
			command = commandStack.pop();
		}
		if(command == null){
			throw new IllegalStateException("Uri doesnt exist");
		}
		command.undo();
		Command helperCommand = helperStack.pop();
		while(helperCommand != null){
			commandStack.push(helperCommand);
			helperCommand = helperStack.pop();
		}
	}

	protected Document getDocument(URI uri){
		return store.get(uri);
	}



}
