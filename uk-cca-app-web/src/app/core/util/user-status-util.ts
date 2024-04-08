import { UserStateDTO } from 'cca-api';

export function shouldShowAccepted(userState: UserStateDTO): boolean {
  return userState?.status === 'ACCEPTED';
}

export function shouldShowDisabled(userState: UserStateDTO): boolean {
  return loginDisabled(userState) || hasNoAuthority(userState);
}

export function hasNoAuthority(user: UserStateDTO): boolean {
  return user?.status === 'NO_AUTHORITY';
}

export function loginDisabled(userState: UserStateDTO): boolean {
  return ['DISABLED', 'TEMP_DISABLED'].includes(userState?.status);
}

export function loginEnabled(userState: UserStateDTO): boolean {
  return userState?.status === 'ENABLED';
}
