package pro.sky.telegrambot.listener;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;


public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {

    NotificationTask findByLocalDateTimeEquals(LocalDateTime currentTime);

}
