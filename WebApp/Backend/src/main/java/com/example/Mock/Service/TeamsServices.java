package com.example.Mock.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.Mock.Dao.TeamsDao;

@Service
public class TeamsServices {
    private TeamsDao teamsDao;

    @Autowired
    public TeamsServices(@Qualifier("Teams") TeamsDao teamsDao){
        this.teamsDao = teamsDao;
    }

    public String getTeam(int teamNumber) {
        return this.teamsDao.getTeam(teamNumber);
    }
}
