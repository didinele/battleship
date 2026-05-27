package com.didinele.battleship.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Board {
    private final int size;
    private final Cell[][] grid;
    private final List<Ship> ships = new ArrayList<>();

    public Board(int size) {
        if (size <= 0) throw new IllegalArgumentException("Board size must be > 0");
        this.size = size;
        this.grid = new Cell[size][size];
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                grid[r][c] = new Cell(r, c);
            }
        }
    }

    public int getSize() {
        return size;
    }

    public Cell getCell(int row, int col) {
        checkBounds(row, col);
        return grid[row][col];
    }

    private void checkBounds(int row, int col) {
        if (row < 0 || row >= size || col < 0 || col >= size)
            throw new IllegalArgumentException("Coordinates out of bounds");
    }

    public List<Ship> getShips() {
        return List.copyOf(ships);
    }

    public boolean hasAllShipsPlaced() {
        // default expectation: one of each ShipType
        return ships.size() == ShipType.values().length;
    }

    public boolean canPlaceShip(Ship ship) {
        Objects.requireNonNull(ship);
        int len = ship.length();
        for (int i = 0; i < len; i++) {
            int r = ship.getOrientation() == Ship.Orientation.HORIZONTAL ? ship.getStartRow() : ship.getStartRow() + i;
            int c = ship.getOrientation() == Ship.Orientation.HORIZONTAL ? ship.getStartCol() + i : ship.getStartCol();
            if (r < 0 || r >= size || c < 0 || c >= size) return false;
            if (grid[r][c].hasShip()) return false;
        }
        return true;
    }

    public void placeShip(Ship ship) {
        if (!canPlaceShip(ship)) throw new IllegalArgumentException("Cannot place ship at given position");
        for (int i = 0; i < ship.length(); i++) {
            int r = ship.getOrientation() == Ship.Orientation.HORIZONTAL ? ship.getStartRow() : ship.getStartRow() + i;
            int c = ship.getOrientation() == Ship.Orientation.HORIZONTAL ? ship.getStartCol() + i : ship.getStartCol();
            grid[r][c].setShip(ship);
        }
        ships.add(ship);
    }

    public Move.Result applyMove(int row, int col) {
        checkBounds(row, col);
        Cell cell = grid[row][col];
        if (cell.isHit()) throw new IllegalArgumentException("Cell already targeted");
        cell.markHit();
        if (!cell.hasShip()) {
            return Move.Result.MISS;
        }
        Ship ship = cell.getShip();
        ship.registerHit(row, col);
        if (ship.isSunk()) return Move.Result.SUNK;
        return Move.Result.HIT;
    }

    public boolean allShipsSunk() {
        if (ships.isEmpty()) return false;
        for (Ship s : ships) {
            if (!s.isSunk()) return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                Cell cell = grid[r][c];
                if (cell.isHit()) {
                    sb.append(cell.hasShip() ? "X" : "~");
                } else {
                    sb.append(".");
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
