package com.mockdraft.core.repo;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.mockdraft.core.db.Database;
import com.mockdraft.core.model.Player;
import com.mockdraft.core.model.Position;

/** Player rankings are static reference data, so they are loaded once and served from memory. */
public final class PlayerRepository {

    private final Database db;
    private volatile Map<Integer, Player> byRank;

    public PlayerRepository(Database db) {
        this.db = db;
    }

    /** All players sorted by rank. */
    public List<Player> all() {
        return List.copyOf(cache().values());
    }

    public Player get(int rank) {
        Player p = cache().get(rank);
        if (p == null) {
            throw new IllegalArgumentException("Unknown player rank " + rank);
        }
        return p;
    }

    public boolean exists(int rank) {
        return cache().containsKey(rank);
    }

    private Map<Integer, Player> cache() {
        Map<Integer, Player> local = byRank;
        if (local == null) {
            synchronized (this) {
                if (byRank == null) {
                    Map<Integer, Player> loaded = new LinkedHashMap<>();
                    for (Player p : db.query(
                            "SELECT player_rank, name, position, projected_points FROM players ORDER BY player_rank",
                            rs -> new Player(rs.getInt(1), rs.getString(2), Position.parse(rs.getString(3)),
                                    rs.getBigDecimal(4).doubleValue()))) {
                        loaded.put(p.rank(), p);
                    }
                    byRank = Collections.unmodifiableMap(loaded);
                }
                local = byRank;
            }
        }
        return local;
    }
}
