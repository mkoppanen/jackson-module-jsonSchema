package com.fasterxml.jackson.module.jsonSchema;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestUnwrapping extends SchemaTestBase
{
    interface InViewMarker {}

    static class IdType {
        public int id;
    }

    static class UnwrapWithView {

        @JsonView({ InViewMarker.class })
        @JsonUnwrapped
        public IdType id;
    }



    static class UnwrappingRoot
    {
        public int age;

        @JsonUnwrapped(prefix="name.")
        public Name name;
    }

    static class Name {
        public String first, last;
    }

    /*
    /**********************************************************
    /* Unit tests, success
    /**********************************************************
     */
    
    private final ObjectMapper MAPPER = objectMapper();

    private final String EXP = "{'type':'object'," +
        "'id':'urn:jsonschema:com:fasterxml:jackson:module:jsonSchema:TestUnwrapping:UnwrappingRoot'," +
        "'properties':{'age':{'type':'integer'},'name.first':{'type':'string'},'name.last':{'type':'string'}}}";

    public void testUnwrapping()  throws Exception
    {
        JsonSchemaGenerator generator = new JsonSchemaGenerator(MAPPER);
        JsonSchema schema = generator.generateSchema(UnwrappingRoot.class);

        String json = MAPPER.writeValueAsString(schema).replace('"', '\'');
        
//System.err.println("JSON -> "+MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(schema));
        assertEquals(EXP, json);
    }

    public void testUnwrappingWithJsonView()  throws Exception
    {
        ObjectMapper mm = new ObjectMapper();
        mm.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
        mm.setConfig(
                mm.getSerializationConfig().withView(InViewMarker.class)
        );

        JsonSchemaGenerator withViewGenerator = new JsonSchemaGenerator(mm);
        JsonSchema schemaWithView = withViewGenerator.generateSchema(UnwrapWithView.class);

        String jsonWithView = MAPPER.writeValueAsString(schemaWithView).replace('"', '\'');

        JsonSchemaGenerator generator = new JsonSchemaGenerator(MAPPER);
        JsonSchema schema = generator.generateSchema(UnwrapWithView.class);

        String json = MAPPER.writeValueAsString(schema).replace('"', '\'');

//System.err.println("JSON -> "+MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(schema));
        assertEquals(json, jsonWithView);
    }
}
