package model.match_mechanisms.vector;

public record Position (double x, double y) {
    public Position add(Position v) {
        return new Position(this.x + v.x, this.y + v.y);
    }

    public Position sub(Position v) {
        return new Position(this.x - v.x, this.y - v.y);
    }

    public Position scale(double d) {
        return new Position(this.x * d, this.y * d);
    }

    public Position negate() {
        return new Position(-this.x, -this.y);
    }

    public double length () {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public Position normalize () {
        double len = length();
        if (len == 0) return Position.ShowZero();
        return new Position(this.x / len, this.y / len);
    }

    public double distanceTo (Position v) {
        return this.sub(v).length();
    }

    public double dot (Position v) {
        return this.x * v.x + this.y * v.y;
    }

    public static Position ShowZero() {
        return new Position(0, 0);
    }

    public static Position of (double x, double y) {
        return new Position(x, y);
    }

}
