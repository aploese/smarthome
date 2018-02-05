/**
 * Copyright (c) 2014,2017 Contributors to the Eclipse Foundation
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
package org.eclipse.smarthome.binding.fhz4j.handler;

import static org.eclipse.smarthome.binding.fhz4j.FHZ4JBindingConstants.*;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ibapl.fhz4j.protocol.fht.Fht80bMode;
import de.ibapl.fhz4j.protocol.fht.Fht80bWarning;
import de.ibapl.fhz4j.protocol.fht.FhtDateMessage;
import de.ibapl.fhz4j.protocol.fht.FhtMessage;
import de.ibapl.fhz4j.protocol.fht.FhtModeMessage;
import de.ibapl.fhz4j.protocol.fht.FhtProperty;
import de.ibapl.fhz4j.protocol.fht.FhtTempMessage;
import de.ibapl.fhz4j.protocol.fht.FhtTimeMessage;
import de.ibapl.fhz4j.protocol.fht.FhtTimesMessage;
import de.ibapl.fhz4j.protocol.fht.FhtValvePosMessage;
import de.ibapl.fhz4j.protocol.fht.FhtWarningMessage;

/**
 * The {@link RadiatorFht80bHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author aploese@gmx.de - Initial contribution
 */
public class RadiatorFht80bHandler extends BaseThingHandler {
    protected ThingStatusDetail fht80HandlerStatus = ThingStatusDetail.HANDLER_CONFIGURATION_PENDING;

    private final Logger logger = LoggerFactory.getLogger(RadiatorFht80bHandler.class);

    private short housecode;

