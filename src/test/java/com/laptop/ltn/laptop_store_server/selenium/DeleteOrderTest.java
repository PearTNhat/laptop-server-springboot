package com.laptop.ltn.laptop_store_server.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class DeleteOrderTest {
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

        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        emailInput.sendKeys("batho09082003@gmail.com");

        WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
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
    public void testDeleteOrder() throws InterruptedException {
        login();
        Thread.sleep(3000);

        Actions actions = new Actions(driver);
        WebElement cartItem = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div/div[2]/div/div[2]/div[3]")
        ));
        actions.moveToElement(cartItem).perform();

        WebElement orderDetailButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div/div[2]/div/div[2]/div[3]/div/div/ul/div/a")
        ));
        orderDetailButton.click();
        Thread.sleep(3000);

        WebElement deleteOrderButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div/div[2]/div/div/div[2]/div[2]/ul[1]/li[5]/button")
        ));
        deleteOrderButton.click();
        Thread.sleep(3000);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
