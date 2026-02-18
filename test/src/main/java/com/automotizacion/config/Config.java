package com.automotizacion.config;

public class Config {
    public static final String DOWNLOAD_DIR = "C:\\Users\\Jordy\\Downloads";
    public static final String CHROME_PROFILE = "C:\\Users\\Jordy\\Desktop\\AutoJava\\chrome_profile";
    public static final String CHROME_PATH = "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe";
    public static final String CARPETA_FACTURAS = "C:\\Users\\Jordy\\Downloads\\Facturas";
    public static final String CARPETA_RETENCIONES = "C:\\Users\\Jordy\\Downloads\\Retenciones";
    public static final int DEBUG_PORT = 9222;
    public static final int THREAD_POOL_SIZE = 10;
    
    // API key should be provided via environment variable to avoid hardcoding secrets.
    // Set in Windows (session): $env:GOOGLE_API_KEY = 'your_key'
    // Or permanently: setx GOOGLE_API_KEY "your_key"
    public static final String GOOGLE_API_KEY = System.getenv("GOOGLE_API_KEY");