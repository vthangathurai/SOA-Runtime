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
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.interpolation.EnvarBasedValueSource;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.PrefixedObjectValueSource;
import org.codehaus.plexus.interpolation.PropertiesBasedValueSource;
import org.codehaus.plexus.interpolation.RegexBasedInterpolator;
import org.codehaus.plexus.resource.ResourceManager;
import org.codehaus.plexus.resource.loader.FileResourceLoader;
import org.codehaus.plexus.resource.loader.ThreadContextClasspathResourceLoader;
import org.codehaus.plexus.resource.loader.URLResourceLoader;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;
import org.ebayopensource.turmeric.plugins.maven.resources.ResourceLocator.Location;
import org.ebayopensource.turmeric.plugins.maven.utils.AddMatchingFilesFilter;
import org.ebayopensource.turmeric.plugins.maven.utils.GenTimestamp;
import org.ebayopensource.turmeric.plugins.maven.utils.LogDelegateHandler;
import org.ebayopensource.turmeric.tools.codegen.NonInteractiveCodeGen;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Base level mojo for the most common turmeric parameters, and methods.
 */
public abstract class AbstractTurmericMojo extends AbstractMojo {
    private static final String GROUPID_SELF = "org.ebayopensource.turmeric.maven";
    private static final String ARTIFACTID_SELF = "turmeric-maven-plugin";

    /**
     * The default maven project object
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * 
     * @parameter expression="${turmeric.verbose}" default-value="true"
     * @required
     */
    protected boolean verbose;

    /**
     * Plexus component used to lookup resources from a variety of search locations (including Thread context
     * classloader, a file resource from the project base, a file resource from the project resources directories, a
     * fully qualified file path, or even a url)
     * 
     * @component role="org.codehaus.plexus.resource.ResourceManager" role-hint="default"
     * @required
     * @readonly
     */
    private ResourceManager locator;

    /**
     * Timestamp file used for tracking last generation and preventing a loop of generation seen in m2eclipse.
     * 
     * @parameter expression="${project.build.directory}/turmeric-maven-plugin-gen-timestamp"
     * @required
     */
    private File timestampFile;

    private GenTimestamp timestamps;

    private boolean locatorAlreadyConfigured = false;
    
