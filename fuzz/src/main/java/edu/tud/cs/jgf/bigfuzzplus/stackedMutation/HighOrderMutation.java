/*
 * Created by Melchior Oudemans for the bachelors research project at the TUDelft. Code has been created by extending on the BigFuzz framework in collaboration with 4 other students at the TU Delft.
 */

package edu.tud.cs.jgf.bigfuzzplus.stackedMutation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static edu.tud.cs.jgf.bigfuzzplus.stackedMutation.HighOrderMutation.HighOrderMutationMethod.*;


public class HighOrderMutation {
    // For each HighOrderMutationMethod enum, a set of other enums is provided to indicate which mutations can NOT be applied after the mutation of the list is applied.
    private static final HighOrderMutation.HighOrderMutationMethod[] changeValueStackExcludeRule = {RemoveElement, EmptyColumn, ChangeValue, RandomCharacter};
    private static final HighOrderMutation.HighOrderMutationMethod[] changeTypeStackExcludeRule = {RemoveElement, EmptyColumn, ChangeType, RandomCharacter};
    private static final HighOrderMutation.HighOrderMutationMethod[] removeElementStackExcludeRule = {ChangeValue, ChangeType, RemoveElement, RandomCharacter, EmptyColumn};
    private static final HighOrderMutation.HighOrderMutationMethod[] addElementStackExcludeRule = {AddElement, RemoveElement};
    private static final HighOrderMutation.HighOrderMutationMethod[] emptyColumnStackExcludeRule = {ChangeValue, ChangeType, RemoveElement, AddElement, EmptyColumn, RandomCharacter};
    private static final HighOrderMutation.HighOrderMutationMethod[] changeDelimiterStackExcludeRule = {ChangeDelimiter};
    private static final HighOrderMutation.HighOrderMutationMethod[] RandomCharacterStackExcludeRule = {RemoveElement, EmptyColumn};

    // Indicates whether a mutation is active or not
    private static final boolean noMutationActive = false;  // Boolean is used to generate mutation list that can be applied. NoMutation is an indication that no Mutation can be performed and does not match a specific mutation approach
    private static final boolean changeValueActive = true;
    private static final boolean changeTypeActive = true;
    private static final boolean removeElementActive = true;
    private static final boolean addElementActive = true;
    private static final boolean emptyColumnActive = true;
    private static final boolean changeDelimiterActive = true;
    private static final boolean randomCharacterActive = true;
    private static ArrayList<HighOrderMutationMethod> activeMutations;

    // Indicates a bias towards the mutation method
    private static final boolean biasEnabled = false;
    private static final float changeValueBias = 1f;
    private static final float changeTypeBias = 1f;
    private static final float removeElementBias = 1f;
    private static final float addElementBias = 1f;
    private static final float emptyColumnBias = 1f;
    private static final float changeDelimiterBias = 1f;
    private static final float randomCharacterBias = 1f;

    /**
     * List of High-order mutations. Indicated with corresponding label from BigFuzz paper when applicable.
     */
    public enum HighOrderMutationMethod {
        NoMutation,
        ChangeValue,    // (M1) TODO: Change naming -> distribution?
        ChangeType,     // (M2)
        ChangeDelimiter,// (M3)
        RandomCharacter,// (M4)
        RemoveElement,  // (M5)
        EmptyColumn,     // (M6)
        AddElement     // Opposite of (M5)
    }

    /**
     * Get random active mutation.
     * @param r Random object from which should be used to randomized (can be left null)
     * @return a random active mutation
     */
    public static HighOrderMutationMethod getRandomMutation(Random r) {
        if (r == null) {
            r = new Random();
        }

        HighOrderMutationMethod m = selectWeightedMutation(r, getActiveHighOrderMutationMethodList());
        return m;
    }

