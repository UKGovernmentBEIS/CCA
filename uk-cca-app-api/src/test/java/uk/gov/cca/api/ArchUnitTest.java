package uk.gov.cca.api;


import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import java.util.Arrays;
import java.util.List;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static uk.gov.cca.api.ArchUnitTest.BASE_PACKAGE;
import static uk.gov.cca.api.ArchUnitTest.COMMON_BASE_PACKAGE;

@AnalyzeClasses(packages = {BASE_PACKAGE, COMMON_BASE_PACKAGE}, importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchUnitTest {

    static final String BASE_PACKAGE = "uk.gov.cca.api";

    static final String COMMON_BASE_PACKAGE = "uk.gov.netz.api";

    //netz
    static final String TERMS_PACKAGE = COMMON_BASE_PACKAGE + ".terms..";
    static final String COMMON_PACKAGE = COMMON_BASE_PACKAGE + ".common..";
    static final String REFERENCE_DATA_PACKAGE = COMMON_BASE_PACKAGE + ".referencedata..";
    static final String FILES_PACKAGE = COMMON_BASE_PACKAGE + ".files..";
    static final String DOCUMENT_TEMPLATE_PACKAGE = COMMON_BASE_PACKAGE + ".documenttemplate..";
    static final String TOKEN_PACKAGE = COMMON_BASE_PACKAGE + ".token..";
    static final String CA_PACKAGE = COMMON_BASE_PACKAGE + ".competentauthority..";
    static final String VERIFICATION_BODY_PACKAGE = COMMON_BASE_PACKAGE + ".verificationbody..";
    static final String USER_INFO_API_PACKAGE = COMMON_BASE_PACKAGE + ".userinfoapi..";
    static final String ACCOUNT_PACKAGE = COMMON_BASE_PACKAGE + ".account..";
    static final String WORKFLOW_PACKAGE = COMMON_BASE_PACKAGE + ".workflow..";
    
    //CCA
    static final String AUTHORIZATION_PACKAGE = BASE_PACKAGE + ".authorization..";
    static final String COMMON_CCA_PACKAGE = BASE_PACKAGE + ".common..";
    static final String NOTIFICATION_PACKAGE = BASE_PACKAGE + ".notification..";
    static final String USER_PACKAGE = BASE_PACKAGE + ".user..";
    static final String ACCOUNT_TARGET_UNIT_PACKAGE = BASE_PACKAGE + ".account..";
    static final String SECTOR_ASSOCIATION_PACKAGE = BASE_PACKAGE + ".sectorassociation..";
    static final String FACILITY_PACKAGE = BASE_PACKAGE + ".facility..";
    static final String TARGET_PERIOD_PACKAGE = BASE_PACKAGE + ".targetperiodreporting.targetperiod..";
    static final String PERFORMANCE_DATA_PACKAGE = BASE_PACKAGE + ".targetperiodreporting.performancedata..";
    static final String PERFORMANCE_ACCOUNT_TEMPLATE_DATA_PACKAGE = BASE_PACKAGE + ".targetperiodreporting.performanceaccounttemplatedata..";
    static final String UNA_PACKAGE = BASE_PACKAGE + ".underlyingagreement..";
    static final String SUBSISTENCE_FEES_PACKAGE = BASE_PACKAGE + ".subsistencefees..";
    static final String BUY_OUT_SURPLUS_PACKAGE = BASE_PACKAGE + ".targetperiodreporting.buyoutsurplus..";
    static final String WORKFLOW_CCA_PACKAGE = BASE_PACKAGE + ".workflow..";
    static final String FACILITY_CERTIFICATION_PACKAGE = BASE_PACKAGE + ".targetperiodreporting.facilitycertification..";

    static final String WEB_PACKAGE = BASE_PACKAGE + ".web..";

    static final List<String> ALL_PACKAGES = List.of(
            TERMS_PACKAGE,
            COMMON_PACKAGE,
            COMMON_CCA_PACKAGE,
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
            SECTOR_ASSOCIATION_PACKAGE,
            ACCOUNT_TARGET_UNIT_PACKAGE,
            UNA_PACKAGE,
            FACILITY_PACKAGE,
            TARGET_PERIOD_PACKAGE,
            PERFORMANCE_DATA_PACKAGE,
            PERFORMANCE_ACCOUNT_TEMPLATE_DATA_PACKAGE,
            BUY_OUT_SURPLUS_PACKAGE,
            SUBSISTENCE_FEES_PACKAGE,
            WORKFLOW_CCA_PACKAGE,
            FACILITY_CERTIFICATION_PACKAGE,

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
                            COMMON_PACKAGE,
                            AUTHORIZATION_PACKAGE));

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
    public static final ArchRule documentTemplatePackageChecks =
            noClasses().that()
                    .resideInAPackage(DOCUMENT_TEMPLATE_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            DOCUMENT_TEMPLATE_PACKAGE,
                            FILES_PACKAGE,
                            COMMON_PACKAGE,
                            AUTHORIZATION_PACKAGE,
                            CA_PACKAGE,
                            TOKEN_PACKAGE));

    @ArchTest
    public static final ArchRule notificationPackageChecks =
            noClasses().that()
                    .resideInAPackage(NOTIFICATION_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            NOTIFICATION_PACKAGE,
                            COMMON_PACKAGE,
                            COMMON_CCA_PACKAGE,
                            AUTHORIZATION_PACKAGE,
                            CA_PACKAGE));

    @ArchTest
    public static final ArchRule tokenPackageChecks =
            noClasses().that()
                    .resideInAPackage(TOKEN_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            TOKEN_PACKAGE,
                            COMMON_PACKAGE,
                            COMMON_CCA_PACKAGE));

    @ArchTest
    public static final ArchRule authorizationPackageChecks =
            noClasses().that()
                    .resideInAPackage(AUTHORIZATION_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            AUTHORIZATION_PACKAGE,
                            COMMON_PACKAGE,
                            COMMON_CCA_PACKAGE,
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
    public static final ArchRule commonCcaPackageChecks =
            noClasses().that()
                    .resideInAPackage(COMMON_CCA_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            COMMON_CCA_PACKAGE,
                            COMMON_PACKAGE,
                            FILES_PACKAGE));

    @ArchTest
    public static final ArchRule userPackageChecks =
            noClasses().that()
                    .resideInAPackage(USER_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            USER_PACKAGE,
                            USER_INFO_API_PACKAGE,
                            COMMON_PACKAGE,
                            COMMON_CCA_PACKAGE,
                            TOKEN_PACKAGE,
                            AUTHORIZATION_PACKAGE,
                            NOTIFICATION_PACKAGE,
                            ACCOUNT_PACKAGE /* CYCLIC3: to get installation name for notification */,
                            ACCOUNT_TARGET_UNIT_PACKAGE,
                            SECTOR_ASSOCIATION_PACKAGE  /* CYCLIC3: to get sector name for notification */,
                            CA_PACKAGE, /* for regulator invitation */
                            VERIFICATION_BODY_PACKAGE /* for verifier invitation */,
                            FILES_PACKAGE /* for signatures */));
    
    @ArchTest
    public static final ArchRule userInfoApiPackageChecks =
            noClasses().that()
                    .resideInAPackage(USER_INFO_API_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            USER_INFO_API_PACKAGE,
                            COMMON_PACKAGE));
    
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
                            VERIFICATION_BODY_PACKAGE));
    
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
                            DOCUMENT_TEMPLATE_PACKAGE,
                            VERIFICATION_BODY_PACKAGE));
    
    @ArchTest
    public static final ArchRule sectorAssociationPackageChecks =
            noClasses().that()
                    .resideInAPackage(SECTOR_ASSOCIATION_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                    		SECTOR_ASSOCIATION_PACKAGE,
                            COMMON_PACKAGE,
                            COMMON_CCA_PACKAGE,
                            AUTHORIZATION_PACKAGE,
                            REFERENCE_DATA_PACKAGE,
                            TOKEN_PACKAGE,
                            CA_PACKAGE,
                            FILES_PACKAGE /* for sector scheme files */));
    
    @ArchTest
    public static final ArchRule accountTargetUnitPackageChecks =
            noClasses().that()
                    .resideInAPackage(ACCOUNT_TARGET_UNIT_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            ACCOUNT_TARGET_UNIT_PACKAGE,
                            ACCOUNT_PACKAGE,
                            COMMON_PACKAGE,
                            COMMON_CCA_PACKAGE,
                            AUTHORIZATION_PACKAGE,
                            SECTOR_ASSOCIATION_PACKAGE,
                            CA_PACKAGE,
                            TOKEN_PACKAGE,
                            REFERENCE_DATA_PACKAGE,
                            USER_INFO_API_PACKAGE));
    
    @ArchTest
    public static final ArchRule unaPackageChecks =
            noClasses().that()
                    .resideInAPackage(UNA_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            UNA_PACKAGE,
                            ACCOUNT_PACKAGE,
                            ACCOUNT_TARGET_UNIT_PACKAGE,
                            COMMON_PACKAGE,
                            COMMON_CCA_PACKAGE,
                            AUTHORIZATION_PACKAGE,
                            CA_PACKAGE,
                            FILES_PACKAGE,
                            FACILITY_PACKAGE,
                            TOKEN_PACKAGE
                            ));
    
    @ArchTest
    public static final ArchRule facilityPackageChecks =
            noClasses().that()
                    .resideInAPackage(FACILITY_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            FACILITY_PACKAGE,
                            ACCOUNT_PACKAGE,
                            ACCOUNT_TARGET_UNIT_PACKAGE,
                            COMMON_PACKAGE,
                            COMMON_CCA_PACKAGE,
                            AUTHORIZATION_PACKAGE,
                            REFERENCE_DATA_PACKAGE,
                            CA_PACKAGE
                            ));
    
    @ArchTest
    public static final ArchRule targetPeriodPackageChecks =
            noClasses().that()
                    .resideInAPackage(TARGET_PERIOD_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                    		TARGET_PERIOD_PACKAGE,
                            COMMON_PACKAGE));
    
    @ArchTest
    public static final ArchRule performanceDataPackageChecks =
            noClasses().that()
                    .resideInAPackage(PERFORMANCE_DATA_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                    		PERFORMANCE_DATA_PACKAGE,
                            TARGET_PERIOD_PACKAGE,
                            ACCOUNT_PACKAGE,
                            ACCOUNT_TARGET_UNIT_PACKAGE,
                            SECTOR_ASSOCIATION_PACKAGE,
                            COMMON_PACKAGE,
                            COMMON_CCA_PACKAGE,
                            AUTHORIZATION_PACKAGE,
                            REFERENCE_DATA_PACKAGE,
                            CA_PACKAGE,
                            FILES_PACKAGE,
                            TOKEN_PACKAGE
                            ));
    
    @ArchTest
    public static final ArchRule performanceAccountTemplateDataPackageChecks =
            noClasses().that()
                    .resideInAPackage(PERFORMANCE_ACCOUNT_TEMPLATE_DATA_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                    		PERFORMANCE_ACCOUNT_TEMPLATE_DATA_PACKAGE,
                            TARGET_PERIOD_PACKAGE,
                            ACCOUNT_PACKAGE,
                            ACCOUNT_TARGET_UNIT_PACKAGE,
                            SECTOR_ASSOCIATION_PACKAGE,
                            COMMON_PACKAGE,
                            COMMON_CCA_PACKAGE,
                            AUTHORIZATION_PACKAGE,
                            REFERENCE_DATA_PACKAGE,
                            CA_PACKAGE,
                            FILES_PACKAGE,
                            TOKEN_PACKAGE
                            ));
    
    @ArchTest
    public static final ArchRule buyOutSurplusPackageChecks =
            noClasses().that()
                    .resideInAPackage(BUY_OUT_SURPLUS_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                    		BUY_OUT_SURPLUS_PACKAGE,
                            ACCOUNT_PACKAGE,
                            ACCOUNT_TARGET_UNIT_PACKAGE,
                            SECTOR_ASSOCIATION_PACKAGE,
                            COMMON_PACKAGE,
                            COMMON_CCA_PACKAGE,
                            AUTHORIZATION_PACKAGE,
                            REFERENCE_DATA_PACKAGE,
                            CA_PACKAGE,
                            FILES_PACKAGE,
                            TOKEN_PACKAGE,
                            TARGET_PERIOD_PACKAGE,
                            PERFORMANCE_DATA_PACKAGE
                            ));
    
    @ArchTest
    public static final ArchRule subsistenceFeesPackageChecks =
            noClasses().that()
                    .resideInAPackage(SUBSISTENCE_FEES_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                    		SUBSISTENCE_FEES_PACKAGE,
                            ACCOUNT_PACKAGE,
                            ACCOUNT_TARGET_UNIT_PACKAGE,
                            SECTOR_ASSOCIATION_PACKAGE,
                            COMMON_PACKAGE,
                            COMMON_CCA_PACKAGE,
                            AUTHORIZATION_PACKAGE,
                            REFERENCE_DATA_PACKAGE,
                            CA_PACKAGE,
                            FILES_PACKAGE,
                            TOKEN_PACKAGE,
                            FACILITY_PACKAGE
                            ));

    @ArchTest
    public static final ArchRule facilityCertificationPackageChecks =
            noClasses().that()
                    .resideInAPackage(FACILITY_CERTIFICATION_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            FACILITY_CERTIFICATION_PACKAGE,
                            COMMON_PACKAGE,
                            COMMON_CCA_PACKAGE,
                            AUTHORIZATION_PACKAGE,
                            CA_PACKAGE,
                            FACILITY_PACKAGE,
                            TARGET_PERIOD_PACKAGE
                    ));
    
    @ArchTest
    public static final ArchRule workflowCcaPackageChecks =
            noClasses().that()
                    .resideInAPackage(WORKFLOW_CCA_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            WORKFLOW_CCA_PACKAGE,
                            WORKFLOW_PACKAGE,
                            COMMON_PACKAGE,
                            COMMON_CCA_PACKAGE,
                            TOKEN_PACKAGE,
                            AUTHORIZATION_PACKAGE,
                            CA_PACKAGE,
                            NOTIFICATION_PACKAGE,
                            ACCOUNT_PACKAGE,
                            ACCOUNT_TARGET_UNIT_PACKAGE,
                            SECTOR_ASSOCIATION_PACKAGE,
                            FILES_PACKAGE,
                            USER_PACKAGE,
                            DOCUMENT_TEMPLATE_PACKAGE,
                            REFERENCE_DATA_PACKAGE,
                            UNA_PACKAGE,
                            FACILITY_PACKAGE,
                            TARGET_PERIOD_PACKAGE,
                            PERFORMANCE_DATA_PACKAGE,
                            PERFORMANCE_ACCOUNT_TEMPLATE_DATA_PACKAGE,
                            BUY_OUT_SURPLUS_PACKAGE,
                            FACILITY_CERTIFICATION_PACKAGE,
                            SUBSISTENCE_FEES_PACKAGE));
    
    private static String[] except(String... packages) {
        return ALL_PACKAGES.stream()
                .filter(p -> !Arrays.asList(packages).contains(p))
                .toList()
                .toArray(String[]::new);
    }
}
