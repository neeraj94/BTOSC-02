package com.rbac.repository;

import com.rbac.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    Boolean existsByName(String name);
    
    @Query("SELECT r FROM Role r WHERE r.isSystemRole = :isSystemRole")
    Page<Role> findByIsSystemRole(@Param("isSystemRole") Boolean isSystemRole, Pageable pageable);
    
    @Query("SELECT r FROM Role r WHERE " +
           "(:name IS NULL OR r.name LIKE %:name%) AND " +
           "(:description IS NULL OR r.description LIKE %:description%)")
    Page<Role> findWithFilters(@Param("name") String name,
                              @Param("description") String description,
                              Pageable pageable);
}