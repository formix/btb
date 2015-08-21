/**
 * Copyright 2012 Jean-Philippe Gravel, P. Eng., CSDP 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not 
 * use this file except in compliance with the License. You may obtain a copy 
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.formix.btb;

/**
 * Creates a join between two database tables using their classes equivalents.
 * 
 * @author jpgravel
 * 
 */
public class Join {

	private Class<?> type1;
	private Class<?> type2;
	private String property1;
	private String property2;
	private JoinType joinType;

	/**
	 * Creates a join between type1 and type2 on property1 inner join property2.
	 * 
	 * @param type1
	 *            the first type of the join.
	 * 
	 * @param type2
	 *            the second type of the join.
	 * 
	 * @param property1
	 *            the property from type1.
	 * 
	 * @param property2
	 *            the property from type2.
	 */
	public Join(Class<?> type1, Class<?> type2, String property1,
			String property2) {
		this(type1, type2, property1, property2, JoinType.INNER);
	}

	/**
	 * Creates a join between type1 and type2 between property1 and property2
	 * from their respective classes using the join type specified.
	 * 
	 * @param type1
	 *            the first type of the join.
	 * 
	 * @param type2
	 *            the second type of the join.
	 * 
	 * @param property1
	 *            the property from type1.
	 * 
	 * @param property2
	 *            the property from type2.
	 * 
	 * @param joinType
	 *            the type of join to do between type1.property1 and
	 *            type2.property2.
	 */
	public Join(Class<?> type1, Class<?> type2, String property1,
			String property2, JoinType joinType) {

		Util.throwIfNull(type1, "type1");
		Util.throwIfNull(type2, "type2");
		Util.throwIfNullOrEmpty(property1, "property1");
		Util.throwIfNullOrEmpty(property2, "property2");

		this.type1 = type1;
		this.type2 = type2;
		this.property1 = property1;
		this.property2 = property2;
		this.setJoinType(joinType);

	}

	/**
	 * Gets the first type from the join clause.
	 * 
	 * @return the first type from the join clause.
	 */
	public Class<?> getType1() {
		return type1;
	}

	/**
	 * Sets the first type from the join clause.
	 * 
	 * @param type1
	 *            the first type from the join clause.
	 */
	public void setType1(Class<?> type1) {
		this.type1 = type1;
	}

	/**
	 * Gets the second type from the join clause.
	 * 
	 * @return the second type from the join clause.
	 */
	public Class<?> getType2() {
		return type2;
	}

	/**
	 * Sets the second type from the join clause.
	 * 
	 * @param type2
	 *            the second type from the join clause.
	 */
	public void setType2(Class<?> type2) {
		this.type2 = type2;
	}

	/**
	 * Gets the property name from the type1 to do the join on.
	 * 
	 * @return the property name from the type1 to do the join on.
	 */
	public String getProperty1() {
		return property1;
	}

	/**
	 * Sets the property name from the type1 to do the join on.
	 * 
	 * @param property1
	 *            the property name from the type1 to do the join on.
	 */
	public void setProperty1(String property1) {
		this.property1 = property1;
	}

	/**
	 * Gets the property name from the type2 to do the join on.
	 * 
	 * @return the property name from the type2 to do the join on.
	 */
	public String getProperty2() {
		return property2;
	}

	/**
	 * Sets the property name from the type2 to do the join on.
	 * 
	 * @param property2
	 *            the property name from the type2 to do the join on.
	 */
	public void setProperty2(String property2) {
		this.property2 = property2;
	}

	/**
	 * Gets the join to do between type1.property1 and type2.property2.
	 * 
	 * @return the join to do between type1.property1 and type2.property2.
	 */
	public JoinType getJoinType() {
		return joinType;
	}

	/**
	 * Sets the join to do between type1.property1 and type2.property2.
	 * 
	 * @param joinType
	 *            the join to do between type1.property1 and type2.property2.
	 */
	public void setJoinType(JoinType joinType) {
		this.joinType = joinType;
	}

	@Override
	public String toString() {
		String table1Name = this.getTableName(this.type1.getSimpleName());
		String table2Name = this.getTableName(this.type2.getSimpleName());
		String joinString = this.getJoinString();

		StringBuilder sb = new StringBuilder();
		sb.append(joinString).append(' ').append(table2Name).append(" ON ")
				.append(table1Name).append('.').append(this.getProperty1())
				.append(" = ").append(table2Name).append('.')
				.append(this.getProperty2());

		// String ret = joinString + " " + table2Name + " ON " + table1Name +
		// "."
		// + this.getProperty1() + " = " + table2Name + "."
		// + this.getProperty2();
		//
		// return ret;

		return sb.toString();
	}

	private String getJoinString() {
		String joinString = "INNER";
		if (this.joinType == JoinType.LEFT)
			joinString = "LEFT";
		if (this.joinType == JoinType.RIGHT)
			joinString = "RIGHT";
		return joinString + " JOIN";
	}

	private String getTableName(String simpleName) {
		if (simpleName.endsWith("Entity"))
			return simpleName.substring(0, simpleName.length() - 6);
		if (simpleName.endsWith("Table"))
			return simpleName.substring(0, simpleName.length() - 5);
		return simpleName;
	}
}
