package me.justup.upme.view.dashboard;

public class MoveHistory {
    int fromP = 0, toP = 0;

    public MoveHistory(int fromP, int toP) {
        this.fromP = fromP;
        this.toP = toP;
    }

    public int getFromP() {
        return fromP;
    }

    public int getToP() {
        return toP;
    }

    @Override
    public String toString() {
        return "MoveHistory{" +
                "fromP=" + fromP +
                ", toP=" + toP +
                '}';
    }
}
