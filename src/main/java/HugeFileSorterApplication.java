import lombok.SneakyThrows;

import java.io.FileInputStream;
import java.util.Objects;
import java.util.Properties;

public class HugeFileSorterApplication {

    @SneakyThrows
    public static void main(String[] args) {
        String rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
        String appConfigPath = rootPath + "property.yaml";
        Properties appProp = new Properties();
        appProp.load(new FileInputStream(appConfigPath));

        String fileName = appProp.getProperty("fileName");
        int numberOfLines = Integer.parseInt(appProp.getProperty("numberOfLines"));
        int maxLineLength = Integer.parseInt(appProp.getProperty("maxLineLength"));
        int sizePartLine = Integer.parseInt(appProp.getProperty("sizePartLine"));
        int lineLimit = Integer.parseInt(appProp.getProperty("lineLimit"));

        FileService.createNewFile(fileName);
        SymbolGenerationService.generateLinesAndWriteToFile(numberOfLines, maxLineLength, fileName);
        LineSorterService.sortLinesInFile(fileName, sizePartLine, lineLimit);
    }
}
