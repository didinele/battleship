package com.didinele.battleship.model;

import java.time.Instant;
import java.util.*;

public class Game {
    private final UUID id;
    private final Player player1;
    private final Player player2;
    private final Map<UUID, Board> boards = new HashMap<>(); // playerId -> Board
    private UUID currentTurnPlayerId;
    private GameStatus status;
    private final Instant createdAt;
    private Instant updatedAt;
    private final List<MoveRecord> moves = new ArrayList<>();
    private UUID winnerPlayerId;
    private final int boardSize;

    public Game(Player player1, Player player2, int boardSize) {
        if (player1 == null || player2 == null) throw new IllegalArgumentException("Players must not be null");
        if (Objects.equals(player1.getId(), player2.getId()))
            throw new IllegalArgumentException("Players must be distinct");

        this.id = UUID.randomUUID();
        this.player1 = player1;
        this.player2 = player2;
        this.boardSize = boardSize;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.status = GameStatus.CREATED;

        boards.put(player1.getId(), new Board(boardSize));
        boards.put(player2.getId(), new Board(boardSize));
    }

    public synchronized void start() {
        if (status != GameStatus.CREATED && status != GameStatus.SETUP)
            throw new IllegalStateException("Cannot start game from state: " + status);

        Board b1 = boards.get(player1.getId());
        Board b2 = boards.get(player2.getId());
        if (b1 == null || b2 == null) throw new IllegalStateException("Boards missing for players");
        if (!b1.hasAllShipsPlaced() || !b2.hasAllShipsPlaced())
            throw new IllegalStateException("Both players must place all ships before starting");

        this.status = GameStatus.IN_PROGRESS;
        this.currentTurnPlayerId = player1.getId();
        this.updatedAt = Instant.now();
    }

    public synchronized MoveResult fire(UUID firingPlayerId, int row, int col) {
        if (status != GameStatus.IN_PROGRESS) throw new IllegalStateException("Game not in progress");
        if (!Objects.equals(firingPlayerId, currentTurnPlayerId))
            throw new IllegalArgumentException("Not this player's turn");

        UUID opponentId = getOpponentId(firingPlayerId);
        Board opponentBoard = boards.get(opponentId);
        if (opponentBoard == null) throw new IllegalStateException("Opponent board not found");

        MoveResult result = opponentBoard.applyMove(row, col);
        MoveRecord record = new MoveRecord(this.id, firingPlayerId, row, col, result, Instant.now());
        moves.add(record);

        if (opponentBoard.allShipsSunk()) {
            this.status = GameStatus.FINISHED;
            this.winnerPlayerId = firingPlayerId;
        } else {
            switchTurn();
        }

        this.updatedAt = Instant.now();
        return result;
    }

    private void switchTurn() {
        if (Objects.equals(currentTurnPlayerId, player1.getId()))
            currentTurnPlayerId = player2.getId();
        else
            currentTurnPlayerId = player1.getId();
    }

    private UUID getOpponentId(UUID playerId) {
        if (Objects.equals(playerId, player1.getId())) return player2.getId();
        if (Objects.equals(playerId, player2.getId())) return player1.getId();
        throw new IllegalArgumentException("Unknown player id: " + playerId);
    }

    public UUID getId() {
        return id;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Map<UUID, Board> getBoards() {
        return Collections.unmodifiableMap(boards);
    }

    public UUID getCurrentTurnPlayerId() {
        return currentTurnPlayerId;
    }

    public GameStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public List<MoveRecord> getMoves() {
        return Collections.unmodifiableList(moves);
    }

    public UUID getWinnerPlayerId() {
        return winnerPlayerId;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public boolean isOver() {
        return status == GameStatus.FINISHED || status == GameStatus.CANCELLED;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", player1=" + player1.getName() +
                ", player2=" + player2.getName() +
                ", status=" + status +
                ", currentTurn=" + (currentTurnPlayerId != null ? currentTurnPlayerId : "null") +
                '}';
    }

    public enum GameStatus {
        CREATED,
        SETUP,
        IN_PROGRESS,
        FINISHED,
        CANCELLED
    }

    public enum MoveResult {
        MISS,
        HIT,
        SUNK
    }

    public record MoveRecord(UUID gameId, UUID playerId, int row, int col, MoveResult result, Instant timestamp) {
    }
}
