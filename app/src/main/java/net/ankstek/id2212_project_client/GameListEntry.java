package net.ankstek.id2212_project_client;

/**
 * Created by Lukas on 2016-01-06.
 */
public class GameListEntry {

    private final String player1;
    private final String player2;
    private String status;
    private final int gameNumber;

    GameListEntry(String player1, String player2, int gameNumber, String status){
        this.status = status;
        this.player1 = player1;
        this.player2 = player2;
        this.gameNumber = gameNumber;
    }

    public String getPlayer1(){
        return player1;
    }

    public String getPlayer2(){
        return player2;
    }

    public String getStatus(){
        return status;
    }

    public int getGameNumber(){
        return gameNumber;
    }

    public void setStatus(String newStatus){
        this.status = newStatus;
    }

}
