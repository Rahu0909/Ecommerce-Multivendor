package com.project.ecommerce.repositories;

import com.project.ecommerce.model.AppRole;
import com.project.ecommerce.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository
        extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleName(AppRole appRole);
}

