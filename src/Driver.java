import java.util.ArrayList;
import java.util.Arrays;

public class Driver
{
    public static void main(String[] args)
    {

        //Testing inputs to verify correct permutation calculations
        ArrayList<Integer> test = new ArrayList<>();
        test.add(7);
        test.add(3);
        test.add(5);
        Group test1 = new Group(test, 20);
        System.out.println(Arrays.toString(test1.Overlap()));
        //test1.printPermutations();
    }
}
