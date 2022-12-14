import java.io.FileWriter;
import java.util.*;

public class Database {

    public static HashMap<String, Integer> ordersData = new HashMap<>();

    public static HashMap<String, Integer> hasProducts = new HashMap<>();

    public static Set<String> activeOrders = new HashSet<>();

    public static FileWriter ordersWriter;
    public static FileWriter productsWriter;

    public static HashMap<String, StringBuilder> productsData = new HashMap<>();

    public static List<Pair> rawData = new ArrayList<>();

    public static ArrayList<StringBuilder> finalData = new ArrayList<>();

    public static ArrayList<Order> orders = new ArrayList<>();

    public static List<Pair> firstPartOrder = new ArrayList<>();

    public static List<Pair> lastPartOrder = new ArrayList<>();

}
