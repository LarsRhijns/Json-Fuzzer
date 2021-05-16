package edu.ucla.cs.jqf.bigfuzz.JSONDataGen;

import com.smartentities.json.generator.generators.*;
import edu.ucla.cs.jqf.bigfuzz.JSONDataGen.Generators.ArrayGenerator;
import edu.ucla.cs.jqf.bigfuzz.JSONDataGen.Generators.EnumGenerator;
import edu.ucla.cs.jqf.bigfuzz.JSONDataGen.Generators.ReferenceGenerator;
import org.everit.json.schema.*;

public class GeneratorFactory {

    public static edu.ucla.cs.jqf.bigfuzz.JSONDataGen.Generators.JsonValueGenerator<?> getGenerator(Schema schema) {

        edu.ucla.cs.jqf.bigfuzz.JSONDataGen.Generators.JsonValueGenerator<?> jsonValueGenerator = null;

        if (schema instanceof StringSchema) {
            jsonValueGenerator = new edu.ucla.cs.jqf.bigfuzz.JSONDataGen.Generators.StringGenerator(schema);
        } else if (schema instanceof NumberSchema) {
            jsonValueGenerator = new edu.ucla.cs.jqf.bigfuzz.JSONDataGen.Generators.NumberGenerator(schema);
        } else if (schema instanceof BooleanSchema) {
            jsonValueGenerator = new edu.ucla.cs.jqf.bigfuzz.JSONDataGen.Generators.BooleanGenerator(schema);
        } else if (schema instanceof ObjectSchema) {
            jsonValueGenerator = new edu.ucla.cs.jqf.bigfuzz.JSONDataGen.Generators.ObjectGenerator(schema);
        } else if (schema instanceof ArraySchema) {
            jsonValueGenerator = new ArrayGenerator(schema);
        } else if (schema instanceof EnumSchema) {
            jsonValueGenerator = new EnumGenerator(schema);
        } else if (schema instanceof ReferenceSchema) {
            jsonValueGenerator = new ReferenceGenerator(schema);
        }

        return jsonValueGenerator;
    }
}
