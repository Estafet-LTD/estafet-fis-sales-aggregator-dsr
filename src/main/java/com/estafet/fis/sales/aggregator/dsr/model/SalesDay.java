package com.estafet.fis.sales.aggregator.dsr.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "SALES_DAY")
public class SalesDay {

	@Id
	@SequenceGenerator(name = "SALES_DAY_ID_SEQ", sequenceName = "SALES_DAY_ID_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SALES_DAY_ID_SEQ")
	@Column(name = "SALES_DAY_ID")
	private Integer id;

	@Column(name = "SALES_TOTAL")
	private Integer salesTotal;

	@Column(name = "SALES_DATE", nullable = false, unique = true)
	private String salesDate;

	@JsonIgnore
	@OneToMany(mappedBy = "taskUpdateSprintDay", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<TaskUpdate> updates = new HashSet<TaskUpdate>();

	void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public Integer getDayNo() {
		return dayNo;
	}

	public String getSprintDay() {
		return sprintDay;
	}

	public Sprint getSprintDaySprint() {
		return sprintDaySprint;
	}

	public Integer getHoursTotal() {
		return hoursTotal;
	}

	public Set<TaskUpdate> getUpdates() {
		return updates;
	}

	public Float getIdealHours() {
		return idealHours;
	}

	public void setIdealHours(Float idealHours) {
		this.idealHours = idealHours;
	}

	public SalesDay setHoursTotal(Integer hoursTotal) {
		this.hoursTotal = hoursTotal;
		return this;
	}

	public SalesDay setDayNo(Integer dayNo) {
		this.dayNo = dayNo;
		return this;
	}

	public SalesDay setSprintDay(String sprintDay) {
		this.sprintDay = sprintDay;
		return this;
	}

	public SalesDay setSprintDaySprint(Sprint sprintDaySprint) {
		this.sprintDaySprint = sprintDaySprint;
		return this;
	}

	private TaskUpdate geTaskUpdate(Integer taskId) {
		for (TaskUpdate update : updates) {
			if (update.getTaskId().equals(taskId)) {
				return update;
			}
		}
		return null;
	}

	public void update(Task task) {
		TaskUpdate update = geTaskUpdate(task.getId());
		if (update == null) {
			update = new TaskUpdate().setTaskId(task.getId()).setTaskUpdateSprintDay(this);
			updates.add(update);
		}
		update.setRemainingHours(task.getRemainingHours());
	}

	public void backfill(Task task) {
		TaskUpdate update = geTaskUpdate(task.getId());
		if (update == null) {
			update = new TaskUpdate().setTaskId(task.getId()).setTaskUpdateSprintDay(this);
			updates.add(update);
			update.setRemainingHours(task.getRemainingHours());
		}
	}

	public void recalculate() {
		if (!updates.isEmpty()) {
			this.hoursTotal = 0;
			for (TaskUpdate update : updates) {
				hoursTotal += update.getRemainingHours();
			}	
		}
	}

	public static SalesDay getAPI() {
		SalesDay day = new SalesDay();
		day.id = 1;
		day.dayNo = 1;
		day.hoursTotal = 10;
		day.sprintDay = "2017-10-16 00:00:00";
		return day;
	}

}
