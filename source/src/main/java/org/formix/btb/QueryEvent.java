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

import java.util.EventObject;

/**
 * An event cast by a query object.
 * 
 * @author Jean-Philippe Gravel
 * @param <I>
 *            The type of the affected item.
 */
public class QueryEvent<I> extends EventObject {

	private static final long serialVersionUID = 0xBBBB50ACA4E91202L;

	private I item;
	private Object itemId;
	private boolean cancelled;

	/**
	 * Constructor of the class.
	 * 
	 * @param source
	 *            The Source of the event.
	 * @param item
	 *            The affected item.
	 */
	public QueryEvent(Object source, I item) {
		this(source, item, null);
	}

	/**
	 * Constructor of the class.
	 * 
	 * @param source
	 *            The Source of the event.
	 * @param item
	 *            The affected item.
	 */
	public QueryEvent(Object source, I item, Object itemId) {
		super(source);
		this.item = item;
		this.itemId = itemId;
		this.cancelled = false;
	}

	/**
	 * @return the affected item.
	 */
	public I getItem() {
		return item;
	}

	/**
	 * @param item
	 *            The affected item.
	 */
	public void setItem(I item) {
		this.item = item;
	}

	public Object getItemId() {
		return itemId;
	}

	/**
	 * Tells if the current QueryEvent further action is to be cancelled.
	 * Cancelled property is false by default.
	 * 
	 * @return true to cancel the remaining actions, false to go on.
	 */
	public boolean isCancelled() {
		return this.cancelled;
	}

	/**
	 * Sets the cancelled property to true or false.
	 * 
	 * @param value
	 *            The velue to be set.
	 */
	public void setCancelled(boolean value) {
		this.cancelled = value;
	}
}
