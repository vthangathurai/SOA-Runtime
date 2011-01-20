/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.plugins.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;
import org.ebayopensource.turmeric.plugins.maven.resources.ResourceLocator;
import org.ebayopensource.turmeric.plugins.maven.resources.ResourceLocator.Location;
import org.ebayopensource.turmeric.plugins.maven.utils.CodegenCommands;
import org.ebayopensource.turmeric.plugins.maven.utils.LegacyProperties;
import org.ebayopensource.turmeric.tools.codegen.InputOptions;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenConstants;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * Perform servicegen on an implementation.
 * 
 * @goal gen-implementation
 * @phase generate-sources
 * @requiresDependencyResolution compile
 * @requiresProject true
 */
public class GenImplMojo extends AbstractTurmericCodegenMojo {
    private static final String LEGACY_PROP_REF = "${project.basedir}/service_impl_project.properties";

    /**
     * Service Name
     * 
     * @parameter expression="${codegen.serviceName}" default-value="${project.artifactId}"
     * @required
     */
    protected String serviceName = "${project.artifactId}";

    /**
     * WSDL Path Reference.
     * <p>
     * Use syntax similar to how {@link ClassLoader#getResource(String)} expects.
     * <p>
     * Actual WSDL File location is looked up via {@link ResourceLocator#findResource(String)}
     * 
     * @parameter expression="${codegen.wsdl.path}"
     *            default-value="META-INF/soa/services/wsdl/$${mojo.serviceName}/$${mojo.serviceName}.wsdl"
     * @optional
     */
    protected String wsdlPathRef = "META-INF/soa/services/wsdl/${mojo.serviceName}/${mojo.serviceName}.wsdl";

    /**
     * CodeGen Type.
     * 
     * @parameter expression="${codegen.generator.type}" default-value="DispatcherForMaven"
     * @required
     */
    protected String genType = "DispatcherForMaven";

    /**
     * Service Operations Path Reference.
     * <p>
     * Use syntax similar to how {@link ClassLoader#getResource(String)} expects.
     * <p>
     * Actual Service Operations File location is looked up via {@link ResourceLocator#findResource(String)}
     * 
     * @parameter expression="${codegen.service.operations.path}" default-value=
     *            "META-INF/soa/services/config/$${mojo.serviceName}/service_operations.properties"
     * @required
     */
    private String serviceOperationsPathRef = "META-INF/soa/services/config/${mojo.serviceName}/service_operations.properties";

    /**
     * Service Metadata Path Reference.
     * <p>
     * Use syntax similar to how {@link ClassLoader#getResource(String)} expects.
     * <p>
     * Actual Service Metadata File location is looked up via {@link ResourceLocator#findResource(String)}
     * 
     * @parameter expression="${codegen.service.metadata.path}" default-value=
     *            "META-INF/soa/common/config/$${mojo.serviceName}/service_metadata.properties"
     * @required
     */
    private String serviceMetadataPathRef = "META-INF/soa/common/config/${mojo.serviceName}/service_metadata.properties";

    /**
     * Service Config Path Reference.
     * <p>
     * Use syntax similar to how {@link ClassLoader#getResource(String)} expects.
     * <p>
     * Actual Service Operations File location is looked up via {@link ResourceLocator#findResource(String)}
     * 
     * @parameter expression="${codegen.service.config.path}"
     *            default-value="META-INF/soa/services/config/$${mojo.serviceName}/ServiceConfig.xml"
     * @required
     */
    private String serviceConfigPathRef = "META-INF/soa/services/config/${mojo.serviceName}/ServiceConfig.xml";

    /**
     * Service Group ID
     * 
     * @parameter expression="${codegen.service.groupId}" default-value="${project.groupId}"
     * @required
     */
    private String serviceGroupId = "${project.groupId}";

    /**
     * Use the service-impl-class-name from the {@link #serviceConfigPathRef} as the name of the generated classname.
     * 
     * @parameter expression="${codegen.classname.from.config}" default-value="true"
     * @optional
     */
    private boolean useClassnameFromConfig = true;

