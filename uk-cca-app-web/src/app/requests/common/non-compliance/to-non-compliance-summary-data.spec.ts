import { mockNonComplianceDetails } from '../../tasks/non-compliance-details/testing/mock-data';
import { toNonComplianceSummaryData } from './to-non-compliance-summary-data';

describe('toNonComplianceSummaryData', () => {
  const allRelevantWorkflows = {
    'WF-001': 'Workflow 1',
    'WF-002': 'Workflow 2',
    'WF-003': 'Workflow 3',
  };

  const allRelevantFacilities = {
    'FAC-001': 'Facility 1',
    'FAC-002': 'Facility 2',
    'HIST-001': 'Historical Facility 1',
  };

  it('should return summary data with 3 sections', () => {
    const summaryData = toNonComplianceSummaryData(
      mockNonComplianceDetails,
      allRelevantWorkflows,
      allRelevantFacilities,
      true,
    );

    expect(summaryData).toHaveLength(3);
    expect(summaryData.map((section) => section.header)).toEqual([
      'Non-compliance details',
      'Relevant items of non-compliance',
      'Enforcement details',
    ]);
  });

  it('should include explanation row when isEnforcementResponseNoticeRequired is false', () => {
    const summaryData = toNonComplianceSummaryData(
      {
        ...mockNonComplianceDetails,
        isEnforcementResponseNoticeRequired: false,
        explanation: 'Some reason',
      },
      allRelevantWorkflows,
      allRelevantFacilities,
      true,
    );

    const enforcementSection = summaryData.find((section) => section.header === 'Enforcement details');

    expect(enforcementSection?.data.some((row) => row.key.includes('Explain why you will not be issuing'))).toBe(true);
  });

  it('should NOT include explanation row when isEnforcementResponseNoticeRequired is true', () => {
    const summaryData = toNonComplianceSummaryData(
      {
        ...mockNonComplianceDetails,
        isEnforcementResponseNoticeRequired: true,
        explanation: 'Ignored explanation',
      },
      allRelevantWorkflows,
      allRelevantFacilities,
      true,
    );

    const enforcementSection = summaryData.find((section) => section.header === 'Enforcement details');

    expect(enforcementSection?.data.some((row) => row.key.includes('Explain why you will not be issuing'))).toBe(false);
  });

  it('should use the updated non-compliance type label text', () => {
    const summaryData = toNonComplianceSummaryData(
      mockNonComplianceDetails,
      allRelevantWorkflows,
      allRelevantFacilities,
      true,
    );

    const detailsSection = summaryData.find((section) => section.header === 'Non-compliance details');
    const typeRow = detailsSection?.data.find((row) => row.key === 'Type of non-compliance');

    expect(typeRow?.value).toEqual(['failure to provide the Target Period Report or Interim Target Period Report']);
  });

  it('should map workflow IDs to labels using allRelevantWorkflows', () => {
    const summaryData = toNonComplianceSummaryData(
      {
        ...mockNonComplianceDetails,
        relevantWorkflows: ['WF-003', 'WF-001'],
      },
      allRelevantWorkflows,
      allRelevantFacilities,
      true,
    );

    const relevantItemsSection = summaryData.find((section) => section.header === 'Relevant items of non-compliance');
    const workflowsRow = relevantItemsSection?.data.find((row) => row.key === 'Relevant workflows');

    expect(workflowsRow?.value).toEqual(['WF-001 - Workflow 1', 'WF-003 - Workflow 3']);
  });

  it('should transform workflow labels using the workflow history display names', () => {
    const summaryData = toNonComplianceSummaryData(
      {
        ...mockNonComplianceDetails,
        relevantWorkflows: ['WF-001', 'WF-002', 'WF-003'],
      },
      {
        'WF-001': 'UNDERLYING_AGREEMENT_VARIATION',
        'WF-002': 'BUY_OUT_SURPLUS_ACCOUNT_PROCESSING',
        'WF-003': 'PERFORMANCE_DATA_SPREADSHEET_PROCESSING',
      },
      allRelevantFacilities,
      true,
    );

    const relevantItemsSection = summaryData.find((section) => section.header === 'Relevant items of non-compliance');
    const workflowsRow = relevantItemsSection?.data.find((row) => row.key === 'Relevant workflows');

    expect(workflowsRow?.value).toEqual([
      'WF-001 - Underlying agreement variation',
      'WF-002 - Buy-out and surplus',
      'WF-003 - Performance Data',
    ]);
  });

  it('should map facility IDs to labels when the lookup contains a name', () => {
    const summaryData = toNonComplianceSummaryData(
      {
        ...mockNonComplianceDetails,
        relevantFacilities: [
          { facilityBusinessId: 'HIST-001', isHistorical: true },
          { facilityBusinessId: 'FAC-001', isHistorical: false },
        ],
      },
      allRelevantWorkflows,
      allRelevantFacilities,
      true,
    );

    const relevantItemsSection = summaryData.find((section) => section.header === 'Relevant items of non-compliance');
    const facilitiesRow = relevantItemsSection?.data.find((row) => row.key === 'Relevant facilities');

    expect(facilitiesRow?.value).toEqual(['FAC-001 - Facility 1', 'HIST-001 - Historical Facility 1']);
  });

  it('should sort workflows and mixed historical and non-historical facilities alphabetically in the summary', () => {
    const summaryData = toNonComplianceSummaryData(
      {
        ...mockNonComplianceDetails,
        relevantWorkflows: ['WF-010', 'WF-002', 'WF-001'],
        relevantFacilities: [
          { facilityBusinessId: 'HIST-010', isHistorical: true },
          { facilityBusinessId: 'FAC-002', isHistorical: false },
          { facilityBusinessId: 'HIST-001', isHistorical: true },
          { facilityBusinessId: 'FAC-001', isHistorical: false },
        ],
      },
      {
        ...allRelevantWorkflows,
        'WF-010': 'Workflow 10',
      },
      {
        ...allRelevantFacilities,
        'HIST-010': 'Historical Facility 10',
      },
      true,
    );

    const relevantItemsSection = summaryData.find((section) => section.header === 'Relevant items of non-compliance');
    const workflowsRow = relevantItemsSection?.data.find((row) => row.key === 'Relevant workflows');
    const facilitiesRow = relevantItemsSection?.data.find((row) => row.key === 'Relevant facilities');

    expect(workflowsRow?.value).toEqual(['WF-001 - Workflow 1', 'WF-002 - Workflow 2', 'WF-010 - Workflow 10']);
    expect(facilitiesRow?.value).toEqual([
      'FAC-001 - Facility 1',
      'FAC-002 - Facility 2',
      'HIST-001 - Historical Facility 1',
      'HIST-010 - Historical Facility 10',
    ]);
  });
});
