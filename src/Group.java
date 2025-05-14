import java.util.Arrays;
import java.util.BitSet;

/**
 * Represents a single row or column group in a Nonogram puzzle.
 * Handles clue interpretation, permutation generation, filtering, and deduction logic.
 * Supports edge pattern deduction, permutation-based filtering, and consistent cell detection.
 */
public class Group
{
    // Constant that determines if a group is a high permutation group.
    private static final int PERMUTATION_LIMIT = 250000;
    // Constant that sets the solution fill threshold before generation.
    private static final double GENERATION_THRESHOLD = 0.2;

    private final int[] clues;
    private final int size;
    private final int groupId;
    private final boolean isHighPermutations;
    private Long[] permutations;
    private final long sizeBits;

    private boolean permutationsGenerated = false;
    private int maxPermutationCount = 0;
    private int filteredPermutationCount = 0;
    private int permutationCount = 0;

    private long partialBits = 0L;
    private long maskBits = 0L;
    private BitSet validMask;

    enum Direction {Start, End}

    /**
     * Constructs a Group with given clues, total length, and unique ID.
     * Pre-allocates space for permutation storage based on combinatorial estimates.
     *
     * @param clues The clue sequence for this group.
     * @param size The number of cells in this group.
     * @param groupId A unique identifier for logging or referencing.
     */
    public Group(int[] clues, int size, int groupId, long sizeBits)
    {
        this.clues = clues;
        this.size = size;
        this.groupId = groupId;
        this.sizeBits = sizeBits;

        int n = size + clues.length - minRequiredLength();

        maxPermutationCount = Util.nCr(n, clues.length);
        isHighPermutations = isHighPermutationGroup();

        if (!isHighPermutations)
        {
            permutations = new Long[maxPermutationCount];
            Timing.addAllocatedPermutationCount(maxPermutationCount);
        }
    }

    /**
     * Getter for the groupId.
     *
     * @return The groupId
     */
    public int getGroupId()
    {
        return groupId;
    }

    /**
     * Getter for state of permutationsGenerated.
     *
     * @return The state of the boolean permutationsGenerated.
     */
    public boolean getPermutationsGenerated()
    {
        return permutationsGenerated;
    }

    /**
     * Getter for maxPermutations.
     *
     * @return The maximum number of permutation for the group.
     */
    public long getMaxPermutationCount()
    {
        return maxPermutationCount;
    }

    /**
     * Recursively generates all possible solution permutations. Permutations are
     * verified to be compatible with the current state of the partial solution
     * before they are stored in the long[]. Permutations are built from the
     * first clue to the last. Everytime a clue is placed the resulting partial
     * permutation as checked against the partial solution to early-prune
     * incompatible groups of permutations.
     *
     * @param clueIndex The index of the clue to be placed into the permutation next.
     * @param current The current state of the permutation being generated.
     * @param position The current index of the permutation to add the beginning of the next clue.
     */
    public void generatePermutationsStart(int clueIndex, long current, int position, boolean countOnly)
    {
        if (clueIndex >= clues.length)
        {
            if (((current & maskBits) ^ partialBits) == 0)
            {
                if (countOnly)
                    filteredPermutationCount++;
                else
                    permutations[permutationCount++] = current;
            }

            return;
        }

        int groupSize = clues[clueIndex];
        int maxStart = size - totalRemainingLengthStart(clueIndex);

        for (int start = position; start <= maxStart; start++)
        {
            Timing.addAttemptedPermutationCount();

            /*
            Places a binary 1 at the start on the long, shifts the binary 1 over
            the size of the clue. Then subtracts an int 1 to have a string of
            binary 1s the same length as the clue size. This string of binary 1s
            is then shifted over to its starting position.
             */
            long groupBits = ((1L << groupSize) - 1) << start;
            long newBits = current | groupBits;

            int newPosition = start + groupSize + 1; // At least 1 zero gap

            long writtenBitsMask = (1L << newPosition) - 1;
            long compareMask = writtenBitsMask & maskBits;

            // Filtering the partial permutation against the partial solution ensuring
            // to only check over the range of the partial permutation.
            if (((newBits & compareMask) ^ (partialBits & compareMask)) != 0)
            {
                Timing.addEarlyPrunedPermutationCount();
                continue;
            }

            generatePermutationsStart(clueIndex + 1, newBits, newPosition, countOnly);
        }
    }

