package org.example;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.locks.StampedLock;
import java.util.concurrent.ConcurrentHashMap;



public class FileHelper {
    StampedLock lock = new StampedLock();

    public void saveStocksToFile(String filePath) throws IOException {
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(filePath))) {
            String header = "Name,Price,Quantity";
            bufferedWriter.write(header);
            bufferedWriter.newLine();

            for (Stock stock : CatalogService.stocksCatalog.values()) {
                String line = stock.getStockName() + "," + stock.getPrice() + "," + stock.getQuantity();
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
        }
    }

    public void saveTradesToFile(String filePath) throws IOException {
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(filePath))) {
            String header = "Trade,Stock Name,Order Type,Quantity,Price";
            bufferedWriter.write(header);
            bufferedWriter.newLine();

            for (Trade trade : CatalogService.tradeCatalog.values()) {
                String line = trade.getTradeNumber() + "," + trade.getName() + ","
                        + trade.getOrderType() + "," + trade.getQuantity();
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
        }
    }

    public void loadStocksFromFile(String filePath) throws IOException {
        try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(filePath))) {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            ConcurrentHashMap<String, Stock> tempStocksCatalog = new ConcurrentHashMap<>();
            lines.stream()
                    .skip(1)
                    .map(line -> line.split(","))
                    .parallel()
                    .forEach(parts -> tempStocksCatalog.put(parts[0], new Stock(parts[0], Float.parseFloat(parts[1]), Integer.parseInt(parts[2]))));
            CatalogService.stocksCatalog = tempStocksCatalog;
        }
    }


    public void loadTradesFromFile(String filePath) throws IOException {
        try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(filePath))) {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            ConcurrentHashMap<Integer, Trade> tempTradeCatalog = new ConcurrentHashMap<>();
            lines.stream()
                    .skip(1)
                    .map(line -> line.split(","))
                    .parallel()
                    .forEach(parts -> tempTradeCatalog.put(Integer.parseInt(parts[0]), new Trade(Integer.parseInt(parts[0]), parts[1], parts[2], Integer.parseInt(parts[3]))));
            CatalogService.tradeCatalog = tempTradeCatalog;
            CatalogService.tradeNumber = CatalogService.tradeCatalog.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
        }
    }

    public boolean updateStocks(String stockName, String tradeType, int quantity) {
        long stamp = lock.writeLock();
        try {
            Stock stock = CatalogService.stocksCatalog.get(stockName);
            if (stock == null) {
                return false;
            }
            if (tradeType.equalsIgnoreCase("buy")) {
                stock.setQuantity(stock.getQuantity() - quantity);
            } else if (tradeType.equalsIgnoreCase("sell")) {
                stock.setQuantity(stock.getQuantity() + quantity);
            } else {
                return false;
            }

            saveStocksToFile("stocksCatalog.csv"); // Save the updated catalog to the CSV file
            return true;
        } catch (IOException e) {
            // Handle the exception
            return false;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public boolean updateTrades(Trade trade) {
        long stamp = lock.writeLock();
        try {
            CatalogService.tradeCatalog.put(trade.getTradeNumber(), trade);

            saveTradesToFile("tradeCatalog.csv"); // Save the updated catalog to the CSV file
            return true;
        } catch (IOException e) {
            // Handle the exception
            return false;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public void initializeCatalog() throws IOException {
        // Get file paths
        String stockCatalogFilePath = "stocksCatalog.csv";
        File stockCatalogFile = new File(stockCatalogFilePath);
        String tradeCatalogFilePath = "tradeCatalog.csv";
        File tradeCatalogFile = new File(tradeCatalogFilePath);

        // Handle stock Catalog
        if (stockCatalogFile.exists()) {
            loadStocksFromFile(stockCatalogFilePath);
        } else {
            // No such file, build a new stock Catalog
            System.out.println("Stock catalog file does not exist");
            String[] stockNames = { "Zsgwsh", "KING", "LOL", "WildRaft", "Bench", "Lab", "Ginger" };
            for (int i = 0; i < stockNames.length; i++) {
                Stock stock = new Stock(stockNames[i], (float) Math.random() * 66, (int) (Math.random() * 268));
                CatalogService.stocksCatalog.put(stockNames[i], stock);
            }
            saveStocksToFile("stocksCatalog.csv");
        }

        if (tradeCatalogFile.exists()) {
            loadTradesFromFile(tradeCatalogFilePath);
        } else {
            System.out.println("Trade catalog file does not exist");
        }
    }
}
