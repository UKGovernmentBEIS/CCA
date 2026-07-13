import { TaskSection } from '@netz/common/model';

export function getAllTPRReportingFormSubmittedSections(): TaskSection[] {
  return [
    {
      title: 'Energy/fuel details',
      tasks: [
        {
          status: '',
          linkText: 'Provide energy/fuel amount consumed',
          link: 'tpr-reporting-form-submitted/energy-fuel-amount',
        },
      ],
    },
    {
      title: 'Throughput details',
      tasks: [
        {
          status: '',
          linkText: 'Provide target period throughput details',
          link: 'tpr-reporting-form-submitted/throughput',
        },
      ],
    },
    {
      title: 'TPR results',
      tasks: [
        {
          status: '',
          linkText: 'Calculated results',
          link: 'tpr-reporting-form-submitted/submit',
        },
      ],
    },
  ];
}
