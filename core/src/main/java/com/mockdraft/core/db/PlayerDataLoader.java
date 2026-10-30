package com.mockdraft.core.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mockdraft.core.model.Player;
import com.mockdraft.core.model.Position;

/**
 * Parses the scraper output ({@code WebScraping/PlayerData.txt}, bundled into the core jar).
 * Each line is {@code <name...> <POS> <projected points> <rank>}; names may contain spaces.
 */
public final class PlayerDataLoader {

    public static final String RESOURCE = "/com/mockdraft/core/data/PlayerData.txt";

    private PlayerDataLoader() {}

    public static List<Player> loadBundled() {
        try (InputStream in = PlayerDataLoader.class.getResourceAsStream(RESOURCE)) {
            if (in == null) {
                throw new IllegalStateException("Bundled player data not found at " + RESOURCE);
            }
            return parse(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static List<Player> parse(InputStream in) throws IOException {
        List<Player> players = new ArrayList<>();
        Set<Integer> ranks = new HashSet<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        String line;
        int lineNo = 0;
        while ((line = reader.readLine()) != null) {
            lineNo++;
            line = line.strip();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            String[] parts = line.split("\\s+");
            if (parts.length < 4) {
                throw new IllegalArgumentException("Malformed player line " + lineNo + ": " + line);
            }
            int rank = Integer.parseInt(parts[parts.length - 1]);
            double points = Double.parseDouble(parts[parts.length - 2]);
            Position position = Position.parse(parts[parts.length - 3]);
            String name = String.join(" ", java.util.Arrays.copyOf(parts, parts.length - 3));
            if (!ranks.add(rank)) {
                throw new IllegalArgumentException("Duplicate player rank " + rank + " on line " + lineNo);
            }
            players.add(new Player(rank, name, position, points));
        }
        return players;
    }
}
