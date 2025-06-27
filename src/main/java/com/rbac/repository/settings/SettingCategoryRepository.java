
package com.rbac.repository.settings;

import com.rbac.entity.settings.SettingCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingCategoryRepository extends JpaRepository<SettingCategory, Long> {
    Optional<SettingCategory> findByName(String name);
    boolean existsByName(String name);
}
