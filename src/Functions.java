import java.util.Scanner;

public class Functions {

    public static synchronized Order processLine(String line) {
        int position = 0;
        int numberOfProducts = 0;
        StringBuilder orderId = new StringBuilder();
        for (int i = 0; i < line.length(); ++i) {
            if (line.charAt(i) != ',') {
                orderId.append(line.charAt(i));
            }
            else {
                position = i + 1;
                break;
            }
        }
        numberOfProducts = Integer.parseInt(line.substring(position));
        if (numberOfProducts != 0) {
            Database.activeOrders.add(orderId.toString());
        }
        return new Order(orderId.toString(), numberOfProducts);
    }

    public static synchronized String readLine(Scanner scanner) {

        if (scanner.hasNextLine()) {
            return scanner.nextLine();
        }

        return null;
    }

}
