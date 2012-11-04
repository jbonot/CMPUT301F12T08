package tasktracker.model.elements;

/**
 * TaskTracker
 * 
 * Copyright 2012 Jeanine Bonot, Michael Dardis, Katherine Jasniewski,
 * Jason Morawski
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may 
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 * specific language governing permissions and limitations under the License.
 */

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import tasktracker.model.enums.*;


/**
 * A class that represents a task.
 * 
 * @author Katherine Jasniewski
 * @author Jeanine Bonot
 * 
 */
public class TaskElement implements Serializable {

	private static final long serialVersionUID = 1L;

	// Used to generate the task ID number.
	private static int _taskCount = 0;

	// Properties that will not change
	private final Date creationDate;
	private final User creator;
	private final int id;

	// Properties that may change
	private String name;
	private String description;
	private List<Requirement> requirements;
	private TaskStatus status;

	// private Status taskStatus;
	// private Visibility taskVisibility;
	// private int taskDeadline;

	/**
	 * Creates a new instance of the TaskElement class.
	 * 
	 * @param creator
	 *            The task creator.
	 */
	public TaskElement(User creator) {
		this(creator, "Untitled", Calendar.getInstance().getTime(), "",
				new ArrayList<Requirement>());
	}

	/**
	 * Creates a new instance of the TaskElement class.
	 * 
	 * @param creator The task creator.
	 * @param name The task name.
	 * @param date The task creation date.
	 * @param description A description of the task.
	 * @param requirements The list of task requirements.
	 */
	public TaskElement(User creator, String name, Date date,
			String description, List<Requirement> requirements) {

		// Required Elements
		this.creator = creator;
		this.creationDate = date;
		this.id = ++_taskCount;

		// Changeable elements
		this.name = name;
		this.description = description;
		this.requirements = requirements;
		this.status = TaskStatus.Unfulfilled;

	}

	/** Gets the task name */
	public String getName() {
		return name;
	}

	/** Sets the task name */
	public void setName(String name) {
		this.name = name;
	}

	/** Gets the date the task was created */
	public Date getDateCreated() {
		return this.creationDate;
	}

	/** Gets the task ID number */
	public int getID() {
		return this.id;
	}

	/** Gets the date in the format of a String */
	public String stringDate() {

		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		String stringdate = dateformat.format(creationDate).toString();
		System.out.println("Test String Date: '" + stringdate + "'");
		return stringdate;
	}

	/** Gets the text description of the task */
	public String getDescription() {
		return description;
	}

	/** Sets the text description of the task */
	public void setDescription(String description) {
		this.description = description;
	}

	/** Gets the list of task requirements */
	public List<Requirement> getRequirements() {
		return requirements;
	}

	/** Sets the list of task requirements. */
	public void setRequirements(List<Requirement> requirements) {
		this.requirements = requirements;
	}

	/** Deletes a requirement according to its index */
	public void deleteRequirement(int requirement) {
		requirements.remove(requirement);
		// TODO: Should be finding the specific requirement, not use indices.
	}

	// Sets the string in specific order for log entry in application.
	// public String toString() {

	// DecimalFormat df = new DecimalFormat ("#0.00");
	// return this.stringDate() + " \n" + description;
	// }
}