import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import {
  ItemDTOResponse,
  ItemsAssignedToMeService,
  ItemsAssignedToOthersService,
  UnassignedItemsService,
} from 'cca-api';

import { WorkflowItemsAssignmentType } from './+store';

@Injectable()
export class WorkflowItemsService {
  constructor(
    private readonly itemsAssignedToMeService: ItemsAssignedToMeService,
    private readonly itemsAssignedToOthersService: ItemsAssignedToOthersService,
    private readonly unassignedItemsService: UnassignedItemsService,
  ) {}

  getItems(type: WorkflowItemsAssignmentType, page: number, pageSize: number): Observable<ItemDTOResponse> {
    switch (type) {
      case 'unassigned':
        return this.unassignedItemsService.getUnassignedItems(page - 1, pageSize);

      case 'assigned-to-others':
        return this.itemsAssignedToOthersService.getAssignedToOthersItems(page - 1, pageSize);

      case 'assigned-to-me':
      default:
        return this.itemsAssignedToMeService.getAssignedItems(page - 1, pageSize);
    }
  }
}
