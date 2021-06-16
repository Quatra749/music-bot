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

import static org.music.tg.bot.telegram.commands.DeleteComposition.deleteCompositionCommand;
import static org.music.tg.bot.telegram.core.Utils.chatId;
import static org.music.tg.bot.telegram.core.Utils.compositionsToMediaAlbum;

public class ListCompositions implements CommandButtonProcessor {
    private final DatabaseManager manager;

    public ListCompositions(DatabaseManager manager) {
        this.manager = manager;
    }

    @Override
    public String description() {
        return "Показати всі композиції";
    }

    @Override
    public String callbackData() {
        return "list_compositions";
    }

    @Override
    public void processCommand(TelegramBot bot, Update update) {
        list(bot, chatId(update));
    }

    @Override
    public void processButton(TelegramBot bot, Update update) {
        list(bot, chatId(update));
    }

    public void list(TelegramBot bot, Long chatId) {
        List<Composition> compositions = manager.listCompositions(chatId);
        bot.execute(compositionsToMediaAlbum(chatId, compositions, description(), "Немає збережених композицій"));
        if(!compositions.isEmpty()) {
            var markup = new InlineKeyboardMarkup();
            markup.addRow(new InlineKeyboardButton("Вибрати композиції для видалення").callbackData(deleteCompositionCommand));
            bot.execute(new SendMessage(chatId, "Ви можете видалити композиції").replyMarkup(markup));
        }
        Menu.renderButton(bot, chatId);
    }
}
