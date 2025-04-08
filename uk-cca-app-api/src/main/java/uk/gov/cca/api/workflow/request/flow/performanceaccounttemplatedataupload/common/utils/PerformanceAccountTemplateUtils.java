package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.utils;

import java.time.MonthDay;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PerformanceAccountTemplateUtils {

	public final MonthDay TERMINATED_END_DATE_FOR_ELIGIBLE_ACCOUNTS_MONTH_DAY = MonthDay.of(5, 1);
	
}
