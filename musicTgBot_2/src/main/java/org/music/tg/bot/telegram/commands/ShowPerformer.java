package org.music.tg.bot.telegram.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import org.music.tg.bot.database.DatabaseManager;
import org.music.tg.bot.telegram.core.CommandButtonProcessor;

import java.util.logging.Logger;

import static org.music.tg.bot.telegram.core.Utils.chatId;
import static org.music.tg.bot.telegram.core.Utils.compositionsToMediaAlbum;

public class ShowPerformer implements CommandButtonProcessor {
    private static final Logger logger = Logger.getLogger(ShowPerformer.class.getName());

    public static final String showPerformerCommand = "performer";

    private final DatabaseManager manager;

    public ShowPerformer(DatabaseManager manager) {
        this.manager = manager;
    }

    @Override
    public String description() {
        return "Показати виконавця";
    }

    @Override
    public String callbackData() {
        return showPerformerCommand;
    }

    @Override
    public void processCommand(TelegramBot bot, Update update) {
    }

    @Override
    public void processButton(TelegramBot bot, Update update) {
        String[] splitData = update.callbackQuery().data().split(":", 2);
        if(splitData.length == 2) {
            listCompositionsOfPerformer(bot, chatId(update), Long.valueOf(splitData[1]));
        } else {
            logger.warning("Cant process performer button data=" + update.callbackQuery().data());
        }
    }

    private void listCompositionsOfPerformer(TelegramBot bot, Long chatId, Long performerId) {
        var compositions = manager.listCompositionsByPerformer(chatId, performerId);
        bot.execute(compositionsToMediaAlbum(chatId, compositions, "", "Немає збережених композицій цього виконавця"));
        Menu.renderButton(bot, chatId);
    }

}
