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
    private static final HighOrderMutation.HighOrderMutationMethod[] changeValueStackExcludeRule = {RemoveElement, EmptyColumn, ChangeValue};
    private static final HighOrderMutation.HighOrderMutationMethod[] changeTypeStackExcludeRule = {RemoveElement, EmptyColumn, ChangeType};
    private static final HighOrderMutation.HighOrderMutationMethod[] removeElementStackExcludeRule = {ChangeValue, ChangeType, AddElement};
    private static final HighOrderMutation.HighOrderMutationMethod[] addElementStackExcludeRule = {AddElement, RemoveElement};
    private static final HighOrderMutation.HighOrderMutationMethod[] emptyColumnStackExcludeRule = {ChangeValue, ChangeType, RemoveElement, AddElement, EmptyColumn};
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
    private static HighOrderMutationMethod[] activeMutations;


    /**
     * Get random active mutation.
     * @param r Random object from which should be used to randomized (can be left null)
     * @return a random active mutation
     */
    public static HighOrderMutationMethod getRandomMutation(Random r) {
        if (r == null) {
            r = new Random();
        }
        int range = getActiveHighOrderMutationMethodList().length;
        return intToHighOrderMutationMethod(r.nextInt(range));
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
        int randomMutationSelection = r.nextInt(availableMutations.size());
        return availableMutations.get(randomMutationSelection);
    }

    /**
     * Get list of mutations that can still be used after applying the passed mutationlist on an input.
     * @param highOrderMutationMethods List of mutations that have already been applied to an input
     * @return List of mutations that can be applied that do not interfere with already applied mutations
     */
    private static ArrayList<HighOrderMutationMethod> getMutationListFromAppliedMutations(ArrayList<HighOrderMutationMethod> highOrderMutationMethods) {
        // Create a list of all available mutations. This list will be reduced by the exclusion rules.
        ArrayList<HighOrderMutationMethod> res = new ArrayList<>(Arrays.asList(getActiveHighOrderMutationMethodList()));

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
     * Returns a list of (active) mutations that can be applied to input.
     *
     * @return list of active mutations
     */
    public static HighOrderMutationMethod[] getActiveHighOrderMutationMethodList() {
        // Mutation active status does not change once the program started. When first time called, create the active mutation list
        if (activeMutations == null) {
            createActiveMutations();
        }
        return activeMutations;
    }

    /**
     * Instantiate the list of activeMutations using the mutation activity booleans defined in this class.
     */
    private static void createActiveMutations() {
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
            }
        }
        // Transform arraylist to an array and assign it to the active mutations
        HighOrderMutationMethod[] res = new HighOrderMutationMethod[holder.size()];
        for (int i = 0; i < holder.size(); i++) {
            res[i] = holder.get(i);
        }
        activeMutations = res;
    }


    /**
     * Returns a HighOrderMutationMethod depending on the passses parameter:
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
}
