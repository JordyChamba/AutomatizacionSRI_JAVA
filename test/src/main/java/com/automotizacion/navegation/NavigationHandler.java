package com.automotizacion.navegation;

import org.openqa.selenium.*;

import com.automotizacion.driver.ChromeDriverManager;

import java.util.Scanner;

public class NavigationHandler {

    public static void irAComprobantesRecibidos(ChromeDriverManager driverManager) throws InterruptedException {
        WebDriver driver = driverManager.getDriver();

        try {
            WebElement facturacion = driver.findElement(By.xpath("//span[contains(text(), 'Facturación')]"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", facturacion);
            Thread.sleep(5000);

            WebElement recibidos = driver.findElement(
                    By.xpath("//span[contains(text(), 'Comprobantes') and contains(text(), 'recibidos')]"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", recibidos);
            System.out.println("Comprobantes recibidos abiertos");
            Thread.sleep(6000);
        } catch (Exception e) {
            System.out.print("→ Ve a Comprobantes recibidos y presiona ENTER...");
            new Scanner(System.in).nextLine();
        }
    }
}