package com.myproject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    private static StockFeeder stockFeeder;
    private static PriceFetchManager priceFetchManager;

    public static void main(String[] args) {
        init();
        run();
    }
    
    private static void init() {
        stockFeeder = StockFeeder.getInstance();

        String filePath = "src/resources/stocks.json";

        HosePriceFetchLib hoseLib = new HosePriceFetchLib(filePath);
        List<String> hoseStockCodes = Arrays.asList("VIC", "VNM", "HPG", "FPT");
        HoseAdapter hoseAdapter = new HoseAdapter(hoseLib, hoseStockCodes);

        HnxPriceFetchLib hnxLib = new HnxPriceFetchLib(filePath);
        List<String> hnxStockCodes = Arrays.asList("SHB", "ACB", "PVS");
        HnxAdapter hnxAdapter = new HnxAdapter(hnxLib, hnxStockCodes);

        priceFetchManager = new PriceFetchManager(stockFeeder);
        priceFetchManager.addFetcher(hoseAdapter);
        priceFetchManager.addFetcher(hnxAdapter);

        // Stock VICstock = new Stock("VIC", "Vingroup");
        // stockFeeder.addStock(VICstock); 

        // stockFeeder.registerViewer("VIC", new StockAlertView(60000, 55000));
        // stockFeeder.registerViewer("VIC", new StockRealtimePriceView());
        // stockFeeder.registerViewer("VIC", new StockTickerView());

        // Combine the lists to test all available stocks.
        List<String> allStockCodes = new ArrayList<>();
        allStockCodes.addAll(hoseStockCodes);
        allStockCodes.addAll(hnxStockCodes);

        // For each stock, add the stock to monitoring and register viewers.
        for (String code : allStockCodes) {
            // Create a Stock instance (using code + " Company" as a sample company name).
            Stock stock = new Stock(code, code + " Company");
            stockFeeder.addStock(stock);

            // Register the viewers.
            stockFeeder.registerViewer(code, new StockAlertView(60000, 55000));
            stockFeeder.registerViewer(code, new StockRealtimePriceView());
            stockFeeder.registerViewer(code, new StockTickerView());
        }

        priceFetchManager.start(); // Start fetching stock prices
    }

    private static void run() {
        try {
            Thread.sleep(Long.MAX_VALUE); // Keeps the program running
        } catch (InterruptedException e) {
            priceFetchManager.stop();
            System.out.println("[ERROR] Stock monitoring service interrupted.");
        }
    }
}
