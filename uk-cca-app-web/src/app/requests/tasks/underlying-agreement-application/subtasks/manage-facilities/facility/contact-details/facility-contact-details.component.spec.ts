import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { mockRequestTaskItemDTO } from '@requests/common';

import { FacilityContactDetailsComponent } from './facility-contact-details.component';

describe('FacilityContactDetailsComponent', () => {
  let fixture: ComponentFixture<FacilityContactDetailsComponent>;
  let store: RequestTaskStore;

  const route = new ActivatedRouteStub({ facilityId: 'ADS_1-F00001' });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FacilityContactDetailsComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem(mockRequestTaskItemDTO);

    fixture = TestBed.createComponent(FacilityContactDetailsComponent);
    fixture.detectChanges();
  });

  it('should show form values', () => {
    expect(fixture.nativeElement.innerHTML).toMatchSnapshot();
  });
});
