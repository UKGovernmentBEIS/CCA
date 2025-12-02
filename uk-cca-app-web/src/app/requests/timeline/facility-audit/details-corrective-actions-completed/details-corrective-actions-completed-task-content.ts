import { TaskSection } from '@netz/common/model';

const routePrefix = 'audit-details-corrective-actions';

export function getDetailsCorrectiveActionsSections(): TaskSection[] {
  return [
    {
      title: 'Audit details',
      tasks: [
        {
          status: '',
          link: `${routePrefix}/details`,
          linkText: 'Details of the audit',
        },
      ],
    },
    {
      title: 'Corrective actions',
      tasks: [
        {
          status: '',
          link: `${routePrefix}/actions`,
          linkText: 'Add corrective actions',
        },
      ],
    },
  ];
}
