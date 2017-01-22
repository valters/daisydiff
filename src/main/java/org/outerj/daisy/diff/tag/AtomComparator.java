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
