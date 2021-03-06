package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.stage5.Document;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DocumentImpl implements Document {
	private int textHashCode;
	private URI uri;
	private String text;
	private byte[] pdfBytes;
	private Map<String , Integer> wordCountMap = new HashMap<>();
	private long lastTimeUsed;
	private Boolean justDeserialized = false;
	//private int memoryUsage = text.getBytes().length + pdfBytes.length;

	//Constructor for TXT
	public DocumentImpl(URI uri, String text, int textHashCode){
		if(uri == null || text == null){
			throw new IllegalArgumentException("URI or text cannot be null");
		}
		this.uri = uri;
		this.text = text;
		this.textHashCode = textHashCode;
		this.wordCountMap = createHashMap(this.text);
		this.lastTimeUsed = System.nanoTime();
		this.pdfBytes = createPDFBytes(this.text);
		this.justDeserialized = false;

	}

	//Constructor for PDF
	public DocumentImpl(URI uri, String text, int textHashCode, byte[] pdfBytes){
		if(uri == null || text == null || pdfBytes == null){
			throw new IllegalArgumentException("URI or text or pdf bytes cannot be null");
		}
		this.uri = uri;
		this.text = text;
		this.textHashCode = textHashCode;
		this.pdfBytes = pdfBytes;
		this.wordCountMap = createHashMap(this.text);
	}

	private Map<String, Integer> createHashMap(String text){
		text = text.replaceAll("[^a-zA-Z0-9 ]", "");
		text = text.toUpperCase();
		Map<String , Integer> map = new HashMap<>();
		String textArray[]= text.split(" ");
		for(String word : textArray){
			if(!map.containsKey(word)){
				map.put(word ,1);
			}
			else{
				int prevCount = map.get(word);
				map.put(word , prevCount + 1);
			}
		}
		return map;
	}

	@Override
	public byte[] getDocumentAsPdf() {
		return pdfBytes;
	}

	@Override
	public String getDocumentAsTxt() {
		return text.trim();
	}//trimmed; check if other things need trimming

	@Override
	public int getDocumentTextHashCode() {
		return textHashCode;
	}

	@Override
	public URI getKey() {
		return uri;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DocumentImpl document = (DocumentImpl) o;
		return textHashCode == document.textHashCode &&
				uri.equals(document.uri);
	}

	@Override
	public int hashCode() {
		return Objects.hash(textHashCode, uri);
	}



	@Override
	public int wordCount(String word) {
		word = word.toUpperCase();
		if(!wordCountMap.containsKey(word)){
			return 0;
		}
		return wordCountMap.get(word);
	}

	@Override
	public long getLastUseTime() {
		return lastTimeUsed;
	}

	@Override
	public void setLastUseTime(long timeInMilliseconds) {
		this.lastTimeUsed = timeInMilliseconds;
	}

	@Override
	public Map<String, Integer> getWordMap() {
		return wordCountMap;
	}

	@Override
	public void setWordMap(Map<String, Integer> wordMap) {
		this.wordCountMap = wordMap;
	}

	@Override
	public int compareTo(Document o) {

		return Long.compare(this.lastTimeUsed , o.getLastUseTime());

		/*if(this.lastTimeUsed < o.getLastUseTime()){
			return -1;
		}else if(o.getLastUseTime() == this.lastTimeUsed){
			return 0;
		}
		return 1;*/


	}

	private byte[] createPDFBytes(String text){
		byte[] byteArray = null;
		try (PDDocument doc = new PDDocument()) {
			PDPage page = new PDPage();
			doc.addPage(page);
			PDFont font = PDType1Font.HELVETICA_BOLD;//changed font
			try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
				contents.beginText();
				contents.setFont(font, 12);
				contents.newLineAtOffset(100, 700);
				contents.showText(this.text);
				contents.endText();
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			doc.save(out);
			doc.close();

			byteArray = out.toByteArray();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return byteArray;

	}

	protected void setJustDeserialized(Boolean b){
		this.justDeserialized = b;
	}
	protected Boolean getJustDeserialized(){
		return this.justDeserialized;
	}
}
