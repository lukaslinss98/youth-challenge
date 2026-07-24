package com.youth.wearables.architecture;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.dependencies.SliceAssignment;
import com.tngtech.archunit.library.dependencies.SliceIdentifier;

@AnalyzeClasses(packages = ArchUnitRuleConfig.BASE_PACKAGE, importOptions = ImportOption.DoNotIncludeTests.class)
class SliceIsolationTest {

    @ArchTest
    static final ArchRule bounded_contexts_are_free_of_cyclic_dependencies =
            slices().assignedFrom(new BoundedContextAssignment()).should().beFreeOfCycles();

    @ArchTest
    static final ArchRule shared_kernel_does_not_depend_on_bounded_contexts =
            noClasses().that().resideInAPackage(ArchUnitRuleConfig.BASE_PACKAGE + ".shared..")
                    .should().dependOnClassesThat(resideInAnyPackage(
                            ArchUnitRuleConfig.BASE_PACKAGE + ".externaldevices..",
                            ArchUnitRuleConfig.BASE_PACKAGE + ".usermanagement.."));

    private static final class BoundedContextAssignment implements SliceAssignment {

        @Override
        public SliceIdentifier getIdentifierOf(JavaClass javaClass) {
            String topLevel = ArchUnitRuleConfig.topLevelSlice(javaClass);
            if (topLevel == null || ArchUnitRuleConfig.EXCLUDED_FROM_SLICES.contains(topLevel)) {
                return SliceIdentifier.ignore();
            }
            return SliceIdentifier.of(topLevel);
        }

        @Override
        public String getDescription() {
            return "Bounded Contexts";
        }
    }
}
