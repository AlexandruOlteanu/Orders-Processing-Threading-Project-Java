import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Comparator;
import java.util.Scanner;
import java.util.concurrent.*;

public class Tema2 {
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

        if (args.length != 2) {
            System.err.println("Wrong number of arguments, to run it use the following format:\n");
            System.out.println("java Main <input file> <number of threads>");
            return;
        }

        FileInputStream ordersInput = new FileInputStream(args[0] + Constants.inputOrdersPath);
        FileInputStream productsInput = new FileInputStream(args[0] + Constants.inputProductsPath);
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

        Tema2.extractOrderData(executorService, ordersInput, threadsNumber);

        executorService = Executors.newFixedThreadPool(threadsNumber);

//        StringBuilder line = new StringBuilder();
//        StringBuilder orderId = new StringBuilder();
//        int numberOfProducts;
//        while (ordersScanner.hasNextLine()) {
//            line.setLength(0);
//            orderId.setLength(0);
//            line.append(ordersScanner.nextLine());
//            int position = 0;
//            for (int i = 0; i < line.length(); ++i) {
//                if (line.charAt(i) != ',') {
//                    orderId.append(line.charAt(i));
//                }
//                else {
//                    position = i + 1;
//                    break;
//                }
//            }
//            numberOfProducts = Integer.parseInt(line.substring(position));
//            Database.ordersData.put(orderId.toString(), numberOfProducts);
//            if (numberOfProducts != 0) {
//                Database.activeOrders.add(orderId.toString());
//            }
//            Database.hasProducts.put(orderId.toString(), numberOfProducts);
//            executorService.submit(new OrderWorker(orderId, numberOfProducts, productsInput, threadsNumber));
//        }

        executorService.shutdown();
        Database.ordersWriter.flush();
        Database.productsWriter.flush();
    }

    public static void extractOrderData(ExecutorService executorService, FileInputStream ordersInput, Integer threadsNumber) throws IOException, InterruptedException {
        FileChannel channel = ordersInput.getChannel();
        int length = Math.toIntExact(channel.size());
        int blockSize = length / threadsNumber;

        Integer id = 1;
        for (int i = 0; i < length; i += blockSize) {
            executorService.submit(new FastRead(channel, i, blockSize, id, "order"));
            ++id;
        }
        int start = length / blockSize * blockSize;
        length = length - start;
        if (length != 0) {
            executorService.submit(new FastRead(channel, start, length, id, "order"));
        }
        executorService.shutdown();
        boolean status = executorService.awaitTermination(100, TimeUnit.SECONDS);

        for (int i = 1; i < Database.firstPartOrder.size(); ++i) {
            StringBuilder line = new StringBuilder();
            if (Database.lastPartOrder.size() > i - 1) {
                line.append(Database.lastPartOrder.get(i - 1).value);
            }
            line.append(Database.firstPartOrder.get(i).value);
            if (!line.isEmpty()) {
                Database.orders.add(Functions.processLine(line.toString()));
            }
        }
        int all = 0;
        for (Order order: Database.orders) {
            System.out.println(order.orderId + " " + order.numberOfProducts);
            ++all;
        }
        System.out.println(all);
    }
}
