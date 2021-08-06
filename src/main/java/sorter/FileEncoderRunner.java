package sorter;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.util.Objects;
import java.util.Properties;

@Component
@RequiredArgsConstructor
public class FileEncoderRunner implements ApplicationRunner {
    private final LineSorterService lineSorterService;
    private final FileService fileService;
    private final SymbolGenerationService symbolGenerationService;

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

        fileService.createNewFile(fileName);
        symbolGenerationService.generateLinesAndWriteToFile(numberOfLines, maxLineLength, fileName);
        lineSorterService.sortLinesInFile(fileName, sizePartLine, lineLimit);
    }
}