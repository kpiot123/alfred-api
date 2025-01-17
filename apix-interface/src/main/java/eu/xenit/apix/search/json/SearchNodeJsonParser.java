package eu.xenit.apix.search.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import eu.xenit.apix.search.nodes.InvertSearchNode;
import eu.xenit.apix.search.nodes.OperatorSearchNode;
import eu.xenit.apix.search.nodes.PropertySearchNode;
import eu.xenit.apix.search.nodes.SearchSyntaxNode;
import eu.xenit.apix.search.nodes.TermSearchNode;
import java.io.IOException;
import java.util.ArrayList;


public class SearchNodeJsonParser {

    public SearchSyntaxNode ParseJSON(String json) throws IOException {
        json = json.replaceAll("'", "\"");
        ObjectMapper mapper = getObjectMapper();
        return mapper.readValue(json, SearchSyntaxNode.class);
    }

    public ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.setSubtypeResolver(new CustomSubtypeResolver());

        // TODO: improve Type configuration location, use annotation in nodes?

        for (String term : newArrayList("type", "aspect", "noderef", "path", "text", "parent", "category", "all")) {
            mapper.registerSubtypes(new NamedType(TermSearchNode.class, term));
        }
        for (String operand : newArrayList("and", "or")) {
            mapper.registerSubtypes(new NamedType(OperatorSearchNode.class, operand));
        }
        mapper.registerSubtypes(new NamedType(PropertySearchNode.class, "property"));
        mapper.registerSubtypes(new NamedType(InvertSearchNode.class, "not"));
        return mapper;
    }

    private ArrayList<String> newArrayList(String... elements) {
        ArrayList<String> ret = new ArrayList<String>();
        for (String el : elements) {
            ret.add(el);
        }

        return ret;
    }

    /*public SearchSyntaxNode ParseJSON(JsonNode node) {

        //TODO:
        return null;

    }*/
}
