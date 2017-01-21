package org.outerj.daisy.diff.tag;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.outerj.eclipse.jgit.diff.Sequence;

public class AtomList extends Sequence {

    @Override
    public String toString() {
        return "AtomList [size()=" + size() + "]";
    }

    private final List<Atom> atoms = new ArrayList<Atom>(50);

    public AtomList(final String s) {
        new AtomBuilder(s, atoms);
    }

    public AtomList(final StringBuilder s) {
         new AtomBuilder(s, atoms);
    }

    public AtomList(final BufferedReader in) throws IOException {
        new AtomBuilder(in, atoms);
    }

    public Atom getAtom(final int i) {
        if (i < 0 || i >= atoms.size())
            throw new IndexOutOfBoundsException("There is no Atom with index "
                    + i + ", size is = "+size());
        return atoms.get(i);
    }

    @Override
    public int size() {
        return atoms.size();
    }

}
