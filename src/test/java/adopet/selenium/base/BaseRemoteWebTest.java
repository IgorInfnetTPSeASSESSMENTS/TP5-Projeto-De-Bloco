package adopet.selenium.base;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;

public abstract class BaseRemoteWebTest {

    protected WebDriver driver;
    protected String baseUrl;


    @BeforeEach
    void setUpDriver() {
        driver = DriverFactory.createChrome();

        String configuredBaseUrl = System.getProperty("app.baseUrl");
        if (configuredBaseUrl == null || configuredBaseUrl.isBlank()) {
            configuredBaseUrl = "http://localhost:8080";
        }

        baseUrl = configuredBaseUrl;
    }

    @AfterEach
    void tearDownDriver() {
        if (driver != null) {
            driver.quit();
        }
    }
}