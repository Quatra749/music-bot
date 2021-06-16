package org.music.tg.bot.telegram.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import org.music.tg.bot.database.DatabaseManager;
import org.music.tg.bot.telegram.core.CommandButtonProcessor;

import java.util.logging.Logger;

import static org.music.tg.bot.telegram.core.Utils.chatId;
import static org.music.tg.bot.telegram.core.Utils.compositionsToMediaAlbum;

public class ShowGenre implements CommandButtonProcessor {
    public static final String showGenreCommand = "genre";
    private static final Logger logger = Logger.getLogger(ShowGenre.class.getName());
    private final DatabaseManager manager;

    public ShowGenre(DatabaseManager manager) {
        this.manager = manager;
    }

    @Override
    public String description() {
        return "Показати жанр";
    }

    @Override
    public String callbackData() {
        return showGenreCommand;
    }

    @Override
    public void processCommand(TelegramBot bot, Update update) {
    }

    @Override
    public void processButton(TelegramBot bot, Update update) {
        String[] splitData = update.callbackQuery().data().split(":", 2);
        if (splitData.length == 2) {
            listCompositionsByGenre(bot, chatId(update), Long.valueOf(splitData[1]));
        } else {
            logger.warning("Cant process genre button data=" + update.callbackQuery().data());
        }
    }

    private void listCompositionsByGenre(TelegramBot bot, Long chatId, Long genreId) {
        var compositions = manager.listCompositionsByGenre(chatId, genreId);
        bot.execute(compositionsToMediaAlbum(chatId, compositions, "", "Немає збережених композицій цього жанра"));
        Menu.renderButton(bot, chatId);
    }
}
