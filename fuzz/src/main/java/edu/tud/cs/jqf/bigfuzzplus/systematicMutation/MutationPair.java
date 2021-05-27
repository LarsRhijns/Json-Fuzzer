package edu.ucla.cs.jqf.bigfuzz;

import static edu.ucla.cs.jqf.bigfuzz.MutationTree.MutationType;
import static edu.ucla.cs.jqf.bigfuzz.MutationTree.MutationType.ChangeDelimiter;

public class MutationPair {

	private final MutationType mutationType;
	private final int column;

	/**
	 * Constructor for any pair that is not a ChangeDelimiter mutation.
	 *
	 * @param mutationType mutation type of pair, can not be ChangeDelimiter
	 * @param column mutation will be applied to this column, value is -1 for mutations that apply to all columns
	 */
	MutationPair(MutationType mutationType, int column) {
		assert mutationType != ChangeDelimiter;
		this.mutationType = mutationType;
		this.column = column;
	}

	/**
	 * Constructor for ChangeDelimiter pair. Column is set to -1 and should not be used.
	 */
	MutationPair() {
		this.mutationType = ChangeDelimiter;
		this.column = -1;
	}

	public MutationType getMutationType() {
		return mutationType;
	}

	public int getColumn() {
		assert this.mutationType != ChangeDelimiter;
		return column;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof MutationPair)) return false;

		MutationPair that = (MutationPair) o;

		if (column != that.column) return false;
		return mutationType == that.mutationType;
	}
}
