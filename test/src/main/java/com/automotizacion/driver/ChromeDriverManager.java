package com.automotizacion.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.automotizacion.config.Config;

import java.io.IOException;
import java.net.Socket;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class ChromeDriverManager {
    private WebDriver driver;
    private WebDriverWait wait;

    public void iniciarChrome() throws IOException, InterruptedException {
        System.out.println("\nIniciando Chrome con tu perfil...");

        try (Socket socket = new Socket()) {
            socket.connect(new java.net.InetSocketAddress("127.0.0.1", Config.DEBUG_PORT), 1000);
            Thread.sleep(3000);
        } catch (Exception e) {
            new ProcessBuilder(Config.CHROME_PATH,
                    "--remote-debugging-port=" + Config.DEBUG_PORT,
                    "--user-data-dir=" + Config.CHROME_PROFILE,
                    "--start-maximized").start();
            Thread.sleep(10000);
        }

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "127.0.0.1:" + Config.DEBUG_PORT);

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", Config.DOWNLOAD_DIR);
        prefs.put("download.prompt_for_download", false);
        prefs.put("directory_upgrade", true);
        prefs.put("plugins.always_open_pdf_externally", true);
        options.setExperimentalOption("prefs", prefs);

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(40));
    }

    public WebDriver getDriver() {
        return driver;
    }

    public WebDriverWait getWait() {
        return wait;
    }

    public void cerrar() {
        if (driver != null)
            driver.quit();
    }
}