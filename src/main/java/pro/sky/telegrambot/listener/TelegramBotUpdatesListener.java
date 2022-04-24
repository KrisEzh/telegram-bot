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

    public TelegramBotUpdatesListener(NotificationTaskRepository notificationTaskRepository) {
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
                telegramBot.execute(new SendMessage(chatId, "Приветик!Пришли мне сообщение вида 01.01.2022 20:00 Сделать домашнюю работу, а я напомню тебе о нем в нужное время))"));
            } else {
                String message = update.message().text();
                Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
                Matcher matcher = pattern.matcher(message);
                if (matcher.matches()) {
                    String time = matcher.group(1);
                    String text = matcher.group(3);
                    LocalDateTime date2 = LocalDateTime.parse(time, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                    NotificationTask object = new NotificationTask(date2, text);
                    notificationTaskRepository.save(object);
                    long chatId = update.message().chat().id();
                    telegramBot.execute(new SendMessage(chatId, "Я напомню тебе"));
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void findMessage() {
        LocalDateTime myTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<NotificationTask> myList = notificationTaskRepository.findByLocalDateTimeEquals(myTime);
            for(NotificationTask notificationTask: myList) {
                if(notificationTask.getLocalDateTime().equals(myTime) )
                telegramBot.execute(new SendMessage(notificationTask.getId(), "Не забудь сделать то, что хотел"));
            }
    }



}

