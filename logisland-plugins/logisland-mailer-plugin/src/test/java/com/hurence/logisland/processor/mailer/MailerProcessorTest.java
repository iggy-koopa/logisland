/**
 * Copyright (C) 2017 Hurence 
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
package com.hurence.logisland.processor.mailer;

import com.hurence.logisland.record.FieldDictionary;
import com.hurence.logisland.record.Record;
import com.hurence.logisland.record.StandardRecord;
import com.hurence.logisland.util.runner.MockRecord;
import com.hurence.logisland.util.runner.TestRunner;
import com.hurence.logisland.util.runner.TestRunners;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test simple Mailer processor.
 */
public class MailerProcessorTest {
    
    private static Logger logger = LoggerFactory.getLogger(MailerProcessorTest.class);
    
    //@Test
    public void testBroConnEvent() {
        final TestRunner testRunner = TestRunners.newTestRunner(new MailerProcessor());
        testRunner.assertValid();
        Record record = new StandardRecord("bro_event");
        record.setStringField(FieldDictionary.RECORD_VALUE, "TODO");
        testRunner.enqueue(record);
        testRunner.clearQueues();
        testRunner.run();
        testRunner.assertAllInputRecordsProcessed();
        testRunner.assertOutputRecordsCount(1);

        MockRecord out = testRunner.getOutputRecords().get(0);
        
        out.assertFieldExists(FieldDictionary.RECORD_TYPE);
        out.assertFieldEquals(FieldDictionary.RECORD_TYPE, "conn");
        
        out.assertFieldExists("ts");
        out.assertFieldEquals("ts", (float)1487603366.277002);
        
        out.assertFieldExists("uid");
        out.assertFieldEquals("uid", "Coo3g71UUMM2AyxWB");

        out.assertFieldExists("id_orig_h");
        out.assertFieldEquals("id_orig_h", "172.17.0.3");
        
        out.assertFieldExists("id_orig_p");
        out.assertFieldEquals("id_orig_p", (int)9200);
        
        out.assertFieldExists("id_resp_h");
        out.assertFieldEquals("id_resp_h", "172.17.0.2");
        
        out.assertFieldExists("id_resp_p");
        out.assertFieldEquals("id_resp_p", (int)42770);
        
        out.assertFieldExists("proto");
        out.assertFieldEquals("proto", "tcp");
        
        out.assertFieldExists("conn_state");
        out.assertFieldEquals("conn_state", "OTH");
        
        out.assertFieldExists("local_orig");
        out.assertFieldEquals("local_orig", true);
        
        out.assertFieldExists("local_resp");
        out.assertFieldEquals("local_resp", true);
        
        out.assertFieldExists("missed_bytes");
        out.assertFieldEquals("missed_bytes", (int)0);
        
        out.assertFieldExists("history");
        out.assertFieldEquals("history", "Cc");
        
        out.assertFieldExists("orig_pkts");
        out.assertFieldEquals("orig_pkts", (int)0);
        
        out.assertFieldExists("orig_ip_bytes");
        out.assertFieldEquals("orig_ip_bytes", (int)0);
        
        out.assertFieldExists("resp_pkts");
        out.assertFieldEquals("resp_pkts", (int)0);

        out.assertFieldExists("resp_ip_bytes");
        out.assertFieldEquals("resp_ip_bytes", (int)0);
        
        out.assertFieldExists("tunnel_parents");
        List<String> tunnelParents = (List<String>)out.getField("tunnel_parents").getRawValue();
        assertEquals(0, tunnelParents.size());       
    }
}
