import java.util.ArrayList;
import java.util.List;

public class Driver
{
    public static void main(String[] args)
    {
        List<Integer> test = new ArrayList<>();
        test.add(7);
        test.add(3);
        test.add(5);
        Group test1 = new Group(test, 20);
        test1.printPermutations();
    }
}
