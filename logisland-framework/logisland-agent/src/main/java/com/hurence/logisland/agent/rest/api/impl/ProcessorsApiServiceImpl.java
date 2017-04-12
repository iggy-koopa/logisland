package com.hurence.logisland.agent.rest.api.impl;

import com.hurence.logisland.agent.rest.api.NotFoundException;
import com.hurence.logisland.agent.rest.api.ProcessorsApiService;
import com.hurence.logisland.kafka.registry.KafkaRegistry;
import com.hurence.logisland.util.file.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-03-23T11:53:12.750+01:00")
public class ProcessorsApiServiceImpl extends ProcessorsApiService {


    private static Logger logger = LoggerFactory.getLogger(ProcessorsApiServiceImpl.class);

    public ProcessorsApiServiceImpl(KafkaRegistry kafkaRegistry) {
        super(kafkaRegistry);
    }

    @Override
    public Response getProcessors(SecurityContext securityContext) throws NotFoundException {
        String jsonPlugins = FileUtil.loadFileContentAsString(JSON_PLUGINS_FILE, "UTF-8");
        // do some magic!
        return Response.ok().entity(jsonPlugins).build();
    }


    private static String JSON_PLUGINS_FILE = "processors.json";

}
