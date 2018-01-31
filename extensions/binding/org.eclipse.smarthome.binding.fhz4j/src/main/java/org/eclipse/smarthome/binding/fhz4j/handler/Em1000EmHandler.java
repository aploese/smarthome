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

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ibapl.fhz4j.protocol.em.EmMessage;

/**
 * The {@link Em1000EmHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author aploese@gmx.de - Initial contribution
 */
public class Em1000EmHandler extends BaseThingHandler {
    protected ThingStatusDetail owHandlerStatus = ThingStatusDetail.HANDLER_CONFIGURATION_PENDING;

    private final Logger logger = LoggerFactory.getLogger(Em1000EmHandler.class);

    private short address;

    public Em1000EmHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // no-op
    }

    @Override
    public void initialize() {
        logger.debug("thing {} is initializing", this.thing.getUID());
        Configuration configuration = getConfig();
        try {
            address = ((Number) configuration.get("address")).shortValue();
        } catch (Exception e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.HANDLER_INITIALIZING_ERROR, "Can't parse housecode");
            return;
        }

        Bridge bridge = getBridge();
        if (bridge == null) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "no bridge assigned");
            owHandlerStatus = ThingStatusDetail.CONFIGURATION_ERROR;
            return;
        } else {
            if (bridge.getStatus().equals(ThingStatus.ONLINE)) {
                updateStatus(ThingStatus.ONLINE);
                owHandlerStatus = ThingStatusDetail.NONE;
            } else {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_OFFLINE);
            }
        }
    }

    @Override
    public void dispose() {
    }

    public short getAddress() {
        return address;
    }

    public void updateFromMsg(EmMessage emMsg) {
        switch (emMsg.emDeviceType) {
            case EM_1000_EM:
                updateState(new ChannelUID(getThing().getUID(), CHANNEL_ENERGY_TOTAL),
                        new DecimalType(EmMessage.EM_1000_EM_ENERY * emMsg.valueCummulated));
                updateState(new ChannelUID(getThing().getUID(), CHANNEL_POWER_5MINUTES),
                        new DecimalType(EmMessage.EM_1000_EM_POWER * emMsg.value5Min));
                updateState(new ChannelUID(getThing().getUID(), CHANNEL_MAX_POWER_5MINUTES),
                        new DecimalType(EmMessage.EM_1000_EM_POWER * emMsg.value5MinPeak));
                break;
            // case EM_1000_S:
            // case EM_1000_GZ:
            default:
                throw new RuntimeException("Cant handle EM 1000 Device: " + emMsg.emDeviceType);
        }
    }

}
