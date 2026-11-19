import { TestBed } from '@angular/core/testing';

import { ConfigStore } from '../config/config.store';
import { AnalyticsService, JourneyOutcomeEvent, ServiceContextEvent } from './analytics.service';

interface DataLayerWindow extends Window {
  dataLayer?: Record<string, unknown>[];
}

describe('AnalyticsService', () => {
  let service: AnalyticsService;
  const gtmWindow = window as unknown as DataLayerWindow;

  const serviceContextEvent: ServiceContextEvent = { event: 'service_context', user_type: 'operator', login: true };
  const journeyOutcomeEvent: JourneyOutcomeEvent = {
    event: 'journey_outcome',
    journey_id: 'J_016',
    task_name: 'cca_variation',
    task_outcome: 'submitted',
    variation_modifications: 'change_throughput_unit',
    variation_modification_count: 1,
  };

  const enableTagManager = () => {
    TestBed.inject(ConfigStore).setState({ analytics: { gtmContainerId: 'GTM-TEST' } });
    service.enableGoogleTagManager();
  };

  beforeEach(() => {
    TestBed.configureTestingModule({});

    service = TestBed.inject(AnalyticsService);
    delete gtmWindow.dataLayer;
  });

  afterEach(() => {
    service.removeGoogleTagManager();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should hold events in memory until Tag Manager is enabled', () => {
    service.push(serviceContextEvent);

    expect(gtmWindow.dataLayer).toBeUndefined();
  });

  it('should forward queued events when Tag Manager is enabled, after the init event', () => {
    service.push(serviceContextEvent);
    service.push(journeyOutcomeEvent);

    enableTagManager();

    expect(document.head.querySelector('script[src*="googletagmanager"]')).toBeTruthy();
    expect(gtmWindow.dataLayer[0]).toMatchObject({ event: 'gtm.js' });
    expect(gtmWindow.dataLayer.slice(1)).toEqual([serviceContextEvent, journeyOutcomeEvent]);
  });

  it('should append events once Tag Manager is enabled', () => {
    enableTagManager();

    service.push(journeyOutcomeEvent);

    expect(gtmWindow.dataLayer?.at(-1)).toEqual(journeyOutcomeEvent);
  });

  it('should drop queued events when Tag Manager is removed', () => {
    service.push(serviceContextEvent);

    service.removeGoogleTagManager();
    enableTagManager();

    expect(gtmWindow.dataLayer?.some((event) => event['event'] === 'service_context')).toBe(false);
  });

  it('should remove the Tag Manager script and data layer when Tag Manager is removed', () => {
    enableTagManager();

    service.removeGoogleTagManager();

    expect(document.head.querySelector('script[src*="googletagmanager"]')).toBeFalsy();
    expect(gtmWindow.dataLayer).toBeUndefined();
  });

  it('should remove Tag Manager safely when it has not been enabled', () => {
    expect(() => service.removeGoogleTagManager()).not.toThrow();
  });
});
