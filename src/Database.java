import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Database {

    public static HashMap<String, Integer> ordersData = new HashMap<>();

    public static HashMap<String, Integer> hasProducts = new HashMap<>();

    public static Set<String> activeOrders = new HashSet<>();

    public static FileWriter ordersWriter;
    public static FileWriter productsWriter;

}
