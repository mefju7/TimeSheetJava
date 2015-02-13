package no.marintek.matthias.data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

// TODO: Auto-generated Javadoc
// @XmlAccessorType(XmlAccessType.FIELD)
   /**
 * The Class TimeSheetEntry.
 */
public class TimeSheetEntry {
	
	/** The start time. */
	Date startTime;	
	
	/** The project. */
	String project="";
	
	/** The task. */
	String task="";
	
	/** The previous left. */
	double previousLeft;
	
	/** The year. */
	@XmlTransient
	public int year;
	
	/** The week. */
	@XmlTransient
	public int week;
	
	/** The weekday. */
	@XmlTransient
	public int weekday;
	
	/** The worked. */
	@XmlTransient
	public double worked; // in hours
	
	/** The billed. */
	@XmlTransient
	public double billed;

	/** The remaining. */
	@XmlTransient
	public double remaining;
	
	/** The previous. */
	@XmlTransient
	public TimeSheetEntry previous; // same project and task
	
	/** The end time. */
	@XmlTransient
	public Date endTime;
	
	/** The scale up. */
	static double scaleUp=16.0/15.0;
	
	private final PropertyChangeSupport support=new PropertyChangeSupport(this);
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		support.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		support.removePropertyChangeListener(listener);
	}
	/**
	 * Instantiates a new time sheet entry.
	 *
	 * @param other the other
	 */
	public TimeSheetEntry(TimeSheetEntry other) {
		// TODO Auto-generated constructor stub
		this.startTime=new Date();
		this.project=other.project;
		this.task=other.task;
	}
	
	
	/**
	 * Instantiates a new time sheet entry.
	 */
	public TimeSheetEntry() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Gets the start time.
	 *
	 * @return the start time
	 */
	@XmlAttribute(name="StartTime",required=true)
	public Date getStartTime() {
		return startTime;
	}
	
	/**
	 * Sets the start time.
	 *
	 * @param startTime the start time
	 * @return the time sheet entry
	 */
	public TimeSheetEntry setStartTime(Date startTime) {
		if(startTime==null)
			startTime=new Date();
		Date old = this.startTime;
		if( old != startTime)
		{
			this.startTime = startTime;
			support.firePropertyChange("start", old, startTime);
		}
		return this;
	}
	
	/**
	 * Gets the project.
	 *
	 * @return the project
	 */
	@XmlAttribute(name="Project",required=true)
	public String getProject() {
		return project;
	}
	
	/**
	 * Sets the project.
	 *
	 * @param project the project
	 * @return the time sheet entry
	 */
	public TimeSheetEntry setProject(String project) {
		String old = this.project;
		if(this.project!= project)
		{
			this.project = project;
		support.firePropertyChange("project", old, project);
		}
		return this;
	}
	
	/**
	 * Gets the task.
	 *
	 * @return the task
	 */
	@XmlAttribute(name="Task",required=true)
	public String getTask() {
		return task;
	}
	
	/**
	 * Sets the task.
	 *
	 * @param task the task
	 * @return the time sheet entry
	 */
	public TimeSheetEntry setTask(String task) {
		String old = this.task;
		if(old!=task)
		{
			this.task = task;
			support.firePropertyChange("task", old, task);
		}
		return this;
	}
	
	/**
	 * Gets the previous left.
	 *
	 * @return the previous left
	 */
	@XmlAttribute(name="Previous")
	public double getPreviousLeft() {
		return previousLeft;
	}
	
	/**
	 * Sets the previous left.
	 *
	 * @param previousLeft the new previous left
	 */
	public void setPreviousLeft(double previousLeft) {
		this.previousLeft = previousLeft;
	}
	
	/**
	 * Calculate.
	 */
	public void calculate() {
		if(project.isEmpty())
		{
			worked=billed=remaining=previousLeft=0;
			return;
		}
		double oldRemaining = this.remaining;
//		double oldWorked = this.worked;
		long seconds=(endTime.getTime()-startTime.getTime())/1000; // from milli to seconds
		worked = seconds / 3600.0;
		if(previous!=null)
		{
			Calendar cal;
			cal = Calendar.getInstance();
			cal.setTime(previous.startTime);
			int prevYear = cal.get(Calendar.YEAR);
			int prevDay = cal.get(Calendar.DAY_OF_YEAR);
			cal.setTime(startTime);
			int curYear=cal.get(Calendar.YEAR);
			int curDay=cal.get(Calendar.DAY_OF_YEAR);
			if((prevYear==curYear) && (prevDay==curDay))
				previous.cancelBilled();
			previousLeft=previous.remaining;
		}
		else
			previousLeft=0;
		double left = previousLeft+scaleUp*worked;
		billed=0;
		if(left>0)
		{
			billed=Math.ceil(left*2.0)/2.0;
		}
		remaining=left-billed;
		support.firePropertyChange("remaining", oldRemaining, remaining);
	}
	
	/**
	 * Cancel billed.
	 */
	private void cancelBilled() {
		// TODO Auto-generated method stub
		remaining= previousLeft + scaleUp*worked;
		billed=0;
	}

	public Date getNextTurnOver() {
	  // TODO Auto-generated method stub
		Calendar cal;
		cal = Calendar.getInstance();
		cal.setTime(startTime);
		double add2start = (billed - previousLeft)/scaleUp;
		add2start*=3600;
		cal.add(Calendar.SECOND, (int) (add2start));
	  return cal.getTime();
  }
	
	
	
}
