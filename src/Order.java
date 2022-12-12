import java.util.ArrayList;

public class Order {
    String orderId;
    int numberOfProducts;

    int numberOfShippedProducts;
    String status;
    ArrayList<Product> products;



    public Order(String orderId, int numberOfProducts, String status) {
        this.orderId = orderId;
        this.numberOfProducts = numberOfProducts;
        this.status = status;
        this.products = new ArrayList<>();
        this.numberOfShippedProducts = 0;
    }

    void addProduct(Product product) {
        products.add(product);
    }

    void shipProduct() {
        ++numberOfShippedProducts;
        if (numberOfShippedProducts == numberOfProducts) {
            status = "shipped";
        }
    }
}
