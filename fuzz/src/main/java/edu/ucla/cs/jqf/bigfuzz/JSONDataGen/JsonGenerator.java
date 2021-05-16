package edu.ucla.cs.jqf.bigfuzz.JSONDataGen;

public class JsonGenerator {

    private GeneratorConfig generatorConfig;

    public JsonGenerator(GeneratorConfig generatorConfig) {
        this.generatorConfig = generatorConfig;
    }

    public String generate() {
        return GeneratorFactory.getGenerator(generatorConfig.schema).generate().toString();
    }
}
