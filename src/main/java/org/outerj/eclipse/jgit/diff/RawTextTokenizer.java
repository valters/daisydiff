package org.outerj.eclipse.jgit.diff;

import org.outerj.eclipse.jgit.util.IntList;

/**
 * Map a token sequence from an existing content byte array.
 *
 */
public interface RawTextTokenizer {
  /**
    * @param buf
    *            the content array. The array is never modified, so passing
    *            through cached arrays is safe.
     * @param ptr
     *            position within the buffer corresponding to the first byte of
     *            line 1.
     * @param end
     *            1 past the end of the content within <code>buf</code>.
     * @return a "token" map indexing the start position of each token (such as line or sentence, or word).
    */
    public IntList tokenMap(final byte[] buf, int ptr, final int end);

}
