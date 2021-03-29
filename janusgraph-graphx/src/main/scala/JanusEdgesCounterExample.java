import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by zhangshiming
 */
public class JanusEdgesCounterExample {
    public static void test() {
        StringBuilder result = new StringBuilder();
        Path path = new Path("hdfs://xxxx:9000/user/janustest.txt");
        Configuration configuration = new Configuration();
        FSDataInputStream fsDataInputStream = null;
        FileSystem fileSystem = null;
        BufferedReader br = null;
        try {

            FileSystem fs = FileSystem.get(new URI("hdfs://xxxx:9000/user/janustest.txt"), configuration, "hadoop");
            fsDataInputStream = fs.open(path);
            br = new BufferedReader(new InputStreamReader(fsDataInputStream));
            String str2;
            while ((str2 = br.readLine()) != null) {
                result.append(str2).append("\n");
            }
            System.out.println(result);
        } catch (IOException | URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        test();


    }
}
