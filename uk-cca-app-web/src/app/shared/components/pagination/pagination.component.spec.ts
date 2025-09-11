import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { PaginationComponent } from './pagination.component';

describe('PaginationComponent', () => {
  let component: PaginationComponent;
  let hostComponent: TestHostComponent;
  let fixture: ComponentFixture<TestHostComponent>;

  @Component({
    standalone: true,
    template: `
      <cca-pagination
        [count]="count"
        [currentPage]="currentPage"
        [pageSize]="pageSize"
        [hideNumbers]="hideNumbers"
        (pageChange)="onPageChange($event)"
        (pageSizeChange)="onPageSizeChange($event)"
      />
    `,
    imports: [PaginationComponent],
  })
  class TestHostComponent {
    count = 0;
    pageSize = 10;
    currentPage = 1;
    hideNumbers = false;
    lastPageChanged: number | null = null;
    lastPageSizeChanged: number | null = null;

    onPageChange(page: number) {
      this.lastPageChanged = page;
    }

    onPageSizeChange(size: number) {
      this.lastPageSizeChanged = size;
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestHostComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TestHostComponent);
    component = fixture.debugElement.query(By.directive(PaginationComponent)).componentInstance;
    hostComponent = fixture.componentInstance;
    fixture.detectChanges();
  });

  const setup = (count: number, currentPage = 1, pageSize = 10, hideNumbers = false) => {
    hostComponent.count = count;
    hostComponent.currentPage = currentPage;
    hostComponent.pageSize = pageSize;
    hostComponent.hideNumbers = hideNumbers;
    fixture.detectChanges();
  };

  const clickElement = (selector: string) => {
    const element = fixture.debugElement.query(By.css(selector));
    element?.nativeElement.click();
    fixture.detectChanges();
  };

  const getAllElements = (selector: string) => fixture.debugElement.queryAll(By.css(selector));

  const elementExists = (selector: string) => !!fixture.debugElement.query(By.css(selector));

  describe('Component Creation', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should initialize with default values', () => {
      expect(component.currentPage()).toBe(1);
      expect(component.pageSize()).toBe(10);
      expect(component.count()).toBe(0);
    });
  });

  describe('Page Calculations', () => {
    it('should calculate total pages correctly', () => {
      setup(0);
      expect(component.totalPages()).toBe(1);

      setup(25, 1, 10);
      expect(component.totalPages()).toBe(3);

      setup(30, 1, 10);
      expect(component.totalPages()).toBe(3);

      setup(31, 1, 10);
      expect(component.totalPages()).toBe(4);
    });

    it('should generate correct page numbers', () => {
      setup(25, 1, 10);
      expect(component.pageNumbers()).toEqual([1, 2, 3]);

      setup(50, 1, 10);
      expect(component.pageNumbers()).toEqual([1, 2, 3, 4, 5]);
    });
  });

  describe('Page Validation', () => {
    it('should display page 5 when count is 0', () => {
      setup(0, 5);
      expect(component.currentPage()).toBe(5);
    });

    it('should display page 10 even when it exceeds total pages', () => {
      setup(25, 10, 10); // page 10 > total pages (3)
      expect(component.currentPage()).toBe(10);
    });

    it('should display negative page if provided', () => {
      setup(25, -1, 10);
      expect(component.currentPage()).toBe(-1);
    });

    it('should display valid page 2', () => {
      setup(25, 2, 10);
      expect(component.currentPage()).toBe(2);
    });
  });

  describe('Page Navigation', () => {
    it('should navigate via next button click', () => {
      setup(30, 1, 10);
      const spy = jest.spyOn(hostComponent, 'onPageChange');

      clickElement('.govuk-pagination__next a');

      expect(spy).toHaveBeenCalledWith(2);
      expect(hostComponent.lastPageChanged).toBe(2);
    });

    it('should emit when trying to navigate to same page', () => {
      setup(30, 2, 10);
      const spy = jest.spyOn(component.pageChange, 'emit');

      component.onPageChange(2); // Same page

      expect(spy).toHaveBeenCalledWith(2); // Component emits even for same page
    });
  });

  describe('Page Size Changes', () => {
    it('should emit page size change when changing size', () => {
      setup(50, 1, 10);
      const spy = jest.spyOn(hostComponent, 'onPageSizeChange');

      component.pageSizeForm.patchValue({ pageSize: 20 });
      component.onPageSizeChange();

      expect(spy).toHaveBeenCalledWith(20);
      expect(component.pageSize()).toBe(10); // pageSize is an input and doesn't change locally
    });

    it('should emit even if same page size selected', () => {
      setup(50, 1, 10);
      const spy = jest.spyOn(hostComponent, 'onPageSizeChange');

      component.pageSizeForm.patchValue({ pageSize: 10 });
      component.onPageSizeChange();

      expect(spy).toHaveBeenCalledWith(10); // Component always emits
    });
  });

  describe('UI Display Elements', () => {
    it('should show/hide previous button based on current page', () => {
      setup(30, 1, 10);
      expect(component.currentPage()).toBe(1);
      expect(elementExists('.govuk-pagination__prev')).toBe(false);

      // Change to page 2
      setup(30, 2, 10);
      expect(elementExists('.govuk-pagination__prev')).toBe(true);
    });

    it('should show/hide next button based on current page and total pages', () => {
      setup(30, 1, 10); // 3 total pages
      expect(component.currentPage()).toBe(1);
      expect(component.totalPages()).toBe(3);
      expect(elementExists('.govuk-pagination__next')).toBe(true);

      // Set to last page
      setup(30, 3, 10);
      expect(elementExists('.govuk-pagination__next')).toBe(false);
    });

    it('should hide pagination list when total pages = 1', () => {
      setup(5, 1, 10);
      expect(elementExists('.govuk-pagination__list')).toBe(false);

      setup(25, 1, 10);
      expect(elementExists('.govuk-pagination__list')).toBe(true);
    });

    it('should hide page numbers when hideNumbers is true', () => {
      setup(50, 1, 10, false);
      expect(getAllElements('.govuk-pagination__item a').length).toBeGreaterThan(0);

      setup(50, 1, 10, true);
      expect(getAllElements('.govuk-pagination__item a').length).toBe(0);
    });

    it('should not show items when count is 0', () => {
      setup(0);
      expect(elementExists('#show-items')).toBe(false);
    });
  });
  describe('Helper Methods', () => {
    it('should calculate starting item correctly', () => {
      setup(50, 1, 10);
      expect(component.getStartingItem()).toBe(1);

      setup(50, 3, 10);
      expect(component.getStartingItem()).toBe(21);

      setup(0);
      expect(component.getStartingItem()).toBe(0);
    });

    it('should calculate ending item correctly', () => {
      setup(50, 1, 10);
      expect(component.getEndingItem()).toBe(10);

      setup(50, 5, 10);
      expect(component.getEndingItem()).toBe(50);

      setup(45, 5, 10);
      expect(component.getEndingItem()).toBe(45);

      setup(0);
      expect(component.getEndingItem()).toBe(0);
    });

    it('should determine displayed pages correctly', () => {
      setup(50, 1, 10); // 5 total pages
      expect(component.isDisplayed(3, 1)).toBe(true);

      setup(100, 5, 10); // 10 total pages
      expect(component.isDisplayed(5, 5)).toBe(true); // current
      expect(component.isDisplayed(4, 5)).toBe(true); // current - 1
      expect(component.isDisplayed(6, 5)).toBe(true); // current + 1
      expect(component.isDisplayed(2, 5)).toBe(false); // too far
    });

    it('should determine dots correctly', () => {
      setup(100, 5, 10); // 10 total pages
      expect(component.isDots(3, 5)).toBe(true); // current - 2
      expect(component.isDots(7, 5)).toBe(true); // current + 2
      expect(component.isDots(1, 5)).toBe(false); // first page
      expect(component.isDots(10, 5)).toBe(false); // last page
      expect(component.isDots(4, 5)).toBe(false); // within range
    });
  });

  describe('Edge Cases', () => {
    it('should handle single page scenario', () => {
      setup(5, 1, 10);
      expect(component.totalPages()).toBe(1);
      expect(elementExists('.govuk-pagination__prev')).toBe(false);
      expect(elementExists('.govuk-pagination__next')).toBe(false);
      expect(elementExists('.govuk-pagination__list')).toBe(false);
    });

    it('should handle zero count', () => {
      setup(0, 1, 10);
      expect(component.totalPages()).toBe(1);
      expect(elementExists('#show-items')).toBe(false);
    });

    it('should handle page size larger than count', () => {
      setup(5, 1, 10);
      expect(component.totalPages()).toBe(1);
      expect(component.getStartingItem()).toBe(1);
      expect(component.getEndingItem()).toBe(5);
    });
  });
});
