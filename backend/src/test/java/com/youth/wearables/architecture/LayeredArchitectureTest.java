package com.youth.wearables.architecture;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import java.util.Set;

@AnalyzeClasses(packages = ArchUnitRuleConfig.BASE_PACKAGE, importOptions = ImportOption.DoNotIncludeTests.class)
class LayeredArchitectureTest {

    @ArchTest
    void bounded_contexts_are_discovered(JavaClasses classes) {
        var discovered = ArchUnitRuleConfig.discoverBoundedContexts(classes);
        assertTrue(discovered.containsAll(Set.of("externaldevices", "usermanagement")),
                "expected known bounded contexts to be discovered, found: " + discovered);
    }

    @ArchTest
    void each_slice_respects_domain_application_infrastructure_layering(JavaClasses classes) {
        for (String slice : ArchUnitRuleConfig.discoverBoundedContexts(classes)) {
            String sliceBasePackage = ArchUnitRuleConfig.BASE_PACKAGE + "." + slice;
            layeringRuleFor(sliceBasePackage).check(classes);
        }
    }

    private ArchRule layeringRuleFor(String sliceBasePackage) {
        return layeredArchitecture()
                .consideringOnlyDependenciesInAnyPackage(sliceBasePackage + "..")
                .layer("Domain").definedBy(sliceBasePackage + ".domain..")
                .layer("Application").definedBy(sliceBasePackage + ".application..")
                .layer("Infrastructure").definedBy(sliceBasePackage + ".infrastructure..")
                .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure")
                .whereLayer("Application").mayOnlyBeAccessedByLayers("Infrastructure")
                .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer();
    }
}
