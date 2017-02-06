package org.outerj.daisy.diff.tag;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.outerj.eclipse.jgit.util.IntList;
import org.outerj.eclipse.jgit.util.RawParseUtils;

public class RawParseUtilsTest {

    @Test
    public void shouldSplitSentences() throws Exception {
        final byte[] myStr = "this is a test. 10.10.2016 . gendalf 2017. you should get ready.".getBytes();
        final IntList map = RawParseUtils.MAP_SENTENCES.tokenMap( myStr, 0, myStr.length );
        System.out.println( map );
        assertThat( map.size(), is( 8 ) );
        assertThat( map.get( 1 ), is( 0 ) );
        assertThat( map.get( 2 ), is( 15 ) );
        assertThat( map.get( 3 ), is( 19 ) );
        assertThat( map.get( 4 ), is( 22 ) );
        assertThat( map.get( 5 ), is( 28 ) );
    }

}
