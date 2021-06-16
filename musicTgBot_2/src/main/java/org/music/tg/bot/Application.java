package org.music.tg.bot;

import org.music.tg.bot.telegram.MusicBot;

public class Application {

    public static void main(String[] args) {
        MusicBot bot = new MusicBot("1893768102:AAGLJXgMylzLf4yuRTDrFWIf0tMYsfN_8vo");
        bot.startBot();
    }
}
