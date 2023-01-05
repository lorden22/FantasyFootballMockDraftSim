package com.example.Mock.Dao;

import java.util.List;
import com.example.Mock.StartingClasses.PlayerModel;

public interface DraftDao {
    public List<PlayerModel> startDraft(String teamName, int draftSize, int desiredDraftPosition);
    public List<PlayerModel> getPlayersLeft();
    public List<PlayerModel> getPlayersDrafted();
    public void getTeamOject();
    public void getTeamString();
    public List<String> simComputerPicks(int nextUserPick); 


}