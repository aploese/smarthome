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
package org.eclipse.smarthome.binding.fhz4j.internal.discovery;

import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.binding.fhz4j.FHZ4JBindingConstants;
import org.eclipse.smarthome.binding.fhz4j.handler.SpswBridgeHandler;
import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ibapl.fhz4j.api.FhzDataListener;
import de.ibapl.fhz4j.parser.cul.CulMessage;
import de.ibapl.fhz4j.protocol.em.EmMessage;
import de.ibapl.fhz4j.protocol.fht.FhtMessage;
import de.ibapl.fhz4j.protocol.fs20.FS20Message;
import de.ibapl.fhz4j.protocol.hms.HmsMessage;
import de.ibapl.fhz4j.protocol.lacrosse.tx2.LaCrosseTx2Message;

/**
 *
 * @author aploese@gmx.de - Initial contribution
 */
@NonNullByDefault
public class FHZ4JDiscoveryService extends AbstractDiscoveryService implements FhzDataListener {

    private final Logger logger = LoggerFactory.getLogger(FHZ4JDiscoveryService.class);

    private final static int SEARCH_TIME = 120;

    private final SpswBridgeHandler spswBridgeHandler;

    public FHZ4JDiscoveryService(SpswBridgeHandler spswBridgeHandler) {
        super(SpswBridgeHandler.SUPPORTED_THING_TYPES_UIDS, SEARCH_TIME, false);
        this.spswBridgeHandler = spswBridgeHandler;
    }

    @Override
    public Set<ThingTypeUID> getSupportedThingTypes() {
        return SpswBridgeHandler.SUPPORTED_THING_TYPES_UIDS;
    }

    @Override
    public void startScan() {
        spswBridgeHandler.setDiscoveryListener(this);
    }

    @Override
    protected synchronized void stopScan() {
        super.stopScan();
        spswBridgeHandler.setDiscoveryListener(null);
        removeOlderResults(getTimestampOfLastScan());
    }

    @Override
    public void deactivate() {
        stopScan();
    }

    private void addFhtDevice(short housecode) {
        final ThingUID bridgeUID = spswBridgeHandler.getThing().getUID();
        final String deviceIdStr = Short.toString(housecode);
        final ThingUID thingUID = getThingUID(deviceIdStr, bridgeUID,
                FHZ4JBindingConstants.THING_TYPE_FHZ4J_RADIATOR_FHT80B);

        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(thingUID)
                .withThingType(FHZ4JBindingConstants.THING_TYPE_FHZ4J_RADIATOR_FHT80B)
                .withProperty("housecode", housecode).withBridge(bridgeUID)
                .withRepresentationProperty(Short.toString(housecode)).withLabel("FHT80b " + housecode).build();

        thingDiscovered(discoveryResult);
    }

    private ThingUID getThingUID(String deviceId, ThingUID bridgeUID, ThingTypeUID thingTypeUID) {
        return new ThingUID(thingTypeUID, bridgeUID, deviceId);
    }

    @Override
    public void emDataParsed(@Nullable EmMessage arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void failed(@Nullable Throwable arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void fhtDataParsed(@Nullable FhtMessage fhtMsg) {
        addFhtDevice(fhtMsg.housecode);
    }

    @Override
    public void fhtPartialDataParsed(@Nullable FhtMessage arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void fs20DataParsed(@Nullable FS20Message arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void hmsDataParsed(@Nullable HmsMessage arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void laCrosseTxParsed(@Nullable LaCrosseTx2Message arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void culMessageParsed(@Nullable CulMessage arg0) {
        // TODO Auto-generated method stub

    }

}
