import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;

public class OrderWorker implements Runnable{

    String ordersInputPath;

    String productsInputPath;

    Integer order_data_start;

    Integer order_data_end;

    String status;

    int threadsNumber;

    ExecutorService productsExecutorService;

    Order order;

    public OrderWorker(String ordersInputPath, String productsInputPath, int order_data_start, int order_data_end, int threadsNumber, ExecutorService productsExecutorService, String status) {
        this.ordersInputPath = ordersInputPath;
        this.productsInputPath = productsInputPath;
        this.order_data_start = order_data_start;
        this.order_data_end = order_data_end;
        this.threadsNumber = threadsNumber;
        this.productsExecutorService = productsExecutorService;
        this.status = status;
    }

    @Override
    public void run() {

        Scanner orderScanner = null;
        try {
            File ordersInput = new File(ordersInputPath);
            orderScanner = new Scanner(ordersInput);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        int lineNumber = 1;
        StringBuilder line = new StringBuilder();
        while (true) {
            if (!status.equals("FINAL") && lineNumber > order_data_end) {
                break;
            }
            line.setLength(Constants.ZERO);
            String value = Functions.readLine(orderScanner);
            if (value == null) {
                break;
            }
            line.append(value);
            if (lineNumber >= order_data_start) {
                order = Functions.processOrderLine(line.toString());
                Database.ordersData.put(order.orderId, order.numberOfProducts);
                if (order.numberOfProducts != 0) {
                    Database.activeOrders.add(order.orderId);
                }
                Database.hasProducts.put(order.orderId, order.numberOfProducts);

                FileInputStream productsInput = null;
                try {
                    productsInput = new FileInputStream(productsInputPath);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                FileChannel fileChannel = productsInput.getChannel();
                long numberOfLines = 0;
                try {
                    numberOfLines = fileChannel.size() / Constants.APPROXIMATE_PRODUCT_LINE_SIZE;
//                    System.out.println(numberOfLines);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                long chunk_size = numberOfLines / threadsNumber;
                int product_data_start = 1, product_data_end = (int) chunk_size;

                for (int i = 0; i < threadsNumber; ++i) {
                    Database.x = 0;
                    if (i < threadsNumber - 1) {
                        productsExecutorService.submit(new ProductWorker(order, productsInputPath, product_data_start, product_data_end, "NOT_FINAL"));
                    }
                    else {
                        productsExecutorService.submit(new ProductWorker(order, productsInputPath, product_data_start, product_data_end, "FINAL"));
                    }
                    product_data_start += chunk_size;
                    product_data_end += chunk_size;
                }
            }
            ++lineNumber;
        }
    }

    public static synchronized void shippedProductNotification(String orderId) {
        Database.hasProducts.replace(orderId, Database.hasProducts.get(orderId) - 1);
        if (Database.hasProducts.get(orderId) == 0) {
            try {
                Database.ordersWriter.write(orderId + "," + Database.ordersData.get(orderId) + ",shipped\n");
                Database.ordersWriter.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
