package com.rbac.repository;

import com.rbac.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByPermissionKey(String permissionKey);
    Boolean existsByPermissionKey(String permissionKey);
    
    @Query("SELECT p FROM Permission p WHERE p.module = :module")
    List<Permission> findByModule(@Param("module") String module);
    
    @Query("SELECT DISTINCT p.module FROM Permission p ORDER BY p.module")
    List<String> findAllModules();
    
    @Query("SELECT p FROM Permission p WHERE " +
           "(:module IS NULL OR p.module = :module) AND " +
           "(:name IS NULL OR p.name LIKE %:name%)")
    Page<Permission> findWithFilters(@Param("module") String module,
                                   @Param("name") String name,
                                   Pageable pageable);
}