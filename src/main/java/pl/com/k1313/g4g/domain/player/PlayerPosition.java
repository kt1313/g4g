package pl.com.k1313.g4g.domain.player;


public enum PlayerPosition {

    NoPosition("FREE","0"),
    GK ("goalkeeper", "3"),
    RWB("right Wingback", "10"),
    RCB("right Centreback", "9"),
    CB("centreback", "8"),
    LCB("left Centreback", "7"),
    LWB("left Wingback", "6"),
    RW("right Winger", "15"),
    CMD("centre Midfielder Defending", "14"),
    CM("centre Midfielder", "13"),
    CMA("centre Midfielder Attacking", "12"),
    LW("left Winger", "11"),
    RF("right Forward", "19"),
    CF("centre Forward", "18"),
    LF("left Forward", "17");

    private final String position;
    private final String number;


    PlayerPosition(String position, String number){
        this.position=position; this.number=number;
    }

    public static String toString(PlayerPosition playerPosition) {
        return PlayerPosition.toString(playerPosition);
    }

    public String toString(){
        return this.position;
    }
}