    /**
     * Recursively generates all possible solution permutations. Permutations are
     * verified to be compatible with the current state of the partial solution
     * before they are stored in the Long[]. Permutations are built from the
     * last clue to the first. Everytime a clue is placed the resulting partial
     * permutation as checked against the partial solution to early-prune
     * incompatible groups of permutations.
     *
     * @param clueIndex The index of the clue to be placed into the permutation next.
     * @param current The current state of the permutation being generated.
     * @param position The current index of the permutation to add the beginning of the next clue.
     */
    public void generatePermutationsEnd(int clueIndex, long current, int position, boolean countOnly)
    {
        if (clueIndex < 0)
        {
            if (((current & maskBits) ^ partialBits) == 0)
            {
                if (countOnly)
                    filteredPermutationCount++;
                else
                    permutations[permutationCount++] = current;
            }

            return;
        }

        int groupSize = clues[clueIndex];
        int maxStart = size - totalRemainingLengthEnd(clueIndex);

        for (int start = position; start <= maxStart; start++)
        {
            Timing.addAttemptedPermutationCount();
            /*\
            Places a binary 1 at the end position of the permutation which is not a fixed
            bit of the long ei. 2^n. Then subtracts a binary 1 shifted from the end position
            closer to the 0th position the size of the clue ie 2^n - 2^(n-cluesize). This
            results in a string of 1s at the end of the permutation the length of the clue.
            This string of 1s is then shifted over to the starting position.
             */
            long groupBits = (sizeBits - (sizeBits >> groupSize)) >> start;
            long newBits = current | groupBits;

            int newPosition = start + groupSize + 1; // At least 1 zero gap

            long writtenBitsMask = sizeBits - (sizeBits >> newPosition);
            long compareMask = writtenBitsMask & maskBits;

            if (((newBits & compareMask) ^ (partialBits & compareMask)) != 0)
            {
                Timing.addEarlyPrunedPermutationCount();
                continue;
            }

            generatePermutationsEnd(clueIndex - 1, newBits, newPosition, countOnly);
        }
    }

    /**
     *
     *
     * @param partial The current partial row/column state.
     * @return A string to denote which direction to start building the permutations from.
     */
    private Direction directionForGeneration(int[] partial)
    {
        int startFilled = 0;
        int endFilled = 0;

        for (int i = 0; i < partial.length / 2; i++)
        {
            if (partial[i] != 0)
                startFilled++;
            if (partial[size - (i + 1)] != 0)
                endFilled++;
        }

        if (startFilled >= endFilled)
            return Direction.Start;
        else
            return Direction.End;
    }

    /**
     * Updates the partialBits and maskBits based on the current state
     * of the partial solution for use in filtering out permutations.
     *
     * @param partial The current partial row/column state.
     */
    public void updateBitMasks(int[] partial)
    {
        partialBits = 0L;
        maskBits = 0L;

        for (int i = 0; i < partial.length; i++)
        {
            if (partial[i] != 0)
            {
                maskBits |= (1L << i);

                if (partial[i] == 1)
                    partialBits |= (1L << i);
            }
        }
    }

    /**
     * Filters the current permutation set using bitwise comparison against the partial mask.
     * Updates the validMask to invalidate inconsistent permutations.
     */
    public void filterPermutations() {

        for (int i = validMask.nextSetBit(0); i >= 0; i = validMask.nextSetBit(i + 1))
            if (((permutations[i] & maskBits) ^ partialBits) != 0)
                validMask.clear(i);
    }

    /**
     *
     *
     * @param partial The current partial row/column state.
     * @return The consistent pattern from valid permutations.
     */
    public int[] generateConsistentPattern(int[] partial)
    {
        int[] result = Arrays.copyOf(partial, partial.length);
        long andMask = -1L; // all 1s
        long orMask = 0L;

        for (int i = validMask.nextSetBit(0); i >= 0; i = validMask.nextSetBit(i + 1))
        {
            andMask &= permutations[i];
            orMask |= permutations[i];
        }

        for (int i = 0; i < size; i++)
        {
            if (result[i] != 0)
                continue;

            long bitMask = 1L << i;

            if ((andMask & bitMask) != 0)
                result[i] = 1;
            else if ((orMask & bitMask) == 0)
                result[i] = -1;
        }

        return result;
    }

    private int totalRemainingLengthStart(int fromIndex)
    {
        int total = 0;
        for (int i = fromIndex; i < clues.length; i++)
            total += clues[i];

        total += (clues.length - fromIndex - 1);
        return total;
    }

