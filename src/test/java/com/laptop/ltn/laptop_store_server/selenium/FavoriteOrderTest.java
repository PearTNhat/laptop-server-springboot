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

public class FavoriteOrderTest {
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
    public void testAddFavoriteOrder() throws InterruptedException {
        login();

        driver.get("https://laptop-ltn105-store.vercel.app");
        Thread.sleep(3000);
        Actions actions = new Actions(driver);
        WebElement productCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#root > div > div > div:nth-child(4) > div:nth-child(2) > div > div:nth-child(1) > div > div > div > div.slick-slide.slick-active.slick-current > div > div > div")
        ));

        actions.moveToElement(productCard).perform();

        WebElement heartButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("#root > div > div > div:nth-child(4) > div:nth-child(2) > div > div:nth-child(1) > div > div > div > div.slick-slide.slick-active.slick-current > div > div > div > div.mb-3.relative > div > div:nth-child(1) > button > svg > path")
        ));
        heartButton.click();

        WebElement favoriteOrderButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#root > div > div > div.main-container.py-\\[35px\\] > div > div.flex.justify-center.items-center > a")));
        favoriteOrderButton.click();
    }

    @Test
    public void testDeleteFavoriteOrder() throws InterruptedException {
        login();
        Thread.sleep(3000);

        driver.get("https://laptop-ltn105-store.vercel.app/user/wishlist");
        Actions actions = new Actions(driver);
        WebElement productCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div/div[2]/div/div/div[2]/div[5]/div")
        ));

        actions.moveToElement(productCard).perform();

        WebElement heartButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/div[1]/div/div/div[2]/div/div/div[2]/div[5]/div/div[1]/div/div[1]/button")
        ));
        heartButton.click();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}