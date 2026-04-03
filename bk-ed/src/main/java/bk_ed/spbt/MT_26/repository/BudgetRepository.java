package bk_ed.spbt.MT_26.repository;

import bk_ed.spbt.MT_26.entity.Budget;
import bk_ed.spbt.MT_26.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUser(AppUser user);
    Optional<Budget> findByIdAndUser(Long id, AppUser user);
    List<Budget> findByUserAndPeriod(AppUser user, String period);
    Optional<Budget> findByUserAndName(AppUser user, String name);
    List<Budget> findByUserAndNameContainingIgnoreCase(AppUser user, String name);
    long countByUser(AppUser user);
}
