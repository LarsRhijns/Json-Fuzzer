package edu.tud.cs.jqf.bigfuzzplus.systematicMutation;

import static edu.tud.cs.jqf.bigfuzzplus.systematicMutation.MutationTree.MutationType;
import static edu.tud.cs.jqf.bigfuzzplus.systematicMutation.MutationTree.MutationType.ChangeDelimiter;

/**
 * Pair class representing combinations of mutation type and column. Used for determining to which column a mutation type will be applied.
 */
public class MutationPair {
	private final MutationTree.MutationType mutationType;
	private final int column;

	/**
	 * Constructor for any pair that is not a ChangeDelimiter mutation.
	 *
	 * @param mutationType mutation type of pair, can not be ChangeDelimiter
	 * @param column       mutation will be applied to this column, value is -1 for mutations that apply to all columns
	 */
	MutationPair(MutationType mutationType, int column) {
		this.mutationType = mutationType;
		this.column = column;
	}

	/**
	 * Getter for mutation type of pair.
	 *
	 * @return MutationType of pair
	 */
	public MutationType getMutationType() {
		return mutationType;
	}

	/**
	 * Getter for column number of pair.
	 *
	 * @return integer representing column of pair
	 */
	public int getColumn() {
		assert this.mutationType != ChangeDelimiter;
		return column;
	}

	/**
	 * Equal method for pair.
	 *
	 * @param o object to compare to
	 * @return boolean representing whether the objects are equal.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof MutationPair)) return false;

		MutationPair that = (MutationPair) o;

		if (column != that.column) return false;
		return mutationType == that.mutationType;
	}
}
