package org.music.tg.bot.telegram.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.music.tg.bot.telegram.core.CommandButtonProcessor;

import java.util.Arrays;

import static org.music.tg.bot.telegram.core.Utils.chatId;
import static org.music.tg.bot.telegram.core.Utils.formatButtonName;

public class Menu implements CommandButtonProcessor {
    private final static String description = "Повернутися до меню";
    private final static String callbackData = "menu";
    private String messageText;
    private InlineKeyboardMarkup markup;

    @Override
    public String description() {
        return description;
    }

    @Override
    public String callbackData() {
        return callbackData;
    }


    @Override
    public void processCommand(TelegramBot bot, Update update) {
        SendMessage message = new SendMessage(chatId(update), messageText != null ? messageText : "Виберіть дію");
        bot.execute(markup == null ? message : message.replyMarkup(markup));
    }

    @Override
    public void processButton(TelegramBot bot, Update update) {
        SendMessage message = new SendMessage(chatId(update), messageText != null ? messageText : "Виберіть дію");
        bot.execute(markup == null ? message : message.replyMarkup(markup));
    }

    public void setSubmenu(CommandButtonProcessor... submenu) {
        markup = new InlineKeyboardMarkup();
        Arrays.stream(submenu)
                .map(p -> new InlineKeyboardButton(formatButtonName(p.description())).callbackData(p.callbackData()))
                .forEach(markup::addRow);
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public static void renderButton(TelegramBot bot, Long chatId) {
        bot.execute(new SendMessage(chatId, "Повернутися до меню")
                .replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton(description).callbackData(callbackData))));
    }
}
