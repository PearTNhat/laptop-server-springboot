package com.laptop.ltn.laptop_store_server.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.interactions.Actions;

import java.io.File;
import java.time.Duration;
import java.util.List;

public class CreateProductTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private Actions actions;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        actions = new Actions(driver);
        System.out.println("Setting up test environment");
    }

    private void loginAsAdmin() {
        // Navigate to login page
        driver.get("http://localhost:6001/login");

        // Enter admin credentials
        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        emailInput.sendKeys("test@gmail.com"); // Replace with your admin email

        WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
        passwordInput.sendKeys("2003925"); // Replace with your admin password
        passwordInput.sendKeys(Keys.RETURN);

        // Wait for successful login
        WebElement welcome = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#root > div > div > div.bg-main > div > span:nth-child(2)")));
        Assertions.assertTrue(welcome.getText().toLowerCase().contains("welcome"), "Đăng nhập thất bại");
        System.out.println("Successfully logged in as admin");
    }

    @Test
    void testCreateProduct() throws InterruptedException {
        // Step 1: Login as admin
        loginAsAdmin();

        // Step 2: Navigate to product creation page
        driver.get("http://localhost:6001/admin/manage/products/create");
        System.out.println("Navigated to product creation page");
        Thread.sleep(1000); // Give page time to fully load

        // Step 3: Upload product image - improved method
        String imagePath = "D:\\Deadline\\DamBaoCLPM\\laptop-server-springboot\\src\\test\\java\\com\\laptop\\ltn\\laptop_store_server\\selenium\\resources\\text_d_i_1__4.webp";
        File imageFile = new File(imagePath);

        // Make sure the file exists
        if (!imageFile.exists()) {
            System.out.println("Warning: Test image file not found at: " + imagePath);
            // Create resource directory if it doesn't exist
            File resourceDir = new File(
                    "D:\\Deadline\\DamBaoCLPM\\laptop-server-springboot\\src\\test\\java\\com\\laptop\\ltn\\laptop_store_server\\selenium\\resources");
            if (!resourceDir.exists()) {
                resourceDir.mkdirs();
                System.out.println("Created resource directory at: " + resourceDir.getAbsolutePath());
            }
        }

        // Use direct input method as it's more reliable across different browsers
        WebElement uploadInput = driver.findElement(By.id("profilePicture"));
        uploadInput.sendKeys(imageFile.getAbsolutePath());
        System.out.println("Attempted to upload image from: " + imageFile.getAbsolutePath());

        // Wait for image to be processed/displayed
        Thread.sleep(1000);

        // Step 4: Fill in basic product information
        WebElement titleInput = driver.findElement(By.id("title"));
        titleInput.clear();
        titleInput.sendKeys("Test Laptop XPS 13");

        WebElement priceInput = driver.findElement(By.id("price"));
        priceInput.sendKeys("25000000");

        WebElement discountPriceInput = driver.findElement(By.id("discountPrice"));
        discountPriceInput.sendKeys("23500000");

        WebElement cpuInput = driver.findElement(By.id("cpu"));
        cpuInput.sendKeys("Intel Core i7-1165G7");

        WebElement gpuInput = driver.findElement(By.id("graphicCard"));
        gpuInput.sendKeys("NVIDIA GeForce RTX 3050 4GB");

        WebElement ramInput = driver.findElement(By.id("ram"));
        ramInput.sendKeys("16GB DDR4 3200MHz");

        WebElement ramStorageInput = driver.findElement(By.id("ram-storage"));
        ramStorageInput.sendKeys("16GB");

        System.out.println("Filled basic product information");

        // Step 5: Fill in hardware specifications
        WebElement hardDriveInput = driver.findElement(By.id("hardDrive"));
        hardDriveInput.sendKeys("512GB SSD NVMe");

        WebElement hardDriveStorageInput = driver.findElement(By.id("hardDrive-storage"));
        hardDriveStorageInput.sendKeys("512GB");

        WebElement refreshRateInput = driver.findElement(By.id("refreshRate"));
        refreshRateInput.sendKeys("120Hz");

        WebElement pannelInput = driver.findElement(By.id("pannel"));
        pannelInput.sendKeys("IPS");

        WebElement screenInput = driver.findElement(By.id("screen"));
        screenInput.sendKeys("15.6 inch");

        WebElement resolutionInput = driver.findElement(By.id("resolution"));
        resolutionInput.sendKeys("1920x1080");

        WebElement sizeInput = driver.findElement(By.id("size"));
        sizeInput.sendKeys("359.86 x 258.7 x 21.9-23.9 mm");

        WebElement weightInput = driver.findElement(By.id("weight"));
        weightInput.sendKeys("1.9kg");

        WebElement osInput = driver.findElement(By.id("operatingSystem"));
        osInput.sendKeys("Windows 11 Home");

        WebElement batteryInput = driver.findElement(By.id("battery"));
        batteryInput.sendKeys("4-cell, 56WHr");

        WebElement needInput = driver.findElement(By.id("need"));
        needInput.sendKeys("Gaming");

        System.out.println("Filled hardware specifications");

        // Step 6: Select brand from dropdown - fixed selector
        WebElement brandDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".react-select__control")));
        brandDropdown.click();

        WebElement brandOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class, 'react-select__option')][contains(text(), 'Asus')]")));
        brandOption.click();
        System.out.println("Selected brand from dropdown");

        // Step 7: Select series from dropdown - wait for the first dropdown action to
        // complete
        Thread.sleep(2000); // Longer wait to ensure UI updates completely

        // Target the second react-select component
        WebElement seriesDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//div[contains(@class, 'react-select__control')])[2]")));
        seriesDropdown.click();

        // Wait longer for dropdown options to appear and print available options for
        // debugging
        Thread.sleep(1000);

        // Find all available options and select the first one (or a specific one if
        // available)
        List<WebElement> seriesOptions = driver
                .findElements(By.xpath("//div[contains(@class, 'react-select__option')]"));

        if (!seriesOptions.isEmpty()) {
            // Debug: Print all available options
            System.out.println("Available series options:");
            for (WebElement option : seriesOptions) {
                System.out.println(" - " + option.getText());
            }

            // Select the first available option instead of looking for a specific one
            seriesOptions.get(0).click();
            System.out.println("Selected the first available series option");
        } else {
            System.out.println("No series options found in dropdown");
        }

        // Step 9: Submit the form
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();
        System.out.println("Submitted the product creation form");

        // Step 10: Verify successful creation
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".toast-success, .swal2-success")));
        System.out.println("Product created successfully");
    }

    @Test
    void testAddProductColor() throws InterruptedException {
        // Step 1: Login as admin
        loginAsAdmin();

        // Step 2: Navigate to products list page
        driver.get("http://localhost:6001/admin/manage/products");
        System.out.println("Navigated to products list page");
        Thread.sleep(1000);

        // Step 3: Click on the first product in the list to view details
        WebElement firstProduct = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("table tbody tr:first-child")));
        firstProduct.click();
        System.out.println("Selected product for color addition");
        Thread.sleep(1000);

        // Step 4: Navigate to add color page for the selected product
        // The URL should include the product ID, but we'll use a generic approach for
        // the test
        WebElement addColorButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(), 'Tạo màu mới') or contains(@href, '/create-color')]")));
        addColorButton.click();
        System.out.println("Navigated to add color page");
        Thread.sleep(1000);

        // Step 5: Fill the color form
        WebElement colorInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("color")));
        colorInput.sendKeys("Vàng");

        WebElement quantityInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("quantity")));
        quantityInput.sendKeys("10");
        System.out.println("Filled color and quantity fields");

        // Step 6: Upload thumbnail image
        WebElement thumbUploadInput = driver.findElement(By.id("profilePicture"));
        String thumbImagePath = "D:\\Deadline\\DamBaoCLPM\\laptop-server-springboot\\src\\test\\java\\com\\laptop\\ltn\\laptop_store_server\\selenium\\resources\\text_d_i_1__4.webp";
        File thumbImageFile = new File(thumbImagePath);
        if (!thumbImageFile.exists()) {
            System.out.println("Warning: Thumbnail image not found at: " + thumbImagePath);
        }
        thumbUploadInput.sendKeys(thumbImageFile.getAbsolutePath());
        System.out.println("Uploaded thumbnail image");
        Thread.sleep(1000);

        // Step 7: Upload multiple images
        WebElement imagesUploadInput = driver.findElement(By.id("images"));
        String imagePath1 = "D:\\Deadline\\DamBaoCLPM\\laptop-server-springboot\\src\\test\\java\\com\\laptop\\ltn\\laptop_store_server\\selenium\\resources\\backgroundDefault.jpg";
        File imageFile1 = new File(imagePath1);

        if (!imageFile1.exists()) {
            System.out.println("Warning: Some product images not found");
        }

        // For multiple file upload, we need to provide paths separated by \n
        String multiplePaths = imageFile1.getAbsolutePath();
        imagesUploadInput.sendKeys(multiplePaths);
        System.out.println("Uploaded multiple product images");
        Thread.sleep(1000);

        // Step 8: Submit the form
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();
        System.out.println("Submitted the color creation form");

        // Step 9: Verify successful creation
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".toast-success, .swal2-success")));
        System.out.println("Color added successfully");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        System.out.println("Test environment cleaned up");
    }
}
