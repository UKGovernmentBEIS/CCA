import { TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { mockFacilityDetails } from '../../testing/mock-data';
import FacilitySurplusConfirmationComponent from './confirmation.component';

describe('FacilitySurplusConfirmationComponent', () => {
  it('should update the return link label when resolved facility data changes', async () => {
    const route = new ActivatedRouteStub(null, null, {
      facilityDetails: { ...mockFacilityDetails, facilityBusinessId: 'AIC-F00040' },
    });
    await TestBed.configureTestingModule({
      imports: [FacilitySurplusConfirmationComponent],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
    const fixture = TestBed.createComponent(FacilitySurplusConfirmationComponent);
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('a').textContent).toContain('AIC-F00040');

    route.setResolveMap({
      facilityDetails: { ...mockFacilityDetails, facilityBusinessId: 'AIC-F00041' },
    });
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('a').textContent).toContain('AIC-F00041');
    expect(fixture.nativeElement.querySelector('a').textContent).not.toContain('AIC-F00040');
  });
});
