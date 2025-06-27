
package com.rbac.repository.settings;

import com.rbac.entity.settings.SettingSubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SettingSubCategoryRepository extends JpaRepository<SettingSubCategory, Long> {
    List<SettingSubCategory> findByCategoryId(Long categoryId);
    Optional<SettingSubCategory> findByNameAndCategoryId(String name, Long categoryId);
    boolean existsByNameAndCategoryId(String name, Long categoryId);
}
