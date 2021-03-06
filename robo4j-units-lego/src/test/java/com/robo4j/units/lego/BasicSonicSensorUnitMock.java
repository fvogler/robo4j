/*
 * Copyright (C) 2014, 2017. Miroslav Wengner, Marcus Hirt
 * This SonicSensorUnitMock.java  is part of robo4j.
 * module: robo4j-units-lego
 *
 * robo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * robo4j is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with robo4j .  If not, see <http://www.gnu.org/licenses/>.
 */

package com.robo4j.units.lego;

import com.robo4j.core.AttributeDescriptor;
import com.robo4j.core.ConfigurationException;
import com.robo4j.core.RoboContext;
import com.robo4j.core.RoboResult;
import com.robo4j.core.configuration.Configuration;
import com.robo4j.hw.lego.enums.DigitalPortEnum;
import com.robo4j.hw.lego.enums.SensorTypeEnum;
import com.robo4j.hw.lego.wrapper.SensorTestWrapper;
import com.robo4j.units.lego.sensor.LegoSensorMessage;

/**
 * @author Marcus Hirt (@hirt)
 * @author Miro Wengner (@miragemiko)
 * @since 04.02.2017
 */
public class BasicSonicSensorUnitMock extends BasicSonicUnit {

    public BasicSonicSensorUnitMock(RoboContext context, String id) {
        super(context, id);
    }

    @Override
    protected void onInitialization(Configuration configuration) throws ConfigurationException {
        super.sensor = new SensorTestWrapper(DigitalPortEnum.S3, SensorTypeEnum.SONIC);
        System.out.println("on initialization");
    }

    @Override
    public void onMessage(LegoSensorMessage message) {
        System.out.println("onMessage : " + message);
    }

    @Override
    public void shutdown() {
        super.shutdown();
        System.out.println("executor is down");
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> R onGetAttribute(AttributeDescriptor<R> attribute) {
        if (attribute.getAttributeName().equals("getStatus") && attribute.getAttributeType() == Boolean.class) {
            return (R) Boolean.valueOf(true);
        }
        return null;
    }



}
