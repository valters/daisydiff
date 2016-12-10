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

import org.outerj.daisy.diff.tag.TagComparator;
import org.outerj.daisy.diff.tag.TagDiffer;
import org.outerj.daisy.diff.tag.TagSaxDiffOutput;
import org.xml.sax.ContentHandler;

public class DaisyDiff {

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

        final TagComparator oldComp = new TagComparator(oldText);
        final TagComparator newComp = new TagComparator(newText);

        diffTag( consumer, oldComp, newComp );
    }

    private static void diffTag( final ContentHandler consumer, final TagComparator oldComp, final TagComparator newComp ) throws Exception {
        final TagSaxDiffOutput output = new TagSaxDiffOutput(consumer);
        final TagDiffer differ = new TagDiffer(output);
        differ.diff(oldComp, newComp);
    }

}
