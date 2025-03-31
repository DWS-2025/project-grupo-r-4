package com.example.demo.Service;

import com.example.demo.Model.User;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {
    private AtomicLong nextId = new AtomicLong();
    private ConcurrentMap<String, User> users = new ConcurrentHashMap<>();

    public Collection<User> findAll() {
        return users.values();
    }

    public UserService(){
        save(new User("user"));
    }

    public User findByUserName(String username) {
        for (User user : users.values()) {
            if (user.getName().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public Optional<User> findById(long id) {
        if(this.users.containsKey(id)){
            return Optional.of(users.get(id));
        }else
            return Optional.empty();
    }
    public void save(User user) {
        long id = nextId.getAndIncrement();
        user.setId(id);
        String username = user.getName();
        users.put(username, user);
    }

    public void deleteByUser(String username) {
        users.remove(username);
    }
    public void deleteById(long id){
        this.users.remove(id);
    }
}
