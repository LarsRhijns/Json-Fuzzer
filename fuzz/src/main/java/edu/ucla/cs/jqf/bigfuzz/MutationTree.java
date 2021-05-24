package edu.ucla.cs.jqf.bigfuzz;

import java.util.ArrayList;
import java.util.Arrays;

import static edu.ucla.cs.jqf.bigfuzz.MutationTree.MutationExclusion.*;
import static edu.ucla.cs.jqf.bigfuzz.MutationTree.MutationType.*;
import static edu.ucla.cs.jqf.bigfuzz.SystematicMutation.MUTATION_DEPTH;

//todo add breadth first search
public class MutationTree {
	private Mutation currentMutation;

	public MutationTree(int columnAmount) {
		this.currentMutation = new Mutation(columnAmount);
	}

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

	public Mutation getCurrentMutation() {
		return currentMutation;
	}

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
			if (mutationType == MutationTree.MutationType.RemoveElement) {
				this.columnAmount = parent.columnAmount - 1;
			} else {
				this.columnAmount = parent.columnAmount;
			}
		}

		/**
		 * Constructor for ChangeDelimiter and AddElement. Requires a separate constructor since the ChangeDelimiter
		 * mutation is applied to all columns.
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
		 * Constructor for root mutation.
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

		public Mutation getParent() {
			if (hasParent()) {
				return parent;
			}
			throw new NullPointerException("No parent to return");
		}

		public int getLevel() {
			return level;
		}

		public MutationType getMutationType() {
			return mutationType;
		}

		public int getColumn() {
			return column;
		}

		public int getColumnAmount() {
			return columnAmount;
		}

		public void setVisited() {
			isVisited = true;
		}

		public boolean hasParent() {
			return parent != null;
		}

		public boolean hasChildren() {
			return !children.isEmpty();
		}

		public void addChild(Mutation child) {
			this.children.add(child);
		}

		public Mutation removeChild() {
			return children.remove(0);
		}

		public void addAllTypes() {
			//for every mutation type besides ChangeDelimiter and AddElement
			for (int i = 1; i < MutationType.values().length - 1; i++) {
				//for every column if turned on
				if (SystematicMutation.MUTATE_COLUMNS) {
					for (int column = 0; column < columnAmount; column++) {
						MutationPair nextValue = new MutationPair(MutationType.values()[i], column);
						MutationPair currentValue = new MutationPair(this.mutationType, this.column);
						if (!this.prevMutations.contains(nextValue) && !nextValue.equals(currentValue)) {
							this.addChild(new Mutation(this, MutationType.values()[i], column));
						}
					}
				} else {
					//otherwise select one random column
					int randomColumn = SystematicMutation.r.nextInt(columnAmount);
					if (this.prevMutations.stream().noneMatch(pair -> pair.getMutationType() == this.mutationType)
							&& this.mutationType != MutationType.values()[i]) {
						this.addChild(new Mutation(this, MutationType.values()[i], randomColumn));
					}
				}
			}
			//add ChangeDelimiter and AddElement
			if (this.prevMutations.stream().noneMatch(pair -> pair.getMutationType() == this.mutationType)
					&& this.mutationType != ChangeDelimiter) {
				this.addChild(new Mutation(this, ChangeDelimiter));
			}
			if (this.prevMutations.stream().noneMatch(pair -> pair.getMutationType() == this.mutationType)
					&& this.mutationType != AddElement) {
				this.addChild(new Mutation(this, AddElement));
			}
			this.excludeTypes();
		}

		private void excludeTypes() {
			ArrayList<MutationPair> excludeMutations = new ArrayList<>(this.prevMutations);
			excludeMutations.add(new MutationPair(this.mutationType, this.column));
			for (MutationPair pair : excludeMutations) {
				this.children.removeIf(child -> pair.getColumn() == child.column &&
						Arrays.stream(pair.getMutationType().exclusion.types).anyMatch(type -> child.getMutationType() == type));
			}
		}
	}

	/*
	 *                    -: no mutation used for root
	 *                    0: random change value   (M1)
	 *                    1: random change into float (M2)
	 *                    2: random insert value in element (M4)
	 *                    3: random delete one column/element (M5)
	 *                    4: Empty String (M6)
	 *                    5: random add one column/element (?)
	 *                    6: change delimiter (M3)
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

	protected enum MutationExclusion {
		ChangeValueExclusion(new MutationType[]{RemoveElement, EmptyColumn}),
		ChangeTypeExclusion(new MutationType[]{ChangeValue, RemoveElement, EmptyColumn}),
		InsertCharExclusion(new MutationType[]{ChangeValue, ChangeType, RemoveElement, EmptyColumn}),
		RemoveElementExclusion(new MutationType[]{ChangeValue, ChangeType, InsertChar, EmptyColumn, AddElement}),
		EmptyColumnExclusion(new MutationType[]{ChangeValue, ChangeType, InsertChar, RemoveElement, AddElement}),
		AddElementExclusion(new MutationType[]{RemoveElement}),
		NoExclusion(new MutationType[]{});
//		ChangeDelimiterExclusion(new MutationType[]{});

		private final MutationType[] types;

		MutationExclusion(MutationType[] exclusions) {
			this.types = exclusions;
		}
	}
}
