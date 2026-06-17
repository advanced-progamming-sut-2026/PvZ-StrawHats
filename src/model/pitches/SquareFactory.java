package model.pitches;

public class SquareFactory extends Square{

    public Square createSquare(String type) {
        if (type.equals("regular")) {
            return new Square();
        }
        else {
            System.out.println("invalid square");
            return null;
        }
    }
}
