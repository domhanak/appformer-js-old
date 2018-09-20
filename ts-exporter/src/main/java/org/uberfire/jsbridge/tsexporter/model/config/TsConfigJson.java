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

import org.uberfire.jsbridge.tsexporter.model.TsClass;
import org.uberfire.jsbridge.tsexporter.model.TsExporterResource;

import static org.uberfire.jsbridge.tsexporter.util.Utils.lines;

public class TsConfigJson implements TsExporterResource {

    private final String moduleName;
    private final List<? extends TsClass> classes;

    public TsConfigJson(final String moduleName,
                        final List<? extends TsClass> classes) {
        this.moduleName = moduleName;
        this.classes = classes;
    }

    @Override
    public String toSource() {
        return lines(
                "{",
                "  \"exclude\": [\"./node_modules\"],",
                "  \"include\": [\"./src\"],",
                "  \"compilerOptions\": {",
                "    \"lib\": [\"es6\", \"dom\"],",
                "    \"module\": \"commonjs\",",
                "    \"target\": \"es5\",",
                "    \"declaration\": true,",
                "    \"sourceMap\": true,",
                "    \"outDir\": \"./\",",
                "    \"noImplicitAny\": true,",
                "    \"strictNullChecks\": true,",
                "    \"experimentalDecorators\": true",
                "  }",
                "}"
        );
    }

    @Override
    public String getScopedNpmPackageName() {
        return moduleName;
    }
}
