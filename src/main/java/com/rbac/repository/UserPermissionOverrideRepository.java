package com.rbac.repository;

import com.rbac.entity.UserPermissionOverride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPermissionOverrideRepository extends JpaRepository<UserPermissionOverride, Long> {
    @Query("SELECT upo FROM UserPermissionOverride upo WHERE upo.user.id = :userId")
    List<UserPermissionOverride> findByUserId(@Param("userId") Long userId);

    @Query("SELECT upo FROM UserPermissionOverride upo WHERE upo.user.id = :userId AND upo.permission.id = :permissionId")
    Optional<UserPermissionOverride> findByUserIdAndPermissionId(@Param("userId") Long userId,
                                                                @Param("permissionId") Long permissionId);

    @Modifying
    @Query("DELETE FROM UserPermissionOverride upo WHERE upo.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}