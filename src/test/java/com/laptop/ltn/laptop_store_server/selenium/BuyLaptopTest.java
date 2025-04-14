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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuyLaptopTest {
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
    public void fullFlow_Login_ProductDetail_Cart_Checkout_Payment() throws Exception {
        // 1. Đăng nhập
        driver.get("https://laptop-ltn105-store.vercel.app/login");

        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        emailInput.sendKeys("letuannhat105@gmail.com");

        WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
        passwordInput.sendKeys("123456");
        passwordInput.sendKeys(Keys.RETURN);

        wait.until(ExpectedConditions.urlToBe("https://laptop-ltn105-store.vercel.app/"));
        String currentUrl = driver.getCurrentUrl();
        Assertions.assertTrue(
                currentUrl.equals("https://laptop-ltn105-store.vercel.app/") || currentUrl.equals("https://laptop-ltn105-store.vercel.app"),
                "URL sau khi đăng nhập không đúng: " + currentUrl
        );

        // 2. Chọn sản phẩm
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#root > div > div > div:nth-child(4) > div:nth-child(2) > div > div:nth-child(1) > div > div > div > div.slick-slide")));
        List<WebElement> products = driver.findElements(By.cssSelector("#root > div > div > div:nth-child(4) > div:nth-child(2) > div > div:nth-child(1) > div > div > div > div.slick-slide"));
        Assertions.assertFalse(products.isEmpty(), "Không tìm thấy sản phẩm nào!");

        WebElement firstProduct = products.get(0);
        WebElement productNameElement = firstProduct.findElement(By.xpath(".//h2"));
        String productName = productNameElement.getText();
        String expectedSlug = productName.toLowerCase().replaceAll("\\s+", "-");

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", firstProduct);
        Thread.sleep(1000);
        firstProduct.click();

        wait.until(ExpectedConditions.urlContains(expectedSlug));
        String productUrl = driver.getCurrentUrl();
        Assertions.assertTrue(productUrl.contains(expectedSlug), "URL không chứa slug sản phẩm: " + productUrl);

        // 3. Tăng giảm số lượng
        System.out.println("Đang ở trang chi tiết sản phẩm: " + productName);
        WebElement minusBtn = driver.findElement(By.cssSelector("#root > div > div > div.text-black.my-2 > div.main-container > div.flex.flex-wrap.gap-4 > div.max-md\\:w-full.w-\\[calc\\(60\\%-8px\\)\\].mt-6 > div.mt-1.short-desc > div:nth-child(4) > div > button.cursor-not-allowed.w-\\[32px\\].h-9.flex.items-center.justify-center.border-r.border-gray-300"));
        WebElement plusBtn = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div[4]/div[2]/div[1]/div[2]/div[3]/div[3]/div/button[2]"));
        WebElement quantityInput = driver.findElement(By.cssSelector("#root > div > div > div.text-black.my-2 > div.main-container > div.flex.flex-wrap.gap-4 > div.max-md\\:w-full.w-\\[calc\\(60\\%-8px\\)\\].mt-6 > div.mt-1.short-desc > div:nth-child(4) > div > input"));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", plusBtn);
        Thread.sleep(500);

        int initialQuantity = Integer.parseInt(quantityInput.getAttribute("value"));

        for (int i = 0; i < 3; i++) {
            plusBtn.click();
            Thread.sleep(300);
        }

        int afterIncrease = Integer.parseInt(quantityInput.getAttribute("value"));
        assertEquals(initialQuantity + 3, afterIncrease, "Số lượng sau khi tăng không đúng");

        minusBtn.click();
        Thread.sleep(300);

        int afterDecrease = Integer.parseInt(quantityInput.getAttribute("value"));
        assertEquals(afterIncrease - 1, afterDecrease, "Số lượng sau khi giảm không đúng");

        // 4. Thêm vào giỏ hàng
        WebElement addToCartBtn = driver.findElement(By.cssSelector("#root > div > div > div.text-black.my-2 > div.main-container > div.flex.flex-wrap.gap-4 > div.max-md\\:w-full.w-\\[calc\\(60\\%-8px\\)\\].mt-6 > div.flex.gap-5.my-3 > button.uppercase.font-medium.leading-none.px-2.py-3.bg-white.border.border-main.text-main.rounded-md.w-full"));
        addToCartBtn.click();
        Thread.sleep(1000);

        // 5. Mở giỏ hàng và nhấn “Thanh toán”
        WebElement cartBtn = driver.findElement(By.cssSelector("#root > div > div > div.text-black.my-2 > div.main-container > div.flex.flex-wrap.gap-4 > div.max-md\\:w-full.w-\\[calc\\(60\\%-8px\\)\\].mt-6 > div.flex.gap-5.my-3 > button.uppercase.font-medium.leading-none.px-2.py-3.text-white.bg-main.rounded-md.w-full"));
        cartBtn.click();

        wait.until(ExpectedConditions.urlContains("/cart"));
        String cartUrl = driver.getCurrentUrl();
        Assertions.assertTrue(cartUrl.contains("/cart"), "Không chuyển đến trang giỏ hàng: " + cartUrl);

//
//// kiểm tra tăng số lượng trong giỏ hàng
//        List<WebElement> cartItems = driver.findElements(By.xpath("//*[@id=\"root\"]/div/div/div[2]/div/div/div[2]/div[2]/ul"));
//        Assertions.assertFalse(cartItems.isEmpty(), "Không tìm thấy sản phẩm nào trong giỏ hàng");
//
//        WebElement firstItem = cartItems.get(0);
//        WebElement plusBtnCart = firstItem.findElement(By.xpath("//*[@id=\"root\"]/div/div/div[2]/div/div/div[2]/div[2]/ul[1]/li[3]/div/button[2]"));
//        WebElement itemQuantityElement = firstItem.findElement(By.xpath("//*[@id=\"root\"]/div/div/div[2]/div/div/div[2]/div[2]/ul[1]/li[3]/div/input"));
//        WebElement priceElement = firstItem.findElement(By.xpath("//*[@id=\"root\"]/div/div/div[2]/div/div/div[2]/div[2]/ul[1]/li[4]"));
//
//        int itemQuantityBefore = Integer.parseInt(itemQuantityElement.getAttribute("value"));
//
//        String currentPriceText = priceElement.getText(); // VD: "2.400.000đ"
//        int currentPrice = Integer.parseInt(currentPriceText.replace(".", "").replace("đ", "").trim());
//        int originalPrice = currentPrice / itemQuantityBefore;
//
//        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", plusBtnCart);
//        Thread.sleep(1000); // đợi animation
//
//        plusBtn.click();
//        Thread.sleep(1000); // đợi cập nhật DOM
//
//        int itemQuantityAfter = Integer.parseInt(itemQuantityElement.getAttribute("value"));
//        String afterPlusPriceText = priceElement.getText();
//        int afterPlusPrice = Integer.parseInt(afterPlusPriceText.replace(".", "").replace("đ", "").trim());
//
//        Assertions.assertEquals(itemQuantityBefore + 1, itemQuantityAfter, "Số lượng không tăng đúng!");
//
//        int expectedPrice = originalPrice * itemQuantityAfter;
//        Assertions.assertEquals(expectedPrice, afterPlusPrice, "Giá sau khi tăng không đúng!");


        WebElement checkoutBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"root\"]/div/div/div[2]/div/div/div[2]/div[4]/button")));
        checkoutBtn.click();

        // 6. Kiểm tra đã tới trang thanh toán
        wait.until(ExpectedConditions.urlContains("/checkout"));
        String finalUrl = driver.getCurrentUrl();
        Assertions.assertTrue(finalUrl.contains("/checkout"), "Không chuyển đến trang thanh toán: " + finalUrl);


        // 2. Lấy input
        WebElement nameInput = driver.findElement(By.id("name"));
        WebElement phoneInput = driver.findElement(By.id("phone"));
        WebElement addressInput = driver.findElement(By.id("address"));
        WebElement btnThanhToan = driver.findElement(By.xpath("/html/body/div/div/form/div/div[2]/div[2]/button"));

        // 3. Clear input
        nameInput.clear();
        phoneInput.clear();
        addressInput.clear();

        // 4. Kiểm tra bỏ trống tất cả
        btnThanhToan.click();
        Thread.sleep(1000);
        Assertions.assertTrue(driver.findElement(By.xpath("/html/body/div/div/form/div/div[1]/div[2]/div[1]/div")).getText().contains("Không được để trống"));
        Assertions.assertTrue(driver.findElement(By.xpath("/html/body/div/div/form/div/div[1]/div[2]/div[2]/div")).getText().contains("Không được để trống"));
        Assertions.assertTrue(driver.findElement(By.xpath("/html/body/div/div/form/div/div[1]/div[2]/div[3]/div")).getText().contains("Không được để trống"));

        // 5. Nhập sai số điện thoại
        nameInput.sendKeys("Nguyen Van A");
        phoneInput.sendKeys("abc");
        addressInput.sendKeys("123 Đường ABC, TP.HCM");
        btnThanhToan.click();
        Thread.sleep(1000);
        String phoneError = driver.findElement(By.xpath("/html/body/div/div/form/div/div[1]/div[2]/div[2]/div")).getText();
        Assertions.assertTrue(phoneError.contains("Số điện thoại phải có 10 số và bắt đầu bằng số 0."), "Lỗi số điện thoại hiển thị sai");

        // 6. Nhập thiếu số
        phoneInput.clear();
        phoneInput.sendKeys("012345678");
        btnThanhToan.click();
        Thread.sleep(1000);
        phoneError = driver.findElement(By.xpath("/html/body/div/div/form/div/div[1]/div[2]/div[2]/div")).getText();
        Assertions.assertTrue(phoneError.contains("Số điện thoại phải có 10 số và bắt đầu bằng số 0."));

        // 7. Không bắt đầu bằng 0
        phoneInput.clear();
        phoneInput.sendKeys("1234567898");
        btnThanhToan.click();
        Thread.sleep(1000);
        phoneError = driver.findElement(By.xpath("/html/body/div/div/form/div/div[1]/div[2]/div[2]/div")).getText();
        Assertions.assertTrue(phoneError.contains("Số điện thoại phải có 10 số và bắt đầu bằng số 0."));

        // 8. Nhập tên chứa số
        nameInput.clear();
        nameInput.sendKeys("Nguyen Van 5");
        phoneInput.clear();
        phoneInput.sendKeys("0987654321");
        btnThanhToan.click();
        Thread.sleep(1000);
        String nameError = driver.findElement(By.xpath("/html/body/div/div/form/div/div[1]/div[2]/div[1]/div")).getText();
        Assertions.assertTrue(nameError.contains("Tên không bao gồm kí tự số và kí tự đặc biệt"));

        // 9. Nhập tên chứa kí tự đặc biệt
        nameInput.clear();
        nameInput.sendKeys("Nguyen Van %*&?<>");
        btnThanhToan.click();
        Thread.sleep(1000);
        nameError = driver.findElement(By.xpath("/html/body/div/div/form/div/div[1]/div[2]/div[1]/div")).getText();
        Assertions.assertTrue(nameError.contains("Tên không bao gồm kí tự số và kí tự đặc biệt"));

        // 10. Nhập đúng tất cả
        nameInput.clear();
        nameInput.sendKeys("Nguyễn Văn A");
        phoneInput.clear();
        phoneInput.sendKeys("0987654321");
        addressInput.clear();
        addressInput.sendKeys("123 Đường ABC, TP.HCM");
        btnThanhToan.click();
        Thread.sleep(2000);

        List<WebElement> errors = driver.findElements(By.xpath("//small"));
        Assertions.assertTrue(errors.isEmpty(), "Vẫn còn lỗi khi nhập đúng dữ liệu!");
 ///  test qr
        // B1: Chờ chuyển hướng đến trang thanh toán MoMo
        wait.until(ExpectedConditions.urlContains("test-payment.momo.vn"));
        WebDriverWait wait20s = new WebDriverWait(driver, Duration.ofSeconds(10));
        // B2: Sau khi thanh toán xong (hoặc mô phỏng callback), redirect về trang chủ
        wait20s.until(ExpectedConditions.urlContains("localhost:5173"));

        // B3: Kiểm tra thông báo hiện ra
        WebElement message = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#swal2-title")));
        String value = message.getText().trim();

        // B4: Kiểm tra nội dung thông báo
        assertEquals("Mua hàng thành công", value);
    }
}
