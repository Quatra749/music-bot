package org.music.tg.bot.telegram.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.music.tg.bot.database.DatabaseManager;
import org.music.tg.bot.database.entity.Playlist;
import org.music.tg.bot.telegram.core.CommandButtonProcessor;

import java.util.List;

import static org.music.tg.bot.telegram.commands.ShowPlaylist.showPlaylistCommand;
import static org.music.tg.bot.telegram.core.Utils.chatId;
import static org.music.tg.bot.telegram.core.Utils.formatButtonName;

public class ListPlaylists implements CommandButtonProcessor {
    private final DatabaseManager manager;

    public ListPlaylists(DatabaseManager manager) {
        this.manager = manager;
    }

    @Override
    public String description() {
        return "Список плейлистів";
    }

    @Override
    public String callbackData() {
        return "list_playlists";
    }

    @Override
    public void processCommand(TelegramBot bot, Update update) {
        listPlaylists(bot, chatId(update));
    }

    @Override
    public void processButton(TelegramBot bot, Update update) {
        listPlaylists(bot, chatId(update));
    }
    public void listPlaylists(TelegramBot bot, Long chatId) {
        List<Playlist> performers = manager.listPlaylists(chatId);
        var markup = new InlineKeyboardMarkup();
        performers.stream()
                .map(p -> new InlineKeyboardButton(formatButtonName(p.getName())).callbackData(showPlaylistCommand + ":" + p.getId()))
                .forEach(markup::addRow);
        bot.execute(new SendMessage(chatId, "Виберіть плейлист").replyMarkup(markup));
    }

}
