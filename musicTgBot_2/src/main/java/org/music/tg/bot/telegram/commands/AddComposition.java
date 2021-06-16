package org.music.tg.bot.telegram.commands;

import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Audio;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.music.tg.bot.database.dao.CompositionDao;
import org.music.tg.bot.database.entity.Composition;
import org.music.tg.bot.database.entity.Performer;
import org.music.tg.bot.database.DatabaseManager;
import org.music.tg.bot.telegram.core.CommandButtonProcessor;

import java.util.List;

import static org.music.tg.bot.database.DaoUtils.getCompositionDao;
import static org.music.tg.bot.telegram.commands.AddCompositionSelectGenre.addCompositionGenreCommand;
import static org.music.tg.bot.telegram.core.Utils.chatId;
import static org.music.tg.bot.telegram.core.Utils.formatButtonName;

public class AddComposition implements CommandButtonProcessor {

    private static final Logger logger = LoggerFactory.getLogger(AddComposition.class.getName());

    private final DatabaseManager manager;

    public AddComposition(DatabaseManager manager) {
        this.manager = manager;
    }

    @Override
    public String description() {
        return "Додати композицію";
    }

    @Override
    public String callbackData() {
        return "add_composition";
    }

    @Override
    public void processCommand(TelegramBot bot, Update update) {
        bot.execute(new SendMessage(chatId(update), "Надішліть файл"));
    }

    @Override
    public void processButton(TelegramBot bot, Update update) {
        bot.execute(new SendMessage(chatId(update), "Надішліть файл"));
    }

    public void processAudio(TelegramBot bot, Update update) {
        Audio audio = update.message().audio();
        String title = audio.title();
        String performerName = audio.performer();
        Long chatId = chatId(update);
        // find similar compositions in DB
        try (var connectionSource = manager.getConnection()) {
            CompositionDao compositionDao = getCompositionDao(connectionSource);
            List<Composition> compositions = compositionDao.findByName(chatId, title);
            List<Composition> similarCompositions = compositions.stream()
                    .filter(c -> c.getPerformer() != null && c.getPerformer().getName().equals(performerName)).toList();
            Composition composition;
            if (similarCompositions.isEmpty()) {
                composition = new Composition();
                composition.setName(title);
                Performer performer = manager.findOrCreatePerformer(connectionSource, chatId, performerName);
                composition.setPerformer(performer);
                composition.setFileId(audio.fileId());
                composition.setChatId(chatId);
                compositionDao.create(composition);
                bot.execute(new SendMessage(chatId
                        , String.format("композиція %s - %s додана", composition.getPerformer().getName(), composition.getName())));

                // genres menu for composition
                var genres = manager.listGenres();
                var markup = new InlineKeyboardMarkup();
                var cmdPrefix = addCompositionGenreCommand + ":" + composition.getId() + ":";
                genres.stream()
                        .map(p -> new InlineKeyboardButton(formatButtonName(p.getName())).callbackData(cmdPrefix + p.getId()))
                        .forEach(markup::addRow);
                bot.execute(new SendMessage(chatId
                        , String.format("Вкажіть жанр композиціі %s - %s", composition.getPerformer().getName(), composition.getName()))
                        .replyMarkup(markup));
            } else {
                composition = compositions.get(0);
                bot.execute(new SendMessage(chatId
                        , String.format("Композиція %s - %s вже була додана", composition.getPerformer().getName(), composition.getName())));

            }
        } catch (Exception e) {
            logger.error(e, "Error adding audio");
        }
    }
}
