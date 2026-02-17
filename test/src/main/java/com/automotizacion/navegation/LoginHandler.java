package com.automotizacion.navegation;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.automotizacion.driver.ChromeDriverManager;

import java.util.Scanner;

public class LoginHandler {

    public static void ejecutar(ChromeDriverManager driverManager) throws InterruptedException {
        WebDriver driver = driverManager.getDriver();
        WebDriverWait wait = driverManager.getWait();

        driver.get("https://srienlinea.sri.gob.ec/sri-en-linea/inicio/NAT");
        Thread.sleep(5000);

        try {
            // Intento 1: XPath original
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[.//p[contains(text(), 'Iniciar sesión')]]")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        } catch (Exception ignored) {
            try {
                // Intento 2: Por texto parcial
                WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath(
                                "//button[contains(text(), 'Iniciar sesión')] | //a[contains(text(), 'Iniciar sesión')]")));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            } catch (Exception ignored2) {
                // Intento 3: Por clase o atributo común
                try {
                    WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
                            By.cssSelector("a.btn-primary, button.btn-primary, [class*='login']")));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
                } catch (Exception ignored3) {
                }
            }
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println(" CREDENCIALES");
        System.out.println("=".repeat(60));

        Scanner sc = new Scanner(System.in);
        System.out.print(" RUC: ");
        String ruc = sc.nextLine().trim();
        System.out.print(" CLAVE: ");
        String clave = sc.nextLine().trim();

        if (ruc.isEmpty() || clave.isEmpty()) {
            System.out.println("Error: Ingresa RUC y clave");
            System.exit(0);
        }

        try {
            WebElement rucInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div/div/div/div/form/div[1]/div[2]/div/input")));
            WebElement claveInput = driver.findElement(
                    By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div/div/div/div/form/div[3]/div[2]/div/input"));
            WebElement btnIngresar = driver.findElement(
                    By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div/div/div/div/form/div[4]/div[1]/div/input"));

            rucInput.clear();
            rucInput.sendKeys(ruc);
            claveInput.clear();
            claveInput.sendKeys(clave);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnIngresar);

            wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.xpath("//span[contains(text(), 'Facturación')]")));
            System.out.println("LOGIN 100% AUTOMÁTICO");
        } catch (Exception e) {
            System.out.print("→ Haz login manual y presiona ENTER...");
            new Scanner(System.in).nextLine();
        }
        Thread.sleep(10000);
    }
}