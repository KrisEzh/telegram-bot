package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.DateTimeException;
import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private NotificationTaskRepository notificationTaskRepository;

    public TelegramBotUpdatesListener(NotificationTaskRepository notificationTaskRepository, TelegramBot telegramBot) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }
    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            Message message = update.message();
            long chatId = update.message().chat().id();
            if (update.message().text().equals(new String("/start"))) {
                telegramBot.execute(new SendMessage(chatId, "Приветик!Пришли мне сообщение вида 01.01.2022 20:00 Сделать домашнюю работу, а я напомню тебе о нем в нужное время))"));
            } else {
               parsedMessage(message);
               telegramBot.execute(new SendMessage(chatId, "Я напомню тебе"));
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
    public void parsedMessage(Message message) {
        try {
            Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
            Matcher matcher = pattern.matcher(message.text());
            if (matcher.matches()) {
                String time = matcher.group(1);
                String text = matcher.group(3);
                LocalDateTime parsedDate = LocalDateTime.parse(time, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                NotificationTask object = new NotificationTask(message.chat().id(),parsedDate, text);
                notificationTaskRepository.save(object);
            }
        } catch (DateTimeException e) {
            throw new DateTimeException("Введите дату правильно");
        }
    }

    @Scheduled(fixedDelay = 60_000L)
    public void findMessage() {
        try {
            LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            List<NotificationTask> notifications = notificationTaskRepository.findByLocalDateTimeEquals(currentTime);
            logger.info("ищу");
            for(NotificationTask notificationTask:notifications) {
                if (notificationTask != null) {
                    telegramBot.execute(new SendMessage(notificationTask.getChatId(), "Не забудь о " + notificationTask.getText()));
                }
            }
        } catch (RuntimeException e) {
            throw new RuntimeException();
        }
    }
}

