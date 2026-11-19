import { inject, Service } from '@angular/core';

import { ConfigStore } from '../config/config.store';

interface GtmWindow extends Window {
  dataLayer: unknown[];
}

export interface ServiceContextEvent {
  event: 'service_context';
  user_type: string;
  login?: true;
}

export interface JourneyOutcomeEvent {
  event: 'journey_outcome';
  journey_id: string;
  task_name: string;
  task_outcome: string;
  variation_modifications: string;
  variation_modification_count: number;
}

export type AnalyticsEvent = ServiceContextEvent | JourneyOutcomeEvent;

@Service()
export class AnalyticsService {
  private readonly configStore = inject(ConfigStore);

  private currentScript: HTMLScriptElement;
  private queuedEvents: AnalyticsEvent[] = [];

  enableGoogleTagManager() {
    const gtmContainerId = this.configStore.state?.analytics?.gtmContainerId;

    if (!gtmContainerId) {
      console.warn('No google tag manager container id found. Environment will not log google analytics.');
      return;
    }

    if (this.currentScript) {
      console.warn('GTM script already loaded in DOM - Aborting...');
      return;
    }

    console.log('Logging to Google Analytics - [GTM Container ID]:', gtmContainerId);

    this.getDataLayer().push({
      'gtm.start': new Date().getTime(),
      event: 'gtm.js',
    });

    const gtmScript = document.createElement('script');

    gtmScript.async = true;
    gtmScript.src = `https://www.googletagmanager.com/gtm.js?id=${gtmContainerId}`;

    const firstHeadChild = document.head.childNodes[0];

    document.head.insertBefore(gtmScript, firstHeadChild);
    this.currentScript = gtmScript;

    this.flushQueuedEvents();
  }

  push(event: AnalyticsEvent): void {
    if (!this.currentScript) {
      this.queuedEvents.push(event);
      return;
    }

    this.getDataLayer().push(event);
  }

  removeGoogleTagManager() {
    if (this.currentScript) {
      document.head.removeChild(this.currentScript);
      this.currentScript = null;
    }

    delete this.getWindow().dataLayer;
    this.queuedEvents = [];
  }

  /**
   * Forwards events queued before consent in the same step Tag Manager starts, so Tag Manager reads them when it loads.
   */
  private flushQueuedEvents(): void {
    const queuedEvents = this.queuedEvents;
    this.queuedEvents = [];

    this.getDataLayer().push(...queuedEvents);
  }

  private getDataLayer(): unknown[] {
    const gtmWindow = this.getWindow();

    gtmWindow.dataLayer = gtmWindow.dataLayer || [];

    return gtmWindow.dataLayer;
  }

  private getWindow(): GtmWindow {
    return window as unknown as GtmWindow;
  }
}
