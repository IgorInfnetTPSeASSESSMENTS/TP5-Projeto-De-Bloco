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
        baseUrl = System.getProperty("app.baseUrl");

        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException("System property 'app.baseUrl' was not provided.");
        }

        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
    }

    @AfterEach
    void tearDownDriver() {
        if (driver != null) {
            driver.quit();
        }
    }
}