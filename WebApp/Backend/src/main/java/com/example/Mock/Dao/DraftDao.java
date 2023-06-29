package com.example.Mock.DAO;

import java.util.List;
import com.example.Mock.StartingClasses.PlayerModel;

public interface DraftDAO {
    public List<PlayerModel> startDraft(String teamName, int draftSize, int desiredDraftPosition);
    public List<PlayerModel> getPlayersLeft();
    public List<PlayerModel> simComputerPicks();
    public List<PlayerModel> userDraftPick(int pick); 
    public int getCurrRound();
    public int getCurrPick();
    public int getNextUserPick();
    public boolean isDraftOver();
}