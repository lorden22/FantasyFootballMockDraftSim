package com.example.Mock.DAO;

import java.util.Comparator;

public interface PlayerDataObjectDAO {
    String getFirstName();
    String getLastName();
    String getFullName();
    String getPosition();
    double getPredictedScore();
    double getRank();
    void setRank(double rankChange);
    double getAvgADP();
    String getSpotDrafted();
    void setSpotDrafted(String spotDraftedChange);
    String getTeamDraftedBy();
    void setTeamDraftedBy(String teamDraftedByChange);
}
