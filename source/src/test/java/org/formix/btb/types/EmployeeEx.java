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

public class EmployeeEx extends Employee {

    private Department department;

    public void setDepartment(Department department) {
        this.department = department;
        if (department != null) {
            setDepartmentId(department.getId());
        } else {
            setDepartmentId(null);
        }
    }

    public Department getDepartment() {
        return department;
    }

    @Override
    public String toString() {
        return super.toString() + " [" + department + "]";
    }
}
