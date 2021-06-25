package edu.tud.cs.jqf.bigfuzzplus.systematicMutation;

import java.util.ArrayList;
import java.util.Arrays;

import static edu.tud.cs.jqf.bigfuzzplus.systematicMutation.MutationTree.MutationExclusion.*;
import static edu.tud.cs.jqf.bigfuzzplus.systematicMutation.MutationTree.MutationType.*;
import static edu.tud.cs.jqf.bigfuzzplus.systematicMutation.SystematicMutation.MUTATION_DEPTH;

/**
 * Provides the next mutation type for exploration of hihger order mutations. Also contains the mutation types and exclusion rules.
 */
public class MutationTree {
	private Mutation currentMutation;

	public MutationTree(int columnAmount) {
		this.currentMutation = new Mutation(columnAmount);
	}

	/**
	 * Traverse the tree to find the next mutation type to be applied.
	 *
	 * @return mutation node to be applied next
	 */
	public Mutation traverseTree() {
		if (!currentMutation.isVisited) {
			if (currentMutation.level < MUTATION_DEPTH) {
				currentMutation.addAllTypes();
				currentMutation.setVisited();
			}
		}
		if (currentMutation.children.isEmpty()) {
			//go to parent
			while (currentMutation.hasParent()) {
				if (currentMutation.mutationType == ChangeDelimiter) {
					SystematicMutation.revertDelimiter();
				}
				currentMutation = currentMutation.parent;
				if (currentMutation.hasChildren()) {
					currentMutation = currentMutation.removeChild();
					return currentMutation;
				}
			}
			//if we reach here we are at seed
			return new Mutation(-1);
		} else {
			currentMutation = currentMutation.removeChild();
		}
		return currentMutation;
	}

	/**
	 * Getter for current mutation in tree.
	 *
	 * @return mutation node of current tree search location
	 */
	public Mutation getCurrentMutation() {
		return currentMutation;
	}

	/**
	 * Mutation node for MutationTree.
	 */
	public static class Mutation {
		private Mutation parent;
		private final ArrayList<Mutation> children;
		private final MutationType mutationType;
		private final ArrayList<MutationPair> prevMutations;
		private final int column;

		//amount of columns after mutation has been applied
		private final int columnAmount;
		private final int level;
		private boolean isVisited;

		/**
		 * Constructor for all mutations beside ChangeDelimiter and AddElement,
		 * since those mutations are applied to all columns.
		 *
		 * @param parent       previous applied mutation
		 * @param mutationType type of mutation to be applied
		 * @param column       column that mutation will be applied to
		 */
		public Mutation(Mutation parent, MutationType mutationType, int column) {
			//separate constructor for changeDelimiter and root
			assert mutationType != ChangeDelimiter && mutationType != NoMutation;
			this.children = new ArrayList<>();
			this.prevMutations = new ArrayList<>();
			this.isVisited = false;
			this.parent = parent;
			this.level = parent.level + 1;
			this.mutationType = mutationType;
			this.column = column;
			//do for any mutation that has had at least 1 mutation before it
			if (level > 1) {
				this.prevMutations.addAll(parent.prevMutations);
				this.prevMutations.add(new MutationPair(parent.mutationType, parent.column));
			}
			if (mutationType == MutationTree.MutationType.RemoveElement && parent.columnAmount > 1) {
				this.columnAmount = parent.columnAmount - 1;
			} else {
				this.columnAmount = parent.columnAmount;
			}
		}

		/**
		 * Constructor for ChangeDelimiter and AddElement. Requires a separate constructor since these mutations are applied to all columns.
		 *
		 * @param parent previous applied mutation.
		 */
		public Mutation(Mutation parent, MutationType type) {
			assert parent != null;
			this.children = new ArrayList<>();
			this.prevMutations = new ArrayList<>();
			this.isVisited = false;
			this.parent = parent;
			this.level = parent.level + 1;
			this.mutationType = type;
			this.columnAmount = parent.columnAmount;
			this.column = -1;
			//do for any mutation that has had at least 1 mutation before it
			if (level > 1) {
				this.prevMutations.addAll(parent.prevMutations);
				this.prevMutations.add(new MutationPair(parent.mutationType, parent.column));
			}
		}

		/**
		 * Constructor for root node.
		 *
		 * @param columnAmount amount of columns after mutation is applied
		 */
		public Mutation(int columnAmount) {
			this.children = new ArrayList<>();
			this.prevMutations = new ArrayList<>();
			isVisited = false;
			this.mutationType = NoMutation;
			this.level = 0;
			this.columnAmount = columnAmount;
			this.column = -1;
		}

		/**
		 * Returns the parent Mutation if the Mutation has a parent, otherwise throws NullPointerException.
		 *
		 * @return parent Mutation
		 * @throws NullPointerException if the Mutation contains no parent
		 */
		public Mutation getParent() {
			if (hasParent()) {
				return parent;
			}
			throw new NullPointerException("No parent to return");
		}

		/**
		 * Getter for level of Mutation.
		 *
		 * @return integer representing level of mutation node in tree
		 */
		public int getLevel() {
			return level;
		}

		/**
		 * Getter for mutation type of Mutation.
		 *
		 * @return MutationType of Mutation.
		 */
		public MutationType getMutationType() {
			return mutationType;
		}

		/**
		 * Getter for column of Mutation.
		 *
		 * @return integer representing column number
		 */
		public int getColumn() {
			return column;
		}

		/**
		 * Getter for column amount of Mutation after mutation has been applied.
		 *
		 * @return integer representing column amount
		 */
		public int getColumnAmount() {
			return columnAmount;
		}

