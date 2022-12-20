import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Scanner;
import java.util.concurrent.*;

public class Tema2 {
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

        if (args.length != 2) {
            System.err.println("Wrong number of arguments, to run it use the following format:\n");
            System.out.println("java Main <input file> <number of threads>");
            return;
        }

        final String ordersInputPath = args[0] + Constants.inputOrdersPath;
        final String productsInputPath = args[0] + Constants.inputProductsPath;

        FileInputStream ordersInput = new FileInputStream(ordersInputPath);
        int threadsNumber = Integer.parseInt(args[1]);

        File ordersOutputFile = new File(Constants.outputOrdersPath);
        File productsOutputFile = new File(Constants.outputProductsPath);

        boolean status = ordersOutputFile.createNewFile();
        status = productsOutputFile.createNewFile();

        Database.ordersWriter = new FileWriter(Constants.outputOrdersPath);
        Database.productsWriter = new FileWriter(Constants.outputProductsPath);
        ExecutorService ordersExecutorService = Executors.newFixedThreadPool(threadsNumber);
        ExecutorService productsExecutorService = Executors.newFixedThreadPool(threadsNumber);

        FileChannel fileChannel = ordersInput.getChannel();
        long numberOfLines = fileChannel.size() / Constants.APPROXIMATE_ORDER_LINE_SIZE;
        long chunk_size = numberOfLines / threadsNumber;
        int order_data_start = 1, order_data_end = (int) chunk_size;
        for (int i = 0; i < threadsNumber; ++i) {
            if (i < threadsNumber - 1) {
                ordersExecutorService.submit(new OrderWorker(ordersInputPath, productsInputPath, order_data_start, order_data_end, threadsNumber, productsExecutorService, "NOT_FINAL"));
            }
            else {
                ordersExecutorService.submit(new OrderWorker(ordersInputPath, productsInputPath, order_data_start, order_data_end, threadsNumber, productsExecutorService, "FINAL"));
            }
            order_data_start += chunk_size;
            order_data_end += chunk_size;
        }
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
        ordersExecutorService.shutdown();
        ordersExecutorService.awaitTermination(100, TimeUnit.SECONDS);
        productsExecutorService.shutdown();
        Database.ordersWriter.flush();
        Database.productsWriter.flush();
    }
}
