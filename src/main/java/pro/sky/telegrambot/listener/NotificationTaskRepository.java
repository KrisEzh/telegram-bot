package pro.sky.telegrambot.listener;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {

    List<NotificationTask> findByLocalDateTimeEquals(LocalDateTime currentTime);

}
