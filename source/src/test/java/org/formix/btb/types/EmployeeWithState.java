package org.formix.btb.types;

public class EmployeeWithState extends Employee {

	private int state;
	
	public EmployeeWithState() {
		super();
		this.state = 0;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	
}