    /**
     * A {@link Location} for the wsdl file referenced in {@link #wsdlPathRef}
     */
    private Location wsdlLocation;

    /**
     * A {@link Location} for the service operations file referenced in {@link #serviceOperationsPathRef}
     */
    private Location serviceOperationsLocation;

    /**
     * A {@link Location} for the service metadata file referenced in {@link #serviceMetadataPathRef}
     */
    private Location serviceMetadataLocation;

    /**
     * A {@link Location} for the service config file referenced in {@link #serviceConfigPathRef}
     */
    private Location serviceConfigLocation;

    private ServiceConfigDetails serviceConfig;

    static class ServiceConfigDetails {
        String serviceImplClassName;
        String serviceInterfaceClassName;
    }

    @Override
    protected String getGoalName() {
        return "gen-implementation";
    }

    @Override
    protected void addCodegenCommands(CodegenCommands commands) throws MojoExecutionException, MojoFailureException {
        getLog().info("Processing Impl Project: " + getProject().getId());

        commands.setServiceName(serviceName);
        commands.add(InputOptions.OPT_ADMIN_NAME, serviceName);
        commands.add(InputOptions.OPT_INTERFACE, serviceConfig.serviceInterfaceClassName);

        if (isLegacyMode()) {
            commands.add(InputOptions.OPT_PROJECT_ROOT, getProject().getBasedir().getAbsolutePath());
        }
        else {
            // From here down is standard mode
            if (useClassnameFromConfig) {
                if (serviceConfig.serviceImplClassName == null) {
                    throw new MojoExecutionException("Can not read implementation classname from: "
                                    + serviceConfigLocation);
                }

                commands.add(InputOptions.OPT_SVC_IMPL_CLASS_NAME, serviceConfig.serviceImplClassName.trim());
            }

            commands.add(InputOptions.OPT_GEN_INTERFACE_PACKAGE, serviceGroupId);

            if ((serviceMetadataLocation != null) && (serviceMetadataLocation.isLocalToProject())) {
                commands.addSingle(InputOptions.OPT_USE_INTERFACE_JAR);
            }
        }
    }

