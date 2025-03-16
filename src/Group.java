import java.util.ArrayList;
import java.util.regex.Pattern;

public class Group
{
    ArrayList<Integer> clues;
    ArrayList<int[]> binaryPermutations = new ArrayList<>();
    ArrayList<int[]> permutations = new ArrayList<>();
    int size;


    /**
     * Constructor with parameters that take an ArrayList of clues
     * and an int for the size of the play field. ie clue: 7, 3, 5
     * to fit in 20(size) squares.
     * @param clues ArrayList of Integers that
     * @param size int The
     */
    public Group(ArrayList<Integer> clues, int size)
    {
        this.clues = clues;
        this.size = size;
        createBinaryPermutations();
        createPermutations();
    }

    /**
     * A method for returning the length needed to print out the clues
     * when printing the solved game board.
     * @return int for the amount of space needed to print the clues to the console
     */
    public int printingClueLength()
    {
        int length = 0;

        for(Integer clue:clues)
        {
            if(clue<10)
                length += 2;
            else
                length += 3;
        }
        return length-1;
    }

    /**
     * Method that calculates and returns the number of clue elements plus
     * the extra blank spaces for a given row/column.
     * @return int Number of clues + number of extra blank spaces.
     */
    public int binaryLength()
    {
        //Starts the sum at -1 to account for there being 1 less required
        //space than number of clue number.
        int clueSum = -1;

        //A loop that adds the values of each clue together plus the 1 space
        //required between each clue.
        for(Integer clueValue : clues)
            clueSum += clueValue + 1;

        return clues.size() + (size-clueSum);
    }

    /**
     * A method that prints to console the binary permutations line by line
     * followed by the full permutations line by line. Method used to verify
     * permutations are being generated as intended.
     */
//    public void printPermutations()
//    {
//        //Loop for printing each of the binary permutations line by line.
//        for(int[] temp:binaryPermutations)
//        {
//            //Loop to print out each element of a binary number held in an array.
//            for(int i:temp)
//                System.out.print(i);
//            System.out.println();
//        }
//        //Loop for printing each permutation line by line.
//        for(int[] temp:permutations)
//        {
//            //Loop for printing out each element of an individual permutation array.
//            for(int i:temp)
//                System.out.print(i);
//            System.out.println();
//        }
//    }


    /**
     * A method that compares the same element of each array of the ArrayList
     * permutations to find any element that may make in all arrays and return
     * an output array matching the common elements.
     * @return int[] of current known values in the solution.
     */
    public int[] commonPermutation()
    {
        int[] output = new int[size];
        //Assigns the first array of permutations to use to compare with all the
        //other arrays of permutations.
        int[] primary = permutations.getFirst();
        boolean match;

        //A loop the iterates through each element of the arrays in the
        //ArrayList permutations to compare their values.
        for(int i=0; i<size; ++i)
        {
            match = true;
            //A loop for iterating through each array in permutations
            for(int[] secondary:permutations)
            {
                if(primary[i]!= secondary[i])
                {
                    match = false;
                    break;
                }
            }
            if(match)
            {
                if(primary[i]>0)
                    output[i] = 1;
                else
                    output[i] = -1;
            }
        }
        return output;
    }

    /**
     * A method that compares each permutation to the current state of the
     * solution and removes any permutation that conflicts as they are
     * no longer a valid permutation.
     * @param arr The current state of the solution on GameBoard
     */
    public void managePermutations(int[] arr)
    {
        //A loop that iterates through each element of arr and takes
        //action depending on its value.
        for(int i=0; i<arr.length; ++i)
        {
            //A value of -1 is used to represent a known blank space
            //A value of 0 is used to represent an unknown square.
            if(arr[i]==-1)
            {
                /*
                A loop that iterates through each array in the ArrayList
                permutations to see if their ith element matches the ith
                element of arr.
                */
                for(int j=0; j<permutations.size(); ++j)
                {
                    int[] temp = permutations.get(j);
                    if(temp[i] != 0)
                    {
                        /*
                        Removing the permutation that doesn't match with the current
                        state of the solution and decrement j as the next permutation
                        to check is at the same index as the deleted one.
                        */
                        permutations.remove(j);
                        --j;
                    }
                }
            }
            //A value of 1 is used to represent a known filled space.
            else if(arr[i]==1)
            {
                /*
                A loop that iterates through each array in the ArrayList
                permutations to see if their ith element matches the ith
                element of arr.
                */
                for(int j=0; j<permutations.size(); ++j)
                {
                    int[] temp = permutations.get(j);
                    if(temp[i] <= 0)
                    {
                        /*
                        Removing the permutation that doesn't match with the current
                        state of the solution and decrement j as the next permutation
                        to check is at the same index as the deleted one.
                        */
                        permutations.remove(j);
                        --j;
                    }
                }
            }
        }
    }


