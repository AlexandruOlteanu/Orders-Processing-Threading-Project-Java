import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Objects;
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

        Scanner orderScanner;
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

                for (int i = 0; i < order.numberOfProducts; ++i) {
                    productsExecutorService.submit(new ProductWorker(order, productsInputPath, i + 1));
                }
            }
            ++lineNumber;
        }
    }

    public static synchronized void shippedProductNotification(String orderId) {
        Database.hasProducts.replace(orderId, Database.hasProducts.get(orderId) - 1);
        if (Objects.equals(Database.hasProducts.get(orderId), Constants.ZERO)) {
            try {
                Database.ordersWriter.write(orderId + "," + Database.ordersData.get(orderId) + ",shipped\n");
                Database.ordersWriter.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
