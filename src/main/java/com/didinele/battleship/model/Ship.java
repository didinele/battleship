package com.didinele.battleship.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Ship {
    public enum Orientation {HORIZONTAL, VERTICAL}

    private final ShipType type;
    private final int startRow;
    private final int startCol;
    private final Orientation orientation;
    private final Set<String> hits = new HashSet<>();

    public Ship(ShipType type, int startRow, int startCol, Orientation orientation) {
        this.type = Objects.requireNonNull(type);
        this.startRow = startRow;
        this.startCol = startCol;
        this.orientation = Objects.requireNonNull(orientation);
    }

    public ShipType getType() {
        return type;
    }

    public int getStartRow() {
        return startRow;
    }

    public int getStartCol() {
        return startCol;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public int length() {
        return type.getSize();
    }

    public boolean occupies(int row, int col) {
        for (int i = 0; i < length(); i++) {
            int r = orientation == Orientation.HORIZONTAL ? startRow : startRow + i;
            int c = orientation == Orientation.HORIZONTAL ? startCol + i : startCol;
            if (r == row && c == col) return true;
        }
        return false;
    }

    public void registerHit(int row, int col) {
        if (!occupies(row, col)) throw new IllegalArgumentException("Ship does not occupy cell");
        hits.add(key(row, col));
    }

    public boolean isSunk() {
        return hits.size() >= length();
    }

    private String key(int r, int c) {
        return r + "," + c;
    }
}
