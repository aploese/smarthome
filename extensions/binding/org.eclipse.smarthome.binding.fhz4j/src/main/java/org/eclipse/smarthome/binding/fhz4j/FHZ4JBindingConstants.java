/**
 * Copyright (c) 2014,2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.smarthome.binding.fhz4j;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link FHZ4JBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author aploese@gmx.de - Initial contribution
 */
@NonNullByDefault
public class FHZ4JBindingConstants {

    public static final String BINDING_ID = "fhz4j";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_FHZ4J_RADIATOR_FHT80B = new ThingTypeUID(BINDING_ID, "fht80b");
    public static final ThingTypeUID THING_TYPE_FHZ4J_UNKNOWN = new ThingTypeUID(BINDING_ID, "unknown");
    public static final ThingTypeUID THING_TYPE_FHZ4J_EM_1000_EM = new ThingTypeUID(BINDING_ID, "em-1000-em");
    public static final ThingTypeUID THING_TYPE_FHZ4J_HMS_100_TF = new ThingTypeUID(BINDING_ID, "hms-100-tf");

    // List of all Bridge Type UIDs
    public static final ThingTypeUID BRIDGE_TYPE_FHZ4J_RS232 = new ThingTypeUID(BINDING_ID, "rs232-bridge-cul");

    // List of all Channel ids
    public static final String CHANNEL_MODE = "mode";
    public static final String CHANNEL_BATT_LOW = "low-battery";
    public static final String CHANNEL_TEMPERATURE_MEASURED = "temperatureMeasured";
    public static final String CHANNEL_HUMIDITY_MEASURED = "humidityMeasured";
    public static final String CHANNEL_TEMPERATURE_DESIRED = "temperatureDesired";
    public static final String CHANNEL_TEMPERATURE_DAY = "temperatureDay";
    public static final String CHANNEL_TEMPERATURE_NIGHT = "temperatureNight";
    public static final String CHANNEL_TEMPERATURE_WINDOW_OPEN = "temperatureWindowOpen";
    public static final String CHANNEL_VALVE_POSITION = "valvePos";
    public static final String CHANNEL_VALVE_ALLOW_LOW_BATT_BEEP = "valveAllowLowBattBeep";

    public static final String CHANNEL_HOLLYDAY_END_DATE = "hollydayEndDate";
    public static final String CHANNEL_PARTY_END_TIME = "partyEndTime";

    public static final String CHANNEL_MONDAY = "monday";
    public static final String CHANNEL_TUESDAY = "tuesday";
    public static final String CHANNEL_WEDNESDAY = "wednesday";
    public static final String CHANNEL_THURSDAY = "thursday";
    public static final String CHANNEL_FRIDAY = "friday";
    public static final String CHANNEL_SATURDAY = "saturday";
    public static final String CHANNEL_SUNDAY = "sunday";

    public static final String CHANNEL_ENERGY_TOTAL = "energyTotal";
    public static final String CHANNEL_POWER_5MINUTES = "power5Minutes";
    public static final String CHANNEL_MAX_POWER_5MINUTES = "maxPower5Minutes";
}
