package sorter;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.io.FileInputStream;
import java.util.Objects;
import java.util.Properties;

@RequiredArgsConstructor
public class FileEncoderRunner implements ApplicationRunner {
    private final LineSorterService lineSorterService;

    @SneakyThrows
    @Override
    public void run(ApplicationArguments args) {
        String rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
        String appConfigPath = rootPath + "property.yaml";
        Properties appProp = new Properties();
        appProp.load(new FileInputStream(appConfigPath));

        String fileName = appProp.getProperty("fileName");
        int numberOfLines = Integer.parseInt(appProp.getProperty("numberOfLines"));
        int maxLineLength = Integer.parseInt(appProp.getProperty("maxLineLength"));
        int sizePartLine = Integer.parseInt(appProp.getProperty("sizePartLine"));
        int lineLimit = Integer.parseInt(appProp.getProperty("lineLimit"));

        new FileService().createNewFile(fileName);
        new SymbolGenerationService().generateLinesAndWriteToFile(numberOfLines, maxLineLength, fileName);
        new LineSorterService().sortLinesInFile(fileName, sizePartLine, lineLimit);
    }
}