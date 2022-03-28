import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Test {
    private static Logger logger = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) {
        try {
            InputStreamReader reader = new InputStreamReader(System.in);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String buffer = null;
            while ((buffer = bufferedReader.readLine()) != null) {
                if (buffer.equals("exit")) {
                    System.exit(1);

                }

                System.out.println("输入内容" + buffer);
            }
        } catch (IOException e) {
            logger.error("",e);
        } finally {

        }
    }


}
