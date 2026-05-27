package com.didinele.battleship.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class Move {
    public enum Result { MISS, HIT, SUNK }

    private final UUID id;
    private final UUID gameId;
    private final UUID playerId;
    private final int row;
    private final int col;
    private final Result result;
    private final Instant timestamp;

    public Move(UUID gameId, UUID playerId, int row, int col, Result result) {
        this.id = UUID.randomUUID();
        this.gameId = Objects.requireNonNull(gameId);
        this.playerId = Objects.requireNonNull(playerId);
        this.row = row;
        this.col = col;
        this.result = Objects.requireNonNull(result);
        this.timestamp = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getGameId() { return gameId; }
    public UUID getPlayerId() { return playerId; }
    public int getRow() { return row; }
    public int getCol() { return col; }
    public Result getResult() { return result; }
    public Instant getTimestamp() { return timestamp; }

    public String toString() {
        return "Move{" + id + ", player=" + playerId + ", (" + row + "," + col + ")=" + result + "}";
    }
}
