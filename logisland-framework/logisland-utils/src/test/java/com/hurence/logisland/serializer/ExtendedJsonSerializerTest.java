/**
 * Copyright (C) 2016 Hurence (support@hurence.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hurence.logisland.serializer;

import com.hurence.logisland.record.FieldType;
import com.hurence.logisland.record.Record;
import com.hurence.logisland.record.StandardRecord;
import org.junit.Test;

import java.io.*;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author tom
 */
public class ExtendedJsonSerializerTest {





    @Test
    public void someSchemaTest() throws Exception {
        final String recordStr = "{\n" +
                "  \"firstName\": \"John\",\n" +
                "  \"lastName\" : \"doe\",\n" +
                "  \"age\"      : 26,\n" +
                "  \"address\"  : {\n" +
                "    \"streetAddress\": \"naist street\",\n" +
                "    \"city\"         : \"Nara\",\n" +
                "    \"postalCode\"   : \"630-0192\"\n" +
                "  },\n" +
                "  \"phoneNumbers\": [\n" +
                "    {\n" +
                "      \"type\"  : \"iPhone\",\n" +
                "      \"number\": \"0123-4567-8888\",\n" +
                "      \"value\": \"1.023\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\"  : \"home\",\n" +
                "      \"number\": \"0123-4567-8910\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        final String schema = "{\n" +
                "  \"version\": 1,\n" +
                "  \"type\": \"record\",\n" +
                "  \"namespace\": \"com.hurence.logisland\",\n" +
                "  \"name\": \"Record\",\n" +
                "  \"fields\": [\n" +
                "    {\n" +
                "      \"name\": \"firstName\",\n" +
                "      \"type\": \"string\"\n" +
                "    }, {\n" +
                "      \"name\": \"lastName\",\n" +
                "      \"type\": \"string\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"age\",\n" +
                "      \"type\": \"long\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"address\",\n" +
                "      \"type\": {\n" +
                "        \"type\": \"record\",\n" +
                "        \"name\": \"Address\",\n" +
                "        \"fields\": [\n" +
                "            {\n" +
                "              \"name\": \"streetAddress\",\n" +
                "              \"type\": \"string\"\n" +
                "            }, {\n" +
                "              \"name\": \"city\",\n" +
                "              \"type\": \"string\"\n" +
                "            }\n" +
                "            ]\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"phoneNumbers\",\n" +
                "      \"type\": {\n" +
                "          \"type\": \"array\",\n" +
                "          \"items\": {\n" +
                "            \"type\": \"record\",\n" +
                "            \"name\": \"PhoneNumber\",\n" +
                "            \"fields\": [\n" +
                "                {\n" +
                "                  \"name\": \"type\",\n" +
                "                  \"type\": \"string\"\n" +
                "                }, \n" +
                "                {\n" +
                "                  \"name\": \"value\",\n" +
                "                  \"type\": \"double\"\n" +
                "                }\n" +
                "            ]\n" +
                "          }\n" +
                "        \n" +
                "      }\n" +
                "    }\n" +
                "    \n" +
                "  ]\n" +
                "}";




        final ExtendedJsonSerializer serializer = new ExtendedJsonSerializer(schema);
        ByteArrayInputStream bais = new ByteArrayInputStream(recordStr.getBytes());
        Record deserializedRecord = serializer.deserialize(bais);
        System.out.println(deserializedRecord);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        serializer.serialize(baos, deserializedRecord);
        baos.close();


        System.out.println(new String(baos.toByteArray()));
    }




    @Test
    public void validateArrays() throws IOException {

        final ExtendedJsonSerializer serializer = new ExtendedJsonSerializer();


        Record record = new StandardRecord("cisco");
        record.setId("firewall_record1");
        record.addError("fatal_error", "ouille");
        //record.setField("tags", FieldType.ARRAY, new ArrayList<>(Arrays.asList("spam", "filter", "mail")));


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        serializer.serialize(baos, record);
        baos.close();


        String strEvent = new String(baos.toByteArray());
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        Record deserializedRecord = serializer.deserialize(bais);

       // assertEquals(record.getAllFieldsSorted(), deserializedRecord.getAllFieldsSorted());

    }

    @Test
    public void validateJsonSerialization() throws IOException {

        final ExtendedJsonSerializer serializer = new ExtendedJsonSerializer();


        Record record = new StandardRecord("cisco");
        record.setId("firewall_record1");
        record.setField("timestamp", FieldType.LONG, new Date().getTime());
        record.setField("method", FieldType.STRING, "GET");
        record.setField("ip_source", FieldType.STRING, "123.34.45.123");
        record.setField("ip_target", FieldType.STRING, "255.255.255.255");
        record.setField("url_scheme", FieldType.STRING, "http");
        record.setField("url_host", FieldType.STRING, "origin-www.20minutes.fr");
        record.setField("url_port", FieldType.STRING, "80");
        record.setField("url_path", FieldType.STRING, "/r15lgc-100KB.js");
        record.setField("request_size", FieldType.INT, 1399);
        record.setField("response_size", FieldType.INT, 452);
        record.setField("is_outside_office_hours", FieldType.BOOLEAN, false);
        record.setField("is_host_blacklisted", FieldType.BOOLEAN, false);
        //record.setField("tags", FieldType.ARRAY, new ArrayList<>(Arrays.asList("spam", "filter", "mail")));


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        serializer.serialize(baos, record);
        baos.close();


        String strEvent = new String(baos.toByteArray());
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        Record deserializedRecord = serializer.deserialize(bais);

        assertEquals(record, deserializedRecord);

    }


    @Test
    public void issueWithDate() {
        final String recordStr = "{ \"id\" : \"0:vfBHTdrZCcFs3H6aO7Yb4UXWVppa80JiKQ7aW0\", \"type\" : \"pageView\", \"creationDate\" : \"Thu Apr 27 11:45:00 CEST 2017\",  \"referer\" : \"https://orexad.preprod.group-iph.com/fr/equipement/c-45\",   \"B2BUnit\" : null,   \"eventCategory\" : null,   \"remoteHost\" : \"149.202.66.102\",   \"userAgentString\" : \"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36\",   \"eventAction\" : null,   \"categoryName\" : null,   \"viewportPixelWidth\" : 1624,   \"hitType\" : null,   \"companyID\" : null,   \"pageType\" : null,   \"Userid\" : null,   \"localPath\" : null,   \"partyId\" : \"0:j1ymvypx:pO~iVklsXUYa6zMKVZeA2nC1YlVzTEw1\",   \"codeProduct\" : null,   \"is_newSession\" : false,   \"GroupCode\" : null,     \"sessionId\" : \"0:j20802wr:Py1qDrBry7UedH6my~6ebE58wRHUXWVp\",   \"categoryCode\" : null,   \"eventLabel\" :null,   \"record_type\" : \"pageView\",   \"n\" : null,   \"record_id\" : \"0:vfBHTdrZCcFs3H6aO7Yb4pa80JiKQ7aW0\",   \"q\" : null,   \"userRoles\" : null,   \"screenPixelWidth\" : 1855,   \"viewportPixelHeight\" : 726,   \"screenPixelHeight\" : 1056,   \"is_PunchOut\" : null,   \"h2kTimestamp\" : 1493286295730,  \"pageViewId\" : \"0:vfBHTdrZCcFs3H6aO7Yb4pa80JiKQ7aW\",   \"location\" : \"https://orexad.preprod.group-iph.com/fr/equipement/c-45\",   \"record_time\" : 1493286300033,   \"pointOfService\" : null }";

        final ExtendedJsonSerializer serializer = new ExtendedJsonSerializer();
        ByteArrayInputStream bais = new ByteArrayInputStream(recordStr.getBytes());
        Record deserializedRecord = serializer.deserialize(bais);
        assertTrue(deserializedRecord.getTime().getTime() == 1493286300033L);
    }

}