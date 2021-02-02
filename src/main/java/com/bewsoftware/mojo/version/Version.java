/*
 * Copyright (C) 2021 Bradley Willcott
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.bewsoftware.mojo.version;

/**
 * Version class is a 'struct' to transport the components of a version number
 * between classes.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.1
 * @version 0.1
 */
public class Version {

    /**
     * Major version number component;
     */
    public String major;

    /**
     * Minor version number component;
     */
    public String minor;

    /**
     * Patch version number component;
     */
    public String patch;

    /**
     * Snapshot version number component;
     */
    public String snapshot;

    /**
     * Accessible only within package.
     */
    Version() {
    }

    /**
     * Formats the components into a full version number string.
     *
     * @return new version string.
     */
    public String format() {
        return String.format("%s.%s.%s%s", major, minor, patch, snapshot);
    }

    @Override
    public String toString() {
        return "Version{" + "major=" + major + ", minor=" + minor + ", patch="
               + patch + ", snapshot=" + snapshot + '}';
    }
}
