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

package org.uberfire.jsbridge.tsexporter.meta.translatable;

import java.util.List;
import java.util.Set;

import org.uberfire.jsbridge.tsexporter.dependency.ImportEntry;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

public class TranslatableDefault implements Translatable {

    private final String translated;
    private final Set<ImportEntry> importEntries;
    private final List<Translatable> aggregatedTypes;

    public TranslatableDefault(final String translated,
                               final Set<ImportEntry> importEntries,
                               final List<Translatable> aggregatedTypes) {

        this.translated = translated;
        this.aggregatedTypes = aggregatedTypes;
        this.importEntries = importEntries;
    }

    @Override
    public String toTypeScript(final SourceUsage sourceUsage) {
        switch (sourceUsage) {
            case TYPE_ARGUMENT_IMPORT:
                return translated;
            case TYPE_ARGUMENT_USE:
            case TYPE_ARGUMENT_DECLARATION:
                return translated + (aggregatedTypes.size() > 0
                        ? "<" + aggregatedTypes.stream().map(s -> s.toTypeScript(sourceUsage)).collect(joining(", ")) + ">"
                        : "");
            default:
                throw new RuntimeException();
        }
    }

    @Override
    public List<ImportEntry> getAggregatedImportEntries() {
        return concat(importEntries.stream(),
                      aggregatedTypes.stream().flatMap(t -> t.getAggregatedImportEntries().stream()))
                .collect(toList());
    }

    @Override
    public boolean canBeSubclassed() {
        return !importEntries.isEmpty();
    }
}
