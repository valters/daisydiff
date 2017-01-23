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
