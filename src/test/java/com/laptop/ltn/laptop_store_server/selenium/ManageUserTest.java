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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ManageUserTest {
    WebDriver driver;
    WebDriverWait wait;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        System.out.println("Before each test");
    }
    @AfterEach
    void tearDown() {
        driver.quit();
        System.out.println("After all tests");
    }

    @Test
    void testManageUser() throws InterruptedException {
        loginToAdmin();
        // 2. Chọn trang quản lý người dùng
        driver.get("https://laptop-ltn105-store.vercel.app/admin/manage/users");
        // Test search
        System.out.println("Test search");
        WebElement searchUserField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#root > div > div > div.max-md\\:mt-\\[65px\\].max-md\\:w-full.w-\\[calc\\(85\\%-6px\\)\\].bg-\\[rgb\\(248\\,248\\,252\\)\\].p-2.overflow-x-auto.overflow-y-hidden > div > div > div > div.flex.justify-between.mt-4 > form > div > div > input")));
        searchUserField.sendKeys("Nhật");
        sleep(1000);
        WebElement elementTable = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div[2]/div/div/div/div[2]/div[1]/table/tbody/tr/td[3]"));
        sleep(2000);
        assertTrue(elementTable.getText().contains("Nhật"), "Không tìm thấy người dùng có tên Nhật");
        System.out.println("Test search thành công");
        System.out.println("Test cập nhật thông tin");
        searchUserField.clear();
        searchUserField.sendKeys(Keys.RETURN);
        sleep(2000);
        WebElement updateBtn = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div[2]/div/div/div/div[2]/div[1]/table/tbody/tr[1]/td[8]/div/button[1]"));
        updateBtn.click();
        WebElement roleValue = driver.findElement(By.cssSelector("#root > div > div > div.max-md\\:mt-\\[65px\\].max-md\\:w-full.w-\\[calc\\(85\\%-6px\\)\\].bg-\\[rgb\\(248\\,248\\,252\\)\\].p-2.overflow-x-auto.overflow-y-hidden > div > div > div > div.py-4 > div.shadow.bg-white.rounded-md.overflow-hidden > table > tbody > tr:nth-child(1) > td:nth-child(6) > div > div > div > div.react-select__value-container.react-select__value-container--has-value.css-hlgwow > div.react-select__single-value.css-1dimb5e-singleValue"));
        WebElement btnDropdown = driver.findElement(By.cssSelector("#root > div > div > div.max-md\\:mt-\\[65px\\].max-md\\:w-full.w-\\[calc\\(85\\%-6px\\)\\].bg-\\[rgb\\(248\\,248\\,252\\)\\].p-2.overflow-x-auto.overflow-y-hidden > div > div > div > div.py-4 > div.shadow.bg-white.rounded-md.overflow-hidden > table > tbody > tr:nth-child(1) > td:nth-child(6) > div > div > div > div.react-select__indicators.css-1wy0on6 > div > svg"));
        btnDropdown.click();
        WebElement adminItem = driver.findElement(By.cssSelector("#react-select-2-option-0"));
        WebElement userItem = driver.findElement(By.cssSelector("#react-select-2-option-1"));
        String role;
        if (roleValue.getText().equalsIgnoreCase("admin")) {
            userItem.click();
            role= "user";
        }else{
            adminItem.click();
            role = "admin";
        }

        WebElement btnSave = driver.findElement(By.cssSelector("#root > div > div > div.max-md\\:mt-\\[65px\\].max-md\\:w-full.w-\\[calc\\(85\\%-6px\\)\\].bg-\\[rgb\\(248\\,248\\,252\\)\\].p-2.overflow-x-auto.overflow-y-hidden > div > div > div > div.flex.justify-between.mt-4 > button"));
        btnSave.click();
        assertTrue(roleValue.getText().equalsIgnoreCase(role));
        sleep(1000);
        // Test khóa người dùng
        WebElement blockBtn = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div[2]/div/div/div/div[2]/div[1]/table/tbody/tr[1]/td[8]/div/button[2]"));
        blockBtn.click();
       WebElement btnConfirm = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("body > div.swal2-container.swal2-center.swal2-backdrop-show > div > div.swal2-actions > button.swal2-confirm.swal2-styled.swal2-default-outline")
        ));
        btnConfirm.click();
        sleep(500);
        WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#swal2-title")));
        assertEquals("Cập nhật thành công",toast.getText());


    }

    void loginToAdmin() {
        // 1. Đăng nhập
        driver.get("https://laptop-ltn105-store.vercel.app/login");

        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        emailInput.sendKeys("letuannhat105@gmail.com");

        WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
        passwordInput.sendKeys("123456");
        passwordInput.sendKeys(Keys.RETURN);

        wait.until(ExpectedConditions.urlToBe("https://laptop-ltn105-store.vercel.app/"));
        String currentUrl = driver.getCurrentUrl();
        assertTrue(
                currentUrl.equals("https://laptop-ltn105-store.vercel.app/") || currentUrl.equals("https://laptop-ltn105-store.vercel.app"),
                "URL sau khi đăng nhập không đúng: " + currentUrl
        );
    }
}
