package edu.ucla.cs.jqf.bigfuzz.JSONDataGen.Generators;

import org.everit.json.schema.Schema;
import org.json.JSONObject;

public class ObjectGenerator extends JsonValueGenerator<JSONObject> {

    public ObjectGenerator(Schema schema) {
        super(schema);
    }

    @Override
    public JSONObject generate() {
        // TODO Parse properties and return random valid option
        return null;
    }
}
