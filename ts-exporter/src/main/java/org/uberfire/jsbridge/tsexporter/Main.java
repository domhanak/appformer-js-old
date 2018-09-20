package org.uberfire.jsbridge.tsexporter;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.uberfire.jsbridge.tsexporter.decorators.DecoratorImportEntry;

import static java.lang.Boolean.getBoolean;
import static java.util.Arrays.asList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static org.uberfire.jsbridge.tsexporter.Main.ENTRY_POINT;
import static org.uberfire.jsbridge.tsexporter.Main.PORTABLE;
import static org.uberfire.jsbridge.tsexporter.Main.REMOTE;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({REMOTE, PORTABLE, ENTRY_POINT})
public class Main extends AbstractProcessor {

    static final String REMOTE = "org.jboss.errai.bus.server.annotations.Remote";
    static final String PORTABLE = "org.jboss.errai.common.client.api.annotations.Portable";
    static final String ENTRY_POINT = "org.jboss.errai.ioc.client.api.EntryPoint";
    static final String TS_EXPORTER_PACKAGE = "org.appformer.tsexporter.exports";

    public static Types types;
    public static Elements elements;
    public static Messager messager;

    private static final List<Element> seenPortables = new ArrayList<>();
    private static final List<Element> seenRemotes = new ArrayList<>();

    @Override
    public synchronized void init(final ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        Main.types = processingEnv.getTypeUtils();
        Main.elements = processingEnv.getElementUtils();
        Main.messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations,
                           final RoundEnvironment roundEnv) {

        if (!getBoolean("ts-exporter")) {
            return false;
        }

        try {
            process(roundEnv, annotations.stream().collect(toMap(identity(), roundEnv::getElementsAnnotatedWith)));
            return false;
        } catch (final Exception e) {
            e.printStackTrace();
            processingEnv.getMessager().printMessage(ERROR, "Error on TypeScript exporter.");
            return false;
        }
    }

    private void process(final RoundEnvironment roundEnv,
                         final Map<TypeElement, Set<? extends Element>> typesByAnnotations) {

        if (!roundEnv.processingOver() && !roundEnv.errorRaised()) {
            typesByAnnotations.forEach((annotation, classes) -> {
                if (REMOTE.equals(annotation.getQualifiedName().toString())) {
                    seenRemotes.addAll(classes);
                } else if (PORTABLE.equals(annotation.getQualifiedName().toString())) {
                    seenPortables.addAll(classes);
                } else if (ENTRY_POINT.equals(annotation.getQualifiedName().toString())) {
                    System.out.println("EntryPoint detected.");
                } else {
                    throw new RuntimeException("Unsupported annotation type.");
                }
            });
        } else {
            writeExportFile(seenPortables, "portables.tsexporter");
            writeExportFile(seenRemotes, "remotes.tsexporter");

            if (!getBoolean("ts-exporter-generate")) {
                System.out.println("TypeScript exporter will not run because ts-exporter-generate property is not set.");
                return;
            }

            System.out.println("Generating TypeScript modules...");
            long start = System.currentTimeMillis();
            new TsCodegenExporter(readDecoratorFiles()).run();
            System.out.println("TypeScript exporter has successfully run. (" + (System.currentTimeMillis() - start) + "ms)");
        }
    }

    private Set<DecoratorImportEntry> readDecoratorFiles() {
        return new HashSet<>(asList(
//                new DecoratorImportEntry("appformer-js-decorators", "PathDEC", "org.uberfire.backend.vfs.Path"),
//                new DecoratorImportEntry("appformer-js-decorators", "PathImplDEC", "org.uberfire.backend.vfs.PathFactory.PathImpl"),
//                new DecoratorImportEntry("appformer-js-decorators", "ObservablePathDEC", "org.uberfire.backend.vfs.ObservablePath"),
//                new DecoratorImportEntry("appformer-js-decorators", "ObservablePathImplDEC", "org.uberfire.backend.vfs.impl.ObservablePathImpl")
        ));
    }

    private void writeExportFile(final List<Element> elements,
                                 final String fileName) {

        try {
            System.out.println("Saving export file: " + fileName + "... ");
            try (final Writer writer = processingEnv.getFiler().createResource(CLASS_OUTPUT, TS_EXPORTER_PACKAGE, fileName).openWriter()) {
                writer.write(elements.stream().map(element -> ((TypeElement) element).getQualifiedName().toString()).distinct().collect(joining("\n")));
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}