package com.example.Mock.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.Mock.Dao.DraftDao;
import com.example.Mock.Dao.TeamsDao;
import com.example.Mock.StartingClasses.PlayerModel;
import com.example.Mock.StartingClasses.TeamModel;

@Service
public class TeamsServices {
    private TeamsDao teamsDao;
    private DraftDao draftDao;

    @Autowired
    public TeamsServices(@Qualifier("Teams") TeamsDao teamsDao, @Qualifier("Draft") DraftDao draftDao ){
        this.teamsDao = teamsDao;
        this.draftDao = draftDao;
    }
    
    public TreeMap<String,ArrayList<PlayerModel>> getTeamObject(int teamNumber) {
        return this.teamsDao.getTeamObject(teamNumber);
    }

    public String getTeamString(int teamNumber){
        return this.teamsDao.getTeamString(teamNumber);
    }

    public List<PlayerModel> getPlayersLeft() {
        return this.draftDao.getPlayersLeft();
    }

    public int getCurrRound() {
        return this.draftDao.getCurrRound();
    }

    public int getCurrPick(){
        return this.draftDao.getCurrPick();
    }

    public int getNextUserPick(){
        return this.draftDao.getNextUserPick();
    }
    
    public List<PlayerModel> getPlayersDraftedRanked() {
        return this.teamsDao.getPlayersDraftedRanked();
    }

    public List<PlayerModel> startDraft(String teamName, int draftSize, int desiredDraftPosition) {
        return this.draftDao.startDraft(teamName, draftSize, desiredDraftPosition);
    }

    public List<PlayerModel> simTo(){
        return this.draftDao.simComputerPicks();
    }

    public List<PlayerModel> userDraftPick(int pick){
        return this.draftDao.userDraftPick(pick);
    }
}
