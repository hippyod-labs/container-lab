package org.hippyod.labs.containerlab;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class DockerfileLabApplication implements CommandLineRunner {
    
    private static final String WELCOME_MESSAGE = "WELCOME_MESSAGE";
        
    private static String containerLabLogFile = "/mnt/logs/containerlab.log";

    @Value("${message.welcome}")
    private String welcomeMsg;

    @Override
    public void run(String... args) throws Exception {        
        int logCounter = getLineCount();
                    
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(containerLabLogFile),
                                                             StandardCharsets.UTF_8,
                                                             StandardOpenOption.CREATE,
                                                             StandardOpenOption.APPEND))
        {
            while (true) {
                String newWelcomeMsg = System.getenv(WELCOME_MESSAGE);
                
                String msg = StringUtils.isNotBlank(newWelcomeMsg) ? newWelcomeMsg: welcomeMsg;
                String logMsg = "GREETING #" + ++logCounter + " : " + msg;
                
                writer.append(logMsg);
                writer.newLine();
                writer.flush();
                System.out.println(logMsg);
                
                TimeUnit.SECONDS.sleep(2);
            }
        }
    }
    
    @GetMapping("/")
    public String logs() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(containerLabLogFile));
        return "<html><meta http-equiv=\"refresh\" content=\"2\"><body>" + String.join("<br/>", lines) + "</body></html>";
    }

    public static void main(String[] args) {
        SpringApplication.run(DockerfileLabApplication.class, args);
    }
    
    private int getLineCount() {
        int lineCount = 0;
        try (Stream<String> fileStream = Files.lines(Paths.get(containerLabLogFile))) {
            lineCount = (int) fileStream.count();
            
            System.out.println("Found logfile;  previous GREETING count: " + lineCount);
        }
        catch (IOException ioe) {
            System.out.println("First run: NO LOG FILE CREATED YET");
        }
        
        return lineCount;
    }

}
