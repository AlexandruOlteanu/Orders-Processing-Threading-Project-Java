import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {

        if (args.length != 2) {
            System.err.println("Wrong number of arguments, to run it use the following format:\n");
            System.out.println("java Main <input file> <number of threads>");
            return;
        }

        File ordersInput = new File(args[0] + "/orders.txt");
        File productsInput = new File(args[0] + "/order_products.txt");
        int threadsNumber = Integer.parseInt(args[1]);

        Scanner ordersScanner = new Scanner(ordersInput);
        Scanner productsScanner = new Scanner(productsInput);

        

    }
}
