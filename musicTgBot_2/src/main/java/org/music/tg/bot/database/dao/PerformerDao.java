package org.music.tg.bot.database.dao;

import com.j256.ormlite.dao.Dao;
import org.music.tg.bot.database.entity.Performer;

import java.sql.SQLException;
import java.util.List;

public interface PerformerDao extends Dao<Performer, Long> {
    List<Performer> findByName(Long chatId, String name) throws SQLException;
}
