package com.example.demo.Service;

import com.example.demo.Model.User;
import com.example.demo.Model.UserDTO;
import com.example.demo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;


    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setAddress(user.getAddress());
        userDTO.setPhone(user.getPhone());
        userDTO.setNumReviews(user.getNumReviews());
        userDTO.setReviewIds(user.getReviews().stream().map(r -> r.getReviewId()).collect(Collectors.toList()));  // Convertir las Reviews a sus IDs
        userDTO.setProductIds(user.getProducts().stream().map(p -> p.getId()).collect(Collectors.toList()));  // Convertir los productos a sus IDs
        return userDTO;
    }


    User convertToEntity(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setName(userDTO.getName());
        user.setAddress(userDTO.getAddress());
        user.setPhone(userDTO.getPhone());
        user.setNumReviews(userDTO.getNumReviews());

        return user;
    }


    public Collection<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    public UserDTO findByUserName(String username) {
        User user = userRepository.findByName(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return convertToDTO(user);
    }


    public Optional<UserDTO> findById(long id) {
        return userRepository.findById(id).map(this::convertToDTO);
    }


    public UserDTO save(UserDTO userDTO) {
        User user = convertToEntity(userDTO);
        user = userRepository.save(user);
        return convertToDTO(user);  // Devolver el usuario guardado como DTO
    }


    public void deleteByUser(String username) {
        User user = userRepository.findByName(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        userRepository.delete(user);
    }


    public void deleteById(long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        userRepository.deleteById(id);
    }
}
