package bk_ed.spbt.MT_26.repository;

import bk_ed.spbt.MT_26.entity.Transaction;
import bk_ed.spbt.MT_26.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser(AppUser user);
    Optional<Transaction> findByIdAndUser(Long id, AppUser user);
    List<Transaction> findByUserAndType(AppUser user, String type);
    List<Transaction> findByUserAndDescriptionContainingIgnoreCase(AppUser user, String description);
    long countByUser(AppUser user);
    
    @Query("SELECT t FROM Transaction t WHERE t.user = :user AND t.transactionDate >= :startDate AND t.transactionDate <= :endDate")
    List<Transaction> findByUserAndDateRange(@Param("user") AppUser user, 
                                             @Param("startDate") LocalDateTime startDate, 
                                             @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT t FROM Transaction t WHERE t.user = :user AND t.category.id = :categoryId")
    List<Transaction> findByUserAndCategory(@Param("user") AppUser user, @Param("categoryId") Long categoryId);
}
