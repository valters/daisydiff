/*
  This file is licensed to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package org.outerj.eclipse.jgit.diff;

import java.util.List;

import org.outerj.daisy.diff.output.TextDiffOutput;

/** jgit diff output */
public class HistogramFormat {
    public static final HistogramFormat INSTANCE = new HistogramFormat();

    public void format(final EditList edits, final RawText a, final RawText b, final TextDiffOutput output) throws Exception {

        for (int curIdx = 0; curIdx < edits.size();) {
            Edit curEdit = edits.get(curIdx);
            final int endIdx = findCombinedEnd(edits, curIdx);

            int aCur = 0;
            int bCur = 0;
            final int aEnd = a.size();
            final int bEnd = b.size();

            while (aCur < aEnd || bCur < bEnd) {
                if (aCur < curEdit.getBeginA() || endIdx + 1 < curIdx) {
                    if( aCur < aEnd) {
                        output.addClearPart( a.getStringN( aCur ) );
                    }
                    aCur++;
                    bCur++;
                } else if (aCur < curEdit.getEndA()) {
                    if( aCur < aEnd) {
                        output.addRemovedPart( a.getStringN( aCur ) );
                    }
                    aCur++;
                } else if (bCur < curEdit.getEndB()) {
                    output.addAddedPart( b.getStringN( bCur ) );
                    bCur++;
                }

                if (end(curEdit, aCur, bCur) && ++curIdx < edits.size()) {
                    curEdit = edits.get(curIdx);
                }
            }
        }
    }

    private int findCombinedEnd(final List<Edit> edits, final int i) {
        int end = i + 1;
        while (end < edits.size()
                && (combineA(edits, end) || combineB(edits, end))) {
            end++;
        }
        return end - 1;
    }

    private final int context = 1000;

    private boolean combineA(final List<Edit> e, final int i) {
        return e.get(i).getBeginA() - e.get(i - 1).getEndA() <= 2 * context;
    }

    private boolean combineB(final List<Edit> e, final int i) {
        return e.get(i).getBeginB() - e.get(i - 1).getEndB() <= 2 * context;
    }

    private static boolean end(final Edit edit, final int a, final int b) {
        return edit.getEndA() <= a && edit.getEndB() <= b;
    }

}
