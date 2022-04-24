package pro.sky.telegrambot.listener;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "notification_task")
public class NotificationTask<N> {

    @Id
    @GeneratedValue
    private long id;

    private String text;
    private LocalDateTime localDateTime;

    private long chatId;

    public NotificationTask(long chatId, LocalDateTime localDateTime, String text) {
        this.chatId = chatId;
        this.text = text;
        this.localDateTime = localDateTime;
    }

    public NotificationTask() {

    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
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
        this.localDateTime = localDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTask<?> that = (NotificationTask<?>) o;
        return id == that.id && chatId == that.chatId && Objects.equals(text, that.text) && Objects.equals(localDateTime, that.localDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, localDateTime, chatId);
    }

    @Override
    public String toString() {
        return "NotificationTask{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", localDateTime=" + localDateTime +
                ", chatId=" + chatId +
                '}';
    }
}
