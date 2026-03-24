import { GovukTableColumn } from '@netz/govuk-components';

export const tableColumns: GovukTableColumn[] = [
  { field: 'task', header: 'Task / Item name', isSortable: false },
  { field: 'type', header: 'Type', isSortable: false },
  { field: 'EXECUTE', header: 'Execute', isSortable: false },
  { field: 'NONE', header: 'None', isSortable: false },
];

export const tableRows = [
  {
    permission: 'MANAGE_SECTOR_ASSOCIATIONS',
    task: 'Manage Sectors',
    type: 'Manage sector details',
  },
  {
    permission: 'ASSIGN_REASSIGN_TASKS',
    task: 'Assign/re-assign tasks',
    type: 'Task assignment',
  },
  {
    permission: 'MANAGE_USERS_AND_CONTACTS',
    task: 'Manage users and contacts',
    type: 'Regulator users and contacts',
  },
  {
    permission: 'MANAGE_SECTOR_USERS',
    task: 'Manage Sector Users',
    type: 'Sector users and contacts',
  },
  {
    permission: 'MANAGE_OPERATOR_USERS',
    task: 'Manage Operator Users',
    type: 'Operator Users and Contacts',
  },
  {
    permission: 'ADMIN_TERMINATION_SUBMISSION',
    task: 'Admin termination submission',
    type: 'Admin termination',
  },
  {
    permission: 'ADMIN_TERMINATION_PEER_REVIEW',
    task: 'Peer Review Admin Termination',
    type: 'Admin termination',
  },
  {
    permission: 'UNDERLYING_AGREEMENT_APPLICATION_REVIEW',
    task: 'Review application for underlying agreement',
    type: 'Underlying agreement',
  },
  {
    permission: 'UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW',
    task: 'Peer review Application for Underlying agreement ',
    type: 'Underlying agreement',
  },
  {
    permission: 'UNDERLYING_AGREEMENT_VARIATION_REVIEW',
    task: 'Review variation to underlying agreement',
    type: 'Variation',
  },
  {
    permission: 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEW',
    task: 'Peer review Application for Underlying agreement variation',
    type: 'Variation',
  },
  {
    permission: 'UNDERLYING_AGREEMENT_VARIATION_SUBMISSION',
    task: `Trigger and Complete Regulator's Variation`,
    type: `Regulator's Variation`,
  },
  {
    permission: 'UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW',
    task: `Peer Review Regulator's Variation`,
    type: `Regulator's Variation`,
  },
  {
    permission: 'MANAGE_FACILITY_AUDIT',
    task: 'Flag for Audit',
    type: 'Audit',
  },
  {
    permission: 'FACILITY_AUDIT_SUBMISSION',
    task: 'Trigger and complete Audit',
    type: 'Audit',
  },
  {
    permission: 'NON_COMPLIANCE_SUBMISSION',
    task: 'Trigger and complete Non-compliance',
    type: 'Non-compliance',
  },
];
