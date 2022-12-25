package pl.com.k1313.g4g.domain.match;

public enum GameStatus {

    NOTPLAYED("notPlayedGame", "0"),
    PLAYED("playedGame", "1");

    private final String gameStatus;
    private final String number;


    GameStatus(String gameStatus, String number) {
        this.gameStatus = gameStatus;
        this.number = number;
    }

    public String toString() {
        return this.gameStatus;
    }
}



