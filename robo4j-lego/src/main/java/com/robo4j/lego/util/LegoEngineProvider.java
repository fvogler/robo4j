/*
 * Copyright (C)  2016. Miroslav Wengner and Marcus Hirt
 * This LegoEngineBaseProvider.java  is part of robo4j.
 *
 *  robo4j is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  robo4j is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with robo4j .  If not, see <http://www.gnu.org/licenses/>.
 */

package com.robo4j.lego.util;

import java.util.Map;
import java.util.stream.Collectors;

import com.robo4j.commons.annotation.RoboProvider;
import com.robo4j.commons.motor.GenericMotor;
import com.robo4j.commons.registry.BaseRegistryProvider;
import com.robo4j.lego.control.LegoEngine;
import com.robo4j.lego.exception.LegoException;
import com.robo4j.lego.motor.LegoEngineWrapper;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.robotics.RegulatedMotor;

/**
 * Provider is responsible for providing Engine
 *
 * @author Miro Kopecky (@miragemiko)
 * @since 26.11.2016
 */
@RoboProvider(id = "engineProvider")
public final class LegoEngineProvider<Type extends LegoEngine> implements BaseRegistryProvider<RegulatedMotor, Type> {
	private static final int DEFAULT_SPEED = 300;

	@Override
	public RegulatedMotor create(final LegoEngine engine) {
		final RegulatedMotor result;
		switch (engine.getEngine()) {
		case LARGE:
			result = new EV3LargeRegulatedMotor(LocalEV3.get().getPort(engine.getPort().getType()));
			break;
		case MEDIUM:
			result = new EV3MediumRegulatedMotor(LocalEV3.get().getPort(engine.getPort().getType()));
			break;
		case NXT:
			result = new NXTRegulatedMotor(LocalEV3.get().getPort(engine.getPort().getType()));
			break;
		default:
			throw new LegoException("Lego engine not supported: " + engine);
		}
		result.setSpeed(DEFAULT_SPEED);
		return result;
	}

	@SuppressWarnings(value = "unchecked")
	@Override
	public Map<String, Type> activate(Map<String, Type> engines) {
		return engines.entrySet().stream().peek(e -> {
			GenericMotor le = e.getValue();
			/* always instance of regulated motor */
			if (le instanceof LegoEngineWrapper) {
				RegulatedMotor rm = create((LegoEngineWrapper) le);
				((LegoEngineWrapper) le).setUnit(rm);
			}
		}).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

}
