package com.example.Mock.API;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;

import com.example.Mock.Service.LoginServices;
import com.example.Mock.DAO.DraftDataObject;
import com.example.Mock.DAO.DraftedTeamsDataObject;
import com.example.Mock.DAO.TeamsDAO;
import com.example.Mock.Service.DraftServices;
import com.example.Mock.StartingClasses.PlayerModel;
import com.example.Mock.StartingClasses.TeamModel;

@RequestMapping("api/teams")
@CrossOrigin
@RestController
public class DraftController {

    private DraftServices draftServices;
    private JdbcTemplate jdbcTemplate;
    
    public DraftController(JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
        Integer count = this.jdbcTemplate.queryForObject("SELECT MAX(draft_id) FROM drafts", Integer.class);
        if(count==null) {
            count=1;
        }
        this.draftServices = new DraftServices(count);
        
        System.out.println("Database Connection: " + this.jdbcTemplate.getDataSource().toString());
        System.out.println("Starting draft_id: " + count);
        if(createPlayerDatabase()) {
            System.out.println("Player Database Created");
        }
        else {
            System.out.println("Player Database Not Created... Check Logs");
        }
    }

    private boolean createPlayerDatabase() {
		try {
			File playerStatFile = new File("/PlayerData.txt");
			Scanner fileReader = new Scanner(playerStatFile);
			while(fileReader.hasNextLine()) {
				String currPlayerStatsString = fileReader.nextLine();
				String[] currPlayerStatsArray = currPlayerStatsString.split(" ");
				ArrayList<Object> otherPlayStats = new ArrayList<Object>(0);

				if(currPlayerStatsArray.length < 3) {
					System.out.println("Error: Player Data File Not Formatted Correctly");
					return false;
				}
				// If the player has a first and last name only, default to index of 2
				int indexAfterFullName = 2;
				if (currPlayerStatsArray.length > 5) {
					indexAfterFullName = 3;
                }
                String playerName = "";
				for (int i = 0; i < indexAfterFullName; i++) {
					playerName += currPlayerStatsArray[i] + " ";
				}
				playerName = playerName.trim();

				for (String nextVal : Arrays.copyOfRange(currPlayerStatsArray, indexAfterFullName, currPlayerStatsArray.length)) {
					try {
						otherPlayStats.add(Double.parseDouble(nextVal));
					} catch (NumberFormatException error) {
						otherPlayStats.add("" + nextVal);
					}
				}
                this.jdbcTemplate.update("INSERT INTO players (name, position, predicted_score, player_rank) values (?, ?, ?, ?)", playerName, otherPlayStats.get(0), otherPlayStats.get(1), otherPlayStats.get(2));
			}
			fileReader.close();
			return true;
		}

		catch (FileNotFoundException error) {
			System.out.println("File Not Found - Check File Name");
		}
		catch (Exception error) {
			System.out.println("Other Error Found - See Below /n ------------------------------------------");
			error.printStackTrace();
		}
		return false;
	}
    
    @PostMapping(path="/initaizeUserAccountSetup")
    public boolean initaizeUserAccountSetup(
        @RequestParam("username") String username) {
            return true;
        }

    @GetMapping(path="/getPlayersLeft/") 
    public List<PlayerModel> getPlayersLeft(
        @RequestParam("username") String username){
        return this.draftServices.getPlayersLeft(this.jdbcTemplate, username);
    }

    @GetMapping(path="/getAllPlayersDrafted/")
    public List<PlayerModel> getAllPlayersDrafted(
        @RequestParam("username") String username) {
            return this.draftServices.getDraftedPlayers(this.jdbcTemplate, username);
    }

    @PostMapping(path="/startDraft/")
    public List<PlayerModel> startDraft(
        @RequestParam("username") String username,
        @RequestParam("teamName") String teamName, 
        @RequestParam("draftSize") int draftSize, 
        @RequestParam("draftPosition") int draftPosition,
        @Autowired DraftDataObject draftDataObject) {
            return this.draftServices.startDraft(this.jdbcTemplate, username, teamName, draftSize, draftPosition, draftDataObject);

    }

