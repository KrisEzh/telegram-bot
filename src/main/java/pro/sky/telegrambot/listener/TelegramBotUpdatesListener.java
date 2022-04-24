package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
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
            if (update.message().text().equals(new String("/start"))) {
                long chatId = update.message().chat().id();
                SendResponse response = telegramBot.execute(new SendMessage(chatId, "Приветик!Пришли мне сообщение вида 01.01.2022 20:00 Сделать домашнюю работу, а я напомню тебе о нем в нужное время))"));
            } else {
                try {
                    String message = update.message().text();
                    Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
                    Matcher matcher = pattern.matcher(message);
                    if (matcher.matches()) {
                        String time = matcher.group(1);
                        String text = matcher.group(3);
                        int chatId = Math.toIntExact(update.message().chat().id());
                        LocalDateTime date2 = LocalDateTime.parse(time, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                        NotificationTask object = new NotificationTask(chatId, date2, text);
                        notificationTaskRepository.save(object);
                        telegramBot.execute(new SendMessage(chatId, "Я напомню тебе"));
                    }
                } catch (DateTimeException e) {
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Scheduled(fixedDelay = 60_000L)
    public void findMessage() {
        try {
            LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            NotificationTask notificationTask = notificationTaskRepository.findByLocalDateTimeEquals(currentTime);
            logger.info("ищу");
            if (notificationTask != null) {
                telegramBot.execute(new SendMessage(notificationTask.getChatId(), "Не забудь сделать то, что хотел"));
            }

        } catch (RuntimeException e) {
        }
    }
}

