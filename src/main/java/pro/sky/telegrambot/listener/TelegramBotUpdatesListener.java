package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {
    private static final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private static final Pattern REMIND_PATTERN =
            Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2})(\\s+)(.+)");
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final TelegramBot telegramBot;
    private final NotificationTaskRepository repository;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationTaskRepository repository) {
        this.telegramBot = telegramBot;
        this.repository = repository;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        for (Update update : updates) {
            try {
                if (update == null || update.message() == null || update.message().text() == null) {
                    continue;
                }
                String text = update.message().text().trim();
                Long chatId = update.message().chat().id();
                logger.info("Incoming message '{}' from chat {}", text, chatId);

                if ("/start".equalsIgnoreCase(text)) {
                    telegramBot.execute(new SendMessage(chatId,
                            "Привет! Я бот-напоминатель. Пиши в формате:\n" +
                            "01.01.2025 20:00 Сделать домашнюю работу\n\n" +
                            "Я пришлю напоминание в указанную минуту."));
                    continue;
                }

                Matcher m = REMIND_PATTERN.matcher(text);
                if (m.matches()) {
                    String dateString = m.group(1);
                    String message = m.group(3);
                    LocalDateTime when = LocalDateTime.parse(dateString, DTF);

                    NotificationTask task = new NotificationTask(chatId, message, when);
                    repository.save(task);

                    telegramBot.execute(new SendMessage(chatId,
                            "Сохранила напоминание на " + when.format(DTF) + ":\n" + message));
                } else {
                    telegramBot.execute(new SendMessage(chatId,
                            "Не распознала формат. Пожалуйста, пиши так:\n" +
                            "01.01.2025 20:00 Текст напоминания"));
                }
            } catch (Exception e) {
                logger.error("Failed to process update", e);
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
