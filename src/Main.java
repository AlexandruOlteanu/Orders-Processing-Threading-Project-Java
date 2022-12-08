import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

class Constants {
    static final String inputOrdersPath = "/orders.txt";
    static final String inputProductsPath = "/order_products.txt";
    static final String outputOrdersPath = "orders_out.txt";
    static final String outputProductsPath = "order_products_out.txt";
}

public class Main {
    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println("Wrong number of arguments, to run it use the following format:\n");
            System.out.println("java Main <input file> <number of threads>");
            return;
        }

        File ordersInput = new File(args[0] + Constants.inputOrdersPath);
        File productsInput = new File(args[0] + Constants.inputProductsPath);
        int threadsNumber = Integer.parseInt(args[1]);

        Scanner ordersScanner = new Scanner(ordersInput);
        Scanner productsScanner = new Scanner(productsInput);

        File ordersOutputFile = new File(Constants.outputOrdersPath);
        File productsOutputFile = new File(Constants.outputProductsPath);

        ordersOutputFile.createNewFile();
        productsOutputFile.createNewFile();

        FileWriter ordersWriter = new FileWriter(Constants.outputOrdersPath);
        FileWriter productsWriter = new FileWriter(Constants.outputProductsPath);

        ordersWriter.write("Alex");
        productsWriter.write("Bogdan");

        ordersWriter.close();
        productsWriter.close();

    }
}
