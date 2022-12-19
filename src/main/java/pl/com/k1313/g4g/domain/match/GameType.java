package pl.com.k1313.g4g.domain.match;

public enum GameType {

    FG("friendly", "0"),
    LG("league", "1"),
    CG("cup", "2");


    private final String gameType;
    private final String number;


    GameType(String gameType, String number) {
        this.gameType = gameType;
        this.number = number;
    }

    public static String toString(GameType gameType) {
        return GameType.toString(gameType);
    }

    public String toString() {
        return this.gameType;
    }
}


