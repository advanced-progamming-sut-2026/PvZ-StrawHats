package model.collections.plant.actstrategy;

import model.collections.item.GroundSun;
import model.collections.plant.Plant;
import model.match_mechanisms.vector.Position;
import model.utils.GameSession;
import view.GeneralPrinter;

public class SunProduceStrategy implements ActStrategy {
    @Override
    public void act(Plant user, GameSession session) {
        if (user.getIntervalTimer() > 0) return;

        Position location = user.getLocation();

        boolean sunAlreadyExists = session.getItems().stream()
                .anyMatch(item -> item instanceof GroundSun sun
                        && !sun.isCollected()
                        && item.isAlive()
                        && item.getPosition() != null
                        && item.getPosition().x() == location.x()
                        && item.getPosition().y() == location.y());

        if (sunAlreadyExists) return;

        int sunValue = (int) user.getAbilityValue();
        session.getItems().add(new GroundSun(location, sunValue));
        GeneralPrinter.print("plant " + user.getName() + " produced a sun at ("
                + (int) location.x() + ", " + (int) location.y() + ").");

        user.setInternalTimer(user.getActionInterval());
    }
}