    public RadiatorFht80bHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        switch (channelUID.getId()) {
            case CHANNEL_TEMPERATURE_DESIRED:
                if (command instanceof DecimalType) {
                    try {
                        ((SpswBridgeHandler) (getBridge().getHandler())).sendFhtMessage(housecode,
                                FhtProperty.DESIRED_TEMP, ((DecimalType) command).floatValue());
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else if (command instanceof RefreshType) {
                    // updateState(new ChannelUID(getThing().getUID(), CHANNEL_TEMPERATURE), new DecimalType(00.00));
                }
                break;
            case CHANNEL_TEMPERATURE_DAY:
                if (command instanceof DecimalType) {
                    try {
                        ((SpswBridgeHandler) (getBridge().getHandler())).sendFhtMessage(housecode, FhtProperty.DAY_TEMP,
                                ((DecimalType) command).floatValue());
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else if (command instanceof RefreshType) {
                    // updateState(new ChannelUID(getThing().getUID(), CHANNEL_TEMPERATURE), new DecimalType(00.00));
                }
                break;
            case CHANNEL_TEMPERATURE_NIGHT:
                if (command instanceof DecimalType) {
                    try {
                        ((SpswBridgeHandler) (getBridge().getHandler())).sendFhtMessage(housecode,
                                FhtProperty.NIGHT_TEMP, ((DecimalType) command).floatValue());
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else if (command instanceof RefreshType) {
                    // updateState(new ChannelUID(getThing().getUID(), CHANNEL_TEMPERATURE), new DecimalType(00.00));
                }
                break;
            case CHANNEL_TEMPERATURE_WINDOW_OPEN:
                if (command instanceof DecimalType) {
                    try {
                        ((SpswBridgeHandler) (getBridge().getHandler())).sendFhtMessage(housecode,
                                FhtProperty.WINDOW_OPEN_TEMP, ((DecimalType) command).floatValue());
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else if (command instanceof RefreshType) {
                    // updateState(new ChannelUID(getThing().getUID(), CHANNEL_TEMPERATURE), new DecimalType(00.00));
                }
                break;
            case CHANNEL_MONDAY:
                if (command instanceof StringType) {
                    sendCycle(DayOfWeek.MONDAY, (StringType) command);
                }
                break;
            case CHANNEL_TUESDAY:
                if (command instanceof StringType) {
                    sendCycle(DayOfWeek.TUESDAY, (StringType) command);
                }
                break;
            case CHANNEL_WEDNESDAY:
                if (command instanceof StringType) {
                    sendCycle(DayOfWeek.WEDNESDAY, (StringType) command);
                }
                break;
            case CHANNEL_THURSDAY:
                if (command instanceof StringType) {
                    sendCycle(DayOfWeek.THURSDAY, (StringType) command);
                }
                break;
            case CHANNEL_FRIDAY:
                if (command instanceof StringType) {
                    sendCycle(DayOfWeek.FRIDAY, (StringType) command);
                }
                break;
            case CHANNEL_SATURDAY:
                if (command instanceof StringType) {
                    sendCycle(DayOfWeek.SATURDAY, (StringType) command);
                }
                break;
            case CHANNEL_SUNDAY:
                if (command instanceof StringType) {
                    sendCycle(DayOfWeek.SUNDAY, (StringType) command);
                }
                break;
            case CHANNEL_VALVE_POSITION:
                if (command instanceof RefreshType) {
                    // updateState(new ChannelUID(getThing().getUID(), CHANNEL_VALVE_POSITION), new DecimalType(22.22));
                }
                break;
            default:
                logger.error("Unknown Fht80 (" + housecode + ") channel: " + channelUID.getId());
        }
    }

    private void sendCycle(DayOfWeek dayOfWeek, @NonNull StringType command) {
        String value = command.toString();

        // TIME_FORMATTER.parse("12:00-134:00 15:00-18:00", new ParsePosition(0)).query(LocalTime::from);
        String val = value.substring(0, 5);
        final LocalTime from1 = TIME_NOT_SET.equals(val) ? null : TIME_FORMATTER.parse(val, LocalTime::from);

        val = value.substring(6, 11);
        LocalTime to1 = TIME_NOT_SET.equals(val) ? null : TIME_FORMATTER.parse(val, LocalTime::from);
        val = value.substring(12, 17);
        LocalTime from2 = TIME_NOT_SET.equals(val) ? null : TIME_FORMATTER.parse(val, LocalTime::from);
        val = value.substring(18, 23);
        LocalTime to2 = TIME_NOT_SET.equals(val) ? null : TIME_FORMATTER.parse(val, LocalTime::from);

        try {
            ((SpswBridgeHandler) (getBridge().getHandler())).sendFhtMessage(housecode, dayOfWeek, from1, to1, from2,
                    to2);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void initialize() {
        logger.debug("thing {} is initializing", this.thing.getUID());
        Configuration configuration = getConfig();
        try {
            housecode = ((Number) configuration.get("housecode")).shortValue();
        } catch (Exception e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.HANDLER_INITIALIZING_ERROR, "Can't parse housecode");
            fht80HandlerStatus = ThingStatusDetail.HANDLER_INITIALIZING_ERROR;
            return;
        }

        Bridge bridge = getBridge();
        if (bridge == null) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "no bridge assigned");
            fht80HandlerStatus = ThingStatusDetail.CONFIGURATION_ERROR;
            return;
        } else {
            if (bridge.getStatus().equals(ThingStatus.ONLINE)) {
                updateStatus(ThingStatus.ONLINE);
                fht80HandlerStatus = ThingStatusDetail.NONE;
            } else {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_OFFLINE);
            }
        }
    }

    @Override
    public void dispose() {
    }

    public short getHousecode() {
        return housecode;
    }

    public void updateFromMsg(FhtMessage fhtMsg) {
        switch (fhtMsg.command) {
            case MODE:
                updateState(new ChannelUID(getThing().getUID(), CHANNEL_MODE),
                        new StringType(((FhtModeMessage) fhtMsg).mode.name()));
                break;
            case MONDAY_TIMES:
                update_FROM_TO(CHANNEL_MONDAY, (FhtTimesMessage) fhtMsg);
                break;
            case TUESDAY_TIMES:
                update_FROM_TO(CHANNEL_TUESDAY, (FhtTimesMessage) fhtMsg);
                break;
            case WEDNESDAY_TIMES:
                update_FROM_TO(CHANNEL_WEDNESDAY, (FhtTimesMessage) fhtMsg);
                break;
            case THURSDAY_TIMES:
                update_FROM_TO(CHANNEL_THURSDAY, (FhtTimesMessage) fhtMsg);
                break;
            case FRIDAY_TIMES:
                update_FROM_TO(CHANNEL_FRIDAY, (FhtTimesMessage) fhtMsg);
                break;
            case SATURDAYDAY_TIMES:
                update_FROM_TO(CHANNEL_SATURDAY, (FhtTimesMessage) fhtMsg);
                break;
            case SUNDAYDAY_TIMES:
                update_FROM_TO(CHANNEL_SUNDAY, (FhtTimesMessage) fhtMsg);
                break;
            case WARNINGS:
                final Set<Fht80bWarning> warnings = ((FhtWarningMessage) fhtMsg).warnings;
                if (warnings.contains(Fht80bWarning.BATT_LOW)) {
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_BATT_LOW), OnOffType.ON);
                } else {
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_BATT_LOW), OnOffType.OFF);
                }
                break;
            case DAY_TEMP:
                updateState(new ChannelUID(getThing().getUID(), CHANNEL_TEMPERATURE_DAY),
                        new DecimalType(((FhtTempMessage) fhtMsg).temp));
                break;
            case NIGHT_TEMP:
                updateState(new ChannelUID(getThing().getUID(), CHANNEL_TEMPERATURE_NIGHT),
                        new DecimalType(((FhtTempMessage) fhtMsg).temp));
                break;
            case WINDOW_OPEN_TEMP:
                updateState(new ChannelUID(getThing().getUID(), CHANNEL_TEMPERATURE_WINDOW_OPEN),
                        new DecimalType(((FhtTempMessage) fhtMsg).temp));
                break;
            case MANU_TEMP:
                break;
            case HOLIDAY_END_DATE:
                updateHolidays((FhtDateMessage) fhtMsg);
                updateState(new ChannelUID(getThing().getUID(), CHANNEL_MODE),
                        new StringType(Fht80bMode.HOLIDAY.name()));
                break;
            case PARTY_END_TIME:
                updateState(new ChannelUID(getThing().getUID(), CHANNEL_PARTY_END_TIME),
                        new StringType((((FhtTimeMessage) fhtMsg).time.format(TIME_FORMATTER))));
                updateState(new ChannelUID(getThing().getUID(), CHANNEL_MODE), new StringType(Fht80bMode.PARTY.name()));
                break;
            case VALVE:
                updateState(new ChannelUID(getThing().getUID(), CHANNEL_VALVE_POSITION),
                        new DecimalType((((FhtValvePosMessage) fhtMsg).position)));
                updateState(new ChannelUID(getThing().getUID(), CHANNEL_VALVE_ALLOW_LOW_BATT_BEEP),
                        ((FhtValvePosMessage) fhtMsg).allowLowBatteryBeep ? OnOffType.ON : OnOffType.OFF);
                break;
            case MEASURED_TEMP:
                updateState(new ChannelUID(getThing().getUID(), CHANNEL_TEMPERATURE_MEASURED),
                        new DecimalType((((FhtTempMessage) fhtMsg).temp)));
                break;
            case DESIRED_TEMP:
                updateState(new ChannelUID(getThing().getUID(), CHANNEL_TEMPERATURE_DESIRED),
                        new DecimalType((((FhtTempMessage) fhtMsg).temp)));
                break;

            default:
                break;
        }
    }

    private void updateHolidays(FhtDateMessage fhtMsg) {
        String result = fhtMsg.day + "." + fhtMsg.month;
        updateState(new ChannelUID(getThing().getUID(), CHANNEL_HOLLYDAY_END_DATE), new StringType((result)));
    }

    private final static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private final static String TIME_NOT_SET = "XX:XX";

    private void update_FROM_TO(@NonNull String channelFromTo, FhtTimesMessage timesMessage) {
        StringBuilder sb = new StringBuilder();
        if (timesMessage.timeFrom1 != null) {
            TIME_FORMATTER.formatTo(timesMessage.timeFrom1, sb);
        } else {
            sb.append(TIME_NOT_SET);
        }
        sb.append('-');
        if (timesMessage.timeTo1 != null) {
            TIME_FORMATTER.formatTo(timesMessage.timeTo1, sb);
        } else {
            sb.append(TIME_NOT_SET);
        }
        sb.append(' ');
        if (timesMessage.timeFrom2 != null) {
            TIME_FORMATTER.formatTo(timesMessage.timeFrom2, sb);
        } else {
            sb.append(TIME_NOT_SET);
        }
        sb.append('-');
        if (timesMessage.timeTo2 != null) {
            TIME_FORMATTER.formatTo(timesMessage.timeTo2, sb);
        } else {
            sb.append(TIME_NOT_SET);
        }

        updateState(new ChannelUID(getThing().getUID(), channelFromTo), new StringType(sb.toString()));
    }

}
