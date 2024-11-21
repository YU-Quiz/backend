package yuquiz.domain.notification.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yuquiz.domain.notification.entity.Notification;
import yuquiz.domain.user.entity.User;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findAllByUserAndIsChecked(User user, boolean isChecked, Pageable pageable);

    Page<Notification> findAllByUser(User user, Pageable pageable);

    @Modifying
    @Query(value = "UPDATE Notification n " +
            "SET n.isChecked = true " +
            "WHERE n.id IN :notifications AND n.user.id = :userId")
    void readNotificationsWithValidation(@Param("notifications") Long[] notifications,
                                              @Param("userId") Long userId);
}
