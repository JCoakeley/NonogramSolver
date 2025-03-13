import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Group
{
    List<Integer> clues;
    List<int[]> binaryPermutations = new ArrayList<>();
    List<int[]> permutations = new ArrayList<>();
    int size;


    public Group(List<Integer> clues, int size)
    {
        this.clues = clues;
        this.size = size;
        createBinaryPermutations();
        createPermutations();
    }


    public int binaryLength()
    {
        int clueSum = 0;
        for(Integer clueValue : clues)
            clueSum += clueValue;

        return clues.size() + (size-(clueSum + clues.size()-1));
    }


    public void printPermutations()
    {
        for(int[] temp:binaryPermutations)
        {
            for(int i:temp)
                System.out.print(i);
            System.out.println();
        }
        for(int[] temp:permutations)
        {
            for(int i:temp)
                System.out.print(i);
            System.out.println();
        }
    }


    public int[] commonPermutation()
    {
        int[] output = new int[size];
        int[] primary = permutations.getFirst();
        boolean match;

        for(int i=0; i<size; ++i)
        {
            match = true;
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


    public void managePermutations(int[] arr)
    {
        for(int i=0; i<arr.length; ++i)
        {
            if(arr[i]==-1)
            {
                for(int j=0; j<permutations.size(); ++j)
                {
                    int[] temp = permutations.get(j);
                    if(temp[i] != 0)
                    {
                        permutations.remove(j);
                        --j;
                    }
                }
            }
            else if(arr[i]==1)
            {
                for(int j=0; j<permutations.size(); ++j)
                {
                    int[] temp = permutations.get(j);
                    if(temp[i] <= 0)
                    {
                        permutations.remove(j);
                        --j;
                    }
                }
            }
        }
    }


    public void createPermutations()
    {
        for(int[] binary:binaryPermutations)
        {
            int[] array1 = new int[size];
            int clueIndex = 0;
            int binaryIndex = 0;
            int count = 0;

            for(int i=0; i<array1.length; ++i)
            {
                if(binary[binaryIndex]==0)
                {
                    array1[i] = 0;
                    ++binaryIndex;
                }
                else
                {
                    if(clueIndex< clues.size())
                    {
                        if(count< clues.get(clueIndex))
                        {
                            array1[i] = 1;
                            ++count;
                        }
                        else
                        {
                            array1[i] = 0;
                            count = 0;
                            ++clueIndex;
                            ++binaryIndex;
                        }
                    }
                }
            }
            permutations.add(array1);
        }
    }


    public void createBinaryPermutations()
    {
        String num;
        String regex = "^([^1]*" + "1[^1]*".repeat(clues.size()) + ")$";
        Pattern pattern = Pattern.compile(regex);
        int length = binaryLength();
        for(int i=0; i<(Math.pow(2, length)); ++i)
        {
            num = Integer.toBinaryString(i);
            if(pattern.matcher(num).matches())
            {
                num = "0".repeat(length-num.length()) + num;

                int[] temp = new int[num.length()];
                for(int j=0; j<num.length(); ++j)
                    temp[j] = Integer.parseInt(String.valueOf(num.charAt(j)));

                binaryPermutations.add(temp);
            }
        }
    }


    public int[] Overlap()
    {
        int[] output = new int[size];
        int[] array1 = new int[size];
        int[] array2 = new int[size];
        int index = 0;
        int value = 1;
        int count = 0;

        for(int i=0; i<array1.length; ++i)
        {
            if(index< clues.size())
            {
                if(count< clues.get(index))
                {
                    array1[i] = value;
                    ++count;
                }
                else
                {
                    array1[i] = 0;
                    count = 0;
                    ++index;
                    ++value;
                }
            }
            else
                array1[i] = 0;
        }

        value = clues.size();
        index = clues.size()-1;
        count = 0;
        for(int i=array2.length-1; i>-1; --i)
        {
            if(index>-1)
            {
                if(count< clues.get(index))
                {
                    array2[i] = value;
                    ++count;
                }
                else
                {
                    array2[i] = 0;
                    count = 0;
                    --index;
                    --value;
                }
            }
            else
                array2[i] = 0;
        }

        for(int i=0; i<output.length; ++i)
        {
            if(array1[i]==array2[i] && array1[i]!=0)
                output[i] = 1;
            else
                output[i] = 0;
        }
        return output;
    }
}
