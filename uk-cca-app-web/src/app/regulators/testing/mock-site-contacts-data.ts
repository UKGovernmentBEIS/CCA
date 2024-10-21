export const mockSiteContactsRouteData = () => ({
  regulators: {
    caUsers: [
      {
        userId: '489aa8e5-c0af-45f6-b418-10749341bf62',
        firstName: 'Regulator',
        lastName: 'Admin',
        authorityCreationDate: '2024-04-22T11:18:34.769203Z',
        authorityStatus: 'ACTIVE',
      },
      {
        userId: '123ase412-c0af-45f6-b418-10749341bf62',
        firstName: 'Regulator2',
        lastName: 'Admin2',
        authorityCreationDate: '2024-04-22T11:18:34.769203Z',
        authorityStatus: 'DISABLED',
      },
    ],
    editable: true,
  },
  siteContactsInfo: {
    siteContacts: [
      {
        sectorAssociationId: 1,
        sectorName: 'ADS - Aerospace',
        userId: '489aa8e5-c0af-45f6-b418-10749341bf62',
      },
      {
        sectorAssociationId: 2,
        sectorName: 'ADS1 - Aerospace1',
        userId: '489aa8e5-c0af-45f6-b418-10749341bf62',
      },
      {
        sectorAssociationId: 3,
        sectorName: 'ADS2 - Aerospace2',
        userId: null,
      },
      {
        sectorAssociationId: 4,
        sectorName: 'ADS3 - Aerospace3',
        userId: null,
      },
    ],
    editable: true,
    totalItems: 4,
  },
});
