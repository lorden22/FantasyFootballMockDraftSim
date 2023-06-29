package com.example.Mock.DAO;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.example.Mock.StartingClasses.MockDraftDriver;
import com.example.Mock.StartingClasses.PlayerModel;

@Repository
@Scope(value="prototype")
public class DraftDataObject implements DraftDAO {

    private MockDraftDriver mockDraft;

    public DraftDataObject() {
        this.mockDraft = new MockDraftDriver();
    }

    public List<PlayerModel> startDraft(String teamName, int draftSize, int desiredDraftPosition){
        this.mockDraft.createdDraftEnv(teamName, draftSize, desiredDraftPosition);
        return this.getPlayersLeft();
    }
    public List<PlayerModel> getPlayersLeft(){
        return this.mockDraft.returnPlayers();
    }

    public int getCurrPick(){
        return this.mockDraft.getCurrPick();
    }

    public int getCurrRound(){
        return this.mockDraft.getCurrRound();
    }

    public int getNextUserPick(){
        return this.mockDraft.getNextUserPick();
    }

    public List<PlayerModel> simComputerPicks(){
        return this.mockDraft.simTo();
    }

    public List<PlayerModel> userDraftPick(int pick){
        return this.mockDraft.userDraftPick(pick);
    }

    public boolean isDraftOver(){
        return this.mockDraft.isDraftOver();
    }
}