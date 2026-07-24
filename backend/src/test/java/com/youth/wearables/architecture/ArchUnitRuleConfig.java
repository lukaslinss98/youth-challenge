package com.youth.wearables.architecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import java.util.Set;
import java.util.stream.Collectors;

final class ArchUnitRuleConfig {

    static final String BASE_PACKAGE = "com.youth.wearables";
    static final Set<String> EXCLUDED_FROM_SLICES = Set.of("config", "shared");

    private ArchUnitRuleConfig() {}

    static Set<String> discoverBoundedContexts(JavaClasses classes) {
        return classes.stream()
                .map(JavaClass::getPackageName)
                .map(ArchUnitRuleConfig::topLevelSlice)
                .filter(top -> top != null && !EXCLUDED_FROM_SLICES.contains(top))
                .collect(Collectors.toSet());
    }

    static String topLevelSlice(JavaClass javaClass) {
        return topLevelSlice(javaClass.getPackageName());
    }

    static String topLevelSlice(String packageName) {
        if (!packageName.startsWith(BASE_PACKAGE + ".")) {
            return null;
        }
        String remainder = packageName.substring(BASE_PACKAGE.length() + 1);
        return remainder.isEmpty() ? null : remainder.split("\\.")[0];
    }
}
