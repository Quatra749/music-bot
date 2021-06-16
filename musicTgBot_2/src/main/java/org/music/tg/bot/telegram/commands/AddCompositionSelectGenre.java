package org.music.tg.bot.telegram.commands;

import com.j256.ormlite.stmt.query.In;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.music.tg.bot.database.DatabaseManager;
import org.music.tg.bot.database.entity.Playlist;
import org.music.tg.bot.telegram.core.CommandButtonProcessor;

import java.util.List;
import java.util.logging.Logger;

import static org.music.tg.bot.telegram.commands.AddCompositionNewPlaylist.addCompositionNewPlaylistCommand;
import static org.music.tg.bot.telegram.commands.AddCompositionSelectPlaylist.addCompositionSelectPlaylistCommand;
import static org.music.tg.bot.telegram.core.Utils.chatId;
import static org.music.tg.bot.telegram.core.Utils.formatButtonName;

public class AddCompositionSelectGenre implements CommandButtonProcessor {
    private static final Logger logger = Logger.getLogger(AddCompositionSelectGenre.class.getName());

    public static final String addCompositionGenreCommand = "add_composition_genre";
    private final DatabaseManager manager;

    public AddCompositionSelectGenre(DatabaseManager manager) {
        this.manager = manager;
    }

    @Override
    public String description() {
        return addCompositionGenreCommand;
    }

    @Override
    public String callbackData() {
        return addCompositionGenreCommand;
    }

    @Override
    public void processCommand(TelegramBot bot, Update update) {
    }

    @Override
    public void processButton(TelegramBot bot, Update update) {
        String[] splitData = update.callbackQuery().data().split(":", 3);
        if (splitData.length == 3) {
            addCompositionGenre(bot, chatId(update), Long.valueOf(splitData[1]), Long.valueOf(splitData[2]));
        } else {
            logger.warning("Cant process genre button data=" + update.callbackQuery().data());
        }
    }

    private void addCompositionGenre(TelegramBot bot, Long chatId, Long compositionId, Long genreId) {
        manager.setCompositionGenre(compositionId, genreId);

        // menu for select playlist
        List<Playlist> playlists = manager.listPlaylists(chatId);
        if (!playlists.isEmpty()) {
            var markup = new InlineKeyboardMarkup();
            var setCommandPrefix = addCompositionSelectPlaylistCommand + ":" + compositionId + ":";
            playlists.stream()
                    .map(p -> new InlineKeyboardButton(formatButtonName(p.getName())).callbackData(setCommandPrefix + p.getId()))
                    .forEach(markup::addRow);
            bot.execute(new SendMessage(chatId, "Обрати плейлист").replyMarkup(markup));
        }
        bot.execute(new SendMessage(chatId, "Або додати плейлист")
                .replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton("Додати плейлист").callbackData(addCompositionNewPlaylistCommand + ":" + compositionId))));
    }
}
