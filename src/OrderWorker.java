import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderWorker implements Runnable{

    String ordersInputPath;

    String productsInputPath;

    Integer data_start;

    Integer data_end;

    String status;

    int threadsNumber;

    ExecutorService productsExecutorService;

    Order order;

    public OrderWorker(String ordersInputPath, String productsInputPath, int data_start, int data_end, int threadsNumber, ExecutorService productsExecutorService, String status) {
        this.ordersInputPath = ordersInputPath;
        this.productsInputPath = productsInputPath;
        this.data_start = data_start;
        this.data_end = data_end;
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
            if (!status.equals("FINAL") && lineNumber > data_end) {
                break;
            }
            line.setLength(Constants.ZERO);
            String value = Functions.readLine(orderScanner);
            if (value == null) {
                break;
            }
            line.append(value);
            if (lineNumber >= data_start) {
                order = Functions.processLine(line.toString());
                Database.ordersData.put(order.orderId, order.numberOfProducts);
                if (order.numberOfProducts != 0) {
                    Database.activeOrders.add(order.orderId);
                }
                Database.hasProducts.put(order.orderId, order.numberOfProducts);

                File productsInput = new File(productsInputPath);
                Scanner productsScanner = null;
                try {
                    productsScanner = new Scanner(productsInput);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

                StringBuilder line1 = new StringBuilder();
                StringBuilder orderId = new StringBuilder();
                StringBuilder productId = new StringBuilder();

                while (productsScanner.hasNextLine()) {
                    line1.setLength(0);
                    orderId.setLength(0);
                    productId.setLength(0);
                    line1.append(productsScanner.nextLine());
//                    System.out.println(line1);
                    int position = 0;
                    for (int i = 0; i < line1.length(); ++i) {
                        if (line1.charAt(i) != ',') {
                            orderId.append(line1.charAt(i));
                        }
                        else {
                            position = i + 1;
                            break;
                        }
                    }
                    productId.append(line1.substring(position));
                    Database.productsData.put(orderId.toString(), productId);
                    if (Database.activeOrders.contains(orderId.toString())) {
                        productsExecutorService.submit(new ProductWorker(order, orderId, productId));
                    }
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
