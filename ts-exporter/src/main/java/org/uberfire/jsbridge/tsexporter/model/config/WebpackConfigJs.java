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

import static java.lang.String.format;
import static org.uberfire.jsbridge.tsexporter.util.Utils.get;
import static org.uberfire.jsbridge.tsexporter.util.Utils.lines;

public class WebpackConfigJs implements TsExporterResource {

    private final String moduleName;
    private final List<? extends TsClass> classes;

    public WebpackConfigJs(final String moduleName,
                           final List<? extends TsClass> classes) {

        this.moduleName = moduleName;
        this.classes = classes;
    }

    @Override
    public String toSource() {
        return format(lines(
                "const path = require('path');",
                "const CleanWebpackPlugin = require('clean-webpack-plugin');",
                "const CircularDependencyPlugin = require('circular-dependency-plugin');",
                "",
                "module.exports = {",
                "  mode: 'development',",
                "  externals: {",
                "    'appformer-js': {",
                "      root: 'AppFormer', //indicates global variable",
                "      commonjs: 'appformer-js',",
                "      commonjs2: 'appformer-js',",
                "      amd: 'appformer-js'",
                "    }",
                "  },",
                "  entry: {",
                "    '%s': './src/index.ts'",
                "  },",
                "  output: {",
                "    path: path.resolve(__dirname, 'dist'),",
                "    filename: 'index.js',",
                "    library: '%s',",
                "    libraryTarget: 'umd',",
                "    umdNamedDefine: true",
                "  },",
                "  plugins: [",
                "    new CleanWebpackPlugin(['dist']),",
                "    new CircularDependencyPlugin({",
                "      exclude: /node_modules/, // exclude detection of files based on a RegExp",
                "      failOnError: false, // add errors to webpack instead of warnings",
                "      cwd: process.cwd() // set the current working directory for displaying module paths",
                "    })",
                "  ],",
                "  module: {",
                "    rules: [",
                "      {",
                "        test: /\\.ts$/,",
                "        loader: 'ts-loader'",
                "      }",
                "    ]",
                "  },",
                "  resolve: {",
                "    extensions: ['.ts'],",
                "    modules: [path.resolve('./node_modules'), path.resolve('./src')]",
                "  }",
                "};",
                ""),

                      get(-1, moduleName.split("/")),
                      get(-1, moduleName.replace("-", "").split("/")));
    }

    @Override
    public String getModuleName() {
        return moduleName;
    }
}
