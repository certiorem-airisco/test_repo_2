package com.pruebasJasmine.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class App {

    private static final int DEFAULT_PORT = 7777;
    private static final String CONTEXT_PATH = "/operations/pruebasJasmine/";
    private static final String BETA_CNTX_PATH = "beta/";
    private static final String WEB_CXT_PATH =  "web/";
    private static final String RESOURCES_CTX_PATH = "resources/";
    private static final String CONFIG_LOCATION = App.class.getPackage().getName();
    private static final String MAPPING_URL = "/*";
    private static final String DEFAULT_PROFILE = "ic";
    private static final String RESOURCES_BASE = "/resources/";
    private static final String BETA_ENVIRONMENT = "beta";


    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    public static void main(String[] args) throws Exception {
        LOGGER.info("Starting...");
        String port = System.getenv("PORT") != null ? System.getenv("PORT") : String.valueOf(DEFAULT_PORT);
        Server server = new Server(Integer.valueOf(port));
        HandlerList handlers = new HandlerList();
        String env = getEnvironmentProfile();
        WebApplicationContext context = getContext(env);
        handlers.setHandlers(new Handler[] { getServletContextHandler(context, env), getResourceHandler(env) });
        GzipHandler gzipHandler = new GzipHandler();
        gzipHandler.setHandler(handlers);
        gzipHandler.setIncludedMimeTypes("application/js1", "image/jpeg","text/css","text/html");
        server.setHandler(gzipHandler);
        server.start();
        server.join();
    }

    private static HandlerWrapper getResourceHandler(String env) {
        ContextHandler contextHandler = new ContextHandler();
        contextHandler.setContextPath(getPath(env, RESOURCES_CTX_PATH));
        ResourceHandler resources = new ResourceHandler();
        contextHandler.setHandler(resources);
        resources.setDirectoriesListed(false);
        resources.setBaseResource(Resource.newClassPathResource(RESOURCES_BASE));
        MimeTypes mimeTypes = new MimeTypes();
        mimeTypes.addMimeMapping("js1", "application/js1; charset=UTF-8");
        resources.setMimeTypes(mimeTypes);
        return contextHandler;
    }

    private static String getPath(String env, String path) {
        return CONTEXT_PATH + (BETA_ENVIRONMENT.equals(env) ? BETA_CNTX_PATH : StringUtils.EMPTY) + path;
    }

    private static ServletContextHandler getServletContextHandler(WebApplicationContext context, String env) {
        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        HashSessionManager manager = new HashSessionManager();
        manager.setSessionIdPathParameterName("none");
        contextHandler.setSessionHandler(new SessionHandler(manager));
        contextHandler.setErrorHandler(null);
        contextHandler.setContextPath(getPath(env, WEB_CXT_PATH));
        contextHandler.addServlet(new ServletHolder(new DispatcherServlet(context)), MAPPING_URL);
        contextHandler.addEventListener(new ContextLoaderListener(context));
//        contextHandler.addEventListener(new LifeCycleListener());
//        contextHandler.addFilter(RoutingFilter.class.getName(), "/*", null);
//        contextHandler.addFilter(HeadersFilter.class.getName(), "/*", null);
//        contextHandler.addFilter(NewRelicFilter.class.getName(), "/*", null);
        return contextHandler;
    }

    private static WebApplicationContext getContext(String env) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setConfigLocation(CONFIG_LOCATION);
        context.getEnvironment().setDefaultProfiles(env);
        return context;
    }

    private static String getEnvironmentProfile() {
//        String profile = CloudiaClusterInfo.getClusterInfo().getContext();
        return /*StringUtils.isNotBlank(profile) ? profile :*/ DEFAULT_PROFILE;
    }

}
