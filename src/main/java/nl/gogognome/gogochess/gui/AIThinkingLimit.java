package nl.gogognome.gogochess.gui;

public class AIThinkingLimit {

    static AIThinkingLimit seconds(int seconds) {
        return new AIThinkingLimit(seconds, Unit.SECONDS);
    }

    static AIThinkingLimit level(int level) {
        return new AIThinkingLimit(level, Unit.LEVEL);
    }

    public enum Unit {
        LEVEL,
        SECONDS
    }

    private final int value;
    private final Unit unit;

    private AIThinkingLimit(int value, Unit unit) {
        this.value = value;
        this.unit = unit;
    }

    public int getValue() {
        return value;
    }

    Unit getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return unit == Unit.LEVEL ? "Level " + value : value + " seconds";
    }
}
