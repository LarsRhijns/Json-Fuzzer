/*
 * Created by Melchior Oudemans for the bachelors research project at the TUDelft. Code has been created by extending on the BigFuzz framework in collaboration with 4 other students at the TU Delft.
 */

package edu.tud.cs.jgf.bigfuzzplus.stackedMutation;

public class StackedMutationEnum {

    public enum StackedMutationMethod {
        Disabled,
        Permute_random,
        Permute_2,
        Permute_3,
        Permute_4,
        Permute_5,
        Smart_stack
    }

    /**
     * Return StackedMutationMethod depending on the passed integer:
     *  0 = Disabled
     *  1 = Permute_random
     *  2 = permute_2
     *  3 = permute_3
     *  4 = permute_4
     *  5 = permute_5
     *  6 = Smart_stack
     *  else: Disabled
     * @param i integer corresponding to a mutation method
     * @return returns StackedMutationMethod depending on the passed integer.
     */
    public static StackedMutationMethod intToStackedMutationMethod(int i) {
        switch (i) {
            case 0: return StackedMutationMethod.Disabled;
            case 1: return StackedMutationMethod.Permute_random;
            case 2: return StackedMutationMethod.Permute_2;
            case 3: return StackedMutationMethod.Permute_3;
            case 4: return StackedMutationMethod.Permute_4;
            case 5: return StackedMutationMethod.Permute_5;
            case 6: return StackedMutationMethod.Smart_stack;
            default: return StackedMutationMethod.Disabled;
        }
    }
}
