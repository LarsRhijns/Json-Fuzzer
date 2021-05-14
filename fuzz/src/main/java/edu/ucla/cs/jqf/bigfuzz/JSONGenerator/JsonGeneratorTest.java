package edu.ucla.cs.jqf.bigfuzz.JSONGenerator;

import com.smartentities.json.generator.GeneratorConfig;
import com.smartentities.json.generator.JsonGenerator;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;

public class JsonGeneratorTest {

    public static void main(String[] args) {
        String ResourcePath = "fuzz/src/main/resources";

        try {
            String schemaPath = ResourcePath.concat("/json-files/schema1.json");

            GeneratorConfig generatorConfig= GeneratorConfig.fromSchemaPath(schemaPath);

            JsonGenerator jsonGenerator = new JsonGenerator(generatorConfig);

            String json = jsonGenerator.generate();
            System.out.println(json);


            //Validate generated message
            JSONArray jsonSubject = new JSONArray(new JSONTokener(new ByteArrayInputStream(json.getBytes(Charset.defaultCharset()))));

            Schema schema = SchemaLoader.load(generatorConfig.getJsonSchema());
            schema.validate(jsonSubject);


        } catch (JSONException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
