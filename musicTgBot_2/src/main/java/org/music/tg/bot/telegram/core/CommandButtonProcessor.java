package org.music.tg.bot.telegram.core;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

/**
 * Command with button processor
 */
public interface CommandButtonProcessor {
    String description();
    String callbackData();

    // process update on command
    void processCommand(TelegramBot bot, Update update);
    // process update on button press
    void processButton(TelegramBot bot, Update update);
}
