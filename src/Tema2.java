import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.*;

public class Tema2 {
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

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

        boolean status = ordersOutputFile.createNewFile();
        status = productsOutputFile.createNewFile();

        Database.ordersWriter = new FileWriter(Constants.outputOrdersPath);
        Database.productsWriter = new FileWriter(Constants.outputProductsPath);
        ExecutorService executorService = Executors.newFixedThreadPool(threadsNumber);

        StringBuilder line = new StringBuilder();
        StringBuilder orderId = new StringBuilder();
        int numberOfProducts;
        while (ordersScanner.hasNextLine()) {
            line.setLength(0);
            orderId.setLength(0);
            line.append(ordersScanner.nextLine());
            int position = 0;
            for (int i = 0; i < line.length(); ++i) {
                if (line.charAt(i) != ',') {
                    orderId.append(line.charAt(i));
                }
                else {
                    position = i + 1;
                    break;
                }
            }
            numberOfProducts = Integer.parseInt(line.substring(position));
            Database.ordersData.put(orderId.toString(), numberOfProducts);
            if (numberOfProducts != 0) {
                Database.activeOrders.add(orderId.toString());
            }
            Database.hasProducts.put(orderId.toString(), numberOfProducts);
            executorService.submit(new OrderWorker(orderId, numberOfProducts, productsInput, threadsNumber));
        }

        executorService.shutdown();
        Database.ordersWriter.flush();
        Database.productsWriter.flush();
    }
}
