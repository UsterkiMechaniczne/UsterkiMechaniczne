package model;

public class Report {
	
	
	
	public Report(Client client, Task task, double cost) {
		super();
		this.client = client;
		this.task = task;
		this.cost = cost;
	}
	public Client getClient() {
		return client;
	}
	public void setClient(Client client) {
		this.client = client;
	}
	public Task getTask() {
		return task;
	}
	public void setTask(Task task) {
		this.task = task;
	}
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	private Client client;
	private Task task;
	@Override
	public String toString() {
		return "Report [client=" + client + ", task=" + task + "]";
	}
	private double cost;
	
	
	
}
