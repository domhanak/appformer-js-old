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

package org.uberfire.jsbridge.tsexporter.model.config;

import java.util.List;
import java.util.Set;

import org.uberfire.jsbridge.tsexporter.dependency.DependencyRelation;
import org.uberfire.jsbridge.tsexporter.dependency.ImportEntry;
import org.uberfire.jsbridge.tsexporter.model.TsClass;
import org.uberfire.jsbridge.tsexporter.model.TsExporterResource;
import org.uberfire.jsbridge.tsexporter.util.Lazy;

import static java.lang.String.format;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static org.uberfire.jsbridge.tsexporter.util.Utils.lines;

public class PackageJson implements TsExporterResource {

    private final String moduleName;
    private final List<? extends TsClass> classes;
    private final Lazy<Set<String>> dependencies;

    public PackageJson(final String moduleName,
                       final List<? extends TsClass> classes) {

        this.moduleName = moduleName;
        this.classes = classes;
        this.dependencies = new Lazy<>(() -> classes.stream()
                .flatMap(c -> c.getDependencies().stream())
                .map(DependencyRelation::getImportEntry)
                .collect(groupingBy(ImportEntry::getModuleName))
                .keySet());
    }

    @Override
    public String toSource() {

        final String dependenciesPart = dependencies.get().stream()
                .filter(s -> !s.equals(moduleName))
                .sorted()
                .map(moduleName -> format("\"%s\": \"1.0.0\"", moduleName))
                .collect(joining(",\n"));

        return format(lines("{",
                            "  \"name\": \"%s\",",
                            "  \"version\": \"%s\",",
                            "  \"license\": \"Apache-2.0\",",
                            "  \"main\": \"./dist/index.js\",",
                            "  \"types\": \"./dist/index.d.ts\",",
                            "  \"dependencies\": {",
                            "%s",
                            "  },",
                            "  \"scripts\": {",
                            "    \"build\": \"webpack\",",
                            "    \"unpublish\": \"npm unpublish --force\"",
                            "  }",
                            "}"),

                      moduleName,
                      "1.0.0",
                      dependenciesPart
        );
    }

    @Override
    public String getModuleName() {
        return moduleName;
    }

    public Lazy<Set<String>> getDependencies() {
        return dependencies;
    }
}
