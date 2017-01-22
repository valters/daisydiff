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
package org.outerj.daisy.diff.output;

import java.util.ArrayList;
import java.util.List;

public class ConsolidateOutput implements TextDiffOutput
{
    /** Type of changes as produced by the diff process */
    private static enum OperationType {
        NO_CHANGE, ADD_TEXT, REMOVE_TEXT
    }

    private static class TextOperation
    {
        @Override
        public String toString() {
            return "[" + type + ": '" + getText() + "']";
        }

        private String text = null;
        private OperationType type = null;
        /** consolidates with following operations */
        private StringBuilder buffer;

        /**
         * @param text the text to set
         */
        public void setText(final String text) {
            this.text = text;
        }

        /**
         * @param type the type to set
         */
        public void setType(final OperationType type) {
            this.type = type;
        }

        /**
         * @return the text
         */
        public String getText() {
            if( text == null ) {
                if( buffer != null ) {
                    text  = buffer.toString();
                    buffer = null;
                }
            }
            return text;
        }

        /**
         * @return the type
         */
        public OperationType getType() {
            return type;
        }

        public void consolidate( final String text ) {
            if( buffer == null ) {
                buffer = new StringBuilder(this.text);
                this.text = null;
            }

            buffer.append( text );
        }

    }

    private final TextDiffOutput parent;

    public ConsolidateOutput( final TextDiffOutput parent ) {
        super();
        this.parent = parent;
    }

    /** A list of text operations produced by the diff process */
    private final List<TextOperation> results = new ArrayList<TextOperation>();
    private TextOperation currOperation = new TextOperation();

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void addAddedPart(final String text) throws Exception {
	    if( currOperation.type == OperationType.ADD_TEXT ) {
	        currOperation.consolidate( text );
	        return;
	    }

		currOperation = new TextOperation();
		currOperation.setText(text);
		currOperation.setType(OperationType.ADD_TEXT);
		results.add(currOperation);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void addClearPart(final String text) throws Exception {
        if( currOperation.type == OperationType.NO_CHANGE ) {
            currOperation.consolidate( text );
            return;
        }

        currOperation = new TextOperation();
        currOperation.setText(text);
		currOperation.setType(OperationType.NO_CHANGE);
        results.add(currOperation);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void addRemovedPart(final String text) throws Exception {
        if( currOperation.type == OperationType.REMOVE_TEXT ) {
            currOperation.consolidate( text );
            return;
        }
        currOperation = new TextOperation();
        currOperation.setText(text);
		currOperation.setType(OperationType.REMOVE_TEXT);
        results.add(currOperation);

	}

	/** send changes to parent output */
	public void flushToParent() throws Exception {
        for( final TextOperation op : results ) {
            switch( op.getType() ) {
                case NO_CHANGE:
                    parent.addClearPart( op.getText() );
                    break;
                case ADD_TEXT:
                    parent.addAddedPart( op.getText() );
                    break;
                case REMOVE_TEXT:
                    parent.addRemovedPart( op.getText() );
                    break;
            }
        }
    }

    public String getOriginalText() {
        final StringBuilder result = new StringBuilder();

        for( final TextOperation op : results ) {
            if( op.getType() == OperationType.ADD_TEXT ) {
                continue;
            }
            result.append( op.getText() );
        }
        return result.toString();
    }

    @Override
    public void newline() {
        currOperation.consolidate( "\n" );
    }

}
