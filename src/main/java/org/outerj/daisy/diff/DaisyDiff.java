/*
 * Copyright 2004 Guy Van den Broeck
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.outerj.daisy.diff;

import java.io.BufferedReader;
import java.io.InputStream;

import org.outerj.daisy.diff.output.ConsolidateOutput;
import org.outerj.daisy.diff.tag.AtomComparator;
import org.outerj.daisy.diff.tag.AtomFormat;
import org.outerj.daisy.diff.tag.AtomList;
import org.outerj.daisy.diff.tag.TagComparator;
import org.outerj.daisy.diff.tag.TagDiffer;
import org.outerj.daisy.diff.tag.TagSaxDiffOutput;
import org.outerj.eclipse.jgit.diff.EditList;
import org.outerj.eclipse.jgit.diff.HistogramDiff;
import org.outerj.eclipse.jgit.diff.HistogramFormat;
import org.outerj.eclipse.jgit.diff.RawText;
import org.outerj.eclipse.jgit.diff.RawTextComparator;
import org.outerj.eclipse.jgit.diff.RawTextTokenizer;
import org.outerj.eclipse.jgit.util.IO;
import org.outerj.eclipse.jgit.util.RawParseUtils;
import org.xml.sax.ContentHandler;

public class DaisyDiff {

    private static final int LAW_SIZE = 10 * 1024; // use 640kb buffer

    /**
     * Diffs two html files word for word as source, outputting the result to
     * the specified consumer.
     */
    public static void diffTag(final String oldText, final String newText,
            final ContentHandler consumer) throws Exception {

        final TagComparator oldComp = new TagComparator(oldText);
        final TagComparator newComp = new TagComparator(newText);

        diffTag( consumer, oldComp, newComp );

    }

    /**
     * Diffs two html files word for word as source, outputting the result to
     * the specified consumer.
     */
    public static void diffTag(final BufferedReader oldText, final BufferedReader newText,
            final ContentHandler consumer) throws Exception {

        try {
            final TagComparator oldComp = new TagComparator(oldText);
            final TagComparator newComp = new TagComparator(newText);

            diffTag( consumer, oldComp, newComp );
        }
        finally {
            oldText.close();
            newText.close();
        }
    }

    private static void diffTag( final ContentHandler consumer, final TagComparator oldComp, final TagComparator newComp ) throws Exception {
        final TagSaxDiffOutput output = new TagSaxDiffOutput(consumer);
        final TagDiffer differ = new TagDiffer(output);
        differ.diff(oldComp, newComp);
    }

    /**
     * Diffs two html files word for word as source using Histogram (Patience) diff variant, outputting the result to
     * the specified consumer.
     */
    public static void diffHistogram( final BufferedReader oldText, final BufferedReader newText, final ContentHandler consumer ) throws Exception {

        try {
            final AtomList oldComp = new AtomList( oldText );
            final AtomList newComp = new AtomList( newText );

            diffHistogram( consumer, oldComp, newComp );
        }
        finally {
            oldText.close();
            newText.close();
        }
    }

    private static void diffHistogram( final ContentHandler consumer, final AtomList oldComp, final AtomList newComp ) throws Exception {
        final ConsolidateOutput output = new ConsolidateOutput( new TagSaxDiffOutput( consumer ) );
        final HistogramDiff differ = new HistogramDiff();
        final EditList editList = differ.diff( AtomComparator.DEFAULT, oldComp, newComp );

        if( editList.isEmpty() ) {
            output.addClearPart( output.getOriginalText() );
        }
        else {
            AtomFormat.INSTANCE.format( editList, oldComp, newComp, output );
        }

        output.flushToParent();
    }

    /** @see #diffHistogramRaw(InputStream, InputStream, ContentHandler, RawTextTokenizer) */
    public static void diffHistogramRaw( final InputStream oldText, final InputStream newText, final ContentHandler consumer ) throws Exception {
        diffHistogramRaw( oldText, newText, consumer, RawParseUtils.MAP_SENTENCES );
    }

    /**
     * Diffs two html files word for word as source using Histogram (Patience) diff variant, outputting the result to
     * the specified consumer.
     * @param oldText compare original text
     * @param newText with new (modified) text
     * @param consumer output html report to
     * @param tokenizer using given algorithm to detect tokens (default is conservative "sentences")
     */
    public static void diffHistogramRaw( final InputStream oldText, final InputStream newText, final ContentHandler consumer, final RawTextTokenizer tokenizer ) throws Exception {

        try {
            final RawText oldComp = new RawText( IO.readWholeStream( oldText, LAW_SIZE ).array(), tokenizer );
            final RawText newComp = new RawText( IO.readWholeStream( newText, LAW_SIZE ).array(), tokenizer );

            diffHistogramRaw( consumer, oldComp, newComp );
        }
        finally {
            oldText.close();
            newText.close();
        }
    }

    private static void diffHistogramRaw( final ContentHandler consumer, final RawText oldComp, final RawText newComp ) throws Exception {
        final TagSaxDiffOutput output = new TagSaxDiffOutput( consumer );
        final HistogramDiff differ = new HistogramDiff();
        final EditList editList = differ.diff( RawTextComparator.WS_IGNORE_ALL, oldComp, newComp );

        if( editList.isEmpty() ) {
            HistogramFormat.INSTANCE.noChanges( newComp, output );
        }
        else {
            HistogramFormat.INSTANCE.format( editList, oldComp, newComp, output );
        }
    }

}
