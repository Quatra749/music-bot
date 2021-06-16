package org.music.tg.bot.telegram.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import org.music.tg.bot.telegram.core.CommandButtonProcessor;

public class Start implements CommandButtonProcessor {
    private final Menu menu;

    public Start(Menu menu) {
        this.menu = menu;
    }

    @Override
    public String description() {
        return "Запуск боту";
    }

    @Override
    public String callbackData() {
        return "start";
    }

    @Override
    public void processCommand(TelegramBot bot, Update update) {
        menu.processCommand(bot, update);
    }

    @Override
    public void processButton(TelegramBot bot, Update update) {
    }
}
