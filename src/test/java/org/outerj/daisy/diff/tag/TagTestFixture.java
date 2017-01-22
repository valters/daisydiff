/*
 * Copyright 2009 Guy Van den Broeck
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

import java.util.ArrayList;
import java.util.List;

import org.outerj.daisy.diff.output.TextDiffOutput;
import org.outerj.eclipse.jgit.diff.EditList;
import org.outerj.eclipse.jgit.diff.HistogramDiff;

/**
 * Minimal test case for Tag mode.
 *
 * @author kapelonk
 *
 */
public class TagTestFixture {

	/** Type of changes as produced by the diff process */
	private enum OperationType {
		NO_CHANGE, ADD_TEXT, REMOVE_TEXT
	}

	/** Keeps a copy of the original text */
	private String oldText = null;

	/** Keeps a copy of the modified text */
	private String newText = null;

	/** A list of text operations produced by the diff process */
	private List<TextOperation> results = null;

	/**
	 * Just empties the results;
	 */
	public TagTestFixture()
	{
		results = new ArrayList<TextOperation>();
	}

	/**
	 * Performs a tag diff againts two html strings.
	 *
	 * @param original html in its old state.
	 * @param modified html in its present state.
	 *
	 * @throws Exception something went wrong.
	 */
	public void performTagDiff(final String original, final String modified) throws Exception
	{
		oldText = original;
		newText = modified;

		final TagComparator oldComp = new TagComparator(oldText);
		final TagComparator newComp = new TagComparator(newText);

        final DummyOutput output = new DummyOutput();
        final TagDiffer differ = new TagDiffer(output);
        differ.diff(oldComp, newComp);

	}

    public void performHistogramDiff(final String original, final String modified) throws Exception
    {
        oldText = original;
        newText = modified;

        final AtomList oldComp = new AtomList(oldText);
        final AtomList newComp = new AtomList(newText);

        final DummyOutput output = new DummyOutput();
        final HistogramDiff differ = new HistogramDiff();
        final EditList editList = differ.diff(AtomComparator.DEFAULT, oldComp, newComp);

        if (editList.isEmpty()) {
            output.addClearPart( newText );
        }
        else {
            AtomFormat.INSTANCE.format( editList, oldComp, newComp, output );
        }

    }

	/**
	 * Attempts to re-construct the original text by looking
	 * at the diff result.
	 *
	 * @return the sum of unchanged and removed text.
	 */
	public String getReconstructedOriginalText()
	{
		final StringBuilder result = new StringBuilder();

		for(final TextOperation operation:results)
		{
			if(operation.getType() == OperationType.ADD_TEXT)
			{
				continue;
			}
			result.append(operation.getText());
		}
		return result.toString();
	}

	/**
	 * Attempts to re-construct the modified text by looking
	 * at the diff result.
	 *
	 * @return the sum of unchanged and added text.
	 */
	public String getReconstructedModifiedText()
	{
		final StringBuilder result = new StringBuilder();

		for(final TextOperation operation:results)
		{
			if(operation.getType() == OperationType.REMOVE_TEXT)
			{
				continue;
			}
			result.append(operation.getText());
		}
		return result.toString();
	}

	/**
	 * Retuns a list of basic operations.
	 * @return the results
	 */
	public List<TextOperation> getResults() {
		return results;
	}



    /**
     * Simple operation for test cases only.
     *
     * @author kapelonk
     *
     */
	protected static class TextOperation
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

	/**
	 * Dummy output that holds all results in a linear list.
	 *
	 * @author kapelonk
	 *
	 */
	private class DummyOutput implements TextDiffOutput
	{
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

        @Override
        public void newline() {
            currOperation.consolidate( "\n" );
        }

	}


}
