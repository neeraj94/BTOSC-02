
package com.rbac.repository.settings;

import com.rbac.entity.settings.SettingField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SettingFieldRepository extends JpaRepository<SettingField, Long> {
    List<SettingField> findBySubCategoryId(Long subCategoryId);
    Optional<SettingField> findByFieldKeyAndSubCategoryId(String fieldKey, Long subCategoryId);
    boolean existsByFieldKeyAndSubCategoryId(String fieldKey, Long subCategoryId);
}
