package org.outerj.daisy.diff.tag;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.AttributesImpl;

public class TagSaxDiffOutputTest {

    @Rule
    public final MockitoRule mockito = MockitoJUnit.rule();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private ContentHandler consumer;

    @Test
    public void shouldLinkChanges() throws Exception {

        final TagSaxDiffOutput o = new TagSaxDiffOutput( consumer );
        o.addClearPart( "1c" );
        o.addAddedPart( "2a" );
        o.addClearPart( "3c" );
        o.addRemovedPart( "4r" );
        o.addClearPart( "5c" );
        o.linkOperations();

        assertEquals( "4r", o.results.get(1).next.text );
        assertEquals( "2a", o.results.get(3).prev.text );
    }

    @Test
    public void shouldAcceptEmptyDoc() throws Exception {

        final TagSaxDiffOutput o = new TagSaxDiffOutput( consumer );
        o.linkOperations();

        assertEquals( 0, o.results.size() );
    }

    @Test
    public void shouldAcceptMinimalDoc() throws Exception {

        final TagSaxDiffOutput o = new TagSaxDiffOutput( consumer );
        o.addAddedPart( "1a" );
        o.addRemovedPart( "2r" );
        o.linkOperations();

        assertEquals( "2r", o.results.get(0).next.text );
        assertEquals( "1a", o.results.get(1).prev.text );
    }

    @Test
    public void shouldDetectInvalidOp() throws Exception {
        thrown.expect(RuntimeException.class);

        final TagSaxDiffOutput o = new TagSaxDiffOutput( consumer );
        o.doOp( new TagSaxDiffOutput.TextOperation( null, null, 0 ) );
    }

    @Test
    public void shouldProduceExpectedXml() throws Exception {

        final SAXTransformerFactory tf = (SAXTransformerFactory) TransformerFactory.newInstance();
        final TransformerHandler content = tf.newTransformerHandler();
        final ByteArrayOutputStream buf = new ByteArrayOutputStream();
        content.setResult(new StreamResult( buf ));
        content.startElement("", "unit-test", "unit-test", new AttributesImpl());

        final TagSaxDiffOutput o = new TagSaxDiffOutput( content );
        o.newline();
        o.addClearPart( "<with tag>" );

        o.addRemovedPart( "1r" );
        o.addAddedPart( "2a" );
        o.addClearPart( "3c" );

        o.flush();

        content.endElement("", "unit-test", "unit-test");
        content.endDocument();

        final String str = new String( buf.toByteArray(), StandardCharsets.UTF_8 );
        System.out.println( str );
        final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( new ByteArrayInputStream( buf.toByteArray() ) );

        final XPath xp = XPathFactory.newInstance().newXPath();

        System.out.println(  nodeToString(doc) );
        assertEquals( "br", findNode( xp, doc, "/unit-test/*[1]" ).getNodeName() );

        assertEquals( "diff-tag-html", findNode( xp, doc, "/unit-test/span[1]/@class" ).getTextContent() );

        assertEquals( "diff-tag-removed", findNode( xp, doc, "/unit-test/span[2]/@class" ).getTextContent() );
        assertEquals( "removed-change-1", findNode( xp, doc, "/unit-test/span[2]/@change-id" ).getTextContent() );
        assertEquals( "first-change", findNode( xp, doc, "/unit-test/span[2]/@previous" ).getTextContent() );
        assertEquals( "added1", findNode( xp, doc, "/unit-test/span[2]/@next" ).getTextContent() );

        assertEquals( "diff-tag-added", findNode( xp, doc, "/unit-test/span[3]/@class" ).getTextContent() );
        assertEquals( "added-change-1", findNode( xp, doc, "/unit-test/span[3]/@change-id" ).getTextContent() );
        assertEquals( "removed1", findNode( xp, doc, "/unit-test/span[3]/@previous" ).getTextContent() );
        assertEquals( "last-change", findNode( xp, doc, "/unit-test/span[3]/@next" ).getTextContent() );
    }

    public String nodeToString( final Node node ) {
        try {
            final Transformer transformer = TransformerFactory.newInstance().newTransformer();

            final StringWriter stw = new StringWriter();
            transformer.transform( new DOMSource( node ), new StreamResult( stw ) );
            return stw.toString();
        }
        catch( TransformerException | TransformerFactoryConfigurationError e ) {
            return "{failed to serialize node " + node + ": " + e + "}";
        }
    }

    public static Node findNode( final XPath xpath, final Document doc, final String xpathExpr ) {
        try {
            final Node node = (Node) xpath.evaluate( xpathExpr, doc, XPathConstants.NODE );
            if( node == null ) {
                System.out.println( "Failed to get node: [" + xpathExpr + "] from [" + doc + "]" );
            }
            return node;
        }
        catch( final XPathExpressionException e ) {
            throw new RuntimeException( "Failed to get node: [" + xpathExpr + "]", e );
        }
    }
}
