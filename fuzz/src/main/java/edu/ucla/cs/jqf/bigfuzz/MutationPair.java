package edu.ucla.cs.jqf.bigfuzz;

public class MutationPair {

    private final Integer elementId;
    private final HighOrderMutation.HighOrderMutationMethod mutation;

    public MutationPair(Integer elementId, HighOrderMutation.HighOrderMutationMethod mutation) {
        this.elementId = elementId;
        this.mutation = mutation;
    }

    public Integer getElementId() {
        return elementId;
    }

    public HighOrderMutation.HighOrderMutationMethod getMutation() {
        return mutation;
    }
}
