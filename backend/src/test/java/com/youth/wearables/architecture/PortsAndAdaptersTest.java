package com.youth.wearables.architecture;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import java.util.Objects;

@AnalyzeClasses(packages = ArchUnitRuleConfig.BASE_PACKAGE, importOptions = ImportOption.DoNotIncludeTests.class)
class PortsAndAdaptersTest {

    @ArchTest
    static final ArchRule port_interfaces_do_not_depend_on_infrastructure =
            noClasses().that().areInterfaces()
                    .and().resideInAnyPackage(ArchUnitRuleConfig.BASE_PACKAGE + "..application..")
                    .should().dependOnClassesThat(resideInAnyPackage(
                            ArchUnitRuleConfig.BASE_PACKAGE + "..infrastructure.."));

    @ArchTest
    static final ArchRule same_slice_port_implementations_reside_in_infrastructure =
            classes().that().implement(resideInAnyPackage(ArchUnitRuleConfig.BASE_PACKAGE + "..application.."))
                    .should(residesInInfrastructureWhenImplementingItsOwnSlicesPort());

    @ArchTest
    static final ArchRule persistence_adapters_implementing_ports_are_named_repository_impl =
            classes().that().resideInAnyPackage(ArchUnitRuleConfig.BASE_PACKAGE + "..infrastructure.persistence..")
                    .and().implement(resideInAnyPackage(ArchUnitRuleConfig.BASE_PACKAGE + "..application.."))
                    .should().haveSimpleNameEndingWith("RepositoryImpl");

    private static ArchCondition<JavaClass> residesInInfrastructureWhenImplementingItsOwnSlicesPort() {
        return new ArchCondition<>(
                "reside in infrastructure when implementing a port from its own bounded context") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                String implementorSlice = ArchUnitRuleConfig.topLevelSlice(javaClass);
                boolean inInfrastructure = javaClass.getPackageName().contains(".infrastructure.");
                javaClass.getInterfaces().stream()
                        .map(JavaClass.class::cast)
                        .filter(port -> port.getPackageName().contains(".application."))
                        .filter(port -> Objects.equals(ArchUnitRuleConfig.topLevelSlice(port), implementorSlice))
                        .forEach(port -> events.add(inInfrastructure
                                ? SimpleConditionEvent.satisfied(javaClass, javaClass.getFullName()
                                        + " resides in infrastructure")
                                : SimpleConditionEvent.violated(javaClass, javaClass.getFullName()
                                        + " implements same-slice port " + port.getFullName()
                                        + " but does not reside in infrastructure")));
            }
        };
    }
}
