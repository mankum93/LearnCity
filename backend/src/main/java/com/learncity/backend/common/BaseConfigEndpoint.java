package com.learncity.backend.common;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiNamespace;

/**
 * Created by DJ on 4/2/2017.
 */

@Api(
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "learncity.com",
                ownerName = "LearnCity Education Pvt. Ltd.",
                packagePath = "backend"
        )
)
/**
 * An Endpoint just to specify the base API properties
 * inherited (Annotation Inheritance) by all other Endpoints
 *  */
public class BaseConfigEndpoint {
        public static final String API_KEY = "AIzaSyCSi1Df1lZNv207--_xddTvrpwHiyNXeJk";
    public static final String PROJECT_ID = "unified-surfer-147104";
}