    @Override
    public String getGenType() {
        return genType;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getServiceGroupId() {
        return serviceGroupId;
    }

    @Override
    public boolean needsGeneration() throws MojoExecutionException {
        if (super.needsGeneration()) {
            return true;
        }

        if (isNewerThanLastTimestamp(wsdlLocation, serviceOperationsLocation, serviceMetadataLocation)) {
            getLog().info("Must Generate: source files modified recently");
            return true;
        }

        getLog().info("Service WSDL has not been updated recently.");
        return false;
    }

    @Override
    protected void onValidateParameters() throws MojoExecutionException, MojoFailureException {
        super.onValidateParameters();

        // Valid service name
        if (StringUtils.isBlank(serviceName)) {
            throw new MojoExecutionException("serviceName not specified");
        }

        genType = expandParameter(genType);

        if (isLegacyMode()) {
            LegacyProperties props = getLegacyProperties(LEGACY_PROP_REF);
            serviceName = props.getProperty("serviceName", serviceName);
            serviceGroupId = props.getProperty("serviceGroupID", serviceGroupId);
        }
        else {
            serviceName = expandParameter(serviceName);
            serviceGroupId = expandParameter(serviceGroupId);
        }

        serviceConfigPathRef = expandParameter(serviceConfigPathRef);
        serviceOperationsPathRef = expandParameter(serviceOperationsPathRef);
        serviceMetadataPathRef = expandParameter(serviceMetadataPathRef);
        wsdlPathRef = expandParameter(wsdlPathRef);

        ResourceLocator locator = new ResourceLocator(getLog(), getProject());
        wsdlLocation = locator.findResource(wsdlPathRef);
        serviceConfigLocation = locator.findResource(serviceConfigPathRef);
        serviceOperationsLocation = locator.findResource(serviceOperationsPathRef);
        serviceMetadataLocation = locator.findResource(serviceMetadataPathRef);

        getLog().debug("wsdlLocation: " + wsdlLocation);
        getLog().debug("serviceConfigLocation: " + serviceConfigLocation);
        getLog().debug("serviceOperationsLocation: " + serviceOperationsLocation);
        getLog().debug("serviceMetadataLocation: " + serviceMetadataLocation);

        // Validate Required Files
        if (wsdlLocation == null) {
            throw new MojoExecutionException("No service <wsdlPathRef/> file"
                            + " found among project resources and dependencies: " + wsdlPathRef);
        }
        if (serviceMetadataLocation == null) {
            throw new MojoExecutionException("No <serviceMetadataPathRef/>"
                            + " file found among project resources and dependencies: " + serviceMetadataPathRef);
        }
        if (serviceConfigLocation == null) {
            throw new MojoExecutionException("No <serviceConfigPathRef/>"
                            + " file found among project resources and dependencies: " + serviceConfigPathRef);
        }
        if (serviceOperationsLocation == null) {
            getLog().warn("No <serviceOperationsPathRef/>" + " file found among project resources and dependencies: "
                            + serviceOperationsPathRef);
        }

        serviceConfig = readServiceConfig(serviceConfigLocation);
        
        // Sanity Check: verify information from Interface project
        Properties svcMetaProps = loadProperties(serviceMetadataLocation);
        String serviceInterfaceClassName = svcMetaProps.getProperty(CodeGenConstants.SERVICE_INTF_CLASS_NAME);
        if (StringUtils.isBlank(serviceInterfaceClassName)) {
            throw new MojoExecutionException("Unable to find service interface class name ("
                            + CodeGenConstants.SERVICE_INTF_CLASS_NAME + " in <serviceMetadataPathRef/> file: "
                            + serviceMetadataLocation.getUri().toASCIIString());
        }

        if (!StringUtils.equals(serviceConfig.serviceInterfaceClassName, serviceInterfaceClassName)) {
            StringBuilder err = new StringBuilder();
            err.append("ERROR: service-interface-class-name mismatch:");
            err.append("\n  The <service-interface-class-name> from ");
            err.append(serviceConfigLocation.getUri().toASCIIString());
            err.append("\n    * ").append(serviceConfig.serviceInterfaceClassName);
            err.append("\n  And \"").append(CodeGenConstants.SERVICE_INTF_CLASS_NAME).append("\" value from ");
            err.append(serviceMetadataLocation.getUri().toASCIIString());
            err.append("\n    * ").append(serviceInterfaceClassName);
            err.append("\n  Do not match.");
            throw new MojoExecutionException(err.toString());
        }
        
        getLog().info("Service Interface Class: " + serviceConfig.serviceInterfaceClassName);
        getLog().info("Service Impl Class     : " + serviceConfig.serviceImplClassName);
    }

    private Properties loadProperties(Location location) throws MojoExecutionException {
        return loadProperties(location.getFile());
    }

    private Properties loadProperties(File file) throws MojoExecutionException {
        Properties props = new Properties();
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            props.load(reader);
            return props;
        }
        catch (Exception e) {
            throw new MojoExecutionException("Unable to read properties file: " + file, e);
        }
        finally {
            IOUtils.closeQuietly(reader);
        }
    }

    private ServiceConfigDetails readServiceConfig(Location serviceConfig) throws MojoExecutionException {
        InputStream io = null;
        try {
            ServiceConfigDetails scd = new ServiceConfigDetails();
            io = new FileInputStream(serviceConfig.getFile());
            final Document document = new SAXBuilder().build(io);
            for (Object obj : document.getRootElement().getChildren()) {
                Element elem = (Element) obj;
                if (elem.getName().equals("service-impl-class-name")) {
                    scd.serviceImplClassName = elem.getValue().trim();
                    continue;
                }

                if (elem.getName().equals("service-interface-class-name")) {
                    scd.serviceInterfaceClassName = elem.getValue().trim();
                    continue;
                }
            }
            return scd;
        }
        catch (Exception e) {
            throw new MojoExecutionException("Unable to read service config file: " + serviceConfig.getFile(), e);
        }
        finally {
            IOUtil.close(io);
        }
    }
}
