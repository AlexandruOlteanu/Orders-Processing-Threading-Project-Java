import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FastRead implements Runnable{

    FileChannel channel;
    Integer start;
    Integer length;

    Integer id;

    String type;

    public FastRead(FileChannel channel, Integer start, Integer length, Integer id, String type) {
        this.channel = channel;
        this.start = start;
        this.length = length;
        this.id = id;
        this.type = type;
    }

    @Override
    public void run() {

        ByteBuffer memory = ByteBuffer.allocate(length);

        try {
            channel.read(memory, start);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String result = new String(memory.array());
        StringBuilder new_result = new StringBuilder();
        for (int i = 0; i < result.length(); ++i) {
            if (result.charAt(i) == '\n') {
                new_result.append('#');
            }
            else {
                new_result.append(result.charAt(i));
            }
        }
//        System.out.println(result);
        if (type.equals("order")) {
            StringBuilder line = new StringBuilder();
            boolean ok = false;
            for (int i = 0; i < result.length(); ++i) {
//                System.out.println(i + "Sunt aici " + id + result.charAt(i) + " " + result.length());
                int j = i;
                while (j < result.length() && result.charAt(j) != '\n') {
                    line.append(result.charAt(j));
                    ++j;
                }
                if (j < result.length()) {
                    if (result.charAt(i) == 'o' && result.charAt(i + 1) == '_') {
//                        System.out.println("MUAHAHAHAHAHAHAHAHA " + line.toString() + "\n\n\n\n");
                        Database.orders.add(Functions.processLine(line.toString()));
                    }
                    else {
                        Database.firstPartOrder.add(new Pair(id, line.toString()));
                    }
                }
                else {
                    Database.lastPartOrder.add(new Pair(id, line.toString()));
                }
                line.setLength(Constants.ZERO);
                i = j;
            }
        }
        else {


        }
    }
}