		/**
		 * Set Mutation node to be visited.
		 */
		public void setVisited() {
			isVisited = true;
		}

		/**
		 * Returns whether the Mutation has a parent.
		 *
		 * @return boolean representing whether the Mutation has a parent Mutation
		 */
		public boolean hasParent() {
			return parent != null;
		}

		/**
		 * Returns whether the Mutation has children.
		 *
		 * @return true of Mutation has at least one child Mutation, false otherwise.
		 */
		public boolean hasChildren() {
			return !children.isEmpty();
		}

		/**
		 * Adds child to Mutation to be mutated next.
		 *
		 * @param child to be added to Mutation
		 */
		public void addChild(Mutation child) {
			this.children.add(child);
		}

		/**
		 * Removes the first child of the Mutation.
		 *
		 * @return first Mutation child of this Mutation
		 */
		public Mutation removeChild() {
			return children.remove(0);
		}

		/**
		 * Add all mutation types as children.
		 * When MUTATE_COLUMNS is true, children are added for all columns for mutation types 1-5.
		 * However, only if the combination of column and mutation type has not been applied by a previous mutation or will be applied by the current mutation.
		 * If MUTATE_COLUMNS is false, random columns are selected for mutation types 1-5 and all mutation types are only added once.
		 * However, only if the mutation type has not been applied before, no matter which column it had been applied to.
		 * Mutation types 6 and 7 apply to all columns, and thus the behaviour does not depend on MUTATE_COLUMNS.
		 * They are added if they have not applied by a previous Mutation or the current Mutation.
		 * After the mutations have been added, they are removed if they fit exclusion rules.
		 */
		public void addAllTypes() {
			MutationPair nextValue;
			MutationPair currentValue;
			//for every mutation type besides ChangeDelimiter and AddElement
			for (int i = 1; i < MutationType.values().length - 2; i++) {
				//for every column if turned on
				if (SystematicMutation.MUTATE_COLUMNS) {
					for (int column = 0; column < columnAmount; column++) {
						nextValue = new MutationPair(MutationType.values()[i], column);
						currentValue = new MutationPair(this.mutationType, this.column);
						if (!this.prevMutations.contains(nextValue) && !nextValue.equals(currentValue)) {
							this.addChild(new Mutation(this, MutationType.values()[i], column));
						}
					}
				} else {
					//otherwise select one random column
					int randomColumn = SystematicMutation.r.nextInt(columnAmount);
					MutationType nextType = MutationType.values()[i];
					if (this.prevMutations.stream().noneMatch(mutation -> mutation.getMutationType() == nextType) && !nextType.equals(this.mutationType)) {
						this.addChild(new Mutation(this, MutationType.values()[i], randomColumn));
					}
				}
			}
			//add ChangeDelimiter and AddElement
			nextValue = new MutationPair(AddElement, -1);
			currentValue = new MutationPair(this.mutationType, this.column);
			if (!this.prevMutations.contains(nextValue) && !nextValue.equals(currentValue)) {
				this.addChild(new Mutation(this, AddElement));
			}
			nextValue = new MutationPair(ChangeDelimiter, -1);
			currentValue = new MutationPair(this.mutationType, this.column);
			if (!this.prevMutations.contains(nextValue) && !nextValue.equals(currentValue)) {
				this.addChild(new Mutation(this, ChangeDelimiter));
			}
			this.excludeTypes();
		}

		/**
		 * Removes children from Mutation that fit exclusion rules. These rules were made by considering illogical combinations of mutation types.
		 */
		private void excludeTypes() {
			ArrayList<MutationPair> excludeMutations = new ArrayList<>(this.prevMutations);
			excludeMutations.add(new MutationPair(this.mutationType, this.column));
			for (MutationPair pair : excludeMutations) {
				this.children.removeIf(child -> pair.getColumn() == child.column &&
						Arrays.stream(pair.getMutationType().exclusion.values).anyMatch(index -> index == child.mutationType.ordinal()));
			}
		}
	}

	/*
	 *                    0: no mutation used for root
	 *                    1: random change value   (M1)
	 *                    2: random change into float (M2)
	 *                    3: random insert value in element (M4)
	 *                    4: random delete one column/element (M5)
	 *                    5: Empty String (M6)
	 *                    6: random add one column/element (M7)
	 *                    7: change delimiter (M3)
	 */
	public enum MutationType {
		NoMutation(NoExclusion),
		ChangeValue(ChangeValueExclusion),
		ChangeType(ChangeTypeExclusion),
		InsertChar(InsertCharExclusion),
		RemoveElement(RemoveElementExclusion),
		EmptyColumn(EmptyColumnExclusion),
		AddElement(AddElementExclusion),
		ChangeDelimiter(NoExclusion);

		public final MutationExclusion exclusion;

		MutationType(MutationExclusion exclusion) {
			this.exclusion = exclusion;
		}

	}

	/**
	 * Exclusions containing integer arrays referring to Mutation type indices.
	 */
	protected enum MutationExclusion {
		ChangeValueExclusion(new int[]{4, 5}),
		ChangeTypeExclusion(new int[]{1, 4, 5}),
		InsertCharExclusion(new int[]{1, 2, 4, 5}),
		RemoveElementExclusion(new int[]{1, 2, 3, 5, 6}),
		EmptyColumnExclusion(new int[]{1, 2, 3, 4, 6}),
		AddElementExclusion(new int[]{4}),
		NoExclusion(new int[]{});

		private final int[] values;

		MutationExclusion(int[] exclusions) {
			this.values = exclusions;
		}
	}
}