    private int totalRemainingLengthEnd(int fromIndex)
    {
        int total = 0;
        for (int i = fromIndex; i >= 0; i--)
            total += clues[i];

        total += fromIndex;
        return total;
    }

    public boolean isHighPermutationGroup()
    {
        return maxPermutationCount > PERMUTATION_LIMIT;
    }

    public boolean isReadyToGenerate(int[] partial) {
        int knownCount = 0;
        for (int val : partial)
            if (val != 0)
                knownCount++;

        return (double) knownCount / size >= GENERATION_THRESHOLD;
    }

    public int minRequiredLength()
    {
        int total = 0;

        for (int clue : clues)
            total += clue;

        total += clues.length - 1;
        return total;
    }

    /**
     * Forces full permutation generation regardless of threshold.
     * Used when progress has stalled or a group is known to be safe to generate.
     *
     * @param partial The current partial row/column state.
     * @return The consistent pattern from valid permutations.
     */
    public int[] forceGeneration(int[] partial)
    {

        if (isHighPermutations)
        {
            Timing.timingStart(Timing.Timings.Initialization);
            permutations = new Long[maxPermutationCount];
            Timing.addAllocatedPermutationCount(maxPermutationCount);
            Timing.timingEnd(Timing.Timings.Initialization);
        }

        Timing.timingStart(Timing.Timings.Generation);
        Direction generationDirection = directionForGeneration(partial);

        if (generationDirection == Direction.Start)
            generatePermutationsStart(0, 0L, 0, false);
        else
            generatePermutationsEnd(clues.length - 1, 0L, 0, false);

        Timing.addPermutationCount(permutationCount);

        validMask = new BitSet(permutationCount);
        validMask.set(0, permutationCount);
        permutationsGenerated = true;
        Timing.timingEnd(Timing.Timings.Generation);

        Timing.timingStart(Timing.Timings.ConsistentPattern);
        int[] result = generateConsistentPattern(partial);
        Timing.timingEnd(Timing.Timings.ConsistentPattern);

        Timing.addMaxPermutationCount(maxPermutationCount);

        String generationStats = String.format("Force Generation: Group %2d: Max Permutations: %,10d Stored Permutations: %,10d%n", groupId, maxPermutationCount, permutationCount);
        GenerationStatWriter.addGenerationStats(generationStats);

        return result;
    }

    /**
     * Main logic for updating this group based on its current partial solution.
     * Performs filtering, generation, or edge logic depending on the current state.
     *
     * @param partial The current partial row/column state.
     * @return The updated array with any new deductions applied.
     */
    public int[] updateGroup(int[] partial)
    {
        updateBitMasks(partial);

        if (permutationsGenerated)
        {
            Timing.timingStart(Timing.Timings.Filtering);
            filterPermutations();
            Timing.timingEnd(Timing.Timings.Filtering);
        }
        else
        {
            Timing.timingStart(Timing.Timings.EdgePattern);
            int[] edgeLogic = deduceEdgePatterns(partial);
            Timing.timingEnd(Timing.Timings.EdgePattern);

            if (hasNewInfo(partial, edgeLogic))
                return edgeLogic;

            if (isHighPermutations && !isReadyToGenerate(partial))
                return partial;
            Direction generationDirection = directionForGeneration(partial);

            if (isHighPermutations)
            {
                Timing.timingStart(Timing.Timings.CountGeneration);
                if (generationDirection == Direction.Start)
                    generatePermutationsStart(0, 0L, 0, true);
                else
                    generatePermutationsEnd(clues.length - 1, 0L, 0, true);
                Timing.timingEnd(Timing.Timings.CountGeneration);

                Timing.timingStart(Timing.Timings.Initialization);
                permutations = new Long[filteredPermutationCount];
                Timing.addAllocatedPermutationCount(filteredPermutationCount);
                Timing.timingEnd(Timing.Timings.Initialization);
            }

            Timing.timingStart(Timing.Timings.Generation);
            if (generationDirection == Direction.Start)
                generatePermutationsStart(0, 0L, 0, false);
            else
                generatePermutationsEnd(clues.length - 1, 0L, 0, false);

            Timing.addPermutationCount(permutationCount);

            validMask = new BitSet(permutationCount);
            validMask.set(0, permutationCount);
            permutationsGenerated = true;
            Timing.timingEnd(Timing.Timings.Generation);

            Timing.addMaxPermutationCount(maxPermutationCount);
            String generationStats = String.format("      Generation: Group %2d: Max Permutations: %,10d Stored Permutations: %,10d%n", groupId, maxPermutationCount, permutationCount);
            GenerationStatWriter.addGenerationStats(generationStats);
        }

        Timing.timingStart(Timing.Timings.ConsistentPattern);
        int[] result = generateConsistentPattern(partial);
        Timing.timingEnd(Timing.Timings.ConsistentPattern);

        return result;
    }

