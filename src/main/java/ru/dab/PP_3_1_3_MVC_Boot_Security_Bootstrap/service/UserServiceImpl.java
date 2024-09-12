package ru.dab.PP_3_1_3_MVC_Boot_Security_Bootstrap.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dab.PP_3_1_3_MVC_Boot_Security_Bootstrap.models.User;
import ru.dab.PP_3_1_3_MVC_Boot_Security_Bootstrap.repositories.UserRepository;
import ru.dab.PP_3_1_3_MVC_Boot_Security_Bootstrap.сustomExeptions.UsernameAlreadyExistsException;


import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    @Transactional
    public void create(User user) {
        User existingUser = userRepository.findByUsername(user.getUsername());

        if (existingUser != null && existingUser.isEnabled()) {
            throw new UsernameAlreadyExistsException("Пользователь с таким именем уже существует");
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void update(User user) {
        Optional<User> existingUserOptional = userRepository.findById(user.getUserId());

        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get(); //получили из БД юзера без обновленных данных и удостоверились что но существует

            User userWithDuplicateUsername = userRepository.findByUsername(user.getUsername()); //проверка существует ли в БД юзер с таким же именем
            if (userWithDuplicateUsername != null && !userWithDuplicateUsername.getUserId().equals(existingUser.getUserId())) {
                throw new UsernameAlreadyExistsException("Пользователь с таким именем уже существует");
            }

            // Проверка, изменялся ли пароль
            if (!existingUser.getPassword().equals(user.getPassword())) {
                user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            } else {
                user.setPassword(existingUser.getPassword()); // сохранить старый пароль
            }

            userRepository.saveAndFlush(user);
        } else {
            throw new EntityNotFoundException("User for updating not found.");
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("User for deletion not found.");
        }
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found by ID."));
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public boolean isUserExist(User user) {
        return userRepository.findByUsername(user.getUsername()) != null;
    }
}
