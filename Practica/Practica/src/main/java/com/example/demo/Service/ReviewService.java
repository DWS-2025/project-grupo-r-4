package com.example.demo.Service;

import com.example.demo.Model.Review;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ReviewService {
    private ConcurrentHashMap<Long, Review> reviews = new ConcurrentHashMap<>();
    private AtomicLong nextId = new AtomicLong();

    public Optional<Review> findById(long id) {
        if(this.reviews.containsKey(id)){
            return Optional.of(reviews.get(id));
        }else
            return Optional.empty();
    }

    public Review save(Review review) {
        long id = nextId.getAndIncrement();
        review.setId(id);
        reviews.put(id, review);
        return review;

    }

    public void deleteById(long id) {
        this.reviews.remove(id);
    }
}
