package com.example.demo.Service;

import com.example.demo.Model.Purchase;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class PurchaseService {
    private final ConcurrentMap<Long, Purchase> purchases = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public Collection<Purchase> findAll(String user) {
        return purchases.values().stream()
                .filter(purchase -> purchase.getUser().equals(user))
                .toList();
    }

    public Purchase findById(long id) {
        return purchases.get(id);
    }

    public Purchase Save(Purchase purchase) {
        long id = nextId.getAndIncrement();
        purchase.setId(id);
        purchases.put(id, purchase);
        return purchase;
    }

    public void deleteById(long id) {
        purchases.remove(id);
    }
}
