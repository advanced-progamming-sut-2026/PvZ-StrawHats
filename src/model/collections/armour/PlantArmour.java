package model.collections.armour;

import model.collections.plant.Plant;
import model.collections.zombie.Zombie;

// ایمپورت‌های خارجی کد دوم برای سیستم انفجار (جهت ایجاد ارور و همگام‌سازی توسط شما)
import com.ussr.pvz.model.App;
import util.GameSession;

public class PlantArmour extends Armour {
    private final int reflectiveDamage;
    private final boolean explodeOnBreak;

    public PlantArmour(int HP, int reflectiveDamage, boolean explodeOnBreak) {
        super(HP);
        this.reflectiveDamage = reflectiveDamage;
        this.explodeOnBreak = explodeOnBreak;
    }

    // متد اصلیِ کلاس پدر (Armour) که بدون گیاه صدا زده می‌شود
    @Override
    public int absorbDamage(int damage) {
        return absorbDamage(damage, null);
    }

    // متد جدید و اورلود شده که کاربر (گیاه) را برای گرفتن پوزیشن و انفجار دریافت می‌کند
    public int absorbDamage(int damage, Plant user) {
        if (damage >= getHP()) {
            int overflow = damage - getHP();
            setHP(0); // این خط اتوماتیک state رو BROKEN میکنه (طبق متد کلاس پدر)

            // اجرای مکانیزم انفجار در صورت شکسته شدن زره
            if (this.explodeOnBreak && user != null) {
                executeArmorExplosion(user);
            }

            return overflow;
        } else {
            setHP(getHP() - damage);
            return 0; // Shield absorbed everything!
        }
    }

    public void handleReflection(Zombie dealer, Plant user) {
        if (dealer != null && this.reflectiveDamage > 0 && getHP() > 0) {
            dealer.takeDamage(this.reflectiveDamage, user);
        }
    }

    public boolean isDestroyed() { return getHP() <= 0; }
    public boolean isExplodeOnBreak() { return explodeOnBreak; }

    private void executeArmorExplosion(Plant user) {
        GameSession session = App.getGameSession();
        if (session == null || session.getZombies() == null) return;

        var plantPos = user.getPosition();
        int explosionDamage = 500;
        for (Zombie zombie : session.getZombies()) {
            if (zombie.isAlive()) {
                var zPos = zombie.getPosition();

                double distance = zPos.distanceTo(plantPos);
                if (distance <= 1.5) {
                    zombie.takeDamage(explosionDamage);
                }
            }
        }
    }
}