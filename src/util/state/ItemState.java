package util.state;

public record ItemState(String type, double x, double y, int lifetime, boolean collected) {}