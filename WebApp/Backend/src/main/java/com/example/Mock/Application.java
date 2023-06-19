package com.example.Mock;
import java.util.ArrayList;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.example.Mock.StartingClasses.MockDraftDriver;

import com.example.Mock.DAO.DraftedTeamsDataObject;
import com.example.Mock.StartingClasses.TeamModel;;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
