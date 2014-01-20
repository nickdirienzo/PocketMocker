package edu.buffalo.cse.blue.recordreplay;

import java.util.ArrayList;
import java.util.List;

import edu.buffalo.cse.blue.recordreplay.models.Objective;

public class ObjectivesManager {
	
	private MainActivity activity;
	private ArrayList<Objective> objectives;
	private Objective addNewObjectiveMock;
	
	public ObjectivesManager(MainActivity a) {
		activity = a;
		addNewObjectiveMock = new Objective(-1, "Add New Objective...", null);
	}
	
	public List<Objective> getObjectives() {
		objectives = activity.getDatabase().getObjectives();
		objectives.add(addNewObjectiveMock);
		return objectives;
	}

}
