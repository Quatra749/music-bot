package org.music.tg.bot.telegram.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import org.music.tg.bot.database.DatabaseManager;
import org.music.tg.bot.telegram.core.CommandButtonProcessor;

import java.util.logging.Logger;

import static org.music.tg.bot.telegram.core.Utils.chatId;

public class DeletePlaylist implements CommandButtonProcessor {
    private static final Logger logger = Logger.getLogger(DeletePlaylist.class.getName());

    public static final String deletePlaylistCommand = "delete_playlist";
    private final DatabaseManager manager;

    public DeletePlaylist(DatabaseManager manager) {
        this.manager = manager;
    }

    @Override
    public String description() {
        return deletePlaylistCommand;
    }

    @Override
    public String callbackData() {
        return deletePlaylistCommand;
    }

    @Override
    public void processCommand(TelegramBot bot, Update update) {
    }

    @Override
    public void processButton(TelegramBot bot, Update update) {
        String data = update.callbackQuery().data();
        String[] splitData = data.split(":", 2);

        if (splitData.length == 2) {
            // delete playlist
            delete(bot, chatId(update), Long.valueOf(splitData[1]));
        } else {
            logger.warning("Cant process delete playlist button data=" + data);
        }
    }

    public void delete(TelegramBot bot, Long chatId, Long playlistId) {
        manager.removePlaylist(playlistId);
        Menu.renderButton(bot, chatId);
    }
}
