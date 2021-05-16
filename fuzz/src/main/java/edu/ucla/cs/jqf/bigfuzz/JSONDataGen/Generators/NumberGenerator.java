package edu.ucla.cs.jqf.bigfuzz.JSONDataGen.Generators;

import org.everit.json.schema.Schema;

public class NumberGenerator extends JsonValueGenerator<Number> {

    public NumberGenerator(Schema schema) {
        super(schema);
    }

    @Override
    public Number generate() {
        // TODO Parse properties and return random option
        return 0;
    }
}