    @PostMapping(path="/simTo/")
    public List<PlayerModel> simtTo(
        @RequestParam("username") String username) {
            return this.draftServices.simTo(this.jdbcTemplate, username);
    }

    @GetMapping(path="/getCurrRound/")
    public int getCurrRound(
        @RequestParam("username") String username) {
            return this.draftServices.getCurrRound(this.jdbcTemplate, username);
    }

    @GetMapping(path="/getCurrPick/")
    public int getCurrPick(
        @RequestParam("username") String username) {
            return this.draftServices.getCurrPick(this.jdbcTemplate, username);
    }

    @GetMapping(path="/getNextUserPick/")
    public int getNextUserPick(
        @RequestParam("username") String username) {
            return this.draftServices.getNextUserPick(this.jdbcTemplate, username);
    }

    @GetMapping(path="/getNextUserPickRound/")
    public int getNextUserPickRound(
        @RequestParam("username") String username) {
            return this.draftServices.getNextUserPickRound(this.jdbcTemplate, username);
    }

    @PostMapping(path="/userDraftPlayer/")
    public List<PlayerModel> userDraftPick(
        @RequestParam("username") String username,
        @RequestParam("playerIndex") int playerIndex) {
            return this.draftServices.userDraftPick(this.jdbcTemplate, username, playerIndex);
    }

    @PostMapping(path="/deleteThisDraft/")
    public boolean deleteThisDraft(
        @RequestParam("username") String username) {
            return this.draftServices.deleteThisDraft(this.jdbcTemplate, username);
    }

    @PostMapping(path="/checkForCurrentDraft/")
    public boolean checkForDraft(
        @RequestParam("username") String username) {
            System.out.println("Checking for current drafts: " + username + " - " + this.draftServices.checkForCurrentDraft(this.jdbcTemplate, username));
            if (this.draftServices.checkForCurrentDraft(this.jdbcTemplate, username)) {
                System.out.println(" - True");
                return true;
            }
            return false;
    }

    @PostMapping(path="/userMarkCurrentDraftComplete/")
    public boolean userMarkCurrentDraftComplete(
        @RequestParam("username") String username) {
            this.draftServices.userMarkCurrentDraftComplete(this.jdbcTemplate, username);
            return this.draftServices.checkForCurrentDraft(this.jdbcTemplate, username);
    }

    @PostMapping(path="/checkForPastDraft/")
    public boolean checkForPastDrafts(
        @RequestParam("username") String username) {
            if (this.draftServices.checkForPastDrafts(this.jdbcTemplate, username)) {
                System.out.println("Checking for past drafts: " + username + " - True");
                return true;
            }
            return false;
    }

    @GetMapping(path="/getDraftHistoryMetaData/")
    public ArrayList<HashMap<String,String>>  getDraftHistoryMetaData(
        @RequestParam("username") String username) {
            return this.draftServices.getDraftHistoryMetaData(this.jdbcTemplate, username);
    }

    @GetMapping(path="/getDraftHistoryPlayerLog/")
    public List<PlayerModel> getDraftHistoryPlayerLog(
        @RequestParam("username") String username,
        @RequestParam("draftID") int draftID) {
            return this.draftServices.getDraftHistoryDraftedPlayerLog(this.jdbcTemplate, username, draftID);
    }

    @GetMapping(path="/getDraftHistoryTeamList/")
    public List<TeamModel> getDraftHistoryTeamList(
        @RequestParam("username") String username,
        @RequestParam("draftID") int draftID) {
            return this.draftServices.getDraftHistoryTeamList(this.jdbcTemplate, username, draftID);
    }


    @GetMapping(path="/getDraftHistoryTeamReview/")
    public TreeMap<String,ArrayList<PlayerModel>> getDraftHistoryTeamReview(
        @RequestParam("username") String username,
        @RequestParam("draftID") int draftID,
        @RequestParam("teamIndex") int teamIndex) {
            List<TreeMap<String,ArrayList<PlayerModel>>> allTreeMaps = this.draftServices.getDraftHistoryAllTeamsMap(this.jdbcTemplate, username, draftID);
            return allTreeMaps.get(teamIndex);
    }

}