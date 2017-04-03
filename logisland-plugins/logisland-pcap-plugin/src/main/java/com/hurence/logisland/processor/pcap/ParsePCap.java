/**
 * Copyright (C) 2017 Hurence
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hurence.logisland.processor.pcap;

import com.hurence.logisland.annotation.documentation.CapabilityDescription;
import com.hurence.logisland.annotation.documentation.Tags;
import com.hurence.logisland.component.PropertyDescriptor;
import com.hurence.logisland.processor.*;
import com.hurence.logisland.record.*;
import com.hurence.logisland.validator.StandardValidators;

import org.krakenapps.pcap.decoder.ip.Ipv4Packet;
import org.krakenapps.pcap.decoder.tcp.TcpPacket;
import org.krakenapps.pcap.decoder.udp.UdpPacket;
import org.krakenapps.pcap.file.GlobalHeader;
import org.krakenapps.pcap.packet.PacketHeader;
import org.krakenapps.pcap.packet.PcapPacket;
import org.krakenapps.pcap.util.Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.EnumMap;

import static com.hurence.logisland.processor.pcap.PcapHelper.ETHERNET_DECODER;

/**
 * PCap processor
 */
@Tags({"PCap", "security", "IDS", "NIDS"})
@CapabilityDescription(
        "The PCap processor is the LogIsland entry point to parse network packets captured in pcap format. "
        +"The PCap processor decodes the bytes of the incoming pcap record, where a Global pcap header followed by a sequence of [packet header, packet data] pairs are stored. "
        +"Each incoming pcap event is parsed into n packet records where packet headers data is made available in dedicated fields."
    )

public class ParsePCap extends AbstractProcessor {

    private static Logger logger = LoggerFactory.getLogger(ParsePCap.class);

    private boolean debug = false;
    
    private static final String DEBUG_PROPERTY_NAME = "debug";
    
    public static final PropertyDescriptor DEBUG = new PropertyDescriptor.Builder()
            .name(DEBUG_PROPERTY_NAME)
            .description("Enable debug. If enabled, the original JSON string is embedded in the record_value field of the record.")
            .addValidator(StandardValidators.BOOLEAN_VALIDATOR)
            .required(false)
            .build();

    @Override
    public void init(final ProcessContext context)
    {
        logger.debug("Initializing PCap Processor");
    }
    
    @Override
    public List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        
        final List<PropertyDescriptor> descriptors = new ArrayList<>();
        descriptors.add(DEBUG);

