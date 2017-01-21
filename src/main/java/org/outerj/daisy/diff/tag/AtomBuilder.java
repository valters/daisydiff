package org.outerj.daisy.diff.tag;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

/** splits input into atoms */
public class AtomBuilder {
    private final List<Atom> atoms;

    public AtomBuilder(final String s, final List<Atom> atoms) {
        this.atoms = atoms;
        generateAtoms(s);
    }

    public AtomBuilder(final StringBuilder s, final List<Atom> atoms) {
        this.atoms = atoms;
        generateAtoms(s.toString());
    }

    public AtomBuilder(final BufferedReader in, final List<Atom> atoms) throws IOException {
        this.atoms = atoms;
        final StringBuilder sb = new StringBuilder();

        boolean allRead = false;
        while (!allRead) {
            final int result = in.read();
            if (result >= 0) {
                sb.append((char) result);
            } else {
                generateAtoms(sb.toString());
                allRead = true;
            }
        }
    }

    private void generateAtoms(final String s) {
        if (atoms.size() > 0)
            throw new IllegalStateException("Atoms can only be generated once");

        final StringBuilder currentWord = new StringBuilder(100);

        for (int i = 0; i < s.length(); i++) {
            final char c = s.charAt(i);

            if (c == '<'
                    && TagAtom
                            .isValidTag(s.substring(i, s.indexOf('>', i) + 1))) {
                // a tag
                if (currentWord.length() > 0) {
                    atoms.add(new TextAtom(currentWord.toString()));
                    currentWord.setLength(0);
                }

                final int end = s.indexOf('>', i);
                atoms.add(new TagAtom(s.substring(i, end + 1)));
                i = end;
            } else if (DelimiterAtom.isValidDelimiter("" + c)) {
                // a delimiter
                if (currentWord.length() > 0) {
                    atoms.add(new TextAtom(currentWord.toString()));
                    currentWord.setLength(0);
                }

                atoms.add(new DelimiterAtom(c));
            } else {
                // something else
                currentWord.append(c);
            }
        }
        if (currentWord.length() > 0) {
            atoms.add(new TextAtom(currentWord.toString()));
            currentWord.setLength(0);
        }
    }

}
