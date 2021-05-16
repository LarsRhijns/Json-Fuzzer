package edu.ucla.cs.jqf.bigfuzz.JSONDataGen.Generators;

import org.everit.json.schema.Schema;

public class BooleanGenerator extends JsonValueGenerator<Boolean> {

    public BooleanGenerator(Schema schema) {
        super(schema);
    }

    @Override
    public Boolean generate() {
        //TODO Check properties and return random option
        return true;
    }
}
