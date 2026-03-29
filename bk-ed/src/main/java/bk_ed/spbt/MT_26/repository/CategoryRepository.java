package bk_ed.spbt.MT_26.repository;

import bk_ed.spbt.MT_26.entity.Category;
import bk_ed.spbt.MT_26.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUser(AppUser user);
    Optional<Category> findByIdAndUser(Long id, AppUser user);
    boolean existsByNameAndUser(String name, AppUser user);
}
