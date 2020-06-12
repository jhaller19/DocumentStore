package edu.yu.cs.com1320.project.stage5.impl;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * created by the document store and given to the BTree via a call to BTree.setPersistenceManager
 */
public class DocumentPersistenceManager implements PersistenceManager<URI, Document> {

    private File baseDir;

    public DocumentPersistenceManager(File baseDir){
       if(baseDir == null){
          this.baseDir = new File(System.getProperty("user.dir"));
       }else{
           if(!baseDir.exists()){
               baseDir.mkdirs();
           }
           this.baseDir = baseDir;
       }
    }

    @Override
    public void serialize(URI uri, Document val) throws IOException {
        Gson gson = new GsonBuilder().registerTypeAdapter(DocumentImpl.class, new DocumentSerializer()).setPrettyPrinting().create();
        FileWriter file = null;
        try {
            File file1 = new File(baseDir.getPath()+ File.separator + uri.getHost() + uri.getPath().replace("/", File.separator) + ".json");
            file1.getParentFile().mkdirs();
            file = new FileWriter(file1);
            //file = new FileWriter("C:\\Users\\jhall\\Desktop\\Doc.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            file.write(gson.toJson(val));
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public Document deserialize(URI uri) throws IOException {
       File check = new File (baseDir.getPath() + File.separator + uri.getHost() + uri.getPath().replace("/", File.separator) + ".json");
       if(!check.exists()){
           return null;
       }

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(baseDir.getPath() + File.separator + uri.getHost() + uri.getPath().replace("/", File.separator) + ".json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Gson gson = new GsonBuilder().registerTypeAdapter(DocumentImpl.class, new DocumentDeserializer()).create();
        DocumentImpl doc = gson.fromJson(br , DocumentImpl.class);
        doc.setJustDeserialized(true);

        br.close();
        //delete the file from disk
        deleteFileAndEmptyDirs(check);



        return doc;
    }

    private void deleteFileAndEmptyDirs(File file){
        if(file.getPath().endsWith(baseDir.getPath())) {
            return;
        }
        if (file.isFile()) {
            file.delete();
        } else if(file.isDirectory()) {
            file.delete();
        }
        deleteFileAndEmptyDirs(file.getParentFile());
    }




    private class DocumentSerializer implements JsonSerializer<Document>{

        @Override
        public JsonElement serialize(Document document, Type type, JsonSerializationContext jsonSerializationContext) {

            JsonObject object = new JsonObject();
            //
            String text = document.getDocumentAsTxt();
            URI uri = document.getKey();
            int hashCode = document.getDocumentTextHashCode();
            Map<String, Integer> wordCountMap = document.getWordMap();
            //
            Gson gson = new Gson();
            JsonElement jsonElement = gson.toJsonTree(uri);
            JsonElement jsonElement2 = gson.toJsonTree(wordCountMap);
            //
            object.addProperty("text", text);
            object.add("uri" , jsonElement);
            object.addProperty("hashCode", hashCode);
            object.add("map", jsonElement2);
            //
            return object;
        }
    }


    public class DocumentDeserializer implements JsonDeserializer<Document> {

        @Override
        public Document deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Gson gson = new Gson();

            String text = json.getAsJsonObject().get("text").getAsString();
            int hashCode = json.getAsJsonObject().get("hashCode").getAsInt();

            URI uri = null;
            Map<String, Integer> map = null;

            uri = gson.fromJson(json.getAsJsonObject().get("uri") , URI.class);

            Type mapType = new TypeToken<Map<String , Integer>>() {
            }.getType();
            map = gson.fromJson(json.getAsJsonObject().get("map"), mapType);


            DocumentImpl doc = new DocumentImpl(uri, text, hashCode);
            doc.setWordMap(map);

            return doc;
        }

    }
    
}
