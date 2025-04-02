package com.example.demo.Service;

import com.example.demo.Model.User;
import com.example.demo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;


    public Collection<User> findAll() {
        return userRepository.findAll();
    }

    /*public UserService(){
        save(new User("user"));
    }*/

    public User findByUserName(String username) {
        return userRepository.findByName(username).orElse(null);
    }

    public Optional<User> findById(long id) {
        return userRepository.findById(id);
    }
    public void save(User user) {
        userRepository.save(user);
    }

    public void deleteByUser(String username) {
        userRepository.deleteByName(username);
    }
    public void deleteById(long id){
        userRepository.deleteById(id);
    }
}
