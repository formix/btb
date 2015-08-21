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

import java.io.File;

/**
 * Utility class.
 * 
 * @author Jean-Philippe Gravel, eng.
 */
public final class Util {

    /**
     * This utility class cannot be instanciated.
     */
    private Util() {
    }

    /**
     * Throws NullPointerException if "s" is null or IllegalArgumentException if
     * "s" is an empty string.
     * 
     * @param s
     *            The string to check.
     * @param argName
     *            The name of the checked argument.
     */
    public static void throwIfNullOrEmpty(final String s, final String argName) {
        throwIfNull(s, argName);
        String msg = "The parameter \"@ARG_NAME\" can't be empty.";
        if (s == null) {
            throw new IllegalArgumentException(msg
                    .replace("@ARG_NAME", argName));
        }
    }

    /**
     * Throws NullPointerException if "o" is null.
     * 
     * @param o
     *            The object to check.
     * @param argName
     *            The name of the checked argument.
     */
    public static void throwIfNull(final Object o, final String argName) {
        String msg = "The parameter \"@ARG_NAME\" can't be null.";
        if (o == null) {
            throw new NullPointerException(msg.replace("@ARG_NAME", argName));
        }
    }

    /**
     * Translate the given comparator to it's SQL string equivalent.
     * 
     * @param comparator
     *            The comparator to translate.
     * @return the SQL String equivalent to the given comparator.
     */
    public static String translate(final Comparators comparator) {

        String ret = "";

        switch (comparator) {

        case DIFFERENT:
            ret = "<>";
            break;

        case EQUAL:
            ret = "=";
            break;

        case GREATER:
            ret = ">";
            break;

        case GREATER_OR_EQUAL:
            ret = ">=";
            break;

        case IS_NOT_NULL:
            ret = "IS NOT NULL";
            break;

        case IS_NULL:
            ret = "IS NULL";
            break;

        case LIKE:
            ret = "LIKE";
            break;

        case LOWER:
            ret = "<";
            break;

        case LOWER_OR_EQUAL:
            ret = "<=";
            break;

        case NOT_LIKE:
            ret = "NOT LIKE";
            break;

        default:
            break;
        }

        return ret;
    }

    /**
     * Translate the given operator to it's SQL string equivalent.
     * 
     * @param operator
     *            The comparator to translate.
     * @return the SQL String equivalent to the given operator.
     */
    public static String translate(final Operators operator) {
        String ret = "";

        switch (operator) {

        case AND:
            ret = "AND";
            break;

        case OR:
            ret = "OR";
            break;

        case NOT:
            ret = "NOT";
            break;

        default:
            break;
        }

        return ret;
    }

    /**
     * Delete the specified file or directory. If file is a directory, deletes
     * it's content recursively before deleting the directory.
     * 
     * @param file
     *            The file or directory to delete.
     */
    public static void delete(final File file) {

        if (file.isFile()) {
            file.delete();
            return;
        }

        File[] subFiles = file.listFiles();
        if (subFiles.length > 0) {
            for (File subFile : subFiles) {
                delete(subFile);
            }
        }

        file.delete();
    }
}
