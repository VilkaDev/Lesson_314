package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepoImpl;
import ru.kata.spring.boot_security.demo.repository.UserRepoImpl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private UserRepoImpl userRepo;
    private RoleRepoImpl roleRepo;

    @PersistenceContext // синхронизация с БД
    private EntityManager em;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepoImpl userRepo, RoleRepoImpl roleRepo, @Lazy BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);   // пытаемся достать юзера по пришедшему имени
        if(user == null) {
            throw new UsernameNotFoundException(String.format("User '%s' not found", username));
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), user.getAuthorities());
    }

    public User findByUsername(String username) {
        return userRepo.findByUsername(username);
    }


    public User findById(Long userId) {
        Optional<User> userFromDb = userRepo.findById(userId);
        return userFromDb.orElse(new User());
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();  // когда делала CRUD репозиторий, светилось красным
    }

    public boolean saveUser(User user) {
//        User userFromDB = userRepo.findByUsername(user.getUsername());
//
//        if (userFromDB != null) {
//            return false;
//        }
//
//        user.setRoles((List<Role>) Collections.singleton(new Role(1L, "ROLE_USER")));
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepo.save(user);
        return true;
    }

    public boolean deleteUser (Long userId) {
        if (userRepo.findById(userId).isPresent()) {
            userRepo.deleteById(userId);
            return true;
        }
        return false;
    }

}
