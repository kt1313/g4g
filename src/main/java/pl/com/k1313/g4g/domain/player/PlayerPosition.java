package pl.com.k1313.g4g.domain.player;


public enum PlayerPosition {

    NoPosition,
    GK ,
    RWB,
    RCB,
    CB,
    LCB,
    LWB,
    RW,
    CMD,
    CM,
    CMA,
    LW,
    RF,
    CF,
    LF;

//    private final String position;
//    private final String number;


//    PlayerPosition(String position, String number){
//        this.position=position; this.number=number;
//    }

    public static String toString(PlayerPosition playerPosition) {
        return PlayerPosition.toString(playerPosition);
    }

//    public String toString(){
//        return this.position;
//    }
}