        return Collections.unmodifiableList(descriptors);
    }
  
    @Override
    public Collection<Record> process(ProcessContext context, Collection<Record> records) {

        if (debug) {
            logger.debug("PCap Processor records input: " + records);
        }

        /**
         * Get the original PCap event as Bytes and do some parsing
         */
        List<Record> outputRecords = new ArrayList<>();
        records.forEach(record -> {
            final Long pcapTimestamp = record.getField(FieldDictionary.RECORD_KEY).asLong();
            final byte[] pcapRawValue = (byte[]) record.getField(FieldDictionary.RECORD_VALUE).getRawValue();

            try {
                LogIslandEthernetDecoder decoder = ETHERNET_DECODER.get();

                PcapByteInputStream pcapByteInputStream = new PcapByteInputStream(pcapRawValue);

                GlobalHeader globalHeader = pcapByteInputStream.getGlobalHeader();

                if (globalHeader.getMagicNumber() != 0xA1B2C3D4 && globalHeader.getMagicNumber() != 0xD4C3B2A1) {
                    throw new InvalidPCapFileException("Invalid pcap file format : Unable to parse the global header magic number");
                }

                while (true) {
                    try {
                        PcapPacket packet = pcapByteInputStream.getPacket();
                        // int packetCounter = 0;
                        // PacketHeader packetHeader = null;
                        // Ipv4Packet ipv4Packet = null;
                        TcpPacket tcpPacket = null;
                        UdpPacket udpPacket = null;
                        // Buffer packetDataBuffer = null;
                        int sourcePort = 0;
                        int destinationPort = 0;

                        // LOG.trace("Got packet # " + ++packetCounter);
                        // LOG.trace(packet.getPacketData());

                        decoder.decode(packet);

                        PacketHeader packetHeader = packet.getPacketHeader();
                        Ipv4Packet ipv4Packet = Ipv4Packet.parse(packet.getPacketData());

                        StandardRecord outputRecord = new StandardRecord();

                        outputRecord.setField(new Field(FieldDictionary.RECORD_KEY, FieldType.LONG, pcapTimestamp));
                        outputRecord.setField(new Field(FieldDictionary.RECORD_TYPE, FieldType.STRING, "network_packet"));
                        outputRecord.setField(new Field(FieldDictionary.RECORD_RAW_VALUE, FieldType.BYTES, packet));
                        outputRecord.setField(new Field(FieldDictionary.PROCESSOR_NAME, FieldType.STRING, this.getClass().getSimpleName()));

                        if (ipv4Packet.getVersion() == Constants.PROTOCOL_IPV4) {
                            if (ipv4Packet.getProtocol() == Constants.PROTOCOL_TCP) {
                                tcpPacket = TcpPacket.parse(ipv4Packet);

                            } else if (ipv4Packet.getProtocol() == Constants.PROTOCOL_UDP) {

                                Buffer packetDataBuffer = ipv4Packet.getData();
                                sourcePort = packetDataBuffer.getUnsignedShort();
                                destinationPort = packetDataBuffer.getUnsignedShort();

                                udpPacket = new UdpPacket(ipv4Packet, sourcePort, destinationPort);

                                udpPacket.setLength(packetDataBuffer.getUnsignedShort());
                                udpPacket.setChecksum(packetDataBuffer.getUnsignedShort());
                                packetDataBuffer.discardReadBytes();
                                udpPacket.setData(packetDataBuffer);
                            } else {
                                outputRecord.addError(ProcessError.NOT_IMPLEMENTED_ERROR.getName(), "Not Implemented protocol inside ipv4 packet : only TCP and UDP protocols are handled so far.");
                            }
                        } else {
                            outputRecord.addError(ProcessError.NOT_IMPLEMENTED_ERROR.getName(), "Not Implemented protocol : only IPv4 protocol (TCP & UDP) is handled so far.");
                        }

                        PacketInfo pi = new PacketInfo(globalHeader, packetHeader, packet, ipv4Packet, tcpPacket, udpPacket);
                        EnumMap<PCapConstants.Fields, Object> result = PcapHelper.packetToFields(pi);

                        for (PCapConstants.Fields field : PCapConstants.Fields.values()) {
                            if (result.containsKey(field)) {
                                outputRecord.setField(new Field(field.getName(), field.getFieldType(), result.get(field)));
                            }
                        }

                        outputRecords.add(outputRecord);

                    } catch (NegativeArraySizeException ignored) {
                        logger.debug("Ignorable exception while parsing packet.", ignored);
                    } catch (EOFException eof) {
                        // Ignore exception and break : the while loop is left when eof is reached
                        break;
                    }
                }
            }
            catch (InvalidPCapFileException e) {
                StandardRecord outputRecord = new StandardRecord();
                outputRecord.addError(ProcessError.INVALID_FILE_FORMAT_ERROR.getName(), e.getMessage());
                outputRecord.setField(new Field(FieldDictionary.RECORD_KEY, FieldType.LONG, pcapTimestamp));
                outputRecord.setField(new Field(FieldDictionary.RECORD_VALUE, FieldType.BYTES, pcapRawValue));
                outputRecords.add(outputRecord);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return outputRecords;
    }
    
    /**
     * Deeply clones the passed map regarding keys (so that one can modify keys of the original map without changing
     * the clone).
     * @param origMap Map to clone.
     * @return Cloned map.
     */
    private static Map<String, Object> cloneMap(Map<String, Object> origMap)
    {
        Map<String, Object> finalMap = new HashMap<String, Object>();
        origMap.forEach( (key, value) -> {
            if (value instanceof Map)
            {
                Map<String, Object> map = (Map<String, Object>)value;
                finalMap.put(key, (Object)cloneMap(map)); 
            } else
            {
                finalMap.put(key, value);
            }
        });
        return finalMap;
    }

}