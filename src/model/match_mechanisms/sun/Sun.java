package model.match_mechanisms.sun;

public abstract class Sun {
    private boolean isFallen;
    public void sunFall(){
        setFallen(true);
    };
    public void destroy(){};
    public abstract int getSunAmount();

    public boolean isFallen() {
        return isFallen;
    }

    public void setFallen(boolean fallen) {
        isFallen = fallen;
    }
}
