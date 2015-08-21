/**
 * Copyright 2008 Jean-Philippe Gravel, eng. Licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.formix.btb.types;

import java.util.Calendar;

public class Employee extends Identifiable {

    private Integer departmentId;
    private String  name;
    private String  sin;
    private double  wages;
    private Calendar birthDate;
    
    // TODO: Test boolean methods (is intead of get)

    public Employee() {
        this.departmentId = null;
        this.name = "new";
        this.sin = "na";
        this.wages = 0;
        this.birthDate = Calendar.getInstance();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setSin(String sin) {
        this.sin = sin;
    }

    public String getSin() {
        return sin;
    }

    public void setWages(double wages) {
        this.wages = wages;
    }

    public double getWages() {
        return wages;
    }
    
    public Calendar getBirthDate() {
    	return this.birthDate;
    }
    
    public void setBirthDate(Calendar value) {
    	this.birthDate = (Calendar) value.clone();
    }

    @Override
    public boolean equals(Object obj) {
        Employee e = (Employee) obj;
        return super.equals(e) && (e.name == name) && (e.sin == sin)
                && (e.wages == wages);
    }

    @Override
    public String toString() {
        return getId() + " : " + name + " [" + sin + "] " + wages;
    }

    protected void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    protected Integer getDepartmentId() {
        return departmentId;
    }
}
