import { SchemeVersionEnum } from '@shared/types';

export const mockBuyoutInfo = {
  excluded: false,
  surplusGainedDTOList: [
    {
      schemeVersion: SchemeVersionEnum.CCA_2,
      surplusGained: '1.213',
      hasHistory: true,
    },
  ],
};
