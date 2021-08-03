import lombok.SneakyThrows;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class WriterClass {

    @SneakyThrows
    public static void writeToFile(String fileName, String lineToAdd, boolean append) {
        try (FileWriter file = new FileWriter("./src/main/resources/helpers/" + fileName, append);
             BufferedWriter bufferWriter = new BufferedWriter(file)
        ) {
            bufferWriter.write(lineToAdd + "\n");
        }
    }
}
