import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        int a, b;
        Scanner scan = new Scanner(new File("input.txt"));
        a = scan.nextInt();
        b = scan.nextInt();
        PrintWriter printWriter = new PrintWriter(new File("output.txt"));
        printWriter.print(a + b);
        printWriter.flush();
    }
}
