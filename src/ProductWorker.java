import java.io.*;
import java.nio.channels.FileChannel;
import java.util.Scanner;

public class ProductWorker implements Runnable{

    Order order;

    String productsInputPath;

    Integer product_data_start;

    Integer product_data_end;

    String status;

    public ProductWorker(Order order, String productsInputPath, Integer product_data_start, Integer product_data_end, String status) {
        this.order = order;
        this.productsInputPath = productsInputPath;
        this.product_data_start = product_data_start;
        this.product_data_end = product_data_end;
        this.status = status;
    }

    @Override
    public void run() {

        Scanner productsScanner = null;
        try {
            FileReader productFileReader = new FileReader(productsInputPath);
            BufferedReader productsInput = new BufferedReader(productFileReader);

            int lineNumber = 1;
            StringBuilder line = new StringBuilder();
            while (true) {
                if (!status.equals("FINAL") && lineNumber > product_data_end) {
                    break;
                }
                line.setLength(Constants.ZERO);
                String value = productsInput.readLine();
//                System.out.println("Alex " + Database.x + " " + product_data_end);
                Database.x++;
                if (value == null) {
                    break;
                }
                line.append(value);
                if (Database.x < 680) {
                    System.out.println(line + " " + Database.x);
                }
                if (lineNumber >= product_data_start) {
                    Product product = Functions.processProductLine(line.toString());
                    Database.productsData.put(product.orderId, product.productId);
                    if (Database.activeOrders.contains(product.orderId)) {
                        if (order.orderId.equals(product.orderId)) {
                            try {
                                Database.productsWriter.write(product.orderId + "," + product.productId + ",shipped\n");
                                Database.productsWriter.flush();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            OrderWorker.shippedProductNotification(product.orderId);
                        }
                    }
                    ++lineNumber;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
