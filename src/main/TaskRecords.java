package main;
import java.util.Vector;

public class TaskRecords {
	private Vector<Task> currentListOfTasks;
	private Vector<Task> allTaskRecords;
	
	public TaskRecords(){
		initialiseAllTaskRecords();
	}
	
	public Vector<Task> getCurrentListOfTasks(){
		return currentListOfTasks;
	}
	public void appendTask(Task taskToBeAdded){}
	public void deleteTask(Task taskToBeDeleted){}
	public void replaceTask(){}
	
	private void initialiseAllTaskRecords(){
		
	}
	
	public Vector<Task> searchTasks(String query){
		return null;
	}
	
	public Task getTask(int taskLineNumber){
		return null;
	}
	
	public Task getTask(String taskName){
		return null;
	}
}