    protected final void ensureDirectoryExists(String id, File dir) throws MojoExecutionException {
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new MojoExecutionException("Unable to create " + id + ": " + dir);
            }
        }
    }

    /**
     * Wraps the mojo execute with some behavioral lifecycle.
     */
    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {
        getLog().debug("[execute]");
        getLog().info("Using turmeric-maven-plugin version " + getTurmericMavenPluginVersion());

        if (executeSkip()) {
            getLog().warn("Skipping execution");
            return;
        }

        if (verbose) {
            logDependencyDetails("Turmeric Maven Plugin", AbstractTurmericMojo.class, GROUPID_SELF, ARTIFACTID_SELF);
            logDependencyDetails("Codegen Tools", NonInteractiveCodeGen.class, "org.ebayopensource.turmeric.runtime", "soa-client");
            getLog().info("Verbose Mode: enabling java.util.logging");
            // Initialize java.util.logging (which is present in CodeGen classes)

            Logger root = Logger.getLogger("");

            // Remove old delegates
            for (Handler handler : root.getHandlers()) {
                getLog().info("Removing existing logging handler: " + handler.getClass().getName());
                root.removeHandler(handler);
            }

            // Add our delegate
            root.addHandler(new LogDelegateHandler(getLog()));
        }

        getLog().debug("[onValidateParameters]");
        onValidateParameters();

        // Test for need to generate
        getLog().debug("[needsGeneration]");
        if (needsGeneration() == false) {
            getLog().warn("No need to generate. skipping turmeric plugin execution.");
            return;
        }

        try {
            getLog().debug("[onRunSetup]");
            onRunSetup();

            // Attach directories to project even if skipping servicegen.
            // This is to be a good maven and m2eclipse citizen.
            getLog().debug("[onAttachGeneratedDirectories]");
            onAttachGeneratedDirectories();

            getLog().debug("[onRun]");
            onRun();
        }
        finally {
            getLog().debug("[onRunTearDown]");
            onRunTearDown();
        }
    }

    
     /**
     * Look into the ${project.basedir}/.project file and see if the turmeric eclipse plugin's nature for codegen is
     * defined.  
     * 
     * @return
     */
    public boolean isTurmericEclipsePluginEnabled() {
        File projectFile = new File(project.getBasedir(), ".project");
        if (!projectFile.exists()) {
            // No .project file, not using turmeric eclipse plugin
            return false;
        }
        
        
        try {
            Document doc = parseXml(projectFile);
            Pattern legacyNaturePattern = Pattern
                            .compile("^org\\.ebay\\.soaframework\\.eclipse\\.SOA[A-Za-z0-9]*ProjectNature$");
            Pattern naturePattern = Pattern
                            .compile("^org\\.ebayopensource\\.turmeric\\.eclipse\\.build\\.SOA[A-Za-z0-9]*ProjectNature$");
            Pattern typeLibNaturePattern = Pattern
            				.compile("^org\\.ebayopensource\\.turmeric\\.eclipse\\.typelibrary\\.Type[A-Za-z0-9]*ProjectNature$");
            Pattern errorLibNaturePattern = Pattern
            				.compile("^org\\.ebayopensource\\.turmeric\\.eclipse\\.errorlibrary\\.properties\\.TurmericError[A-Za-z0-9]*ProjectNature$");
            Element root = doc.getRootElement();
            if (!"projectDescription".equals(root.getName())) {
                getLog().error("Not an eclipse .project file" + " (format mismatch): " + projectFile);
                return false;
            }
            @SuppressWarnings("unchecked")
            List<Element> naturesList = root.getChildren("natures");
            for (Element natureRoot : naturesList) {
                @SuppressWarnings("unchecked")
                List<Element> natures = natureRoot.getChildren("nature");
                for (Element nature : natures) {
                    String natureSpec = nature.getTextTrim();
                    if (naturePattern.matcher(natureSpec).matches()
                        || legacyNaturePattern.matcher(natureSpec).matches()
                        || typeLibNaturePattern.matcher(natureSpec).matches()
                        || errorLibNaturePattern.matcher(natureSpec).matches()) {
                        return true;
                    }
                }
            }

        }
        catch (MojoExecutionException e) {
            getLog().error("Unable to parse Eclipse .project file: " + projectFile, e);
        }
        return false;
    }

    /**
     * Obtains the as-built version of the turmeric maven plugin for reporting to the user.
     */
    private String getTurmericMavenPluginVersion() {
        String pompath = String.format("META-INF/maven/%s/%s/pom.properties", GROUPID_SELF, ARTIFACTID_SELF);
        URL url = this.getClass().getClassLoader().getResource(pompath);
        if (url == null) {
            return "(unknown/dev)";
        }
        Properties props = new Properties();
        InputStream stream = null;
        try {
            stream = url.openStream();
            props.load(stream);
            String version = props.getProperty("version");
            if (StringUtils.isBlank(version)) {
                return "(unknown)";
            }
            return version;
        }
        catch (IOException e) {
            getLog().debug("Unable to read version from: " + pompath, e);
            return "(unknown/io)";
        }
        finally {
            IOUtil.close(stream);
        }
    }

    public void logDependencyDetails(String header, Class<?> clazz, String groupId, String artifactId) {
        StringBuilder msg = new StringBuilder();
        msg.append(header);
        msg.append("\n    Version: ").append(getClassVersion(clazz, groupId, artifactId));
        msg.append("\n   Location: ").append(getJarLocationOfClass(clazz));
        getLog().info(msg.toString());
    }

    public void logClassLocation(Class<?> clazz) {
        getLog().info("Location of class: " + clazz.getName());
        getLog().info("               is: " + getJarLocationOfClass(clazz));
    }

    public String getClassVersion(Class<?> clazz, String groupId, String artifactId) {
        try {
            String ver;

            ver = getPackageVersion(clazz);
            if (ver != null) {
                return ver;
            }

            ver = getMavenVersion(groupId, artifactId);
            if (ver != null) {
                return ver;
            }
        }
        catch (Exception ignore) {
            /* ignore */
        }
        return "<unknown>";
    }

    private String getMavenVersion(String groupId, String artifactId) {
        try {
            String resource = String.format("META-INF/maven/%s/%s/pom.properties", groupId, artifactId);
            URL url = this.getClass().getClassLoader().getResource(resource);
            if (url == null) {
                return null;
            }
            InputStream in = null;
            try {
                in = url.openStream();
                Properties props = new Properties();
                props.load(in);
                return props.getProperty("version");
            }
            finally {
                IOUtil.close(in);
            }
        }
        catch (Exception ignore) {
            /* ignore */
        }
        return null;
    }

    private static String getPackageVersion(Class<?> clazz) {
        Package p = clazz.getPackage();
        if (p == null) {
            return null;
        }

        String ver = p.getImplementationVersion();
        if (ver == null) {
            return null;
        }
        return ver;
    }

    public static String getJarLocationOfClass(Class<?> clazz) {
        try {
            String classpath = clazz.getName().replace('.', '/') + ".class";
            URL resource = clazz.getClassLoader().getResource(classpath);
            if (resource != null) {
                String uri = resource.toExternalForm();
                int idx = uri.lastIndexOf("!");
                if (idx > 0) {
                    uri = uri.substring(0, idx);
                    return uri;
                }
                else if (uri.endsWith(classpath)) {
                    return uri.substring(0, uri.length() - classpath.length());
                }
                else {
                    return uri;
                }
            }
        }
        catch (Exception ignore) {
            /* ignore */
        }
        return "<unknown>";
    }

    /**
     * Attempt to determine if we are running within the Eclipse environment.
     */
    public boolean isMojoRunningInEclipse() {
        try {
            getLog().debug("Eclipse Check: is org.eclipse.core.launcher.Main in classpath?");
            // Simple check: is launcher main in classpath
            Class<?> c = Class.forName("org.eclipse.core.launcher.Main");
            if (c != null) {
                return true;
            }
        }
        catch (ClassNotFoundException e) {
            /*
             * Launcher not found in classpath. Perform other tests in an attempt to verify.
             */
        }

        String FS = File.separator;

        // Are we using a plugin embedded maven (1/2)
        // Test the "java.class.path" property
        String jClassPath = System.getProperty("java.class.path");
        if (StringUtils.isNotBlank(jClassPath)) {
            String expected = FS + "plugins" + FS + "org.maven.ide.eclipse";
            getLog().debug("Eclipse Check: \"java.class.path\" = \"" + jClassPath + "\".contains(\"" + expected + "\")");
            if (jClassPath.contains(expected)) {
                // We are using a plugin embedded maven
                return true;
            }
        }

        // Are we using a plugin embedded maven (2/2)
        // Test the "classworlds.conf" property
        String cworldConf = System.getProperty("classworlds.conf");
        if (StringUtils.isNotBlank(cworldConf)) {
            String expected = FS + ".metadata" + FS + ".plugins" + FS + "org.maven.ide.eclipse";
            getLog().debug("Eclipse Check: \"classworlds.conf\" = \"" + cworldConf + "\".contains(\"" + expected
                            + "\")");
            if (cworldConf.contains(expected)) {
                // We are using a plugin embedded maven.
                return true;
            }
        }

        getLog().debug("Eclipse Check: Not running in Eclipse.");
        return false;
    }

    /**
     * Convenience method for using {@link #expandParameter(String)} with File objects.
     * 
     * @param rawfile
     *            the raw file object
     * @return null if rawfile is null, or the expanded File object.
     * @throws MojoExecutionException
     */
    public File expandFile(File rawfile) throws MojoExecutionException {
        if (rawfile == null) {
            return null;
        }
        String rawpath = rawfile.getPath();
        return new File(expandParameter(rawpath));
    }

    /**
     * Take a raw parameter value, and expand any found properties within it.
     * 
     * @param parameter
     * @return the expanded parameter
     * @throws MojoExecutionException
     *             if unable to interpolate
     */
    public String expandParameter(String rawparameter) throws MojoExecutionException {
        if (StringUtils.isBlank(rawparameter)) {
            return rawparameter;
        }

        try {
            Interpolator interpolator = new RegexBasedInterpolator();
            interpolator.addValueSource(new PrefixedObjectValueSource("project", project));
            interpolator.addValueSource(new PrefixedObjectValueSource("mojo", this));
            interpolator.addValueSource(new PropertiesBasedValueSource(System.getProperties()));
            interpolator.addValueSource(new EnvarBasedValueSource());

            String result = interpolator.interpolate(rawparameter);
            if (getLog().isDebugEnabled()) {
                getLog().debug("Expand Parameter: " + "\n      Raw: " + rawparameter + "\n Expanded: " + result);
            }
            return result;
        }
        catch (IOException e) {
            throw new MojoExecutionException("Unable to use Environment for Parameter Interpolation", e);
        }
        catch (InterpolationException e) {
            throw new MojoExecutionException("Unable to use interpolatate: " + rawparameter, e);
        }
    }

    /**
     * Look through all project.resources trees and find the pathref specified.
     * <p>
     * Since this mojo executes in the 'generate-sources' phase, we can't rely on the resources being (yet) present in
     * the output directories, as that occurs later in the maven lifecycle in the 'process-resources' phase. See <a
     * href="http://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html#Lifecycle_Reference" >Maven
     * Lifecycle</a> for details of phase order.
     * 
     * @param pathref
     *            the pathref within the resource directory to look for
     * @return the File path pointing to the resources directory content, or null if not found.
     */
    protected final File findResourceFile(String pathref) {
        if (StringUtils.isBlank(pathref)) {
            getLog().warn("Unable to lookup resource for null pathref: " + pathref);
            return null;
        }
        List<?> resources = project.getBuild().getResources();
        getLog().debug("Looking for resource in [" + resources.size() + "] directories: " + pathref);
        Iterator<?> iter = resources.iterator();
        while (iter.hasNext()) {
            Resource resource = (Resource) iter.next();
            String resDir = resource.getDirectory();
            if (StringUtils.isBlank(resDir)) {
                getLog().warn("Blank resources directory: " + resource);
                continue;
            }
            File dir = new File(resDir);
            getLog().debug("Testing for resource in dir: " + dir);
            File possiblePath = new File(dir, pathref);
            if (possiblePath.exists()) {
                getLog().debug("Found resource: " + possiblePath);
                return possiblePath;
            }
        }
        return null;
    }

    /**
     * Get a configured ResourceManager for location lookups.
     * <p>
     * Note: be sure you set the {@link ResourceManager#setOutputDirectory(File)}!
     * 
     * @return the resource locator to use.
     */
    protected ResourceManager getResourceLocator() {
        if (locatorAlreadyConfigured) {
            return locator;
        }

        locatorAlreadyConfigured = true;
        locator.addSearchPath(ThreadContextClasspathResourceLoader.ID, "");
        locator.addSearchPath(FileResourceLoader.ID, project.getBasedir().getAbsolutePath());

        List<?> resources = project.getBuild().getResources();
        Iterator<?> iter = resources.iterator();
        while (iter.hasNext()) {
            Resource resource = (Resource) iter.next();
            locator.addSearchPath(FileResourceLoader.ID, resource.getDirectory());
        }
        locator.addSearchPath(URLResourceLoader.ID, "");

        return locator;
    }

    public MavenProject getProject() {
        return project;
    }

    public boolean isVerbose() {
        return verbose;
    }

    /**
     * Internal check to see if execution is needed.
     * <p>
     * This differs from {@link #needsGeneration()} in so far that the variables on the mojo are not validated or
     * expanded at this point in the mojo lifecycle.
     * <p>
     * This is really only useful in mitigating the conflict that appears when the same functionality exists between a
     * specific Mojo goal and an Eclipse Plugin.
     * 
     * @return true to indicate that execution can be skipped.
     * @throws MojoExecutionException
     *             when wrapping other exceptions. Will cause build to go into error, resulting in a "BUILD ERROR"
     *             message. The build will stop.
     * @throws MojoFailureException
     *             to throw new exceptions. Will cause build to go into error, resulting in a "BUILD FAILURE" message.
     *             The build will stop.
     */
    public boolean executeSkip() throws MojoExecutionException, MojoFailureException {
        // Default is to not skip execution.
        return false;
    }

    /**
     * Internal check to see if generation is needed.
     * 
     * @return true to indicate that generation is required, false to skip generation
     * @throws MojoExecutionException
     *             when wrapping other exceptions. Will cause build to go into error, resulting in a "BUILD ERROR"
     *             message. The build will stop.
     * @throws MojoFailureException
     *             to throw new exceptions. Will cause build to go into error, resulting in a "BUILD FAILURE" message.
     *             The build will stop.
     */
    public abstract boolean needsGeneration() throws MojoExecutionException, MojoFailureException;

    /**
     * Attach any of the generated directories.
     */
    protected abstract void onAttachGeneratedDirectories();

    /**
     * The name of the goal for the mojo.
     */
    protected abstract String getGoalName();

    /**
     * Event to perform mojo operations on.
     * 
     * @throws MojoExecutionException
     *             when wrapping other exceptions. Will cause build to go into error, resulting in a "BUILD ERROR"
     *             message. The build will stop.
     * @throws MojoFailureException
     *             to throw new exceptions. Will cause build to go into error, resulting in a "BUILD FAILURE" message.
     *             The build will stop.
     */
    public void onRun() throws MojoExecutionException, MojoFailureException {
        /* perform actual mojo steps */
    }

    /**
     * Event to perform setup for a run of the mojo.
     * 
     * @throws MojoExecutionException
     *             when wrapping other exceptions. Will cause build to go into error, resulting in a "BUILD ERROR"
     *             message. The build will stop.
     * @throws MojoFailureException
     *             to throw new exceptions. Will cause build to go into error, resulting in a "BUILD FAILURE" message.
     *             The build will stop.
     */
    protected void onRunSetup() throws MojoExecutionException, MojoFailureException {
        /* setup directory output, copy files around, download files, etc ... */
    }

    /**
     * Event to perform teardown for a run of the mojo.
     * 
     * @throws MojoExecutionException
     *             when wrapping other exceptions. Will cause build to go into error, resulting in a "BUILD ERROR"
     *             message. The build will stop.
     * @throws MojoFailureException
     *             to throw new exceptions. Will cause build to go into error, resulting in a "BUILD FAILURE" message.
     *             The build will stop.
     */
    protected void onRunTearDown() throws MojoExecutionException, MojoFailureException {
        /* cleanup temporary files, etc ... */
        ensureDirectoryExists("Timestamp Home Dir", timestampFile.getParentFile());
        timestamps.write();
    }

    /**
     * Event to perform parameter validation on.
     * 
     * @throws MojoExecutionException
     *             when wrapping other exceptions. Will cause build to go into error, resulting in a "BUILD ERROR"
     *             message. The build will stop.
     * @throws MojoFailureException
     *             to throw new exceptions. Will cause build to go into error, resulting in a "BUILD FAILURE" message.
     *             The build will stop.
     */
    protected void onValidateParameters() throws MojoExecutionException, MojoFailureException {
        if (project == null) {
            throw new MojoFailureException("No project present!");
        }
        timestampFile = expandFile(timestampFile);
        timestamps = new GenTimestamp(getLog(), timestampFile);
    }

    /**
     * Tests the provided paths against the last timestamp persisted to know if any of the paths have changed.
     * 
     * @param paths
     *            the paths to verify against.
     * @return true if a timestamp is newer than the persisted one.
     * @see GenTimestamp#isNewerThan(String, File...)
     */
    protected boolean isNewerThanLastTimestamp(File... paths) {
        return timestamps.isNewerThan(getGoalName(), paths);
    }

    /**
     * Tests the provided live locations against the last timestamp persisted to know if any of the locations have
     * changed.
     * 
     * @param locations
     *            the locations to verify against. (non-live locations are ignored)
     * @return true if a timestamp is newer than the persisted one.
     * @see GenTimestamp#isNewerThan(String, File...)
     */
    protected boolean isNewerThanLastTimestamp(Location... locations) {
        List<File> paths = new ArrayList<File>();
        for (Location location : locations) {
            if (location == null) {
                continue; // skip null entries
            }
            if (location.isLiveFile()) {
                paths.add(location.getFile());
            }
        }

        if (paths.isEmpty()) {
            // No paths, no change.
            return false;
        }

        return timestamps.isNewerThan(getGoalName(), paths.toArray(new File[0]));
    }

    /**
     * Get the project's pom.xml file.
     * 
     * @return the project's pom.xml file (could be named something other than pom.xml)
     */
    protected File getProjectPomFile() {
        return project.getFile();
    }

    /**
     * Ensure that the path has appropriate separators for the system in use.
     * 
     * @param path
     *            the raw input path.
     * @return the corrected system separators
     */
    protected String toOS(String path) {
        return FilenameUtils.separatorsToSystem(path);
    }

    /**
     * Validates that a resources exists in the project's resources.
     * 
     * @param msg
     *            the type of resource
     * @param pathref
     *            the path reference to the resource.
     * @throws MojoFailureException
     */
    protected void validateResourceExists(String msg, String pathref) throws MojoFailureException {
        File path = findResourceFile(pathref);
        if ((path == null) || (path.exists() == false)) {
            throw new MojoFailureException("Missing Required resource (" + msg + "): " + pathref);
        }
    }

    protected Document parseXml(File xmlfile) throws MojoExecutionException {
        try {
            SAXBuilder builder = new SAXBuilder(false);
            return builder.build(xmlfile);
        }
        catch (JDOMException e) {
            throw new MojoExecutionException("Unable to parse: " + xmlfile, e);
        }
        catch (IOException e) {
            throw new MojoExecutionException("Unable to parse: " + xmlfile, e);
        }
    }

    /**
     * Search through the project resource directories, and attempt to find a set of {@link File}s that match the
     * provided regex pattern.
     * 
     * @param regex
     *            the regex pattern to look for
     * @return the list of Files found (may be empty)
     */
    protected List<File> findProjectResourceFiles(String regex) {
        List<File> hits = new ArrayList<File>();

        Pattern pattern = Pattern.compile(regex);
        File dir;

        @SuppressWarnings("unchecked")
        List<Resource> resources = project.getBuild().getResources();
        for (Resource resource : resources) {
            dir = new File(resource.getDirectory());
            recursiveAddMatchingFiles(dir, new AddMatchingFilesFilter(getLog(), dir, pattern, hits));
        }

        return hits;
    }

    private void recursiveAddMatchingFiles(File dir, FileFilter filter) {
        for (File path : dir.listFiles()) {
            if (path.isFile()) {
                filter.accept(path);
                continue;
            }

            if (path.isDirectory()) {
                recursiveAddMatchingFiles(path, filter);
            }
        }
    }
}
