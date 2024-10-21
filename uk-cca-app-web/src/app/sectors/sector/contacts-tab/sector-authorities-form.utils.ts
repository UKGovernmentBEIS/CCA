import { FormArray, FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { SectorUserAuthorityInfoDTO } from 'cca-api';

export type SectorAuthorityFormModel = FormGroup<{
  userType: FormControl<string>;
  status: FormControl<SectorUserAuthorityInfoDTO['status']>;
  userId: FormControl<string>;
}>;

export type SectorAuthoritiesFormModel = FormGroup<{
  authorities: FormArray<SectorAuthorityFormModel>;
}>;

export function isTheOnlyAdministrator(userId: string, sectorAuthorities: SectorUserAuthorityInfoDTO[]): boolean {
  const user = sectorAuthorities.find((u) => u.userId === userId);
  const isAdmin = user.roleCode === 'sector_user_administrator';
  const isActive = user.status === 'ACTIVE';

  const activeAdminUsers = sectorAuthorities.filter(
    (u) => u.roleCode === 'sector_user_administrator' && u.status === 'ACTIVE',
  );

  return isAdmin && isActive && activeAdminUsers.length < 2;
}

export function setAdministratorFormError(form: SectorAuthoritiesFormModel) {
  form.setErrors({
    atLeastOneAdmin: 'You must have an administrator user on your account',
  });
}

export function hasAdministrator(form: SectorAuthoritiesFormModel): boolean {
  const activeAdminUsers = form.value.authorities.filter(
    (u) => u.userType === 'sector_user_administrator' && u.status === 'ACTIVE',
  );

  return activeAdminUsers.length > 0;
}

export function patchAuthoritiesForm(
  usersAuthorities: SectorUserAuthorityInfoDTO[],
  form: SectorAuthoritiesFormModel,
  fb: FormBuilder,
) {
  usersAuthorities.forEach((auth, index) => {
    form.controls.authorities.setControl(
      index,
      fb.group({ userId: auth.userId, userType: auth.roleCode, status: auth.status }),
    );
  });
}
