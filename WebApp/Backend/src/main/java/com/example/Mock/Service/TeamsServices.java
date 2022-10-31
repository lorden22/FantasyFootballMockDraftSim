package com.example.Mock.Service;

import java.util.ArrayList;
import java.util.TreeMap;

import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.Mock.Dao.TeamsDao;
import com.example.Mock.StartingClasses.PlayerModel;
import com.example.Mock.StartingClasses.TeamModel;

@Service
public class TeamsServices {
    private TeamsDao teamsDao;

    @Autowired
    public TeamsServices(@Qualifier("Teams") TeamsDao teamsDao){
        this.teamsDao = teamsDao;
    }
    
    public TreeMap<String,ArrayList<PlayerModel>> getTeamObject(int teamNumber) {
        return this.teamsDao.getTeamObject(teamNumber);
    }

    public String getTeamString(int teamNumber){
        return this.teamsDao.getTeamString(teamNumber);
    }
}
