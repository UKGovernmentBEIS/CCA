import { SummaryData } from './type';

export const summaryData: SummaryData = [
  {
    header: 'Personal Information',
    data: [
      { key: 'Name', value: ['John Doe'] },
      { key: 'Email', value: ['john.doe@example.com'], change: true },
      { key: 'Phone', value: ['+123456789'], preline: true },
    ],
    changeLink: '/change-personal-info',
  },
  {
    header: 'Address',
    data: [
      { key: 'Street', value: ['123 Main St'] },
      { key: 'City', value: ['Anytown'], change: true },
      { key: 'Postal Code', value: ['12345'], preline: true },
    ],
    changeLink: '/change-address',
  },
  {
    header: 'Preferences',
    data: [
      { key: 'Newsletter', value: ['Subscribed'] },
      { key: 'Notifications', value: ['Enabled'], change: true },
    ],
    changeLink: '/change-preferences',
  },
];

export const summaryDataNoChangeParam: SummaryData = [
  {
    header: 'Personal Information',
    data: [
      { key: 'Name', value: ['John Doe'] },
      { key: 'Email', value: ['john.doe@example.com'], change: true, appendChangeParam: false },
      { key: 'Phone', value: ['+123456789'], preline: true, appendChangeParam: false },
    ],
    changeLink: '/change-personal-info',
  },
];
