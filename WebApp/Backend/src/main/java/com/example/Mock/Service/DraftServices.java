package com.example.Mock.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import javax.naming.ldap.HasControls;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;
import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.origin.SystemEnvironmentOrigin;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.example.Mock.DAO.DraftDataObject;
import com.example.Mock.DAO.DraftedTeamsDataObject;
import com.example.Mock.StartingClasses.PlayerModel;

@Service
@Scope(value="prototype")
public class DraftServices {

    private HashMap<DraftDataObject, DraftedTeamsDataObject> allPastDrafts;
    private DraftedTeamsDataObject draftedTeamsDataObject;
    private DraftDataObject draftDataObject;

    public DraftServices() {
        this.allPastDrafts = new HashMap<DraftDataObject, DraftedTeamsDataObject>();
    }

    
   public TreeMap<String,ArrayList<PlayerModel>> getTeamObject(int teamNumber) {
        return this.draftedTeamsDataObject.getTeamObject(teamNumber);
    }

    public String getTeamString(int teamNumber){
        return this.draftedTeamsDataObject.getTeamString(teamNumber);
    }

    public List<PlayerModel> getPlayersLeft() {
        return this.draftDataObject.getPlayersLeft();
    }

    public List<PlayerModel> getDraftedPlayers() {
        return this.draftDataObject.getDraftedPlayers();
    }

    public int getCurrRound() {
        return this.draftDataObject.getCurrRound();
    }

    public int getCurrPick(){
        return this.draftDataObject.getCurrPick();
    }

    public int getNextUserPick(){
        return this.draftDataObject.getNextUserPick();
    }
    
    public List<PlayerModel> getPlayersDraftedRanked() {
        return this.draftedTeamsDataObject.getPlayersDraftedRanked();
    }

    public List<PlayerModel> startDraft(String teamName, int draftSize, int desiredDraftPosition, DraftDataObject draftDataObject, DraftedTeamsDataObject draftedTeamsDataObject) {
        this.draftDataObject = draftDataObject;
        this.draftedTeamsDataObject = draftedTeamsDataObject;
        return this.draftDataObject.startDraft(teamName, draftSize, desiredDraftPosition);
    }

    public List<PlayerModel> simTo(){
        return this.draftDataObject.simComputerPicks();
    }

    public List<PlayerModel> userDraftPick(int pick){
        return this.draftDataObject.userDraftPick(pick);
    }

    public boolean isDraftOver(){
        return this.draftDataObject.isDraftOver();
    }

    public boolean checkForPastDrafts() {
        return !this.allPastDrafts.isEmpty();
    }

    public boolean deleteThisDraft() {
        this.draftedTeamsDataObject = null;
        this.draftDataObject = null;
        return checkForDraft();    
    }

    public boolean checkForDraft() {
        return (this.draftedTeamsDataObject != null || this.draftDataObject != null);
    }
}