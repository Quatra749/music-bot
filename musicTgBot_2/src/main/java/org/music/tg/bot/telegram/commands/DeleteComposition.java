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

public class DeleteComposition implements CommandButtonProcessor {
    private static final Logger logger = Logger.getLogger(DeleteComposition.class.getName());

    public static final String deleteCompositionCommand = "delete_composition";
    private final DatabaseManager manager;

    public DeleteComposition(DatabaseManager manager) {
        this.manager = manager;
    }

    @Override
    public String description() {
        return deleteCompositionCommand;
    }

    @Override
    public String callbackData() {
        return deleteCompositionCommand;
    }

    @Override
    public void processCommand(TelegramBot bot, Update update) {
    }

    @Override
    public void processButton(TelegramBot bot, Update update) {
        String data = update.callbackQuery().data();
        String[] splitData = data.split(":", 2);

        Long chatId = chatId(update);
        if (splitData.length == 1) {
            // list compositions for deletion
            List<Composition> compositions = manager.listCompositions(chatId);
            var markup = new InlineKeyboardMarkup();
            compositions.stream().map(c -> new InlineKeyboardButton("Видалити " + c.getPerformer().getName() + " - " + c.getName())
                    .callbackData(data + ":" + c.getId())
            ).forEach(markup::addRow);
            bot.execute(new SendMessage(chatId, "Виберіть композицію для видалення").replyMarkup(markup));
        } else if (splitData.length == 2) {
            // delete composition
            delete(bot, chatId, Long.valueOf(splitData[1]));
        } else {
            logger.warning("Cant process delete composition button data=" + data);
        }
    }

    public void delete(TelegramBot bot, Long chatId, Long compositionId) {
        manager.removeComposition(compositionId);
        Menu.renderButton(bot, chatId);
    }
}
