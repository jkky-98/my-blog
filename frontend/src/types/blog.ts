export type PostStatus = 'draft' | 'published' | 'hidden'

export type AccentName = 'mint' | 'purple' | 'blue'

export type PageResponse<T> = {
  items: T[]
  page: number
  size: number
  totalCount: number
  totalPages: number
}

export type CategorySummary = {
  name: string
  key: string
  count: number
}

export type TagSummary = {
  name: string
  key: string
  count: number
}

export type PostSummary = {
  id: number
  title: string
  slug: string
  description: string
  category: string
  categoryKey: string
  tags: string[]
  tagKeys: string[]
  createdAt: string
  updatedAt: string
  readingTime: number
  viewCount: number
  author: string
  featured?: boolean
  accent: AccentName
}

export type PostDetail = PostSummary & {
  content: string
}

export type AdminPostSummary = PostSummary & {
  status: PostStatus
}

export type AdminPostDetail = PostDetail & {
  status: PostStatus
}

export type AdminPostRequest = {
  title: string
  category: string
  tags: string[]
  featured: boolean
  status: PostStatus
  content: string
}

export type ProjectSummary = {
  id: number
  name: string
  description: string
  stack: string[]
  status: 'Design' | 'Mockup' | 'Planning'
}

export type AdminUser = {
  id: number
  username: string
  displayName: string
}

export type CsrfToken = {
  headerName: string
  token: string
}
