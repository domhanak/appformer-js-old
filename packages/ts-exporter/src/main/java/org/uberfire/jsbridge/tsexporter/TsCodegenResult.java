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

package org.uberfire.jsbridge.tsexporter;

import java.util.Map;
import java.util.Set;

import org.uberfire.jsbridge.tsexporter.decorators.DecoratorStore;
import org.uberfire.jsbridge.tsexporter.decorators.NpmPackageForDecorators;
import org.uberfire.jsbridge.tsexporter.model.NpmPackageGenerated;
import org.uberfire.jsbridge.tsexporter.model.TsExporterResource;
import org.uberfire.jsbridge.tsexporter.model.config.LernaJson;
import org.uberfire.jsbridge.tsexporter.model.config.PackageJsonRoot;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.uberfire.jsbridge.tsexporter.model.NpmPackage.Type.RAW;
import static org.uberfire.jsbridge.tsexporter.model.TsClass.PACKAGES_SCOPE;

public class TsCodegenResult {

    private final String version;
    private final DecoratorStore decoratorStore;
    private final Set<NpmPackageGenerated> npmPackages;

    public TsCodegenResult(final String version,
                           final DecoratorStore decoratorStore,
                           final Set<NpmPackageGenerated> npmPackages) {

        this.version = version;
        this.decoratorStore = decoratorStore;
        this.npmPackages = npmPackages;
    }

    private Map<String, NpmPackageGenerated> generatedNpmPackagesByName() {
        return npmPackages.stream()
                .collect(toMap(NpmPackageGenerated::getName,
                               identity(),
                               (a, b) -> a.getType().equals(RAW) ? b : a));
    }

    public Set<NpmPackageGenerated> getNpmPackages() {
        return npmPackages;
    }

    public TsExporterResource getRootPackageJson() {
        return new PackageJsonRoot();
    }

    public TsExporterResource getLernaJson() {
        return new LernaJson(version);
    }

    public String getDecoratorsNpmPackageName(final NpmPackageGenerated npmPackage) {
        return decoratorStore.getDecoratorsNpmPackageNameFor(npmPackage);
    }

    public Map<NpmPackageGenerated, NpmPackageForDecorators> getDecoratorsNpmPackagesByDecoratedNpmPackages() {
        return decoratorStore.getDecoratorNpmPackageNamesByDecoratedMvnModuleNames().entrySet().stream()
                .collect(toMap(e -> generatedNpmPackagesByName().get(PACKAGES_SCOPE + "/" + e.getKey()),
                               e -> new NpmPackageForDecorators(e.getValue(), version)));
    }
}
