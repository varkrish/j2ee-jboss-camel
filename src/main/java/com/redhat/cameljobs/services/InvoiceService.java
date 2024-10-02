package com.redhat.cameljobs.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvoiceService {

    // Mock method to simulate fetching invoice items
    public List<Map<String, Object>> getInvoiceItems(String invoiceId) {
        List<Map<String, Object>> invoiceItems = new ArrayList<>();

        // Example mock items for the invoice
        for (int i = 1; i <= 3; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("item_id", "item" + i);
            item.put("invoice_id", invoiceId);
            invoiceItems.add(item);
        }

        return invoiceItems;
    }

    // Mock method to simulate fetching invoice item details
    public Map<String, Object> getInvoiceItemDetails(String itemId) {
        Map<String, Object> itemDetails = new HashMap<>();
        itemDetails.put("item_id", itemId);
        itemDetails.put("description", "Description for " + itemId);
        itemDetails.put("quantity", 10);
        itemDetails.put("price", 100);
        return itemDetails;
    }
}
