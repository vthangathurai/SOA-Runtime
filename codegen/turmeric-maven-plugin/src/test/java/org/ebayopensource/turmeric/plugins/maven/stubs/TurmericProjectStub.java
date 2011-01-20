/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.plugins.maven.stubs;

import java.io.File;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Resource;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.codehaus.plexus.util.StringUtils;
import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.junit.Assert;

/**
 * Build a {@link MavenProjectStub} based on a target/tests/{projectDirName}/plugin.config.xml definition.
 */
public class TurmericProjectStub extends MavenProjectStub {
    class CompileArtifact extends ArtifactStub {
        public CompileArtifact(File file, Dependency dependency) {
            setFile(file);
            setGroupId(dependency.getGroupId());
            setArtifactId(dependency.getArtifactId());
            setVersion(dependency.getVersion());
        }
    }

    private File basedir;
    private List<Dependency> dependencies = new ArrayList<Dependency>();
    private Properties properties = new Properties();

    @SuppressWarnings("unchecked")
    public TurmericProjectStub(String projectDirName) {
        basedir = new File(super.getBasedir(), toOS("target/tests/" + projectDirName));

        File pom = new File(getBasedir(), "plugin-config.xml");
        MavenXpp3Reader pomReader = new MavenXpp3Reader();
        Model model = null;

        try {
            model = pomReader.read(new FileReader(pom));
            setModel(model);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        setGroupId(model.getGroupId());
        setArtifactId(model.getArtifactId());
        setVersion(model.getVersion());
        setName(model.getName());
        setUrl(model.getUrl());
        setPackaging(model.getPackaging());
        setFile(pom);

        if (model.getDependencies() != null) {
            dependencies.addAll(model.getDependencies());
        }
        if (model.getProperties() != null) {
            properties.putAll(model.getProperties());
        }
        setBuild(model.getBuild());

        File srcDir = ensureDirExists(getBuild().getSourceDirectory(), "src/main/java");
        getBuild().setSourceDirectory(srcDir.getAbsolutePath());

        File targetDir = ensureDirExists(getBuild().getDirectory(), "target");
        getBuild().setDirectory(targetDir.getAbsolutePath());

        File outputDir = ensureDirExists(getBuild().getOutputDirectory(), "target/classes");
        getBuild().setOutputDirectory(outputDir.getAbsolutePath());

        List<Resource> resources = new ArrayList<Resource>();
        resources.addAll(getBuild().getResources());

        // Only add resource dir if none are defined.
        if (resources.isEmpty()) {
            resources = new ArrayList<Resource>();
            Resource resource = new Resource();
            File resourceDir = new File(getBasedir(), toOS("src/main/resources"));
            resource.setDirectory(resourceDir.getAbsolutePath());
            ensureDirExists(resourceDir);
            resources.add(resource);
        }
        else {
            // Fix any relative resource paths.
            for (Resource resource : resources) {
                String resDir = toOS(resource.getDirectory());
                File dir = new File(resDir);
                if (!dir.isAbsolute()) {
                    dir = new File(getBasedir(), resDir);
                    resource.setDirectory(dir.getAbsolutePath());
                }
            }
        }

        getBuild().setResources(resources);

        // Process Dependencies into Compile Artifacts (if possible)
        List<Artifact> artifacts = new ArrayList<Artifact>();
        final List<Dependency> dependencies = getDependencies();
        if (dependencies != null) {
            for (Dependency dep : dependencies) {
                File artiFile = getArtifactFile(dep);
                if (artiFile != null && artiFile.exists()) {
                    artifacts.add(new CompileArtifact(artiFile, dep));
                }
            }
        }

        setCompileArtifacts(artifacts);
    }

    private File ensureDirExists(String dirname, String defaultname) {
        File dir;

        if (StringUtils.isBlank(dirname)) {
            dir = new File(getBasedir(), toOS(defaultname));
        }
        else {
            dir = new File(dirname);
            if (!dir.isAbsolute()) {
                dir = new File(getBasedir(), dirname);
            }
        }

        ensureDirExists(dir);
        return dir;
    }

    private File getArtifactFile(Dependency dep) {
        // Try deps dir first.
        File depsDir = new File(MavenTestingUtils.getTestResourcesDir(), "deps");

        String classifierFilename = null;
        String filename = String.format("%s.jar", dep.getArtifactId());
        if (StringUtils.isNotBlank(dep.getClassifier())) {
            classifierFilename = String.format("%s-%s.jar", dep.getArtifactId(), dep.getClassifier());
        }

        File file = findFile(depsDir, classifierFilename, filename);
        if (file != null) {
            System.out.printf("Project Dependency: src/test/resources/deps/%s%n", file.getName());
            return file;
        }

        // Now try to use the actual artifact based on pom.properties existance, or class existance.
        String sysPath = dep.getSystemPath();
        if ((sysPath != null) && (!sysPath.endsWith(".class"))) {
            sysPath += ".class";
        }
        String path = String.format("META-INF/maven/%s/%s/pom.properties", dep.getGroupId(), dep.getArtifactId());
        file = findResourceAsFile(path, sysPath);
        if (file == null) {
            System.out.printf("Project Dependency Not Found: %s%n", dep);
            return null;
        }
        System.out.printf("Project Dependency: %s%n", file.getAbsolutePath());
        return file;
    }

    /**
     * Look in the directory for the specified filenames. Search order is the order of the filenames provided. Null
     * filenames are acceptable as arguments, but are ignored for search.
     * 
     * @param dir
     *            the directory to search in.
     * @param filenames
     *            the filenames to look for.
     * @return the file found, or null if not found.
     */
    private File findFile(File dir, String... filenames) {
        File file = null;
        for (String filename : filenames) {
            if (StringUtils.isBlank(filename)) {
                continue; // skip blanks
            }
            file = new File(dir, filename);
            if (file.exists() && file.isFile()) {
                return file;
            }
        }
        return null;
    }

    /**
     * Look in the Thread local context classloader for the specified resources.
     * 
     * @param paths
     *            all possible paths to look up
     * @return
     */
    private File findResourceAsFile(String... paths) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        for (String path : paths) {
            if (StringUtils.isBlank(path)) {
                continue; // skip blanks
            }
            System.out.printf("Looking for resource: %s%n", path);
            URL url = cl.getResource(path);
            if (url != null) {
                System.out.printf("Found resource: %s%n", url);
                return toFile(url, path);
            }
            System.out.printf("Not found: %s%n", path);
        }
        return null;
    }

