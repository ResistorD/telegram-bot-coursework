package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class NotificationScheduler {
    private final NotificationTaskRepository repository;
    private final TelegramBot bot;

    public NotificationScheduler(NotificationTaskRepository repository, TelegramBot bot) {
        this.repository = repository;
        this.bot = bot;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void sendDueNotifications() {
        LocalDateTime nowTruncated = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<NotificationTask> due = repository.findByNotificationTime(nowTruncated);
        for (NotificationTask task : due) {
            bot.execute(new SendMessage(task.getChatId(), task.getNotificationText()));
        }
    }
}
