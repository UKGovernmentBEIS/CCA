import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { mockRequestTaskItemDTO } from '@requests/common';

import { FacilityContactDetailsComponent } from './facility-contact-details.component';

describe('FacilityContactDetailsComponent', () => {
  let fixture: ComponentFixture<FacilityContactDetailsComponent>;
  let store: RequestTaskStore;

  const route: any = { snapshot: { params: { facilityId: 'ADS_1-F00001' }, pathFromRoot: [] } };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [FacilityContactDetailsComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
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
    expect(fixture).toMatchSnapshot();
  });
});
