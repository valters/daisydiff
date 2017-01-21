package org.outerj.daisy.diff.tag;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.Test;
import org.outerj.daisy.diff.tag.TagTestFixture.TextOperation;

public class LawDifferTest {

    @Test
    public void shouldOptimize30() throws Exception {
        final String original = read( get( "law/30a.txt" ) );
        final String modified = read( get( "law/30b.txt" ) );

        diff( original, modified );
    }

    @Test
    public void shouldOptimize30x() throws Exception {
        final String original = read( get( "law/30xa.txt" ) );
        final String modified = read( get( "law/30xb.txt" ) );

        diff( original, modified );
    }

    @Test
    public void shouldOptimize40() throws Exception {
        final String original = read( get( "law/40a.txt" ) );
        final String modified = read( get( "law/40b.txt" ) );

        diff( original, modified );
    }

    @Test
    public void shouldOptimize49() throws Exception {
        final String original = read( get( "law/49a.txt" ) );
        final String modified = read( get( "law/49b.txt" ) );

        diff( original, modified );
    }

    @Test
    public void shouldOptimize74() throws Exception {
        final String original = read( get( "law/74a.txt" ) );
        final String modified = read( get( "law/74b.txt" ) );

        diff( original, modified );
    }

    @Test
    public void shouldOptimize79() throws Exception {
        final String original = read( get( "law/79a.txt" ) );
        final String modified = read( get( "law/79b.txt" ) );

        diff( original, modified );
    }

    private void diff( final String original, final String modified ) throws Exception {
        final TagTestFixture tagTest = new TagTestFixture();
        tagTest.performTagDiff(original, modified);

        final List<TextOperation> res = tagTest.getResults();
        System.out.println( res.size() );
        System.out.println( res );
        System.out.println( "====================== histogram ==================" );

        final TagTestFixture tagTest2 = new TagTestFixture();
        tagTest2.performHistogramDiff(original, modified);
        final List<TextOperation> res2 = tagTest2.getResults();
        System.out.println( res2.size() );
        System.out.println( res2 );

        assertEquals("Expected original text", original, tagTest2.getReconstructedOriginalText());
        assertEquals("Expected modified text", modified, tagTest2.getReconstructedModifiedText());

    }


    public InputStream get( final String res ) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream( res );
    }

    public String read( final InputStream inputStream ) throws IOException {
        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        final byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString(StandardCharsets.UTF_8.name());
    }
}
