package sorter;

import lombok.SneakyThrows;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class HugeFileSorterApplication {

    @SneakyThrows
    public static void main(String[] args) {
        new SpringApplicationBuilder(HugeFileSorterApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .logStartupInfo(false)
                .registerShutdownHook(true)
                .run();
    }
}
