package com.example.Mock.Dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.Mock.StartingClasses.MockDraftDriver;
import com.example.Mock.StartingClasses.PlayerModel;

@Repository("Draft")
public class DraftDataObject implements DraftDao {
    private MockDraftDriver mockDraft;

    public DraftDataObject() {
        this.mockDraft = new MockDraftDriver();
    }

    public List<PlayerModel> startDraft(String teamName, int draftSize, int desiredDraftPosition){
        this.mockDraft.createdDraftEnv(teamName, draftSize, desiredDraftPosition);
        return this.mockDraft.returnPlayers();
    }
    public void getPlayersLeft(){

    }
    public void getPlayersDrafted(){

    }
    public void getTeamOject(){

    }
    public void getTeamString(){

    }
    public void simComputerPicks(){

    }
}
