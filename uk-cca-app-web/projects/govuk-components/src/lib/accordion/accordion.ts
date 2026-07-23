import { FactoryProvider, InjectionToken, isDevMode } from '@angular/core';

export interface Accordion {
  id: string;
  openIndexes: number[];
  itemCount: number;
  cacheDisabled: boolean;
}

export const ACCORDION = new InjectionToken<Accordion>('Accordion');

export const accordionFactory: FactoryProvider = {
  provide: ACCORDION,
  useFactory: (): Accordion => ({
    id: 'accordion',
    itemCount: 0,
    openIndexes: [],
    cacheDisabled: false,
  }),
};

export const isSessionStorageAvailable: () => boolean = () => {
  let isStorageAvailable = false;
  try {
    sessionStorage.setItem('test', 'test string');
    isStorageAvailable = sessionStorage.getItem('test') === 'test string';
    sessionStorage.removeItem('test');
  } catch (exception) {
    if (isDevMode()) console.error(exception);
  }
  return isStorageAvailable;
};
