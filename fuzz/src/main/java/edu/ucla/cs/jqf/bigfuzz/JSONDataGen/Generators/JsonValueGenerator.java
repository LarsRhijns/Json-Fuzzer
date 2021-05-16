package edu.ucla.cs.jqf.bigfuzz.JSONDataGen.Generators;

import java.util.Random;

import org.everit.json.schema.Schema;

public abstract class JsonValueGenerator<T> {

    protected Schema schema;

    Random random = new Random();
    public JsonValueGenerator(Schema schema) {
        this.schema = schema;
    }

    public abstract T generate();
}
