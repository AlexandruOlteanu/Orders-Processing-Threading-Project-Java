public class Functions {

    public static Order processLine(String line) {
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
        String doNumber = line.substring(position);
        numberOfProducts = 0;
        for (int i = 0; i < doNumber.length(); ++i) {
            numberOfProducts = numberOfProducts * 10 + (doNumber.charAt(i) - '0');
//            System.out.println(line + " Alex " + numberOfProducts + " Maria\n");
        }
        if (numberOfProducts != 0) {
            Database.activeOrders.add(orderId.toString());
        }
        return new Order(orderId.toString(), numberOfProducts);
    }

}
