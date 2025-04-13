package com.laptop.ltn.laptop_store_server.selenium;

import com.laptop.ltn.laptop_store_server.entity.User;
import com.laptop.ltn.laptop_store_server.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

@SpringBootTest
public class Register {
    WebDriver driver;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        driver.get("https://laptop-ltn105-store.vercel.app/register");
        System.out.println("Before each test");
    }

    @Test
    void register_success() {
        // Tìm các field đăng nhập
        WebElement firstNameField = driver.findElement(By.id("firstName"));
        WebElement lastNameField = driver.findElement(By.id("lastName"));
        WebElement emailField = driver.findElement(By.id("email"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement confirmPasswordField = driver.findElement(By.id("confirmPassword"));

        WebElement loginButton = driver.findElement(By.cssSelector("#root > div > div > div.flex.justify-center.items-center.my-16 > form > div.mt-4.mb-2 > button"));
        // Nhập thông tin đăng nhập
        firstNameField.sendKeys("Le");
        lastNameField.sendKeys("Davis");
        emailField.sendKeys("letuannhat105a@gmail.com");
        passwordField.sendKeys("123456");
        confirmPasswordField.sendKeys("123456");
        // Nhấn nút đăng nhập
        loginButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#root > div > div > div.absolute.bg-\\[rgba\\(0\\,0\\,0\\,0\\.1\\)\\].inset-0.z-20 > div > form > input")));
        String opt = fetchOtpFromMongo("letuannhat105a@gmail.com");
        WebElement otpField = driver.findElement(By.cssSelector("#root > div > div > div.absolute.bg-\\[rgba\\(0\\,0\\,0\\,0\\.1\\)\\].inset-0.z-20 > div > form > input"));
        System.out.println("opt: " + opt);
        otpField.sendKeys(opt);
        WebElement submitOtp = driver.findElement(By.cssSelector("#root > div > div > div.absolute.bg-\\[rgba\\(0\\,0\\,0\\,0\\.1\\)\\].inset-0.z-20 > div > form > div.w-\\[80\\%\\].mx-auto > button"));
        submitOtp.click();
        WebElement register_success = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("swal2-html-container")));
        // Kiểm tra xem phần tử có hiển thị không và có chứa chữ "Thoát" không
        Assertions.assertTrue(register_success.getText().equals("Register successfully"), "Đăng ký thất bại");
    }
    @Test
    void register_failed_field_null() {
        // Tìm các field đăng nhập
        WebElement firstNameField = driver.findElement(By.id("firstName"));
        WebElement lastNameField = driver.findElement(By.id("lastName"));
        WebElement emailField = driver.findElement(By.id("email"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement confirmPasswordField = driver.findElement(By.id("confirmPassword"));

        WebElement registerBtn = driver.findElement(By.cssSelector("#root > div > div > div.flex.justify-center.items-center.my-16 > form > div.mt-4.mb-2 > button"));
        // Nhập thông tin đăng nhập
        firstNameField.sendKeys("");
        lastNameField.sendKeys("");
        emailField.sendKeys("");
        passwordField.sendKeys("");
        confirmPasswordField.sendKeys("");
        registerBtn.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement errorFN = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#root > div > div > div.flex.justify-center.items-center.my-16 > form > div.flex.gap-4 > div:nth-child(1) > small")));
        WebElement errorLN = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#root > div > div > div.flex.justify-center.items-center.my-16 > form > div.flex.gap-4 > div:nth-child(2) > small")));
        WebElement errorEM = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#root > div > div > div.flex.justify-center.items-center.my-16 > form > div:nth-child(3) > small")));
        WebElement errorPW = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#root > div > div > div.flex.justify-center.items-center.my-16 > form > div:nth-child(4) > small")));
        WebElement errorCPW = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#root > div > div > div.flex.justify-center.items-center.my-16 > form > div:nth-child(5) > small")));

        Assertions.assertEquals("Yêu cầu họ",errorFN.getText());
        Assertions.assertEquals("Last name is required",errorLN.getText());
        Assertions.assertEquals("Email is required",errorEM.getText());
        Assertions.assertEquals("Password is required",errorPW.getText());
        Assertions.assertEquals("Confirm password is required",errorCPW.getText());

    }
    @Test
    void register_failed_otp() {
        // Tìm các field đăng nhập
        WebElement firstNameField = driver.findElement(By.id("firstName"));
        WebElement lastNameField = driver.findElement(By.id("lastName"));
        WebElement emailField = driver.findElement(By.id("email"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement confirmPasswordField = driver.findElement(By.id("confirmPassword"));

        WebElement loginButton = driver.findElement(By.cssSelector("#root > div > div > div.flex.justify-center.items-center.my-16 > form > div.mt-4.mb-2 > button"));
        // Nhập thông tin đăng nhập
        firstNameField.sendKeys("Le");
        lastNameField.sendKeys("Davis");
        emailField.sendKeys("letuannhat105a@gmail.com");
        passwordField.sendKeys("123456");
        confirmPasswordField.sendKeys("123456");
        // Nhấn nút đăng nhập
        loginButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#root > div > div > div.absolute.bg-\\[rgba\\(0\\,0\\,0\\,0\\.1\\)\\].inset-0.z-20 > div > form > input")));
        WebElement otpField = driver.findElement(By.cssSelector("#root > div > div > div.absolute.bg-\\[rgba\\(0\\,0\\,0\\,0\\.1\\)\\].inset-0.z-20 > div > form > input"));
        otpField.sendKeys("1234");
        WebElement submitOtp = driver.findElement(By.cssSelector("#root > div > div > div.absolute.bg-\\[rgba\\(0\\,0\\,0\\,0\\.1\\)\\].inset-0.z-20 > div > form > div.w-\\[80\\%\\].mx-auto > button"));
        submitOtp.click();
        WebElement register_success = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("swal2-html-container")));
        // Kiểm tra xem phần tử có hiển thị không và có chứa chữ "Thoát" không
        Assertions.assertEquals("OTP is not correct",register_success.getText());
    }


    // 🧪 Dummy method – bạn cần triển khai hàm này bằng cách gọi MongoDB hoặc API test-only
    private String fetchOtpFromMongo(String email) {
        // Tìm user có email chứa "&" và đúng phần đầu
        String regex = "^" + email + "&\\d{6}$"; // Ví dụ: "abc@gmail.com&123456"
        User user = userRepository.findByEmailRegex(regex);
        if (user == null) throw new RuntimeException("Không tìm thấy user chứa OTP");
        String emailWithOtp = user.getEmail();
        return emailWithOtp.split("&")[1]; // Lấy phần OTP sau dấu &
    }

    @AfterEach
    void tearDown() {
        driver.quit();
        System.out.println("After all tests");
    }
}
