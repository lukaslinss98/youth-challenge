package com.youth.wearables.architecture;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.assignableTo;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.equivalentTo;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.base.DescribedPredicate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@AnalyzeClasses(packages = ArchUnitRuleConfig.BASE_PACKAGE, importOptions = ImportOption.DoNotIncludeTests.class)
class ControllerConventionsTest {

    private static final DescribedPredicate<JavaClass> ALLOWED_CONTROLLER_RETURN_TYPES =
            resideInAnyPackage(ArchUnitRuleConfig.BASE_PACKAGE + "..infrastructure.controllers.dto..")
                    .or(equivalentTo(void.class))
                    .or(assignableTo(ResponseEntity.class))
                    .or(equivalentTo(String.class))
                    .as("a DTO, void, ResponseEntity or String");

    @ArchTest
    static final ArchRule controllers_are_package_private =
            classes().that().areAnnotatedWith(RestController.class)
                    .should().notHaveModifier(JavaModifier.PUBLIC);

    @ArchTest
    static final ArchRule controllers_reside_in_infrastructure =
            classes().that().areAnnotatedWith(RestController.class)
                    .should().resideInAnyPackage(ArchUnitRuleConfig.BASE_PACKAGE + "..infrastructure..");

    @ArchTest
    static final ArchRule controller_public_methods_do_not_return_domain_or_application_types =
            methods().that().areDeclaredInClassesThat().areAnnotatedWith(RestController.class)
                    .and().arePublic()
                    .should().haveRawReturnType(ALLOWED_CONTROLLER_RETURN_TYPES);
}
