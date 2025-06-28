
package com.rbac.repository;

import com.rbac.entity.Setting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Long> {
    
    Optional<Setting> findByKey(String key);
    
    boolean existsByKey(String key);
    
    @Query("SELECT s FROM Setting s WHERE " +
           "(:key IS NULL OR LOWER(s.key) LIKE LOWER(CONCAT('%', :key, '%'))) AND " +
           "(:type IS NULL OR LOWER(s.type) = LOWER(:type))")
    Page<Setting> findWithFilters(@Param("key") String key, 
                                 @Param("type") String type, 
                                 Pageable pageable);
}