    private boolean hasNewInfo(int[] original, int[] updated) {
        for (int i = 0; i < original.length; i++)
            if (original[i] == 0 && updated[i] != 0)
                return true;

        return false;
    }

    /**
     * Applies custom edge-based logic to fill certain cells near the left and right edges
     * based on the first and last clues and known filled cells.
     *
     * @param partial The current partial row/column state.
     * @return An updated version with edge-derived deductions filled in.
     */
    public int[] deduceEdgePatterns(int[] partial)
    {
        int[] result = Arrays.copyOf(partial, partial.length);

        int left = 0;
        for (int i = 0; i <= clues[0]; i++)
        {
            while (result[left] == -1)
                ++left;

            if (result[i + left] == 1)
            {
                if (i == 0)
                {
                    for (int j = left + 1; j < clues[0] + left; ++j)
                        result[j] = 1;

                    result[clues[0] + left] = -1;
                    break;

                } else if (i == clues[0])
                    result[left++] = -1;

                else
                {
                    for (int j = left + i + 1; j < clues[0] + left; ++j)
                        result[j] = 1;

                    while (result[clues[0] + left] == 1)
                        result[left++] = -1;

                    if (result[left] == 1)
                    {
                        result[clues[0] + left] = -1;
                    }

                    for (int j = left + clues[0]; j < left + (clues[0] * 2) && j < partial.length; ++j)
                        if (result[j] == -1)
                        {
                            for (int k = left + clues[0] - 1; k > j - clues[0] - 1; --k)
                                result[k] = 1;

                            break;
                        }
                    break;
                }
            } else if (i > 0 && i < clues[0] && result[i + left] == -1)
            {
                for (int j = 0; j < i; ++j)
                    result[j] = -1;
            }
        }

        int temp;

        for(
                int i = 0;
                i<=result.length/2; ++i)

        {
            temp = result[i];
            result[i] = result[result.length - i - 1];
            result[result.length - i - 1] = temp;
        }

        int right = 0;
        for(int i = 0;i<=clues[clues.length-1]; ++i)

        {
            while (result[right] == -1)
                ++right;

            if (result[i + right] == 1)
            {
                if (i == 0)
                {
                    for (int j = right + 1; j < clues[clues.length-1] + right; ++j)
                        result[j] = 1;

                    result[clues[clues.length-1] + right] = -1;



                    break;
                } else if (i == clues[clues.length-1])
                    result[right++] = -1;

                else
                {
                    for (int j = right + i + 1; j < clues[clues.length-1] + right; ++j)
                        result[j] = 1;

                    while (result[clues[clues.length-1] + right] == 1)
                        result[right++] = -1;

                    if (result[right] == 1)
                    {
                        result[clues[clues.length-1] + right] = -1;

                    }

                    for (int j = right + clues[clues.length-1]; j < right + (clues[clues.length-1] * 2) && j < partial.length; ++j)
                        if (result[j] == -1)
                        {
                            for (int k = right + clues[clues.length-1] - 1; k > j - clues[clues.length-1] - 1; --k)
                                result[k] = 1;

                            break;
                        }
                    break;
                }
            } else if (i > 0 && i < clues[clues.length-1] && result[i + right] == -1)
            {
                for (int j = 0; j < i; ++j)
                    result[j] = -1;
            }
        }

        for(
                int i = 0;
                i<=result.length/2; ++i)

        {
            temp = result[i];
            result[i] = result[result.length - i - 1];
            result[result.length - i - 1] = temp;
        }
        return result;
    }

    public int[] overlap()
    {
        int[] result = new int[size];
        int leftPos = 0;
        int rightPos = size - minRequiredLength();

        for (int clue : clues)
        {
            int leftEnd = leftPos + clue;
            leftPos = leftEnd + 1;

            int rightStart = rightPos;
            rightPos += clue + 1;

            for (int j = rightStart; j < leftEnd; ++j)
                result[j] = 1;
        }
        return result;
    }
}