    /**
     * Select a random mutation depending on the bias defined in the class
     * @param r Random object from which the selection should be done
     * @param mutationList
     * @return a random weighted mutation
     */
    private static HighOrderMutationMethod selectWeightedMutation(Random r, ArrayList<HighOrderMutationMethod> mutationList) {
        // If bias is not enabled, return a random index of the mutation list (for performance reasons)
        if(!biasEnabled) {
            return mutationList.get(r.nextInt(mutationList.size()));
        }

        float[] biasWeights = new float[mutationList.size()];
        float total = 0;
        for (int i = 0; i < mutationList.size(); i++) {
            float f = getMutationMethodBias(mutationList.get(i));
            biasWeights[i] = f;
            total += f;
        }

        // If there is no weight found, either all mutations are set to 0 or the passed mutation list is empty. No mutation can be selected
        if(total == 0) {
            return NoMutation;
        }

        float randomFloatMultiple = r.nextFloat();
        float randomFloat = randomFloatMultiple * total;
        float sumFloat = 0;
        for (int i = 0; i < mutationList.size(); i++) {
            sumFloat += biasWeights[i];
            if(sumFloat >= randomFloat) {
                return mutationList.get(i);
            }
        }

        //Code should not reach this part
        System.err.println("Something went wrong in selecting a weighted mutation: " + mutationList);
        return NoMutation;
    }


    /**
     * Return the defined bias of the passed MutationMethod
     * @param highOrderMutationMethod MutationMethod from which the bias should be selected
     * @return float containing the bias of the method
     */
    private static float getMutationMethodBias(HighOrderMutationMethod highOrderMutationMethod) {
        float res= 1;

        // Identify which exclusion rules list need to be used
        switch (highOrderMutationMethod) {
            case ChangeValue:
                res = changeValueBias;
                break;
            case ChangeType:
                res = changeTypeBias;
                break;
            case RemoveElement:
                res = removeElementBias;
                break;
            case AddElement:
                res = addElementBias;
                break;
            case EmptyColumn:
                res = emptyColumnBias;
                break;
            case ChangeDelimiter:
                res = changeDelimiterBias;
                break;
            case RandomCharacter:
                res = randomCharacterBias;
                break;
        }
        return res;
    }

    /**
     * Get a random mutation method applying the smart mutation method. Method only selects a mutation method that does not interfere with already used mutations
     * @param r Random object from which should be used to randomized (can be left null)
     * @param highOrderMutationMethods List of already used mutations
     * @return A random mutation which does not interfere with already applied mutations
     */
    public static HighOrderMutationMethod getRandomSmartMutation(Random r, ArrayList<HighOrderMutationMethod> highOrderMutationMethods) {
        // If random object is left null, initialise it
        if (r == null) {
            r = new Random();
        }

        // Get a list of available mutations, taking into account the already used mutations
        ArrayList<HighOrderMutationMethod> availableMutations = getMutationListFromAppliedMutations(highOrderMutationMethods);

        // If there are no available mutations, no mutation can be performed
        if (availableMutations.size() == 0) {
            return NoMutation;
        }

        // Select a random mutation from the available mutations.
        HighOrderMutationMethod m = selectWeightedMutation(r, availableMutations);
        return m;
    }

    /**
     * Get list of mutations that can still be used after applying the passed mutationlist on an input.
     * @param highOrderMutationMethods List of mutations that have already been applied to an input
     * @return List of mutations that can be applied that do not interfere with already applied mutations
     */
    private static ArrayList<HighOrderMutationMethod> getMutationListFromAppliedMutations(ArrayList<HighOrderMutationMethod> highOrderMutationMethods) {
        // Create a list of all available mutations. This list will be reduced by the exclusion rules.
        ArrayList<HighOrderMutationMethod> res = new ArrayList(getActiveHighOrderMutationMethodList());

        // Loop over every used mutation and apply the exclusion rules on it
        for (HighOrderMutationMethod usedMethod :
                highOrderMutationMethods) {
            applyExclusionRulesToList(res, usedMethod);
        }
        return res;
    }


    /**
     * Applies exclusion rules defined in this class to the different mutationMethods.
     *
     * @param res        List of mutation methods on which the exclusion rules need to be applied (given list is modified)
     * @param usedMethod mutation method corresponding to the exclusion rules that need to be applied
     */
    private static void applyExclusionRulesToList(ArrayList<HighOrderMutationMethod> res, HighOrderMutationMethod usedMethod) {
        HighOrderMutationMethod[] exclusionList;

        // Identify which exclusion rules list need to be used
        switch (usedMethod) {
            case ChangeValue:
                exclusionList = changeValueStackExcludeRule;
                break;
            case ChangeType:
                exclusionList = changeTypeStackExcludeRule;
                break;
            case RemoveElement:
                exclusionList = removeElementStackExcludeRule;
                break;
            case AddElement:
                exclusionList = addElementStackExcludeRule;
                break;
            case EmptyColumn:
                exclusionList = emptyColumnStackExcludeRule;
                break;
            case ChangeDelimiter:
                exclusionList = changeDelimiterStackExcludeRule;
                break;
            case RandomCharacter:
                exclusionList = RandomCharacterStackExcludeRule;
                break;
            default:
                exclusionList = null;
        }
        // If no matching exclusion list is found, there are no rules defined
        if (exclusionList == null) {
            return;
        }

        // Remove the elements in the exclusionlist from the provided mutation list
        res.removeAll(Arrays.asList(exclusionList));
    }

