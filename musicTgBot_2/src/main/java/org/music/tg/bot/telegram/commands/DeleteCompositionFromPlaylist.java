package org.music.tg.bot.telegram.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.music.tg.bot.database.DatabaseManager;
import org.music.tg.bot.database.entity.Composition;
import org.music.tg.bot.telegram.core.CommandButtonProcessor;

import java.util.List;
import java.util.logging.Logger;

import static org.music.tg.bot.telegram.core.Utils.chatId;

public class DeleteCompositionFromPlaylist implements CommandButtonProcessor {
    private static final Logger logger = Logger.getLogger(DeleteCompositionFromPlaylist.class.getName());

    public static final String deleteCompositionFromPlaylistCommand = "delete_composition_from_playlist";
    private final DatabaseManager manager;

    public DeleteCompositionFromPlaylist(DatabaseManager manager) {
        this.manager = manager;
    }

    @Override
    public String description() {
        return deleteCompositionFromPlaylistCommand;
    }

    @Override
    public String callbackData() {
        return deleteCompositionFromPlaylistCommand;
    }

    @Override
    public void processCommand(TelegramBot bot, Update update) {
    }

    @Override
    public void processButton(TelegramBot bot, Update update) {
        String data = update.callbackQuery().data();
        String[] splitData = data.split(":", 3);

        Long chatId = chatId(update);
        if (splitData.length == 2) {
            // show audios for deletion
            List<Composition> compositions = manager.listCompositionsByPlaylist(chatId, Long.valueOf(splitData[1]));
            var markup = new InlineKeyboardMarkup();
            compositions.stream().map(c -> new InlineKeyboardButton("Видалити " + c.getPerformer().getName() + " - " + c.getName())
                    .callbackData(data + ":" + c.getId())
            ).forEach(markup::addRow);
            bot.execute(new SendMessage(chatId, "Виберіть композицію для видалення").replyMarkup(markup));
        } else if (splitData.length == 3) {
            // delete audio
            delete(bot, chatId, Long.valueOf(splitData[1]), Long.valueOf(splitData[2]));
        } else {
            logger.warning("Cant process playlist button data=" + data);
        }
    }

    public void delete(TelegramBot bot, Long chatId, Long playlistId, Long compositionId) {
        manager.removeCompositionFromPlaylist(chatId, compositionId, playlistId);
        Menu.renderButton(bot, chatId);
    }
}
