import java.util.ArrayList;

public class Order {
    String orderId;
    int numberOfProducts;

    int numberOfShippedProducts;
    ArrayList<Product> products;



    public Order(String orderId, int numberOfProducts) {
        this.orderId = orderId;
        this.numberOfProducts = numberOfProducts;
    }

    void addProduct(Product product) {
        products.add(product);
    }

}
