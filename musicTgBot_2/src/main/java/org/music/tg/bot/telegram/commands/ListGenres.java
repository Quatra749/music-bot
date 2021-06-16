package org.music.tg.bot.telegram.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.music.tg.bot.database.DatabaseManager;
import org.music.tg.bot.telegram.core.CommandButtonProcessor;

import static org.music.tg.bot.telegram.commands.ShowGenre.showGenreCommand;
import static org.music.tg.bot.telegram.core.Utils.chatId;
import static org.music.tg.bot.telegram.core.Utils.formatButtonName;

public class ListGenres implements CommandButtonProcessor {
    private final DatabaseManager manager;

    public ListGenres(DatabaseManager manager) {
        this.manager = manager;
    }

    @Override
    public String description() {
        return "Відсортувати за жанром";
    }

    @Override
    public String callbackData() {
        return "list_genres";
    }

    @Override
    public void processCommand(TelegramBot bot, Update update) {
        listGenres(chatId(update), bot);
    }

    @Override
    public void processButton(TelegramBot bot, Update update) {
        listGenres(chatId(update), bot);
    }

    public void listGenres(Long chatId, TelegramBot bot) {
        var genres = manager.listGenres();
        var markup = new InlineKeyboardMarkup();
        genres.stream()
                .map(p -> new InlineKeyboardButton(formatButtonName(p.getName())).callbackData(showGenreCommand + ":" + p.getId()))
                .forEach(markup::addRow);
        bot.execute(new SendMessage(chatId, "Виберіть жанр").replyMarkup(markup));
    }
}
