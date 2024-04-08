package uk.gov.cca.api;


import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import java.util.Arrays;
import java.util.List;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static java.util.stream.Collectors.toList;
import static uk.gov.cca.api.ArchUnitTest.BASE_PACKAGE;
import static uk.gov.cca.api.ArchUnitTest.COMMON_BASE_PACKAGE;

@AnalyzeClasses(packages = {BASE_PACKAGE, COMMON_BASE_PACKAGE}, importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchUnitTest {

    static final String BASE_PACKAGE = "uk.gov.cca.api";

    static final String COMMON_BASE_PACKAGE = "uk.gov.netz.api";

    static final String TERMS_PACKAGE = COMMON_BASE_PACKAGE + ".terms..";

    static final String COMMON_PACKAGE = COMMON_BASE_PACKAGE + ".common..";
    static final String REFERENCE_DATA_PACKAGE = BASE_PACKAGE + ".referencedata..";
    static final String FILES_PACKAGE = COMMON_BASE_PACKAGE + ".files..";
    static final String NOTIFICATION_PACKAGE = BASE_PACKAGE + ".notification..";
    static final String TOKEN_PACKAGE = COMMON_BASE_PACKAGE + ".token..";
    static final String AUTHORIZATION_PACKAGE = BASE_PACKAGE + ".authorization..";
    static final String CA_PACKAGE = COMMON_BASE_PACKAGE + ".competentauthority..";
    static final String VERIFICATION_BODY_PACKAGE = BASE_PACKAGE + ".verificationbody..";
    static final String USER_PACKAGE = BASE_PACKAGE + ".user..";
    static final String ACCOUNT_PACKAGE = BASE_PACKAGE + ".account..";

    static final String WORKFLOW_PACKAGE = BASE_PACKAGE + ".workflow..";

    static final String WEB_PACKAGE = BASE_PACKAGE + ".web..";

    static final List<String> ALL_PACKAGES = List.of(
            TERMS_PACKAGE,
            COMMON_PACKAGE,
            REFERENCE_DATA_PACKAGE,
            FILES_PACKAGE,
            NOTIFICATION_PACKAGE,
            TOKEN_PACKAGE,
            AUTHORIZATION_PACKAGE,
            CA_PACKAGE,
            VERIFICATION_BODY_PACKAGE,
            USER_PACKAGE,
            ACCOUNT_PACKAGE,
            WORKFLOW_PACKAGE,

            WEB_PACKAGE
    );

    /**
     CYCLIC2: verificationBody/authorization due to impact on Verifier authorities on VB status change (event)
     CYCLIC3: account/users
     CYCLIC4: permit/reporting for CalculationActivityDataMonitoringTier in CalculationParameterType
     **/

    @ArchTest
    public static final ArchRule termsPackageChecks =
            noClasses().that()
                    .resideInAPackage(TERMS_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            TERMS_PACKAGE,
                            COMMON_PACKAGE));

    @ArchTest
    public static final ArchRule commonPackageChecks =
            noClasses().that()
                    .resideInAPackage(COMMON_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            COMMON_PACKAGE));

    @ArchTest
    public static final ArchRule referencedataPackageChecks =
            noClasses().that()
                    .resideInAPackage(REFERENCE_DATA_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            REFERENCE_DATA_PACKAGE,
                            COMMON_PACKAGE));

    @ArchTest
    public static final ArchRule filesPackageChecks =
            noClasses().that()
                    .resideInAPackage(FILES_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            FILES_PACKAGE,
                            COMMON_PACKAGE,
                            TOKEN_PACKAGE));

    @ArchTest
    public static final ArchRule notificationPackageChecks =
            noClasses().that()
                    .resideInAPackage(NOTIFICATION_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            NOTIFICATION_PACKAGE,
                            COMMON_PACKAGE,
                            AUTHORIZATION_PACKAGE,
                            CA_PACKAGE,
                            FILES_PACKAGE,
                            TOKEN_PACKAGE));

    @ArchTest
    public static final ArchRule tokenPackageChecks =
            noClasses().that()
                    .resideInAPackage(TOKEN_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            TOKEN_PACKAGE,
                            COMMON_PACKAGE));

    @ArchTest
    public static final ArchRule authorizationPackageChecks =
            noClasses().that()
                    .resideInAPackage(AUTHORIZATION_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            AUTHORIZATION_PACKAGE,
                            COMMON_PACKAGE,
                            CA_PACKAGE,
                            VERIFICATION_BODY_PACKAGE /* CYCLIC2: due to impact on Verifier authorities on VB status change */));

    @ArchTest
    public static final ArchRule competentAuthorityPackageChecks =
            noClasses().that()
                    .resideInAPackage(CA_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            CA_PACKAGE,
                            COMMON_PACKAGE));

    @ArchTest
    public static final ArchRule verificationBodyPackageChecks =
            noClasses().that()
                    .resideInAPackage(VERIFICATION_BODY_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            VERIFICATION_BODY_PACKAGE,
                            COMMON_PACKAGE,
                            REFERENCE_DATA_PACKAGE,
                            AUTHORIZATION_PACKAGE));

    @ArchTest
    public static final ArchRule userPackageChecks =
            noClasses().that()
                    .resideInAPackage(USER_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            USER_PACKAGE,
                            COMMON_PACKAGE,
                            TOKEN_PACKAGE,
                            AUTHORIZATION_PACKAGE,
                            NOTIFICATION_PACKAGE,
                            ACCOUNT_PACKAGE /* CYCLIC3: to get installation name for notification */,
                            VERIFICATION_BODY_PACKAGE /* for verifier invitation */,
                            FILES_PACKAGE /* for signatures */));
    @ArchTest
    public static final ArchRule accountPackageChecks =
            noClasses().that()
                    .resideInAPackage(ACCOUNT_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            ACCOUNT_PACKAGE,
                            COMMON_PACKAGE,
                            AUTHORIZATION_PACKAGE,
                            CA_PACKAGE,
                            FILES_PACKAGE, /* for notes */
                            TOKEN_PACKAGE,
                            USER_PACKAGE, /* CYCLIC3:  getServiceContactDetails */
                            VERIFICATION_BODY_PACKAGE,
                            NOTIFICATION_PACKAGE,
                            REFERENCE_DATA_PACKAGE));


    @ArchTest
    public static final ArchRule workflowPackageChecks =
            noClasses().that()
                    .resideInAPackage(WORKFLOW_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            WORKFLOW_PACKAGE,
                            COMMON_PACKAGE,
                            TOKEN_PACKAGE,
                            AUTHORIZATION_PACKAGE,
                            CA_PACKAGE,
                            NOTIFICATION_PACKAGE,
                            ACCOUNT_PACKAGE,
                            FILES_PACKAGE,
                            USER_PACKAGE,
                            REFERENCE_DATA_PACKAGE,
                            VERIFICATION_BODY_PACKAGE));



    private static String[] except(String... packages) {
        return ALL_PACKAGES.stream()
                .filter(p -> !Arrays.asList(packages).contains(p))
                .collect(toList())
                .toArray(String[]::new);
    }
}