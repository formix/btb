package org.formix.btb;

/**
 * The type of join to perform in the where clause between two type's
 * properties.
 * 
 * @author jpgravel
 * 
 */
public enum JoinType {

	/**
	 * Does an inner join. 
	 */
	INNER,

	/**
	 * Does an left join.
	 */
	LEFT,

	/**
	 * Does an right join.
	 */
	RIGHT

}
