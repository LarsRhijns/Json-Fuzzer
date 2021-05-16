package edu.ucla.cs.jqf.bigfuzz.JSONDataGen.Generators;

import org.everit.json.schema.Schema;

public class EnumGenerator extends JsonValueGenerator<Object> {

    public EnumGenerator(Schema schema) {
        super(schema);
    }

    @Override
    public Object generate() {
        // TODO Parse properties and return any option
        return null;
    }
}
