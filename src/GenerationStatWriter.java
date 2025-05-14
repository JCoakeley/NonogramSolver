import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;

public class GenerationStatWriter
{
    private static String generationStats = "";
    public static void writeToFile()
    {
        try (PrintWriter pw = new PrintWriter(new FileWriter("Generation Stats.txt")))
        {
            pw.print(generationStats);
        }catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void addGenerationStats(String generationStats)
    {
        GenerationStatWriter.generationStats += generationStats;
    }
}
