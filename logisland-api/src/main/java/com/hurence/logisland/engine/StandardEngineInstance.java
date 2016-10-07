/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hurence.logisland.engine;

import com.hurence.logisland.processor.chain.StandardProcessorChainInstance;
import com.hurence.logisland.component.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class StandardEngineInstance extends AbstractConfiguredComponent {

    private final StreamProcessingEngine engine;

    private final List<StandardProcessorChainInstance> processorChainInstances = new ArrayList<>();

    public StandardEngineInstance(StreamProcessingEngine engine, String id) {
        super((ConfigurableComponent)engine, id);
        this.engine = engine;
    }

    public void addProcessorChainInstance(StandardProcessorChainInstance processorChainInstance){
        processorChainInstances.add(processorChainInstance);
    }

    public List<StandardProcessorChainInstance> getProcessorChainInstances() {
        return processorChainInstances;
    }

    public StreamProcessingEngine getEngine() {
        return engine;
    }

    @Override
    public void verifyModifiable() throws IllegalStateException {

    }
}
