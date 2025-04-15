package com.laptop.ltn.laptop_store_server.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class FilterProductTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        System.out.println("Before each test");
    }

    private void login() {
        driver.get("https://laptop-ltn105-store.vercel.app/login");

        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("email")
        ));
        emailInput.sendKeys("batho09082003@gmail.com");

        WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("password")
        ));
        passwordInput.sendKeys("123456");
        passwordInput.sendKeys(Keys.RETURN);

        wait.until(ExpectedConditions.urlToBe("https://laptop-ltn105-store.vercel.app/"));
        String currentUrl = driver.getCurrentUrl();
        Assertions.assertTrue(
                currentUrl.equals("https://laptop-ltn105-store.vercel.app/") || currentUrl.equals("https://laptop-ltn105-store.vercel.app"),
                "URL sau khi đăng nhập không đúng: " + currentUrl
        );
    }

    @Test
    void testFilterProductByPrice() throws InterruptedException {
        login();
        driver.get("https://laptop-ltn105-store.vercel.app/laptop?page=1&limit=12");
        Thread.sleep(3000);

        WebElement priceFilter = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div/div[4]/div[2]/div[1]/div/div[1]/div/div[1]/div[1]")
        ));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", priceFilter);

        WebElement priceBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div/div[4]/div[2]/div[1]/div/div[1]/div/div[1]/div[2]/div[2]")
        ));

        WebElement fromLowerPrice = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div/div[4]/div[2]/div[1]/div/div[1]/div/div[1]/div[2]/div[2]/div[1]/div/input")
        ));
        fromLowerPrice.sendKeys("25000000");

        WebElement toHigherPrice = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div/div[4]/div[2]/div[1]/div/div[1]/div/div[1]/div[2]/div[2]/div[2]/div/input")
        ));
        toHigherPrice.clear();
        toHigherPrice.sendKeys("50000000");
        priceFilter.click();
        Thread.sleep(3000);
    }

    @Test
    void testFilterProductByBrand() throws InterruptedException {
        login();
        driver.get("https://laptop-ltn105-store.vercel.app/laptop?page=1&limit=12");
        Thread.sleep(3000);

        WebElement brandFilter = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div/div[4]/div[2]/div[1]/div/div[1]/div/div[2]/div[1]")
        ));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", brandFilter);

        WebElement brandBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div/div[4]/div[2]/div[1]/div/div[1]/div/div[2]/div[2]")
        ));

        WebElement selectedBrand = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div/div[4]/div[2]/div[1]/div/div[1]/div/div[2]/div[2]/div[5]/input")
        ));
        selectedBrand.click();
        brandFilter.click();
        Thread.sleep(3000);
    }

    @Test
    void testFilterProductByColor() throws InterruptedException {
        login();
        driver.get("https://laptop-ltn105-store.vercel.app/laptop?page=1&limit=12");
        Thread.sleep(3000);

        WebElement colorFilter = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div/div[4]/div[2]/div[1]/div/div[1]/div/div[3]/div[1]")
        ));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", colorFilter);

        WebElement colorBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div/div[4]/div[2]/div[1]/div/div[1]/div/div[3]/div[2]")
        ));

        WebElement selectedColor = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div/div[4]/div[2]/div[1]/div/div[1]/div/div[3]/div[2]/div[2]/input")
        ));
        selectedColor.click();
        colorFilter.click();
        Thread.sleep(3000);
    }

    @Test
    void testFilterProductByRam() throws InterruptedException {
        login();
        driver.get("https://laptop-ltn105-store.vercel.app/laptop?page=1&limit=12");
        Thread.sleep(3000);

        WebElement ramFilter = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div/div[4]/div[2]/div[1]/div/div[1]/div/div[4]/div[1]")
        ));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", ramFilter);

        WebElement ramBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div/div[4]/div[2]/div[1]/div/div[1]/div/div[4]/div[2]")
        ));

        WebElement selectedRam = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div/div[4]/div[2]/div[1]/div/div[1]/div/div[4]/div[2]/div[4]/input")
        ));
        selectedRam.click();
        ramFilter.click();
        Thread.sleep(3000);
    }

    @Test
    void testFilterProductByDataStorage() throws InterruptedException {
        login();
        driver.get("https://laptop-ltn105-store.vercel.app/laptop?page=1&limit=12");
        Thread.sleep(3000);

        WebElement storageFilter = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div/div[4]/div[2]/div[1]/div/div[1]/div/div[5]/div[1]")
        ));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", storageFilter);

        WebElement storageBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div/div[4]/div[2]/div[1]/div/div[1]/div/div[5]/div[2]")
        ));

        WebElement selectedStorage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div/div[4]/div[2]/div[1]/div/div[1]/div/div[5]/div[2]/div[4]/input")
        ));
        selectedStorage.click();
        storageFilter.click();
        Thread.sleep(3000);
    }

    @Test
    void testFilterProductByChoosing() throws InterruptedException {
        login();
        driver.get("https://laptop-ltn105-store.vercel.app/laptop?page=1&limit=12");
        Thread.sleep(3000);

        WebElement choosingFilter = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div/div[4]/div[2]/div[1]/div/div[2]/div/select")
        ));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", choosingFilter);

        WebElement choosingBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div/div[4]/div[2]/div[1]/div/div[2]/div/select")
        ));
        choosingBox.click();

        WebElement selectedChoosing = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div/div[4]/div[2]/div[1]/div/div[2]/div/select/option[6]")
        ));
        selectedChoosing.click();
        choosingBox.click();
        Thread.sleep(3000);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
