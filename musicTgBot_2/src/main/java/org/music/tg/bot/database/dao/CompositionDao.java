package org.music.tg.bot.database.dao;

import com.j256.ormlite.dao.Dao;
import org.music.tg.bot.database.entity.Composition;

import java.sql.SQLException;
import java.util.List;

public interface CompositionDao extends Dao<Composition, Long> {
    List<Composition> findByName(Long chatId, String name) throws SQLException;
    List<Composition> findByPerformer(Long chatId, Long performerId) throws SQLException;
    List<Composition> findByGenre(Long chatId, Long genreId) throws SQLException;
}
