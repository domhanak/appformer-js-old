/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.jsbridge.tsexporter.model;

import java.util.Set;
import java.util.regex.Pattern;

import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.uberfire.jsbridge.tsexporter.decorators.CopiedResource;

import static java.util.stream.Collectors.toSet;

public class NpmPackageForProjects implements NpmPackage {

    private final String name;
    private final String version;
    private final Type type;
    private final Set<CopiedResource> resources;

    public NpmPackageForProjects(final String name,
                                 final String version,
                                 final Type type) {

        this.name = name;
        this.version = version;
        this.type = type;
        this.resources = new Reflections(name, new ResourcesScanner()).getResources(Pattern.compile(".*")).stream()
                .filter(resourceName -> !resourceName.contains("/node_modules/"))
                .filter(resourceName -> !resourceName.contains("/dist/"))
                .map(resourceName -> new CopiedResource(name, resourceName))
                .collect(toSet());

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getUnscopedNpmPackageName() {
        return name;
    }

    @Override
    public Type getType() {
        return type;
    }

    public Set<CopiedResource> getResources() {
        return resources;
    }
}
