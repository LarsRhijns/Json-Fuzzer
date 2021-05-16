package edu.ucla.cs.jqf.bigfuzz.JSONDataGen.Generators;

import org.everit.json.schema.Schema;

public class StringGenerator extends JsonValueGenerator<String> {

    public StringGenerator(Schema schema) {
        super(schema);
    }

    @Override
    public String generate() {
        // TODO Parse properties and return valid option
        return "Hello World!";
    }
}