    private File toFile(URL url, String path) {
        if (url.getProtocol().equals("jar")) {
            String jarfilename = url.toExternalForm();
            jarfilename = jarfilename.substring("jar:".length());
            int idx = jarfilename.indexOf("!/");
            if (idx > 0) {
                jarfilename = jarfilename.substring(0, idx);
            }
            System.out.printf("Project Dependency: %s%n", jarfilename);
            return new File(jarfilename);
        }

        if (url.getProtocol().equals("file")) {
            File file;
            try {
                file = new File(url.toURI());
            }
            catch (URISyntaxException e) {
                file = new File(url.toExternalForm());
            }
            String filename = file.getAbsolutePath();
            String syspath = FilenameUtils.separatorsToSystem(path);
            if (filename.endsWith(syspath)) {
                filename = filename.substring(0, filename.length() - syspath.length());
            }
            return new File(filename);
        }

        return null;
    }

    @Override
    public List<Dependency> getDependencies() {
        return dependencies;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    private void ensureDirExists(File dir) {
        if (dir.exists() == false) {
            Assert.assertTrue("Unable to create directory: " + dir, dir.mkdirs());
        }
    }

    private String toOS(String path) {
        return FilenameUtils.separatorsToSystem(path);
    }

    @Override
    public File getBasedir() {
        return basedir;
    }
}
