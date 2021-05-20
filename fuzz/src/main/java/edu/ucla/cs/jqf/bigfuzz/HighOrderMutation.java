package edu.ucla.cs.jqf.bigfuzz;

import org.graalvm.compiler.core.common.type.ArithmeticOpTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static edu.ucla.cs.jqf.bigfuzz.HighOrderMutation.HighOrderMutationMethod.*;

public class HighOrderMutation {
    // For each HighOrderMutationMethod enum, a set of other enums is provided to indicate which mutations can NOT be applied after the mutation of the list is applied.
    private static final HighOrderMutation.HighOrderMutationMethod[] noMutationStackExcludeRule = {}; // TODO Is this needed?
    private static final HighOrderMutation.HighOrderMutationMethod[] ChangeValueStackExcludeRule = {RemoveElement, EmptyColumn, ChangeValue};
    private static final HighOrderMutation.HighOrderMutationMethod[] ChangeTypeStackExcludeRule = {RemoveElement, EmptyColumn, ChangeType};
    private static final HighOrderMutation.HighOrderMutationMethod[] RemoveElementStackExcludeRule = {ChangeValue, ChangeType, AddElement};
    private static final HighOrderMutation.HighOrderMutationMethod[] AddElementStackExcludeRule = {AddElement, RemoveElement};
    private static final HighOrderMutation.HighOrderMutationMethod[] EmptyColumnStackExcludeRule = {ChangeValue, ChangeType, RemoveElement, AddElement, EmptyColumn};
    private static final HighOrderMutation.HighOrderMutationMethod[] ChangeDelimiterStackExcludeRule = {ChangeDelimiter};

    // Indicates whether a mutation is active or not
    private static final boolean noMutationActive = true;
    private static final boolean ChangeValueActive = true;
    private static final boolean ChangeTypeActive = true;
    private static final boolean RemoveElementActive = true;
    private static final boolean AddElementActive = true;
    private static final boolean EmptyColumnActive = true;
    private static final boolean ChangeDelimiterActive = true;


    public static HighOrderMutationMethod getRandomMutation(Random r) {
        if (r == null) {
            r = new Random();
        }
        int range = getActiveHighOrderMutationMethodList().length;
        return intToHighOrderMutationMethod(r.nextInt(range));
    }

    public static HighOrderMutationMethod getRandomSmartMutation(Random r, ArrayList<HighOrderMutationMethod> highOrderMutationMethods) {
        if (r == null) {
            r = new Random();
        }
        ArrayList<HighOrderMutationMethod> availableMutations = getMutationListFromAppliedMutations(highOrderMutationMethods);

        if(availableMutations.size() == 0) {
            return NoMutation;
        }

        int randomMutationSelection = r.nextInt(availableMutations.size());
        return availableMutations.get(randomMutationSelection);
    }

    private static ArrayList<HighOrderMutationMethod> getMutationListFromAppliedMutations(ArrayList<HighOrderMutationMethod> highOrderMutationMethods) {
        ArrayList<HighOrderMutationMethod> res = new ArrayList<>(Arrays.asList(getActiveHighOrderMutationMethodList()));

        for (HighOrderMutationMethod usedMethod :
                highOrderMutationMethods) {
            applyExclusionRulesToList(res, usedMethod);
        }
        return res;
    }

    private static void applyExclusionRulesToList(ArrayList<HighOrderMutationMethod> res, HighOrderMutationMethod usedMethod) {
        HighOrderMutationMethod[] exclusionList;
        switch (usedMethod) {
            case ChangeValue:
                exclusionList = ChangeValueStackExcludeRule;
                break;
            case ChangeType:
                exclusionList = ChangeTypeStackExcludeRule;
                break;
            case RemoveElement:
                exclusionList = RemoveElementStackExcludeRule;
                break;
            case AddElement:
                exclusionList = AddElementStackExcludeRule;
                break;
            case EmptyColumn:
                exclusionList = EmptyColumnStackExcludeRule;
                break;
            case ChangeDelimiter:
                exclusionList = ChangeDelimiterStackExcludeRule;
                break;
            default:
                exclusionList = null;
        }
        if(exclusionList == null) {
            return;
        }
       res.removeAll(Arrays.asList(exclusionList));
    }

    //         *                    0: random change value   (M1)
//     *                    1: random change into float (M2)
//     *                    2: random insert value in element (M4)
//     *                    3: random delete one column/element (M5)
//     *                    4: random add one column/element (?)
//     *                    5: Empty String (M6)
//     *                    6: random delimiter (M3), not applied in this method
    public enum HighOrderMutationMethod {
        NoMutation,
        ChangeValue,
        ChangeType,
        RemoveElement,
        AddElement,
        EmptyColumn,
        ChangeDelimiter
    }

    public static HighOrderMutationMethod[] getActiveHighOrderMutationMethodList() {
        ArrayList<HighOrderMutationMethod> res = new ArrayList<>();
        for (HighOrderMutationMethod h :
                HighOrderMutationMethod.values()) {
            switch (h) {
                case ChangeValue:
                    if (ChangeValueActive) {
                        res.add(ChangeValue);
                    }
                    break;
                case ChangeType:
                    if (ChangeTypeActive) {
                        res.add(ChangeType);
                    }
                    break;
                case RemoveElement:
                    if (RemoveElementActive) {
                        res.add(RemoveElement);
                    }
                    break;
                case AddElement:
                    if (AddElementActive) {
                        res.add(AddElement);
                    }
                    break;
                case EmptyColumn:
                    if (EmptyColumnActive) {
                        res.add(EmptyColumn);
                    }
                    break;
                case ChangeDelimiter:
                    if (ChangeDelimiterActive) {
                        res.add(ChangeDelimiter);
                    }
                    break;
            }
        }
        return (HighOrderMutationMethod[]) res.toArray();
    }


    public static HighOrderMutation.HighOrderMutationMethod intToHighOrderMutationMethod(int i) {
        switch (i) {
            case 0:
                return ChangeValue;
            case 1:
                return ChangeType;
            case 2:
                return RemoveElement;
            case 3:
                return AddElement;
            case 4:
                return EmptyColumn;
            case 5:
                return ChangeDelimiter;
            default:
                return HighOrderMutationMethod.NoMutation;
        }
    }
}
