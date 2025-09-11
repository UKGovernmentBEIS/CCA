import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { PATUploadSubmittedComponent } from './pat-upload-submitted.component';
import { mockRequestActionStatePATUpload } from './testing/mock-data';

describe('PatUploadSubmittedComponent', () => {
  let component: PATUploadSubmittedComponent;
  let fixture: ComponentFixture<PATUploadSubmittedComponent>;
  let store: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PATUploadSubmittedComponent],
      providers: [RequestActionStore, { provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState(mockRequestActionStatePATUpload);
    fixture = TestBed.createComponent(PATUploadSubmittedComponent);

    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary values', () => {
    expect(fixture).toMatchSnapshot();
  });
});
