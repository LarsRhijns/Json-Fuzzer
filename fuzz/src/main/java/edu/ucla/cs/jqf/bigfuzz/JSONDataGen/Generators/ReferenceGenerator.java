package edu.ucla.cs.jqf.bigfuzz.JSONDataGen.Generators;

import org.everit.json.schema.Schema;

public class ReferenceGenerator extends JsonValueGenerator<Object>  {

    public ReferenceGenerator(Schema schema) {
        super(schema);
    }

    @Override
    public Object generate() {
        // TODO Parse properties and generate valid object
        return null;
    }
}
