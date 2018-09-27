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

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

import org.uberfire.jsbridge.tsexporter.decorators.DecoratorStore;
import org.uberfire.jsbridge.tsexporter.dependency.DependencyGraph;
import org.uberfire.jsbridge.tsexporter.model.PojoTsClass;
import org.uberfire.jsbridge.tsexporter.model.RpcCallerTsClass;
import org.uberfire.jsbridge.tsexporter.model.TsClass;
import org.uberfire.jsbridge.tsexporter.model.TsNpmPackage;
import org.uberfire.jsbridge.tsexporter.util.Utils;

import static java.util.Arrays.stream;
import static java.util.Collections.list;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.empty;
import static org.uberfire.jsbridge.tsexporter.Main.TS_EXPORTER_PACKAGE;
import static org.uberfire.jsbridge.tsexporter.Main.elements;
import static org.uberfire.jsbridge.tsexporter.model.TsNpmPackage.Type.FINAL;
import static org.uberfire.jsbridge.tsexporter.model.TsNpmPackage.Type.RAW;
import static org.uberfire.jsbridge.tsexporter.model.TsNpmPackage.Type.UNDECORATED;
import static org.uberfire.jsbridge.tsexporter.util.Utils.distinctBy;
import static org.uberfire.jsbridge.tsexporter.util.Utils.get;
import static org.uberfire.jsbridge.tsexporter.util.Utils.getResources;

public class TsCodegen {

    private final DecoratorStore decoratorStore;
    private final String version;

    public TsCodegen(final String version,
                     final DecoratorStore decoratorStore) {

        this.decoratorStore = decoratorStore;
        this.version = version;
    }

    public TsCodegenResult generate() {
        return new TsCodegenResult(decoratorStore,
                                   concat(generateRaw().stream(),
                                          generateFinals().stream()).collect(toSet()));
    }


    private Set<TsNpmPackage> generateRaw() {
        final DecoratorStore decoratorStore = this.decoratorStore.ignoringForCurrentNpmPackage();
        final DependencyGraph dependencyGraph = new DependencyGraph(decoratorStore);

        findAllPortableTypes()
                .map(t -> new PojoTsClass((DeclaredType) t.asType(), decoratorStore))
                .filter(decoratorStore::hasDecoratorFor)
                .map(TsClass::asElement)
                .forEach(dependencyGraph::add);

        return dependencyGraph.vertices().parallelStream()
                .map(DependencyGraph.Vertex::getPojoClass)
                .filter(distinctBy(tsClass -> tsClass.getType().toString()))
                .collect(groupingBy(TsClass::getNpmPackageName))
                .entrySet()
                .parallelStream()
                .map(e -> {
                    final boolean hasDecorators = decoratorStore.hasDecoratorFor(get(-1, e.getKey().split("/")));
                    if (hasDecorators) {
                        return new TsNpmPackage(e.getKey(), e.getValue(), version + "-raw", RAW);
                    } else {
                        return new TsNpmPackage(e.getKey(), e.getValue(), version, UNDECORATED);
                    }
                })
                .collect(toSet());
    }

    private Set<TsNpmPackage> generateFinals() {

        //FIXME: Duplicated generation of UNDECORATED modules
        final DependencyGraph dependencyGraph = new DependencyGraph(decoratorStore);
        findAllPortableTypes().forEach(dependencyGraph::add);

        final Set<? extends TsClass> rpcTsClasses = getTsFilesFrom("remotes.tsexporter").stream()
                .map(element -> new RpcCallerTsClass(element, dependencyGraph, decoratorStore))
                .peek(TsClass::toSource)
                .collect(toSet());

        final Stream<TsClass> tsClasses = concat(
                dependencyGraph.vertices().parallelStream().map(DependencyGraph.Vertex::getPojoClass),
                rpcTsClasses.parallelStream());

        return tsClasses
                .filter(distinctBy(tsClass -> tsClass.getType().toString()))
                .collect(groupingBy(TsClass::getNpmPackageName))
                .entrySet()
                .parallelStream()
                .map(e -> {
                    final boolean hasDecorators = decoratorStore.hasDecoratorFor(get(-1, e.getKey().split("/")));
                    if (hasDecorators) {
                        return new TsNpmPackage(e.getKey(), e.getValue(), version, FINAL);
                    } else {
                        return new TsNpmPackage(e.getKey(), e.getValue(), version, UNDECORATED);
                    }
                })
                .collect(toSet());
    }

    private Stream<Element> findAllPortableTypes() {
        return concat(getTsFilesFrom("portables.tsexporter").stream(),
                      getClassesFromErraiAppPropertiesFiles().stream());
    }

    private List<? extends Element> getClassesFromErraiAppPropertiesFiles() {
        return list(getResources("META-INF/ErraiApp.properties")).stream()
                .map(Utils::loadPropertiesFile)
                .map(properties -> Optional.ofNullable(properties.getProperty("errai.marshalling.serializableTypes")))
                .filter(Optional::isPresent).map(Optional::get)
                .flatMap(serializableTypes -> stream(serializableTypes.split(" \n?")))
                .map(fqcn -> elements.getTypeElement(fqcn.trim().replace("$", ".")))
                .collect(toList());
    }

    private List<TypeElement> getTsFilesFrom(final String exportFileName) {
        return readAllExportFiles(exportFileName).stream()
                .map(elements::getTypeElement)
                .collect(toList());
    }

    private List<String> readAllExportFiles(final String fileName) {
        return list(getResources(TS_EXPORTER_PACKAGE.replace(".", "/") + "/" + fileName)).stream()
                .flatMap(url -> {
                    try {
                        final Scanner scanner = new Scanner(url.openStream()).useDelimiter("\\A");
                        return scanner.hasNext() ? stream(scanner.next().split("\n")) : empty();
                    } catch (final IOException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(toList());
    }
}
