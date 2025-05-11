package com.example.demo.Service;

import com.example.demo.Model.Purchase;
import com.example.demo.Model.Review;
import com.example.demo.Model.User;
import com.example.demo.Model.UserDTO;
import com.example.demo.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setAddress(user.getAddress());
        userDTO.setPhone(user.getPhone());
        userDTO.setNumReviews(user.getNumReviews());
        userDTO.setReviewIds(user.getReviews().stream().map(Review::getReviewId).collect(Collectors.toList()));  // Convertir las Reviews a sus IDs
        userDTO.setProductIds(user.getProducts().stream().map(Purchase::getId).collect(Collectors.toList()));  // Convertir los productos a sus IDs
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


    public List<UserDTO> findAll() {
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

    public User findByNameDatabse(String name){
        return userRepository.findByName(name).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    public User registerNewUser(User user) {
        if (userRepository.findByName(user.getName()).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }

        // Codificar la contraseña antes de guardarla
        user.setEncodedPassword(passwordEncoder.encode(user.getEncodedPassword()));

        // Asignar rol por defecto
        user.setRoles(List.of("USER"));

        return userRepository.save(user);
    }
}
