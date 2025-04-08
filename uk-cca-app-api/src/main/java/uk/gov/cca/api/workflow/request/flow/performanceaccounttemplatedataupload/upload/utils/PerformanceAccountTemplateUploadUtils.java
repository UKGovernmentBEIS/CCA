package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.utils;

import lombok.experimental.UtilityClass;
import uk.gov.cca.api.account.util.TargetUnitAccountBusinessIdUtil;

@UtilityClass
public class PerformanceAccountTemplateUploadUtils {
	
	public final String REPORT_FILE_NAME_REGEX = "^(?<targetunit>"
			+ TargetUnitAccountBusinessIdUtil.TARGET_UNIT_BUSINESS_ID_REGEX.substring(1,
					TargetUnitAccountBusinessIdUtil.TARGET_UNIT_BUSINESS_ID_REGEX.length() - 1)
			+ ")_PAT_(?<targetperiod>[A-Za-z0-9]+)(?<version>_V\\d+)?\\.(?<extension>xlsx)$";
	
}
