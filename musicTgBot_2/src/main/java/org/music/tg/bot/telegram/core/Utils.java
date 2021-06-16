package org.music.tg.bot.telegram.core;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InputMediaAudio;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMediaGroup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import org.music.tg.bot.database.entity.Composition;

import java.util.List;

public final class Utils {
    private Utils(){}

    // get chatId of Update message
    public static Long chatId(Update update) {
        if (update.callbackQuery() != null) {
            return update.callbackQuery().message().chat().id();
        }
        if (update.message() != null) {
            return update.message().chat().id();
        }

        throw new RuntimeException("unexpected update " + update);
    }

    // crop button name as telegram API requirements
    public static String formatButtonName(String name) {
        return name.substring(0, Math.min(24, name.length()));
    }

    // create media album (aka media group, single message with multiple audios) message from compositions list
    public static BaseRequest<? extends BaseRequest<?,?>, ? extends BaseResponse>
        compositionsToMediaAlbum(Long chatId, List<Composition> compositions, String caption, String altText) {
        InputMediaAudio[] inputMediaAudios = compositions.stream()
                .map(composition -> new InputMediaAudio(composition.getFileId())).toArray(InputMediaAudio[]::new);
        if(inputMediaAudios.length > 0) {
            return new SendMediaGroup(chatId, inputMediaAudios);
        } else {
            // send alt text if list empty
            return new SendMessage(chatId, altText);
        }
    }

}
