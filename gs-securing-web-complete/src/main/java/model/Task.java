package model;

public class Task {

	private int id;
	private String plate;
	private String mechanic;
	private String description;
	private String title;
	private int hours;
	private String date;
	private Double partsCosts;
	private Double repairCosts;
	private Boolean isDone;
	
	public Task(int id, String plate, String mechanic, String description,
			String title, int hours, String date, Double partsCosts,
			Double repairCosts, Boolean isDone) {
		super();
		this.id = id;
		this.plate = plate;
		this.mechanic = mechanic;
		this.description = description;
		this.title = title;
		this.hours = hours;
		this.date = date;
		this.partsCosts = partsCosts;
		this.repairCosts = repairCosts;
		this.isDone = isDone;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPlate() {
		return plate;
	}
	public void setPlate(String plate) {
		this.plate = plate;
	}
	public String getMechanic() {
		return mechanic;
	}
	public void setMechanic(String mechanic) {
		this.mechanic = mechanic;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getHours() {
		return hours;
	}
	public void setHours(int hours) {
		this.hours = hours;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Double getPartsCosts() {
		return partsCosts;
	}
	public void setPartsCosts(Double partsCosts) {
		this.partsCosts = partsCosts;
	}
	public Double getRepairCosts() {
		return repairCosts;
	}
	public void setRepairCosts(Double repairCosts) {
		this.repairCosts = repairCosts;
	}
	public Boolean getIsDone() {
		return isDone;
	}
	public void setIsDone(Boolean isDone) {
		this.isDone = isDone;
	}
	@Override
	public String toString() {
		return "Taks [id=" + id + ", plate=" + plate + ", mechanic=" + mechanic
				+ ", description=" + description + ", title=" + title
				+ ", hours=" + hours + ", date=" + date + ", partsCosts="
				+ partsCosts + ", repairCosts=" + repairCosts + ", isDone="
				+ isDone + "]";
	}

}
