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

import static org.eclipse.smarthome.binding.fhz4j.FHZ4JBindingConstants.THING_TYPE_FHZ4J_RADIATOR_FHT80B;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ibapl.fhz4j.api.FhzDataListener;
import de.ibapl.fhz4j.api.FhzMessage;
import de.ibapl.fhz4j.parser.cul.CulMessage;
import de.ibapl.fhz4j.parser.cul.CulParser;
import de.ibapl.fhz4j.parser.cul.CulWriter;
import de.ibapl.fhz4j.protocol.em.EmMessage;
import de.ibapl.fhz4j.protocol.fht.FhtMessage;
import de.ibapl.fhz4j.protocol.fht.FhtProperty;
import de.ibapl.fhz4j.protocol.fs20.FS20Message;
import de.ibapl.fhz4j.protocol.hms.HmsMessage;
import de.ibapl.fhz4j.protocol.lacrosse.tx2.LaCrosseTx2Message;
import de.ibapl.spsw.api.SerialPortSocket;
import de.ibapl.spsw.api.SerialPortSocketFactory;

/**
 *
 * @author aploese@gmx.de - Initial contribution
 */
public class SpswBridgeHandler extends BaseBridgeHandler implements FhzDataListener {

    private SerialPortSocketFactory serialPortSocketFactory;

    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Stream.of(THING_TYPE_FHZ4J_RADIATOR_FHT80B)
            .collect(Collectors.toSet());

    private static final String PORT_PARAM = "port";
    private static final String REFRESH_RATE_PARAM = "refreshrate";
    private static final String HOUSE_CODE_PARAM = "housecode";

    private final Logger logger = LoggerFactory.getLogger(SpswBridgeHandler.class);

    private String port;
    private short housecode;
    private int refreshRate;
    private ScheduledFuture<?> refreshJob;
    private SerialPortSocket serialPortSocket;
    private CulParser<FhzMessage> culParser;
    private CulWriter culWriter;
    private final Map<Short, RadiatorFht80bHandler> fhtThingHandler = new HashMap<>();
    private final Map<Short, Hms100TfHandler> hmsThingHandler = new HashMap<>();
    private final Map<Short, Em1000EmHandler> emThingHandler = new HashMap<>();
    private FhzDataListener discoveryListener;

    public SpswBridgeHandler(Bridge bridge, SerialPortSocketFactory serialPortSocketFactory) {
        super(bridge);
        this.serialPortSocketFactory = serialPortSocketFactory;
        // TODO Auto-generated constructor stub
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // TODO Auto-generated method stub

    }

