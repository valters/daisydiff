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
