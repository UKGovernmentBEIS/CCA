import { mockNonComplianceDetails } from '../testing/mock-data';
import { toNonComplianceSummaryData } from './check-your-answers-summary-data';

describe('toNonComplianceSummaryData', () => {
  const allRelevantWorkflows = {
    'WF-001': 'Workflow 1',
    'WF-002': 'Workflow 2',
    'WF-003': 'Workflow 3',
  };

  const allRelevantFacilities = {
    'FAC-001': 'Facility 1',
    'FAC-002': 'Facility 2',
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
        relevantWorkflows: ['WF-001', 'WF-003'],
      },
      allRelevantWorkflows,
      allRelevantFacilities,
      true,
    );

    const relevantItemsSection = summaryData.find((section) => section.header === 'Relevant items of non-compliance');
    const workflowsRow = relevantItemsSection?.data.find((row) => row.key === 'Relevant workflows');

    expect(workflowsRow?.value).toEqual(['Workflow 1', 'Workflow 3']);
  });

  it('should transform enum-like workflow labels to readable text', () => {
    const summaryData = toNonComplianceSummaryData(
      {
        ...mockNonComplianceDetails,
        relevantWorkflows: ['WF-001'],
      },
      {
        'WF-001': 'UNDERLYING_AGREEMENT_VARIATION',
      },
      allRelevantFacilities,
      true,
    );

    const relevantItemsSection = summaryData.find((section) => section.header === 'Relevant items of non-compliance');
    const workflowsRow = relevantItemsSection?.data.find((row) => row.key === 'Relevant workflows');

    expect(workflowsRow?.value).toEqual(['Underlying Agreement Variation']);
  });

  it('should map active facility IDs to labels and keep historical IDs as-is', () => {
    const summaryData = toNonComplianceSummaryData(
      {
        ...mockNonComplianceDetails,
        relevantFacilities: [
          { facilityBusinessId: 'FAC-001', isHistorical: false },
          { facilityBusinessId: 'HIST-001', isHistorical: true },
        ],
      },
      allRelevantWorkflows,
      allRelevantFacilities,
      true,
    );

    const relevantItemsSection = summaryData.find((section) => section.header === 'Relevant items of non-compliance');
    const facilitiesRow = relevantItemsSection?.data.find((row) => row.key === 'Relevant facilities');

    expect(facilitiesRow?.value).toEqual(['Facility 1', 'HIST-001']);
  });
});
