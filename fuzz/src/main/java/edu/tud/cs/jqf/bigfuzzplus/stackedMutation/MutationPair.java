/*
 * Created by Melchior Oudemans for the bachelors research project at the TUDelft. Code has been created by extending on the BigFuzz framework in collaboration with 4 other students at the TU Delft.
 */

package edu.tud.cs.jqf.bigfuzzplus.stackedMutation;

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
