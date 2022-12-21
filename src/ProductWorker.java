import java.io.*;

public class ProductWorker implements Runnable{

    Order order;

    String productsInputPath;

    Integer productId;

    public ProductWorker(Order order, String productsInputPath, Integer productId) {
        this.order = order;
        this.productsInputPath = productsInputPath;
        this.productId = productId;
    }

    @Override
    public void run() {

        try {
            FileReader productFileReader = new FileReader(productsInputPath);
            BufferedReader productsInput = new BufferedReader(productFileReader);

            int foundProduct = 0;
            StringBuilder line = new StringBuilder();
            while (true) {
                line.setLength(Constants.ZERO);
                String value = productsInput.readLine();
                if (value == null) {
                    break;
                }
                line.append(value);
                Product product = Functions.processProductLine(line.toString());
                if (product.orderId.equals(order.orderId)) {
                    ++foundProduct;
                }
                if (foundProduct == productId) {
                    if (Database.activeOrders.contains(product.orderId)) {
                            try {
                                Database.productsWriter.write(product.orderId + "," + product.productId + ",shipped\n");
                                Database.productsWriter.flush();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            OrderWorker.shippedProductNotification(product.orderId);
                    }
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
