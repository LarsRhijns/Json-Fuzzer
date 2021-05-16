package edu.ucla.cs.jqf.bigfuzz.JSONDataGen.Generators;

import org.everit.json.schema.Schema;
import org.json.JSONArray;

import java.lang.reflect.Array;

public class ArrayGenerator extends JsonValueGenerator<JSONArray> {

    public ArrayGenerator(Schema schema) {
        super(schema);
    }

    @Override
    public JSONArray generate() {
        // TODO Parse properties of the array
        return new JSONArray();
    }
}
