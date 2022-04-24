package pro.sky.telegrambot.listener;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table (name="notification_task")
public class NotificationTask {

        @Id
        @GeneratedValue
        private long id;

        private String text;
        private LocalDateTime localDateTime;


    public NotificationTask(LocalDateTime myTime, String text) {
        this.text = text;
        this.localDateTime = myTime;
    }

    public NotificationTask() {

    }


    public long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public LocalDateTime getLocalDateTime() {
            return localDateTime;
        }

        public void setLocalDateTime(LocalDateTime myTime) {
            this.localDateTime = myTime;
        }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTask that = (NotificationTask) o;
        return id == that.id && Objects.equals(text, that.text) && Objects.equals(localDateTime, that.localDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, localDateTime);
    }

    @Override
    public String toString() {
        return "NotificationTask{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", localDateTime=" + localDateTime +
                '}';
    }
}
