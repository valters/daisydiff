package org.outerj.daisy.diff.tag;

import java.util.List;

import org.outerj.daisy.diff.output.TextDiffOutput;
import org.outerj.eclipse.jgit.diff.Edit;
import org.outerj.eclipse.jgit.diff.EditList;

public class AtomFormat {
    public static final AtomFormat INSTANCE = new AtomFormat();

    /** jgit diff output */
    public void format(final EditList edits, final AtomList a, final AtomList b, final TextDiffOutput output) throws Exception {

        for (int curIdx = 0; curIdx < edits.size();) {
            Edit curEdit = edits.get(curIdx);
            final int endIdx = findCombinedEnd(edits, curIdx);
            final Edit endEdit = edits.get(endIdx);

            int aCur = 0; //(int) Math.max(0, (long) curEdit.getBeginA() - context);
            int bCur = 0; //(int) Math.max(0, (long) curEdit.getBeginB() - context);
            final int aEnd = a.size() ; //(int) Math.min(a.size(), (long) endEdit.getEndA() + context);
            final int bEnd = b.size(); //(int) Math.min(b.size(), (long) endEdit.getEndB() + context);

            while (aCur < aEnd || bCur < bEnd) {
                if (aCur < curEdit.getBeginA() || endIdx + 1 < curIdx) {
                    if( aCur < aEnd ) {
                        output.addClearPart( a.getAtom( aCur ).getFullText() );
                    }
                    aCur++;
                    bCur++;
                } else if (aCur < curEdit.getEndA()) {
                    output.addRemovedPart( a.getAtom( aCur ).getFullText() );
                    aCur++;
                } else if (bCur < curEdit.getEndB()) {
                    output.addAddedPart( b.getAtom( bCur ).getFullText() );
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

    private final int context = 3000000;

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
