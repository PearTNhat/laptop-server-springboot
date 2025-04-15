package com.laptop.ltn.laptop_store_server.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import java.lang.Thread;

import java.time.Duration;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RatingTest {

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
    @Order(1)
    void testAddRating() throws InterruptedException {
        login();
        driver.get("https://laptop-ltn105-store.vercel.app/laptop-gaming-acer-nitro-5-tiger-an515-58-773y");
        Thread.sleep(3000);
        WebElement ratingButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div/div[4]/div[2]/div[3]/div[1]/div[2]/button")
        ));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({ behavior: 'smooth', block: 'center' });", ratingButton);
        Thread.sleep(2000);

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", ratingButton);

        WebElement threeStar = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("#root > div > div.fixed.p-1.w-full.h-full.top-0.left-0.z-\\[9999\\].flex.items-center.justify-center > div.h-full.flex.items-center.animate-slide-in-left.z-\\[9999\\] > div > div.px-2 > div > div:nth-child(3) > label > div > svg > path")
        ));
        threeStar.click();

        WebElement commentBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#root > div > div.fixed.p-1.w-full.h-full.top-0.left-0.z-\\[9999\\].flex.items-center.justify-center > div.h-full.flex.items-center.animate-slide-in-left.z-\\[9999\\] > div > div.px-2 > textarea")));
        commentBox.sendKeys("This comment uses for testing");

        WebElement submitCommentButton = driver.findElement(
                By.cssSelector("#root > div > div.fixed.p-1.w-full.h-full.top-0.left-0.z-\\[9999\\].flex.items-center.justify-center > div.h-full.flex.items-center.animate-slide-in-left.z-\\[9999\\] > div > div.px-2 > button"));
        submitCommentButton.click();
        Thread.sleep(3000);
    }

    @Test
    @Order(2)
    void testLikeComment() throws InterruptedException {
        login();
        driver.get("https://laptop-ltn105-store.vercel.app/laptop-gaming-acer-nitro-5-tiger-an515-58-773y");
        Thread.sleep(3000);
        WebElement existedComment = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div/div/div/div[4]/div[2]/div[3]/div[2]/div[2]/div[2]")
        ));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({ behavior: 'smooth', block: 'center' });", existedComment);

        Thread.sleep(2000);

        WebElement comment = driver.findElement(
                By.xpath("/html/body/div/div/div/div[4]/div[2]/div[3]/div[2]/div[2]/div[2]/p")
        );

        WebElement likeButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#root > div > div > div.text-black.my-2 > div.main-container > div:nth-child(3) > div:nth-child(2) > div.ml-\\[32px\\] > div.mt-3.p-2.shadow-\\[rgba\\(0\\,_0\\,_0\\,_0\\.24\\)_0px_3px_8px\\].rounded-md > div > button:nth-child(1) > svg")));
        likeButton.click();
        Thread.sleep(3000);
    }

    @Test
    @Order(3)
    void testReplyComment() throws InterruptedException {
        login();
        driver.get("https://laptop-ltn105-store.vercel.app/laptop-gaming-acer-nitro-5-tiger-an515-58-773y");
        Thread.sleep(3000);
        WebElement commentToReply = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div/div[4]/div[2]/div[3]/div[2]/div[2]/div[2]/div/button[2]/span")
        ));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({ behavior: 'smooth', block: 'center' });", commentToReply);

        Thread.sleep(2000);
        commentToReply.click();

        WebElement replyCommentBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div/div[4]/div[2]/div[3]/div[2]/div[2]/div[3]/div[1]/div/textarea")
        ));
        replyCommentBox.sendKeys("This is an auto reply comment for testing");

        WebElement replyButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div/div[4]/div[2]/div[3]/div[2]/div[2]/div[3]/div[1]/div/div/button[2]")));
        replyButton.click();
        Thread.sleep(3000);
    }

    @Test
    @Order(4)
    void testDeleteComment() throws InterruptedException {
        login();
        driver.get("https://laptop-ltn105-store.vercel.app/laptop-gaming-acer-nitro-5-tiger-an515-58-773y");
        Thread.sleep(3000);
        WebElement commentToDelete = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div/div/div/div[4]/div[2]/div[3]/div[2]/div[2]/div[3]/div[3]/div[2]/div[2]/p")
        ));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({ behavior: 'smooth', block: 'center' });", commentToDelete);

        Thread.sleep(2000);

        WebElement deleteCommentButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div/div/div/div[4]/div[2]/div[3]/div[2]/div[2]/div[3]/div[3]/div[2]/div[2]/div/button[4]/span")
        ));

        deleteCommentButton.click();
        Thread.sleep(3000);
    }

    @Test
    @Order(5)
    void testEditComment() throws InterruptedException {
        login();
        driver.get("https://laptop-ltn105-store.vercel.app/laptop-gaming-acer-nitro-5-tiger-an515-58-773y");
        Thread.sleep(3000);
        WebElement existedComment = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div/div[4]/div[2]/div[3]/div[1]/div[2]/div[2]/div[2]/p")
        ));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({ behavior: 'smooth', block: 'center' });", existedComment);

        Thread.sleep(2000);

        WebElement editCommentButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div/div[4]/div[2]/div[3]/div[1]/div[2]/div[2]/div[2]/div/button[1]/span")
        ));

        editCommentButton.click();
        Thread.sleep(1000);

        WebElement commentBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div[1]/div[2]/div/div[2]/textarea")
        ));
        commentBox.clear();
        commentBox.sendKeys("This is an edit comment for testing");

        WebElement submitButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[1]/div/div[1]/div[2]/div/div[2]/button")
        ));
        submitButton.click();
        Thread.sleep(3000);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
