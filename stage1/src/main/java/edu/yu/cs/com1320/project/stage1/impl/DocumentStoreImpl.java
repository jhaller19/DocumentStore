package edu.yu.cs.com1320.project.stage1.impl;

import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.stage1.Document;
import edu.yu.cs.com1320.project.stage1.DocumentStore;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

public class DocumentStoreImpl implements DocumentStore {

    private HashTableImpl<URI , DocumentImpl> store = new HashTableImpl<>();

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
                deleteDocument(uri);
                return documentTextHashcode; //NOT SURE
            }
            //If uri doesnt exist return 0
            else{
                return 0;
            }
        }

        //(1)
        byte[] byteArray;
        byteArray = toByteArray(input);

        if(format == DocumentFormat.TXT){
            //2a
            String txtAsString = new String(byteArray);
            //3a
            int txtStringHashCode = txtAsString.hashCode();
            //4a
            if(store.get(uri) != null && store.get(uri).getDocumentTextHashCode() == txtStringHashCode){
                return txtStringHashCode;//NOT SURE IF RIGHT>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            }
            //5a
            DocumentImpl document = new DocumentImpl(uri , txtAsString, txtStringHashCode);
            if(store.get(uri) == null){
                store.put(uri , document);
                return 0;
            }else{
                int oldHashCode = store.get(uri).getDocumentTextHashCode();
                store.put(uri , document);
                return oldHashCode;
            }
        }else{
            //2b
            String pdfAsString = null;
            pdfAsString = pdfToString(byteArray);
            //3b
            int pdfStringHashCode = pdfAsString.hashCode();
            //4a
            if (store.get(uri) != null && store.get(uri).getDocumentTextHashCode() == pdfStringHashCode){
                return pdfStringHashCode;
            }
            //5b
            DocumentImpl document = new DocumentImpl( uri,  pdfAsString, pdfStringHashCode, byteArray);
            if(store.get(uri) == null){
                store.put(uri , document);
                return 0;
            }else{
                int oldHashCode = store.get(uri).getDocumentTextHashCode();
                store.put(uri , document);
                return oldHashCode;
            }
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
            return false;
        }
        //If document exists
        store.put(uri,null);
        return true;
    }

    /*public static void main(String[] args) throws URISyntaxException {
        DocumentStoreImpl store = new DocumentStoreImpl();
        String text = "hello";
        InputStream input = new ByteArrayInputStream(text.getBytes());
        URI uri = null;
        uri = new URI("hi");
        String text2 = "hi";
        InputStream input2 = new ByteArrayInputStream(text2.getBytes());
        System.out.println(store.putDocument(input , uri , DocumentFormat.TXT));
        System.out.println(text.hashCode());
        System.out.println(store.putDocument(input2 , uri , DocumentFormat.TXT));
        System.out.println(text2.hashCode());




    }*/


}
