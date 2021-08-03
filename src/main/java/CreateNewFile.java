import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class CreateNewFile {

    @SneakyThrows
    public static void createNewFile(String fileName) {
        //создаем файл только с указанием имени файла
        File file = new File(fileName);
        if (file.createNewFile()) {
            log.info(fileName + " - файл создан по указанному пути");
        } else {
            log.info(fileName + " - файл по указанному пути уже существует");
        }
    }
}
