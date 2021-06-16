package org.music.tg.bot.telegram;

import com.google.gson.Gson;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SetMyCommands;
import org.music.tg.bot.database.DatabaseManager;
import org.music.tg.bot.telegram.commands.*;
import org.music.tg.bot.telegram.core.CommandButtonRegistry;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static org.music.tg.bot.telegram.core.Utils.chatId;

public class MusicBot {
    private final static Logger logger = Logger.getLogger(MusicBot.class.getName());
    private final String token;

    private final DatabaseManager manager;
    private final CommandButtonRegistry commandButtonRegistry;

    private AddComposition addComposition;
    private AddCompositionNewPlaylist addCompositionNewPlaylist;

    public MusicBot(String token) {
        this.token = token;
        this.commandButtonRegistry = new CommandButtonRegistry();
        this.manager = new DatabaseManager("jdbc:h2:file:./music");
        manager.initDatabase();
        initProcessors();
    }

    private void initProcessors() {
        // init button processors and its submenus
        var menu = new Menu();
        var start = new Start(menu);
        addComposition = new AddComposition(manager);
        var addCompositionGenre = new AddCompositionSelectGenre(manager);
        addCompositionNewPlaylist = new AddCompositionNewPlaylist(manager);
        var addCompositionSelectPlaylist = new AddCompositionSelectPlaylist(manager);
        var deleteCompositionFromPlaylist = new DeleteCompositionFromPlaylist(manager);
        var deletePlaylist = new DeletePlaylist(manager);
        var deleteComposition = new DeleteComposition(manager);
        var listGenres = new ListGenres(manager);
        var listPerformers = new ListPerformers(manager);
        var listCompositions = new ListCompositions(manager);
        var listPlaylists = new ListPlaylists(manager);
        var showPerformer = new ShowPerformer(manager);
        var showGenre = new ShowGenre(manager);
        var showPlaylist = new ShowPlaylist(manager);

        menu.setSubmenu(listGenres, listPerformers, listCompositions, listPlaylists, addComposition);

        // register command processors
        Arrays.asList(menu, start, addComposition, listGenres, listPerformers, listCompositions, listPlaylists
                , showPerformer, showGenre, showPlaylist, addCompositionGenre, addCompositionNewPlaylist, addCompositionSelectPlaylist
                , deleteCompositionFromPlaylist, deletePlaylist, deleteComposition
        ).forEach(commandButtonRegistry::registerCommand);
    }

    public void startBot() {
        // Create your bot passing the token received from @BotFather
        TelegramBot bot = new TelegramBot(token);
        // setting available commands for autocompletion
        List<BotCommand> commandsList = commandButtonRegistry.getCommands().stream()
                .map(c -> new BotCommand(c, c)).toList();
        bot.execute(new SetMyCommands(commandsList.toArray(new BotCommand[0])));
        // Register update listener
        bot.setUpdatesListener(updates -> {
            updates.forEach(update -> {
                boolean processed = commandButtonRegistry.processUpdate(bot, update);
                if (!processed) {
                    var message = update.message();
                    if (message != null && message.audio() != null) {
                        // add uploaded audio
                        addComposition.processAudio(bot, update);
                    } else if (message != null && message.text() != null) {
                        // create new playlist for composition
                        addCompositionNewPlaylist.addCompositionNewPlaylist(bot, chatId(update), message.text());
                    } else {
                        logger.warning("Cant process message " + new Gson().toJson(update));
                    }
                }
            });

            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

}
