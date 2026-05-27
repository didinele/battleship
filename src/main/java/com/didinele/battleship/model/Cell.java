package com.didinele.battleship.model;

public class Cell {
    private final int row;
    private final int col;
    private Ship ship; // nullable
    private boolean hit;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.hit = false;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isHit() {
        return hit;
    }

    public void markHit() {
        this.hit = true;
    }

    public boolean hasShip() {
        return ship != null;
    }

    public Ship getShip() {
        return ship;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
    }
}
