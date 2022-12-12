public class ProductWorker implements Runnable{

    Order order;
    StringBuilder productId;

    public ProductWorker(Order order, StringBuilder productId) {
        this.order = order;
        this.productId = productId;
    }

    @Override
    public void run() {

    }
}
