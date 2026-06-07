import type { AdminUser, CsrfToken } from '@/types/blog'

import { delay } from './http'

const authStorageKey = 'my-blog-admin-auth'

const mockAdmin: AdminUser = {
  id: 1,
  username: 'admin',
  displayName: 'Jin',
}

export function isAdminAuthenticated() {
  return window.localStorage.getItem(authStorageKey) === 'true'
}

export async function fetchCsrfToken(): Promise<CsrfToken> {
  return delay({
    headerName: 'X-CSRF-TOKEN',
    token: 'mock-csrf-token',
  })
}

export async function login(username: string, password: string) {
  await fetchCsrfToken()

  if (username === 'admin' && password === 'password') {
    window.localStorage.setItem(authStorageKey, 'true')
    return delay(mockAdmin)
  }

  throw new Error('아이디 또는 비밀번호가 올바르지 않습니다.')
}

export function logout() {
  window.localStorage.removeItem(authStorageKey)
  return delay(undefined)
}

export function fetchMe() {
  if (!isAdminAuthenticated()) {
    throw new Error('로그인이 만료되었습니다.')
  }

  return delay(mockAdmin)
}
