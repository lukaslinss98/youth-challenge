package com.youth.wearables.architecture;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = ArchUnitRuleConfig.BASE_PACKAGE, importOptions = ImportOption.DoNotIncludeTests.class)
class DomainPurityTest {

    @ArchTest
    static final ArchRule domain_classes_do_not_depend_on_frameworks =
            noClasses().that().resideInAnyPackage(ArchUnitRuleConfig.BASE_PACKAGE + "..domain..")
                    .should().dependOnClassesThat(resideInAnyPackage(
                            "org.springframework..",
                            "jakarta.persistence..",
                            "com.fasterxml.jackson..",
                            "org.hibernate..",
                            "io.jsonwebtoken.."));
}
