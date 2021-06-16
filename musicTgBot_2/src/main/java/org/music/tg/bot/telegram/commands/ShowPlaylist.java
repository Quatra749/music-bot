package org.music.tg.bot.telegram.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.music.tg.bot.database.DatabaseManager;
import org.music.tg.bot.telegram.core.CommandButtonProcessor;

import java.util.logging.Logger;

import static org.music.tg.bot.telegram.commands.DeleteCompositionFromPlaylist.deleteCompositionFromPlaylistCommand;
import static org.music.tg.bot.telegram.commands.DeletePlaylist.deletePlaylistCommand;
import static org.music.tg.bot.telegram.core.Utils.chatId;
import static org.music.tg.bot.telegram.core.Utils.compositionsToMediaAlbum;

public class ShowPlaylist implements CommandButtonProcessor {
    public static final String showPlaylistCommand = "playlist";
    private static final Logger logger = Logger.getLogger(ShowPlaylist.class.getName());
    private final DatabaseManager manager;

    public ShowPlaylist(DatabaseManager manager) {
        this.manager = manager;
    }

    @Override
    public String description() {
        return "Показати плейлист";
    }

    @Override
    public String callbackData() {
        return showPlaylistCommand;
    }

    @Override
    public void processCommand(TelegramBot bot, Update update) {
    }

    @Override
    public void processButton(TelegramBot bot, Update update) {
        String[] splitData = update.callbackQuery().data().split(":", 2);
        if (splitData.length == 2) {
            listCompositionsByPlaylist(bot, chatId(update), Long.valueOf(splitData[1]));
        } else {
            logger.warning("Cant process playlist button data=" + update.callbackQuery().data());
        }
    }

    private void listCompositionsByPlaylist(TelegramBot bot, Long chatId, Long playlistId) {
        var compositions = manager.listCompositionsByPlaylist(chatId, playlistId);
        bot.execute(compositionsToMediaAlbum(chatId, compositions, "", "Цей плейлист порожній"));
        var markup = new InlineKeyboardMarkup();
        markup.addRow(new InlineKeyboardButton("Видалити композицію").callbackData(deleteCompositionFromPlaylistCommand + ":" + playlistId));
        markup.addRow(new InlineKeyboardButton("Видалити плейлист").callbackData(deletePlaylistCommand + ":" + playlistId));
        bot.execute(new SendMessage(chatId, "Змінити плейлист").replyMarkup(markup));
        Menu.renderButton(bot, chatId);
    }
}
