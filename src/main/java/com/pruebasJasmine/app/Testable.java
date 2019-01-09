package com.pruebasJasmine.app;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Testable {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public void testable1(String data){
        if(!StringUtils.isEmpty(data)) {
            LOGGER.info("data: {}", data);
        } else{
            LOGGER.warn("data no tiene datos");
        }

    }

}
