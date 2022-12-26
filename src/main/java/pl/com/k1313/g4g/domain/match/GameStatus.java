package pl.com.k1313.g4g.domain.match;

public enum GameStatus {

    NOTPLAYED("not played"),
    PLAYED("played");

    private final String gameStatus;

     GameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }

    public String toString() {
        return gameStatus;
    }
}