    /**
     * Creates all permutations of the clue numbers based on the binary permutations
     * These permutations are used as possible solutions to a row/column.
     */
    public void createPermutations()
    {
        /*
        A loop to iterate through each array in the ArrayList binaryPermutations.
        Each array is used as a template to create a full permutation which are
        add to the ArrayList permutations.
        */
        for(int[] binary:binaryPermutations)
        {
            int[] arr = new int[size];
            int clueIndex = 0;
            int binaryIndex = 0;
            int count = 0;

            /*
            A loop for iterating through each element of the int[] arr and
            assigning values based on the template binary permutation and the
            clues.
            */
            for(int i = 0; i< arr.length; ++i)
            {
                if(binary[binaryIndex]==0)
                {
                    arr[i] = 0;
                    ++binaryIndex;
                }
                else
                {
                    if(clueIndex<clues.size())
                    {
                        if(count<clues.get(clueIndex))
                        {
                            arr[i] = 1;
                            ++count;
                        }
                        else
                        {
                            arr[i] = 0;
                            count = 0;
                            ++clueIndex;
                            ++binaryIndex;
                        }
                    }
                }
            }
            permutations.add(arr);
        }
    }


    /**
     * A method that creates all possible binary numbers of a fixed length
     * and with a specified number of 1s.
     */
    public void createBinaryPermutations()
    {
        String num;
        //Regular expression that will only match with the specified number of 1s
        String regex = "^(0*" + "10*".repeat(clues.size()) + ")$";
        Pattern pattern = Pattern.compile(regex);
        int length = binaryLength();

        /*
        A loop that generates all integers up to a specified power of 2 and
        converts them to binary. If the binary number has the correct number
        of 1s leading 0s are added to bring it up to a specified length and
        added to the ArrayList binaryPermutations.
        */
        for(int i=0; i<(Math.pow(2, length)); ++i)
        {
            num = Integer.toBinaryString(i);
            if(pattern.matcher(num).matches())
            {
                num = "0".repeat(length-num.length()) + num;
                int[] temp = new int[num.length()];

                //A loop to convert the binary string into an int[]
                for(int j=0; j<num.length(); ++j)
                    temp[j] = Character.getNumericValue(num.charAt(j));

                binaryPermutations.add(temp);
            }
        }
    }


    /**
     * A method for determining if there's any overlap between individual clues in a
     * row/column when the clue sequence starts from either end of the grid.
     * @return int[] Known filled squares of the solution based on a simple overlap check
     */
    public int[] overlap()
    {
        int[] arr = new int[size];
        int index = 0;
        int value = 1;
        int count = 0;

        /*
        A loop for filling the int[] arr with a possible solution
        starting at the first element and no additional blank spaces
        between each clue number.
        */
        for(int i = 0; i< arr.length; ++i)
        {
            if(index<clues.size())
            {
                if(count<clues.get(index))
                {
                    arr[i] = value;
                    ++count;
                }
                else
                {
                    arr[i] = 0;
                    count = 0;
                    ++index;
                    ++value;
                }
            }
            else
                arr[i] = 0;
        }

        value = clues.size();
        index = clues.size()-1;
        count = 0;

        /*
        A loop for comparing the int[] arr with the possible solution
        that starts at the last element and has no additional blank spaces
        between each clue number.
        */
        for(int i=arr.length-1; i>-1; --i)
        {
            if(index>-1)
            {
                if(count<clues.get(index))
                {
                    if(arr[i] == value && arr[i] !=0)
                    {
                        arr[i] = 1;
                    }
                    else
                        arr[i] = 0;
                    ++count;
                }
                else
                {
                    arr[i] = 0;
                    count = 0;
                    --index;
                    --value;
                }
            }
            else
                arr[i] = 0;
        }
        return arr;
    }
}
