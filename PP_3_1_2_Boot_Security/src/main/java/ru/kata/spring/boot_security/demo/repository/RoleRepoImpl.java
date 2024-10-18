package ru.kata.spring.boot_security.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import ru.kata.spring.boot_security.demo.model.Role;

public interface RoleRepoImpl extends JpaRepository<Role, Long> {
}
