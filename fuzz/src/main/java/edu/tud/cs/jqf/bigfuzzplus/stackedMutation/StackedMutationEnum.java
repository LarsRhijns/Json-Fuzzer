/*
 * Created by Melchior Oudemans for the bachelors research project at the TUDelft. Code has been created by extending on the BigFuzz framework in collaboration with 4 other students at the TU Delft.
 */

package edu.tud.cs.jqf.bigfuzzplus.stackedMutation;

public class StackedMutationEnum {

    public enum StackedMutationMethod {
        Disabled,
        Permute_random,
        Permute_max,
        Smart_stack,
        Single_mutate
    }

    /**
     * Return StackedMutationMethod depending on the passed integer:
     *  0 = Disabled
     *  1 = Permute_random (permute between 1 and the max amount of mutations)
     *  2 = Permute_max (Always permute until the max amount of mutations)
     *  3 = Smart_stack (Apply highorder mutation exclusion rules)
     *  4 = Single mutate (Only apply 1 mutation per column)
     *  else: Disabled
     * @param i integer corresponding to a mutation method
     * @return returns StackedMutationMethod depending on the passed integer.
     */
    public static StackedMutationMethod intToStackedMutationMethod(int i) {
        switch (i) {
            case 0: return StackedMutationMethod.Disabled;
            case 1: return StackedMutationMethod.Permute_random;
            case 2: return StackedMutationMethod.Permute_max;
            case 3: return StackedMutationMethod.Smart_stack;
            case 4: return StackedMutationMethod.Single_mutate;
            default: return StackedMutationMethod.Disabled;
        }
    }
}
