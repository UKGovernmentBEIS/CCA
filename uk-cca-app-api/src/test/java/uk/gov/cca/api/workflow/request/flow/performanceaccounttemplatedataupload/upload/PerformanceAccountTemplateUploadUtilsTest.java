package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.utils.PerformanceAccountTemplateUploadUtils;

class PerformanceAccountTemplateUploadUtilsTest {

	@Test
    void testValidFilenames() {
        assertTrue(matchesRegex("AIC-T00001_PAT_TP10_V1.xlsx"));
        assertTrue(matchesRegex("MY-COMPANY-01-T54321_PAT_TarPer6.xlsx"));
        assertTrue(matchesRegex("XYZ_123-T00001_PAT_TP6000_V5.xlsx"));
        assertTrue(matchesRegex("A-T99999_PAT_TP1_V100.xlsx"));
    }

    @Test
    void testInvalidFilenames() {
        assertFalse(matchesRegex("ABC-T1234_PAT_TP61234_V1.xlsx")); //wrong account business id
        assertFalse(matchesRegex("ABC_PAT_TP61234_V1.xlsx")); //missing at all account business id
        assertFalse(matchesRegex("ABC-T12345_PAT_TP61234_VA.xlsx")); //wrong version
        assertFalse(matchesRegex("ABC-T12345_TP61234_V1.xlsx")); //missing PAT fixed word
        assertFalse(matchesRegex("ABC-T12345_PAT_TP61234.docx")); //wrong extension
    }

    private boolean matchesRegex(String filename) {
    	return Pattern.matches(PerformanceAccountTemplateUploadUtils.REPORT_FILE_NAME_REGEX, filename);
    }
}
