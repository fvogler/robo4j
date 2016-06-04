/*
 * Copyright (C) 2016. Miroslav Kopecky
 * This FrontHandCommandEnum.java is part of robo4j.
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

package com.robo4j.core.fronthand.command;

import com.robo4j.core.bridge.command.CommandTypeEnum;
import com.robo4j.core.control.RoboCoreCommand;
import com.robo4j.core.control.RoboTypeCommand;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by miroslavkopecky on 27/04/16.
 */
public enum FrontHandCommandEnum implements RoboCoreCommand, RoboTypeCommand {

    //@formatter:off
    EXIT            (0, "exit"),
    COMMAND         (1, "command"),
    ;
    //@formatter:on

    private int code;
    private String name;

    private volatile static Map<Integer, FrontHandCommandEnum> codeToLegoCommandTypeMapping;
    private volatile static Map<String, FrontHandCommandEnum> codeToLegoCommandNameMapping;


    FrontHandCommandEnum(int c, String name){
        this.code = c;
        this.name = name;
    }

    private static void initMapping(){
        codeToLegoCommandTypeMapping = new HashMap<>();
        codeToLegoCommandNameMapping = new HashMap<>();
        for(FrontHandCommandEnum ct: values()){
            codeToLegoCommandTypeMapping.put(ct.getCode(), ct);
            codeToLegoCommandNameMapping.put(ct.getName(), ct);
        }
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public CommandTypeEnum getType() {
        return CommandTypeEnum.HAND;
    }


    public static FrontHandCommandEnum getCommand(int code){
        if(codeToLegoCommandTypeMapping == null)
            initMapping();

        return codeToLegoCommandTypeMapping.get(code);
    }

    public static FrontHandCommandEnum getCommand(String code){
        if(codeToLegoCommandNameMapping == null)
            initMapping();

        return codeToLegoCommandNameMapping.get(code);
    }


}
