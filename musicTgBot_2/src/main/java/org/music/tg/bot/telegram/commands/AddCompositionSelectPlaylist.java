package org.music.tg.bot.telegram.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.music.tg.bot.database.DatabaseManager;
import org.music.tg.bot.telegram.core.CommandButtonProcessor;

import java.util.logging.Logger;

import static org.music.tg.bot.telegram.core.Utils.chatId;

public class AddCompositionSelectPlaylist implements CommandButtonProcessor {
    private static final Logger logger = Logger.getLogger(AddCompositionSelectPlaylist.class.getName());

    public static final String addCompositionSelectPlaylistCommand = "add_composition_select_playlist";
    private final DatabaseManager manager;

    public AddCompositionSelectPlaylist(DatabaseManager manager) {
        this.manager = manager;
    }

    @Override
    public String description() {
        return addCompositionSelectPlaylistCommand;
    }

    @Override
    public String callbackData() {
        return addCompositionSelectPlaylistCommand;
    }

    @Override
    public void processCommand(TelegramBot bot, Update update) {
    }

    @Override
    public void processButton(TelegramBot bot, Update update) {
        String[] splitData = update.callbackQuery().data().split(":", 3);
        if (splitData.length == 3) {
            addCompositionPlaylist(bot, chatId(update), Long.valueOf(splitData[1]), Long.valueOf(splitData[2]));
        } else {
            logger.warning("Cant process playlist button data=" + update.callbackQuery().data());
        }
    }

    private void addCompositionPlaylist(TelegramBot bot, Long chatId, Long compositionId, Long playlistId) {
        manager.addCompositionPlaylist(chatId, compositionId, playlistId);
        Menu.renderButton(bot, chatId);
    }
}