    @Override
    public void childHandlerInitialized(ThingHandler childHandler, Thing childThing) {
        super.childHandlerInitialized(childHandler, childThing);
        if (childHandler instanceof RadiatorFht80bHandler) {
            final RadiatorFht80bHandler rfh = (RadiatorFht80bHandler) childHandler;
            fhtThingHandler.put(rfh.getHousecode(), rfh);
            logger.info("Added FHT 80B " + rfh.getHousecode());
            try {
                culWriter.initFhtReporting(rfh.getHousecode());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (childHandler instanceof Em1000EmHandler) {
            final Em1000EmHandler emh = (Em1000EmHandler) childHandler;
            emThingHandler.put(emh.getAddress(), emh);
            logger.info("Added EM 1000 EM " + emh.getAddress());
        } else if (childHandler instanceof Hms100TfHandler) {
            final Hms100TfHandler hmsh = (Hms100TfHandler) childHandler;
            hmsThingHandler.put(hmsh.getHousecode(), hmsh);
            logger.info("Added HMS 100 TF " + hmsh.getHousecode());
        } else {
            // TODO
        }
    }

    @Override
    public void childHandlerDisposed(ThingHandler childHandler, Thing childThing) {
        super.childHandlerDisposed(childHandler, childThing);
        if (childHandler instanceof RadiatorFht80bHandler) {
            final RadiatorFht80bHandler rfh = (RadiatorFht80bHandler) childHandler;
            fhtThingHandler.remove(rfh.getHousecode());
        } else if (childHandler instanceof Em1000EmHandler) {
            final Em1000EmHandler emh = (Em1000EmHandler) childHandler;
            emThingHandler.remove(emh.getAddress());
        } else if (childHandler instanceof Hms100TfHandler) {
            final Hms100TfHandler hmsh = (Hms100TfHandler) childHandler;
            hmsThingHandler.remove(hmsh.getHousecode());
        } else {
            // TODO
        }
    }

    @Override
    public void initialize() {
        logger.debug("Initializing SpswBridgeHandler.");

        Configuration config = getThing().getConfiguration();

        port = (String) config.get(PORT_PARAM);

        housecode = ((BigDecimal) config.get(HOUSE_CODE_PARAM)).shortValue();

        refreshRate = ((BigDecimal) config.get(REFRESH_RATE_PARAM)).intValue();

        serialPortSocket = serialPortSocketFactory.createSerialPortSocket(port);

        fhtThingHandler.clear();
        emThingHandler.clear();
        hmsThingHandler.clear();

        try {
            culParser = new CulParser<FhzMessage>(this);
            culWriter = new CulWriter();
            CulParser.openPort(serialPortSocket);
            culParser.setInputStream(serialPortSocket.getInputStream());
            culWriter.setOutputStream(serialPortSocket.getOutputStream());
            culWriter.initFhz(housecode);
        } catch (Exception e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
            serialPortSocket = null;
            return;
        }

        refreshJob = scheduler.scheduleWithFixedDelay(() -> {
            try {
                culWriter.initFhtReporting(fhtThingHandler.keySet());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }, 0, refreshRate, TimeUnit.DAYS);

        updateStatus(ThingStatus.ONLINE);

    }

    @Override
    public void dispose() {
        if (refreshJob != null) {
            refreshJob.cancel(true);
        }
        final CulParser<FhzMessage> cp = culParser;
        if (cp != null) {
            try {
                cp.close();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        culParser = null;
        culWriter = null;
        if (serialPortSocket != null) {
            try {
                serialPortSocket.close();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        serialPortSocket = null;
        fhtThingHandler.clear();
        emThingHandler.clear();
        hmsThingHandler.clear();
    }

    @Override
    public void emDataParsed(EmMessage emMsg) {
        final Em1000EmHandler emh = emThingHandler.get(emMsg.address);
        if (emh == null) {
            // Discovery
            if (discoveryListener != null) {
                discoveryListener.emDataParsed(emMsg);
            }
            return;
        }
        emh.updateFromMsg(emMsg);
    }

    @Override
    public void failed(Throwable arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void fhtDataParsed(FhtMessage fhtMsg) {
        final RadiatorFht80bHandler rfh = fhtThingHandler.get(fhtMsg.housecode);
        if (rfh == null) {
            // Discovery
            if (discoveryListener != null) {
                discoveryListener.fhtDataParsed(fhtMsg);
            }
            return;
        }
        rfh.updateFromMsg(fhtMsg);
    }

    @Override
    public void fhtPartialDataParsed(FhtMessage fhtMsg) {
        // TODO Auto-generated method stub

    }

    @Override
    public void fs20DataParsed(FS20Message arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void hmsDataParsed(HmsMessage hmsMsg) {
        final Hms100TfHandler hmsh = hmsThingHandler.get(hmsMsg.housecode);
        if (hmsh == null) {
            // Discovery
            if (discoveryListener != null) {
                discoveryListener.hmsDataParsed(hmsMsg);
            }
            return;
        }
        hmsh.updateFromMsg(hmsMsg);
    }

    @Override
    public void laCrosseTxParsed(LaCrosseTx2Message arg0) {
        // TODO Auto-generated method stub

    }

    public FhzDataListener getDiscoveryListener() {
        return discoveryListener;
    }

    public void setDiscoveryListener(FhzDataListener discoveryListener) {
        this.discoveryListener = discoveryListener;
    }

    public void sendFhtMessage(short housecode, FhtProperty fhtProperty, float value) throws IOException {
        culWriter.writeFht(housecode, fhtProperty, value);
    }

    public void sendFhtMessage(short housecode, DayOfWeek dayOfWeek, LocalTime from1, LocalTime to1, LocalTime from2,
            LocalTime to2) throws IOException {
        culWriter.writeFhtCycle(housecode, dayOfWeek, from1, to1, from2, to2);
    }

    @Override
    public void culMessageParsed(CulMessage arg0) {
        // TODO Auto-generated method stub

    }

}
