import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderWorker implements Runnable{

    StringBuilder orderId;
    int numberOfProducts;

    File productInput;

    int threadsNumber;

    Order order;

    public OrderWorker(StringBuilder orderId, int numberOfProducts, File productInput, int threadsNumber) {
        this.orderId = orderId;
        this.numberOfProducts = numberOfProducts;
        this.productInput = productInput;
        this.threadsNumber = threadsNumber;
        order = new Order(orderId.toString(), numberOfProducts, "Unshipped");
    }

    @Override
    public void run() {

        ExecutorService executorService = Executors.newFixedThreadPool(threadsNumber);

        Scanner productScanner = null;
        try {
            productScanner = new Scanner(productInput);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        StringBuilder line = new StringBuilder();
        StringBuilder orderId = new StringBuilder();
        StringBuilder productId = new StringBuilder();

        while (productScanner.hasNextLine()) {
            line.setLength(0);
            orderId.setLength(0);
            productId.setLength(0);
            line.append(productScanner.nextLine());
            int position = 0;
            for (int i = 0; i < line.length(); ++i) {
                if (line.charAt(i) != ',') {
                    orderId.append(line.charAt(i));
                }
                else {
                    position = i + 1;
                    break;
                }
            }
            Product product = new Product();
            productId.append(line.substring(position));
            executorService.submit(new ProductWorker(order, productId));
        }
        executorService.shutdown();
    }
}
