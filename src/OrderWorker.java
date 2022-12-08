import java.io.File;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderWorker implements Runnable{

    StringBuilder orderId;
    int numberOfProducts;
    Scanner productScanner;

    int threadsNumber;

    public OrderWorker(StringBuilder orderId, int numberOfProducts, Scanner productsScanner, int threadsNumber) {
        this.orderId = orderId;
        this.numberOfProducts = numberOfProducts;
        this.productScanner = productsScanner;
        this.threadsNumber = threadsNumber;
    }

    @Override
    public void run() {

        ExecutorService executorService = Executors.newFixedThreadPool(threadsNumber);

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
            productId.append(line.substring(position));
            executorService.submit(new ProductWorker());
        }

    }
}
