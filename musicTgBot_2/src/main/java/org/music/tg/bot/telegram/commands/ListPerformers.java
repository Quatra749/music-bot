package org.music.tg.bot.telegram.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.music.tg.bot.database.DatabaseManager;
import org.music.tg.bot.database.entity.Performer;
import org.music.tg.bot.telegram.core.CommandButtonProcessor;

import java.util.List;

import static org.music.tg.bot.telegram.commands.ShowPerformer.showPerformerCommand;
import static org.music.tg.bot.telegram.core.Utils.chatId;
import static org.music.tg.bot.telegram.core.Utils.formatButtonName;

public class ListPerformers implements CommandButtonProcessor {
    private final DatabaseManager manager;

    public ListPerformers(DatabaseManager manager) {
        this.manager = manager;
    }

    @Override
    public String description() {
        return "Виконавці";
    }

    @Override
    public String callbackData() {
        return "list_performers";
    }

    @Override
    public void processCommand(TelegramBot bot, Update update) {
        listPerformers(bot, chatId(update));
    }

    @Override
    public void processButton(TelegramBot bot, Update update) {
        listPerformers(bot, chatId(update));
    }

    public void listPerformers(TelegramBot bot, Long chatId) {
        List<Performer> performers = manager.listPerformers(chatId);
        var markup = new InlineKeyboardMarkup();
        performers.stream()
                .map(p -> new InlineKeyboardButton(formatButtonName(p.getName())).callbackData(showPerformerCommand + ":" + p.getId()))
                .forEach(markup::addRow);
        bot.execute(new SendMessage(chatId, "Виберіть виконавця").replyMarkup(markup));
    }
}
