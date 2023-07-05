package com.example.Mock.DAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.example.Mock.StartingClasses.MockDraftDriver;
import com.example.Mock.StartingClasses.PlayerModel;
import com.fasterxml.jackson.databind.util.JSONPObject;

@Repository
@Scope(value="prototype")
public class DraftDataObject implements DraftDAO {

    private MockDraftDriver mockDraft;
    private int draftID;

    public DraftDataObject() {
        this.mockDraft = new MockDraftDriver();
    }

    public List<PlayerModel> startDraft(String teamName, int draftSize, int desiredDraftPosition, int draftID){
        this.mockDraft.createdDraftEnv(teamName, draftSize, desiredDraftPosition);
        this.draftID = draftID;
        return this.getPlayersLeft();
    }
    public List<PlayerModel> getPlayersLeft(){
        return this.mockDraft.returnPlayers();
    }

    public List<PlayerModel> getDraftedPlayers(){
        return this.mockDraft.returnDraftLog();
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

    public HashMap<String,String> getDraftMetaData(){
        HashMap<String,String> draftInfo = new HashMap<String,String>();
        draftInfo.put("draftID", this.draftID+"");
        draftInfo.put("teamName", this.mockDraft.getUserTeamName());
        draftInfo.put("draftPosition", this.mockDraft.startingDraftPick()+"");
        draftInfo.put("draftSize", this.mockDraft.getDraftSize()+"");
        draftInfo.put("Date", this.mockDraft.getDate());
        draftInfo.put("Time", this.mockDraft.getTime());
        return draftInfo; 
    }

    public List<TreeMap<String,ArrayList<PlayerModel>>> getDraftHistoryAllTeamsMap(){
        return this.mockDraft.getAllTeamsMap();    
    }

}