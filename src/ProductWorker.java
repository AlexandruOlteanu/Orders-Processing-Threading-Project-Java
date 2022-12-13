import java.io.IOException;

public class ProductWorker implements Runnable{

    Order order;
    StringBuilder orderId;
    StringBuilder productId;

    public ProductWorker(Order order, StringBuilder orderId, StringBuilder productId) {
        this.order = new Order(order.orderId, order.numberOfProducts);
        this.orderId = new StringBuilder(orderId);
        this.productId = new StringBuilder(productId);
    }

    @Override
    public void run() {
        if (order.orderId.equals(orderId.toString())) {
            try {
                Database.productsWriter.write(orderId + "," + productId + ",shipped\n");
                Database.productsWriter.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            OrderWorker.shippedProductNotification(orderId.toString());
        }
    }
}
