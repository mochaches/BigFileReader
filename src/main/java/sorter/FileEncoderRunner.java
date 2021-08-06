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


    @Override
    @SneakyThrows
    public void run(ApplicationArguments args) {
        var rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
        var appConfigPath = rootPath + "application.yaml";
        var appProp = new Properties();
        appProp.load(new FileInputStream(appConfigPath));

        var fileName = appProp.getProperty("fileName");
        var numberOfLines = Integer.parseInt(appProp.getProperty("numberOfLines"));
        var maxLineLength = Integer.parseInt(appProp.getProperty("maxLineLength"));
        var sizePartLine = Integer.parseInt(appProp.getProperty("sizePartLine"));
        var lineLimit = Integer.parseInt(appProp.getProperty("lineLimit"));

        fileService.createNewFile(fileName);
        symbolGenerationService.generateLinesAndWriteToFile(numberOfLines, maxLineLength, fileName);
        lineSorterService.sortLinesInFile(fileName, sizePartLine, lineLimit);
    }
}