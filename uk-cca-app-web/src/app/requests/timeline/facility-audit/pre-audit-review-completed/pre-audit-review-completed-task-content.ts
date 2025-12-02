import { TaskSection } from '@netz/common/model';

const routePrefix = 'pre-audit-review';

export function getPreAuditReviewSections(): TaskSection[] {
  return [
    {
      title: 'Pre-audit review reason',
      tasks: [
        {
          status: '',
          link: `${routePrefix}/reason`,
          linkText: 'Describe the reason for pre-audit review',
        },
      ],
    },
    {
      title: 'Requested documents',
      tasks: [
        {
          status: '',
          link: `${routePrefix}/requested-documents`,
          linkText: 'Upload documents',
        },
      ],
    },
    {
      title: 'Pre-audit review determination',
      tasks: [
        {
          status: '',
          link: `${routePrefix}/determination`,
          linkText: 'Pre-audit review determination',
        },
      ],
    },
  ];
}
