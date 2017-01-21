package org.outerj.daisy.diff.tag;

import org.outerj.eclipse.jgit.diff.Edit;
import org.outerj.eclipse.jgit.diff.SequenceComparator;

/** Equivalence function for {@link AtomList}. */
public class AtomComparator extends SequenceComparator<AtomList> {
    public static AtomComparator DEFAULT = new AtomComparator();

    @Override
    public boolean equals( final AtomList a, final int ai, final AtomList b, final int bi ) {
        return a.getAtom( ai ).equalsIdentifier( b.getAtom( bi ) );
    }

    protected int hashRegion( final byte[] raw, int ptr, final int end ) {
        int hash = 5381;
        for( ; ptr < end; ptr++ )
            hash = ( ( hash << 5 ) + hash ) + ( raw[ptr] & 0xff );
        return hash;
    }

    @Override
    public int hash(final AtomList seq, final int lno) {
        final String str = seq.getAtom( lno ).getIdentifier();
        final int begin = 0;
        final int end = str.length();
        return hashRegion(str.getBytes(), begin, end);
    }

    @Override
    public Edit reduceCommonStartEnd(final AtomList a, final AtomList b, final Edit e) {

        if (e.beginA == e.endA || e.beginB == e.endB)
            return e;


        return super.reduceCommonStartEnd(a, b, e);
    }

}
