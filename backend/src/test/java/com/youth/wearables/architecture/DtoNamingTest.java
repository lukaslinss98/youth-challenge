package com.youth.wearables.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = ArchUnitRuleConfig.BASE_PACKAGE, importOptions = ImportOption.DoNotIncludeTests.class)
class DtoNamingTest {

    @ArchTest
    static final ArchRule controller_dtos_are_named_request_or_response_dto =
            classes().that().resideInAnyPackage(ArchUnitRuleConfig.BASE_PACKAGE + "..infrastructure.controllers.dto..")
                    .and().areTopLevelClasses()
                    .should().haveSimpleNameEndingWith("RequestDto")
                    .orShould().haveSimpleNameEndingWith("ResponseDto");
}
