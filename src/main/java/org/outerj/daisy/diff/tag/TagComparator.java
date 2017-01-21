/*
 * Copyright 2007 Guy Van den Broeck
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
package org.outerj.daisy.diff.tag;

/**
 * Takes a String and generates tokens/atoms that can be used by LCS. This
 * comparator is used specifically for HTML documents.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.outerj.eclipse.compare.rangedifferencer.IRangeComparator;

public class TagComparator implements IAtomSplitter {

    private final List<Atom> atoms = new ArrayList<Atom>(50);

    public TagComparator(final String s) {
        new AtomBuilder(s, atoms);
    }

    public TagComparator(final StringBuilder s) {
         new AtomBuilder(s, atoms);
    }

    public TagComparator(final BufferedReader in) throws IOException {
        new AtomBuilder(in, atoms);
    }

    public List<Atom> getAtoms() {
        return new ArrayList<Atom>(atoms);
    }


    @Override
    public String substring(final int startAtom, final int endAtom) {
        if (startAtom == endAtom)
            return "";
        else {
            final StringBuilder result = new StringBuilder();
            for (int i = startAtom; i < endAtom; i++) {
                result.append(atoms.get(i).getFullText());
            }
            return result.toString();
        }
    }

    @Override
    public String substring(final int startAtom) {
        return substring(startAtom, atoms.size());
    }

    @Override
    public Atom getAtom(final int i) {
        if (i < 0 || i >= atoms.size())
            throw new IndexOutOfBoundsException("There is no Atom with index "
                    + i);
        return atoms.get(i);
    }

    @Override
    public int getRangeCount() {
        return atoms.size();
    }

    @Override
    public boolean rangesEqual(final int thisIndex, final IRangeComparator other,
            final int otherIndex) {
        TagComparator tc2;
        try {
            tc2 = (TagComparator) other;
        } catch (final ClassCastException e) {
            return false;
        }
        return tc2.getAtom(otherIndex).equalsIdentifier(getAtom(thisIndex));
    }

    @Override
    public boolean skipRangeComparison(final int length, final int maxLength,
            final IRangeComparator other) {
        return false;
    }

}
