package eu.xenit.apix.tests.search;

import eu.xenit.apix.alfresco.search.LuceneNodeVisitor;
import eu.xenit.apix.search.QueryBuilder;
import eu.xenit.apix.search.json.SearchNodeJsonParser;
import eu.xenit.apix.search.nodes.SearchSyntaxNode;
import eu.xenit.apix.search.visitors.SearchSyntaxPrinter;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LuceneNodeVisitorTest {

    private final static Logger logger = LoggerFactory.getLogger(LuceneNodeVisitorTest.class);

    private QueryBuilder builder;

    @Before
    public void Setup() {
        builder = new QueryBuilder();
    }

    @Test
    public void TestTerm() {
        java.util.List<String> terms = Arrays.asList("type", "aspect", "path", "parent", "text", "category");
        for (String t : terms) {
            builder = new QueryBuilder();
            SearchSyntaxNode node = builder.term(t, "myVal").create();
            Assert.assertEquals(t.toUpperCase() + ":\"myVal\"", toLucene(node));
        }
        builder = new QueryBuilder();
        SearchSyntaxNode node = builder.term("noderef", "workspace://SpacesStore/some-uuid").create();
        Assert.assertEquals("ID:\"workspace\\://SpacesStore/some\\-uuid\"", toLucene(node));
    }

    @Test
    public void TestTermALL() {
        builder = new QueryBuilder();
        SearchSyntaxNode node = builder.term("all", "fred").create();

        Assert.assertEquals(
                "(TEXT:\"fred\" OR @cm\\:name:\"fred\" OR @cm\\:author:\"fred\" OR @cm\\:creator:\"fred\" OR @cm\\:modifier:\"fred\")",
                toLucene(node));
    }

    @Test
    public void TestTermEscaped() {
        String t = "type";
        SearchSyntaxNode node = builder.term(t, "my\"Val").create();
        Assert.assertEquals(t.toUpperCase() + ":\"my\\\"Val\"", toLucene(node));
    }

    @Test
    public void TestProperty() {
        String t = "type";
        SearchSyntaxNode node = builder.property("cm:content", "myContent").create();
        Assert.assertEquals("@cm\\:content:\"myContent\"", toLucene(node));
    }

    @Test
    public void TestPropertyEscaped() {
        SearchSyntaxNode node = builder.property("cm:content", "my\"Content").create();
        Assert.assertEquals("@cm\\:content:\"my\\\"Content\"", toLucene(node));

    }

    @Test
    public void TestAnd() {
        SearchSyntaxNode node = builder.startAnd().term("type", "myType").term("aspect", "myAspect")
                .term("aspect", "myAspect2").end().create();
        Assert.assertEquals("(TYPE:\"myType\" AND ASPECT:\"myAspect\" AND ASPECT:\"myAspect2\")", toLucene(node));
    }

    @Test
    public void TestOr() {
        SearchSyntaxNode node = builder.startOr().term("type", "myType").term("aspect", "myAspect")
                .term("aspect", "myAspect2").end().create();
        Assert.assertEquals("(TYPE:\"myType\" OR ASPECT:\"myAspect\" OR ASPECT:\"myAspect2\")", toLucene(node));
    }

    @Test
    public void TestNot() {
        SearchSyntaxNode node = builder.not().term("type", "myType").create();
        Assert.assertEquals("-TYPE:\"myType\"", toLucene(node));
    }

    @Test
    public void TestNested() {
        SearchSyntaxNode node =
                builder.not().startAnd()
                        .not().term("aspect", "a")
                        .property("cm:content", "lala")
                        .not().property("cm:content", "bye")
                        .startOr()
                        .term("aspect", "c")
                        .end()
                        .end()
                        .create();

       /* SearchSyntaxNode node =  builder.start
        assertEquals("(TYPE:myType OR ASPECT:myAspect OR ASPECT:myAspect2)",toLucene(node));*/
    }

    @Test
    public void TestAll_And() throws IOException {
        SearchSyntaxNode node =
                builder.startAnd()
                        .term("all", "hello")
                        .term("type", "cm:content")
                        .end()
                        .create();
        //SearchNodeJsonParser parser = new SearchNodeJsonParser();
        //SearchSyntaxNode val = parser.ParseJSON("{\"not\":{\"property\":{\"name\":\"@{claims.model}duplicate\",\"value\":\"true\"}}}");

        logger.debug(toLucene(node));
        Assert.assertEquals(
                "((TEXT:\"hello\" OR @cm\\:name:\"hello\" OR @cm\\:author:\"hello\" OR @cm\\:creator:\"hello\" OR @cm\\:modifier:\"hello\") AND TYPE:\"cm\\:content\")",
                toLucene(node));

    }

    @Test
    public void TestTemp() throws IOException {
        SearchNodeJsonParser parser = new SearchNodeJsonParser();
        SearchSyntaxNode val = parser
                .ParseJSON("{\"not\":{\"property\":{\"name\":\"@{claims.model}duplicate\",\"value\":\"true\"}}}");

        logger.debug(toLucene(val));
    }


    private String toLucene(SearchSyntaxNode node) {
        logger.debug(SearchSyntaxPrinter.Print(node));
        LuceneNodeVisitor visitor = new LuceneNodeVisitor();
        String ret = visitor.visit(node);

        logger.debug(ret);
        return ret;
    }
}
