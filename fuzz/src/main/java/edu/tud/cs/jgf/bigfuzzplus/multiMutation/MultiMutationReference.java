/*
 * Created by Melchior Oudemans for the bachelors research project at the TUDelft. Code has been created by extending on the BigFuzz framework in collaboration with 4 other students at the TU Delft.
 */

package edu.tud.cs.jgf.bigfuzzplus.multiMutation;

public class MultiMutationReference {

    public enum MultiMutationMethod {
        Disabled,
        Permute_random,
        Permute_2,
        Permute_3,
        Permute_4,
        Permute_5,
        Smart_mutate
    }

    public static MultiMutationMethod intToMultiMutationMethod(int i) {
        switch (i) {
            case 0: return MultiMutationMethod.Disabled;
            case 1: return MultiMutationMethod.Permute_random;
            case 2: return MultiMutationMethod.Permute_2;
            case 3: return MultiMutationMethod.Permute_3;
            case 4: return MultiMutationMethod.Permute_4;
            case 5: return MultiMutationMethod.Permute_5;
            case 6: return MultiMutationMethod.Smart_mutate;
            default: return MultiMutationMethod.Disabled;
        }
    }
}
