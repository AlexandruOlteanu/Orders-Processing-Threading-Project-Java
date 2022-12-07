public class Main {
    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.println("Usage: Tema2 <workers> <in_file> <out_file>");
            return;
        }

        int NUMBER_OF_THREADS = Integer.parseInt(args[0]);

        System.out.println(NUMBER_OF_THREADS);

    }



}
