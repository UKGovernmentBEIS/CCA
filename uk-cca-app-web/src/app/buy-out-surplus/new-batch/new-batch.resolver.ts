import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';

import { AvailableTargetPeriodsBuyOutDTO, BuyOutAndSurplusInfoService } from 'cca-api';

export const AvailableTargetPeriodsResolver: ResolveFn<AvailableTargetPeriodsBuyOutDTO> = () =>
  inject(BuyOutAndSurplusInfoService).getAvailableBuyOutTargetPeriods();
