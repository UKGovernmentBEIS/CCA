import { inject } from '@angular/core';
import { Router } from '@angular/router';

export const backlinkResolver = (summaryRoute: string, previousStepRoute: string) => {
  return () => {
    const router = inject(Router);
    const navigation = router.currentNavigation();
    const isChangeClicked = navigation ? !!navigation.finalUrl?.queryParams?.change : false;

    return isChangeClicked ? summaryRoute : `../${previousStepRoute}`;
  };
};
