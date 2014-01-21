package edu.buffalo.cse.blue.recordreplay;

import java.util.ArrayList;
import java.util.List;

import edu.buffalo.cse.blue.recordreplay.models.Objective;

public class ObjectivesManager {
	
	private MainActivity activity;
	private ArrayList<Objective> objectives;
	private Objective addNewObjectiveMock;
	private String mockObjectiveString;
	
	public ObjectivesManager(MainActivity a) {
		activity = a;
		mockObjectiveString = "Add New Objective...";
		addNewObjectiveMock = new Objective(-1, mockObjectiveString, null);
	}
	
	public List<Objective> getObjectives() {
		objectives = activity.getDatabase().getObjectives();
		objectives.add(addNewObjectiveMock);
		return objectives;
	}
	
	public String getMockObjectiveString() {
		return mockObjectiveString;
	}

}
