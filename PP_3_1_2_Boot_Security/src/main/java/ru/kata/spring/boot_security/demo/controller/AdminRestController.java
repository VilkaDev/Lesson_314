package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/admin/users")
public class AdminRestController {

    // TODO внимание!!! в файле admin.js  в первых строках необходимо расставить айди ролей согласно вашей БД!

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminRestController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping()
    public List<User> listAllUsers() {
        List<User> users = userService.getAllUsers();
        return users;
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") Long id) {
        User user = userService.findById(id);
        return user;
        // не вижу смысла делать проверку на пустого юзера
    }

    @PostMapping()
    public User saveUser(@RequestBody User user) {
        userService.saveUser(user);
        return user;
    }

    @PatchMapping()
    public User editUser(@RequestBody User user) {
        userService.saveUser(user);
        return user;
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return String.format("User with ID = %d was deleted", id);
        //тоже не вижу смысла делать проверку на пустого юзера тк кнопка будет всегда на существующем. не иначе
    }
}