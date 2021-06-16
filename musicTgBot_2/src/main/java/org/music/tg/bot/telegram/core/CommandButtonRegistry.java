package org.music.tg.bot.telegram.core;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import java.util.*;
import java.util.logging.Logger;

public class CommandButtonRegistry {
    private final static Logger logger = Logger.getLogger(CommandButtonRegistry.class.getName());

    private LinkedHashMap<String, CommandButtonProcessor> buttons = new LinkedHashMap<>();

    public void registerCommand(CommandButtonProcessor commandButton) {
        if (buttons.containsKey(commandButton.callbackData())) {
            logger.warning("Already has button with name " + commandButton.callbackData() + ". Ignore " + commandButton.getClass());
        } else {
            buttons.put(commandButton.callbackData(), commandButton);
        }
    }

    public Set<String> getCommands() {
        return buttons.keySet();
    }

    public boolean processUpdate(TelegramBot bot, Update update) {
        // process callbackQuery (result of button push)
        if (update.callbackQuery() != null) {
            String buttonName = update.callbackQuery().data().split(":", 2)[0];
            var processor = Optional.ofNullable(buttons.get(buttonName));
            processor.ifPresent(b -> b.processButton(bot, update));
            if(processor.isEmpty()) {
                logger.warning("Cant find button processor for callback " + buttonName);
            }
            return processor.isPresent();
        }

        // process command (result of /command)
        var message = update.message();
        if (message != null && message.text() != null && message.text().startsWith("/")) {
            String commandName = message.text().substring(1); // drop first '/'
            var processor = Optional.ofNullable(buttons.get(commandName));
            processor.ifPresent(c -> c.processCommand(bot, update));
            if(processor.isEmpty()) {
                logger.warning("Cant find command processor for callback " + commandName);
            }
            return processor.isPresent();
        }

        return false;
    }
}
