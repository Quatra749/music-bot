package org.music.tg.bot.telegram.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.music.tg.bot.database.DatabaseManager;
import org.music.tg.bot.telegram.core.CommandButtonProcessor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static org.music.tg.bot.telegram.core.Utils.chatId;

public class AddCompositionNewPlaylist implements CommandButtonProcessor {
    private static final Logger logger = Logger.getLogger(AddCompositionNewPlaylist.class.getName());

    public static final String addCompositionNewPlaylistCommand = "add_composition_new_playlist";
    private final DatabaseManager manager;

    private static final ConcurrentHashMap<Long, Long> chatIdToComposition = new ConcurrentHashMap<>();

    public AddCompositionNewPlaylist(DatabaseManager manager) {
        this.manager = manager;
    }

    @Override
    public String description() {
        return addCompositionNewPlaylistCommand;
    }

    @Override
    public String callbackData() {
        return addCompositionNewPlaylistCommand;
    }

    @Override
    public void processCommand(TelegramBot bot, Update update) {
    }

    @Override
    public void processButton(TelegramBot bot, Update update) {
        String[] splitData = update.callbackQuery().data().split(":", 2);
        if (splitData.length == 2) {
            chatIdToComposition.put(chatId(update), Long.valueOf(splitData[1]));
            bot.execute(new SendMessage(chatId(update), "Введіть назву плейлиста"));
        } else {
            logger.warning("Cant process playlist button data=" + update.callbackQuery().data());
        }
    }

    public void addCompositionNewPlaylist(TelegramBot bot, Long chatId, String playlistName) {
        Long compositionId = chatIdToComposition.remove(chatId);
        if (compositionId != null) {
            manager.addCompositionToNewPlaylist(chatId, compositionId, playlistName);
        }
        Menu.renderButton(bot, chatId);
    }
}
