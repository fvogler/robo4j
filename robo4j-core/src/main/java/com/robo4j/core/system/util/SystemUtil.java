/*
 * Copyright (C) 2016. Miroslav Kopecky
 * This SystemUtil.java is part of robo4j.
 *
 *     robo4j is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     robo4j is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with robo4j .  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.robo4j.core.system.util;

import com.robo4j.core.system.LegoSystemEnum;

/**
 * Created by miroslavkopecky on 04/05/16.
 */
public final class SystemUtil {

    public static <Source extends LegoSystemEnum<Type>, Type> Type getType(Source sourceEnum){
        return sourceEnum.getType();
    }

}
