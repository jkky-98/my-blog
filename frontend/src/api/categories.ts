import { addCategory, getAdminCategories, getCategories } from '@/data/posts'

import { delay } from './http'

export function fetchCategories() {
  return delay(getCategories())
}

export function fetchAdminCategories() {
  return delay(getAdminCategories())
}

export function createCategory(name: string) {
  return delay(addCategory(name))
}