    /**
     * Returns a list of (active) mutations that can be applied to input.
     *
     * @return list of active mutations
     */
    public static ArrayList<HighOrderMutationMethod> getActiveHighOrderMutationMethodList() {
        if (activeMutations == null) {
            activeMutations = createActiveMutations();
            // Mutation active status does not change once the program started. When first time called, create the active mutation list
            if (activeMutations.size() == 0) {
                System.err.println("No active mutations found");
                System.exit(0);
            }
        }


        return activeMutations;
    }

    /**
     * Instantiate the list of activeMutations using the mutation activity booleans defined in this class.
     * @return
     */
    private static ArrayList<HighOrderMutationMethod> createActiveMutations() {
        ArrayList<HighOrderMutationMethod> holder = new ArrayList<>();

        // For Every mutation defined in the enum. Check if the mutation has been enables and ad it to the mutation list
        for (HighOrderMutationMethod h :
                HighOrderMutationMethod.values()) {
            switch (h) {
                case ChangeValue:
                    if (changeValueActive) {
                        holder.add(h);
                    }
                    break;
                case ChangeType:
                    if (changeTypeActive) {
                        holder.add(h);
                    }
                    break;
                case RemoveElement:
                    if (removeElementActive) {
                        holder.add(h);
                    }
                    break;
                case AddElement:
                    if (addElementActive) {
                        holder.add(h);
                    }
                    break;
                case EmptyColumn:
                    if (emptyColumnActive) {
                        holder.add(h);
                    }
                    break;
                case ChangeDelimiter:
                    if (changeDelimiterActive) {
                        holder.add(h);
                    }
                    break;
                case RandomCharacter:
                    if(randomCharacterActive) {
                        holder.add(h);
                    }
                    break;
            }
        }
        return holder;
    }


    /**
     * Returns a HighOrderMutationMethod depending on the passed parameter:
     * 0: ChangeValue
     * 1: ChangeType
     * 2: ChangeDelimiter
     * 3: RandomCharacter
     * 4: RemoveElement
     * 5: EmptyColumn
     * 6: AddElement
     *
     * @param i integer corresponding to a HighOrderMutationMethod
     * @return HighOrderMutationMethod matching the passed integer i
     */
    public static HighOrderMutation.HighOrderMutationMethod intToHighOrderMutationMethod(int i) {
        switch (i) {
            case 0:
                return ChangeValue;
            case 1:
                return ChangeType;
            case 2:
                return ChangeDelimiter;
            case 3:
                return RandomCharacter;
            case 4:
                return RemoveElement;
            case 5:
                return EmptyColumn;
            case 6:
                return AddElement;
            default:
                return HighOrderMutationMethod.NoMutation;
        }
    }

    /**
     * Returns a int depending on the passed parameter:
     *  ChangeValue     - 0
     *  ChangeType      - 1
     *  ChangeDelimiter - 2
     *  RandomCharacter - 3
     *  RemoveElement   - 4
     *  EmptyColumn     - 5
     *  AddElement      - 6
     *
     * @param m HighOrderMutationMethod
     * @return Integer that matched the passed highordermutationmethod
     */
    public static int highOrderMutationMethodToInt(HighOrderMutationMethod m) {
        switch (m) {
            case ChangeValue:
                return 0;
            case ChangeType:
                return 1;
            case ChangeDelimiter:
                return 2;
            case RandomCharacter:
                return 3;
            case RemoveElement:
                return 4;
            case EmptyColumn:
                return 5;
            case AddElement:
                return 6;
            default:
                return -1;
        }
    }
}
