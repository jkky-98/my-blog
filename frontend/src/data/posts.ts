import type {
  AdminPostDetail,
  AdminPostRequest,
  AdminPostSummary,
  CategorySummary,
  PageResponse,
  PostDetail,
  PostStatus,
  PostSummary,
  TagSummary,
} from '@/types/blog'

type PostQuery = {
  categoryKey?: string
  tagKey?: string
  page?: number
  size?: number
}

type AdminPostQuery = PostQuery & {
  status?: PostStatus | ''
  keyword?: string
}

const author = 'Jin'

const baseDate = '2026-06-07T09:00:00+09:00'

export const mockPosts: AdminPostDetail[] = [
  {
    id: 1,
    title: 'Spring Boot에서 Redis 캐시를 적용하며 배운 것',
    slug: 'spring-boot-redis-cache',
    description: '조회수 캐싱을 구현하며 겪은 고민과 설계 과정을 정리했다.',
    category: 'Backend',
    categoryKey: 'backend',
    tags: ['Spring Boot', 'Redis'],
    tagKeys: ['spring-boot', 'redis'],
    createdAt: '2026-06-06T12:00:00+09:00',
    updatedAt: '2026-06-06T12:00:00+09:00',
    readingTime: 5,
    viewCount: 120,
    author,
    featured: true,
    accent: 'mint',
    status: 'published',
    content: `# Spring Boot에서 Redis 캐시를 적용하며 배운 것

조회수가 많은 글 목록을 매번 DB에서 계산하면 단순한 블로그에서도 응답 시간이 흔들릴 수 있다. 이번 목업에서는 실제 API 연결 전이지만, 백엔드에서 어떤 데이터를 내려줄지 상상하며 화면 구조를 잡았다.

## 왜 Redis였나

Redis는 조회수가 자주 변하는 데이터를 빠르게 읽고 쓰기 좋다. 특히 인기 글, 태그별 카운트, 최근 본 글처럼 화면에서 반복 노출되는 값은 캐시 계층을 두면 훨씬 단순해진다.

## 설계하면서 본 지점

- DB에는 최종적으로 신뢰할 수 있는 값을 남긴다.
- Redis에는 자주 바뀌는 조회수와 인기글 랭킹을 둔다.
- 프론트엔드는 API 응답 형태가 바뀌어도 카드 컴포넌트가 흔들리지 않도록 만든다.

### 예시 코드

\`\`\`ts
export type PopularPost = {
  title: string
  slug: string
  viewCount: number
}
\`\`\`

## 마무리

처음부터 모든 것을 서버와 연결하지 않고 정적 데이터로 화면을 세우면, UI의 밀도와 정보 우선순위를 먼저 검증할 수 있다.`,
  },
  {
    id: 2,
    title: '나만의 블로그를 직접 만드는 이유',
    slug: 'why-i-build-my-own-blog',
    description: '단순한 블로그가 아니라 개발자의 개인 공간을 만드는 과정.',
    category: 'Project',
    categoryKey: 'project',
    tags: ['Vue', 'Design'],
    tagKeys: ['vue', 'design'],
    createdAt: '2026-06-07T10:00:00+09:00',
    updatedAt: '2026-06-07T10:00:00+09:00',
    readingTime: 4,
    viewCount: 96,
    author,
    featured: true,
    accent: 'purple',
    status: 'published',
    content: `# 나만의 블로그를 직접 만드는 이유

블로그는 글을 올리는 도구이기도 하지만, 개발자가 어떤 방식으로 문제를 보고 해결하는지 보여주는 포트폴리오이기도 하다.

## 직접 만드는 장점

템플릿을 쓰면 빠르지만, 직접 만들면 글 목록, 프로젝트 소개, 상세 페이지의 독서 경험을 내 방식으로 조절할 수 있다.

## 프론트엔드 우선순위

- 글을 읽는 흐름을 방해하지 않는 폭과 타이포그래피
- 모바일에서 무너지지 않는 카드와 사이드바 구조
- 백엔드 API가 붙기 쉬운 데이터 모델

## 다음 단계

정적 목업이 안정되면 Spring Boot API와 연결하고, Markdown 렌더링과 검색 기능을 붙일 예정이다.`,
  },
  {
    id: 3,
    title: 'Vue 3 Composition API로 화면 상태 정리하기',
    slug: 'vue-composition-api-page-state',
    description: '페이지 상태와 계산 값을 작게 나누어 컴포넌트를 읽기 쉽게 유지하는 방법.',
    category: 'Frontend',
    categoryKey: 'frontend',
    tags: ['Vue', 'Architecture'],
    tagKeys: ['vue', 'architecture'],
    createdAt: '2026-06-10T09:30:00+09:00',
    updatedAt: '2026-06-10T09:30:00+09:00',
    readingTime: 6,
    viewCount: 74,
    author,
    accent: 'blue',
    status: 'published',
    content: `# Vue 3 Composition API로 화면 상태 정리하기

Composition API는 단순히 setup 문법을 쓰는 것이 아니라, 화면의 관심사를 더 작게 나누기 위한 도구다.

## 페이지에서 할 일

페이지 컴포넌트는 데이터를 고르고 배치하는 역할에 집중한다. 카드, 메타 정보, 태그 같은 표현은 하위 컴포넌트에 맡긴다.

### 계산 값

필터, 정렬, 페이지네이션처럼 화면에서만 필요한 값은 computed로 작게 나누면 흐름을 읽기 쉽다.

## 컴포넌트에서 할 일

컴포넌트는 props로 받은 데이터를 명확하게 보여준다. 화면 상태가 복잡해질수록 props 타입이 문서 역할을 한다.

## 정리

작은 컴포넌트를 먼저 만들면 API 연결 이후에도 변경 범위가 좁아진다.`,
  },
  {
    id: 4,
    title: '읽기 좋은 글 상세 페이지의 폭과 여백',
    slug: 'readable-post-detail-layout',
    description: '긴 글을 편하게 읽을 수 있도록 content width, line-height, TOC를 조정했다.',
    category: 'Design',
    categoryKey: 'design',
    tags: ['CSS', 'UX'],
    tagKeys: ['css', 'ux'],
    createdAt: '2026-06-12T13:20:00+09:00',
    updatedAt: '2026-06-12T13:20:00+09:00',
    readingTime: 3,
    viewCount: 62,
    author,
    accent: 'mint',
    status: 'published',
    content: `# 읽기 좋은 글 상세 페이지의 폭과 여백

글 상세 화면은 많은 정보를 담기보다 읽는 리듬을 유지하는 것이 중요하다.

## 본문 폭

너무 넓은 본문은 시선 이동이 커진다. 데스크탑에서도 본문 폭을 제한하고, 사이드바는 보조 정보로만 둔다.

## 타이포그래피

제목은 크게, 본문은 과하게 작지 않게 잡는다. 코드블럭은 배경 대비와 패딩을 충분히 둔다.

## 반응형

태블릿 이하에서는 사이드바를 본문 아래로 보내 한 방향으로 읽히게 만든다.`,
  },
  {
    id: 5,
    title: '관리자 글쓰기 화면을 목업으로 먼저 만드는 이유',
    slug: 'admin-editor-mock-first',
    description: '서버 없이도 작성, 미리보기, 검증 흐름을 먼저 정리하면 API 구현 기준이 선명해진다.',
    category: 'Project',
    categoryKey: 'project',
    tags: ['Vue', 'Markdown'],
    tagKeys: ['vue', 'markdown'],
    createdAt: '2026-06-14T11:00:00+09:00',
    updatedAt: '2026-06-14T11:00:00+09:00',
    readingTime: 4,
    viewCount: 51,
    author,
    accent: 'purple',
    status: 'published',
    content: `# 관리자 글쓰기 화면을 목업으로 먼저 만드는 이유

관리자 화면은 예쁘기보다 빠르게 쓰고 실수하기 어렵게 만드는 것이 중요하다.

## 좌우 분할 미리보기

왼쪽에 Markdown 원문을 쓰고 오른쪽에서 실제 렌더링 결과를 보면 저장 전 품질 확인이 쉽다.

## 검증 흐름

태그 누락, 제목 누락, 본문 누락처럼 서버가 막을 값은 프론트에서도 같은 맥락으로 안내해야 한다.

## API 계약

목업 저장소가 실제 API와 같은 필드를 쓰면, 나중에 fetch 함수만 교체하면 된다.`,
  },
  {
    id: 6,
    title: 'MySQL로 개인 블로그를 단순하게 시작하기',
    slug: 'simple-mysql-blog-start',
    description: '처음부터 마이그레이션 도구까지 넣기보다 DDL 문서와 작은 스키마로 시작하는 선택.',
    category: 'Backend',
    categoryKey: 'backend',
    tags: ['MySQL', 'JPA'],
    tagKeys: ['mysql', 'jpa'],
    createdAt: '2026-06-16T08:30:00+09:00',
    updatedAt: '2026-06-16T08:30:00+09:00',
    readingTime: 5,
    viewCount: 43,
    author,
    accent: 'blue',
    status: 'published',
    content: `# MySQL로 개인 블로그를 단순하게 시작하기

개인 프로젝트에서는 운영 복잡도를 줄이는 선택이 오래 간다.

## 초기 스키마

Post, Category, Tag, PostTag 정도만 분리해도 글 목록과 상세, 필터, 관리자 수정 흐름을 충분히 만들 수 있다.

## UTC 저장

서버와 DB는 UTC로 저장하고, 화면에서는 KST로 보여주면 기준이 분명해진다.

## 나중에 추가할 것

데이터가 늘고 배포 흐름이 정리되면 마이그레이션 도구를 다시 검토할 수 있다.`,
  },
  {
    id: 7,
    title: '초안 글: 인증 흐름 점검',
    slug: 'draft-auth-flow-check',
    description: '관리자 화면에서만 보여야 하는 초안 글 목업.',
    category: 'Backend',
    categoryKey: 'backend',
    tags: ['Security', 'Spring Boot'],
    tagKeys: ['security', 'spring-boot'],
    createdAt: '2026-06-18T08:30:00+09:00',
    updatedAt: '2026-06-18T08:30:00+09:00',
    readingTime: 2,
    viewCount: 0,
    author,
    accent: 'mint',
    status: 'draft',
    content: `# 초안 글: 인증 흐름 점검

이 글은 관리자 목록과 수정 화면에서만 확인하기 위한 초안 목업이다.

## 확인할 것

세션 만료, CSRF 토큰, 로그인 실패 메시지를 한 번에 점검한다.`,
  },
  {
    id: 8,
    title: '숨김 글: 공개 접근 차단',
    slug: 'hidden-public-access-blocked',
    description: '공개 상세에서 비공개 안내 메시지를 확인하기 위한 숨김 글.',
    category: 'Project',
    categoryKey: 'project',
    tags: ['Policy'],
    tagKeys: ['policy'],
    createdAt: '2026-06-19T08:30:00+09:00',
    updatedAt: '2026-06-19T08:30:00+09:00',
    readingTime: 1,
    viewCount: 0,
    author,
    accent: 'purple',
    status: 'hidden',
    content: `# 숨김 글: 공개 접근 차단

이 글은 공개 API에서 차단되는 상태를 확인하기 위한 목업이다.`,
  },
]

export const posts = mockPosts.filter(isPublished)

export const popularPosts = [...getPublishedPosts()]
  .sort((postA, postB) => postB.viewCount - postA.viewCount)
  .slice(0, 3)

export function findPostBySlug(slug: string) {
  return mockPosts.find(post => post.slug === slug)
}

export function findPostById(id: number) {
  return mockPosts.find(post => post.id === id)
}

export function findPublicPostBySlug(slug: string) {
  const post = findPostBySlug(slug)
  return post?.status === 'published' ? post : undefined
}

export function getFeaturedPost() {
  const featuredPosts = getPublishedPosts().filter(post => post.featured)
  if (!featuredPosts.length) return undefined

  return featuredPosts[Math.floor(Math.random() * featuredPosts.length)]
}

export function getPublicPosts(query: PostQuery = {}): PageResponse<PostSummary> {
  const page = query.page ?? 1
  const size = query.size ?? 6
  const filtered = getPublishedPosts().filter(post => {
    const categoryMatches = query.categoryKey ? post.categoryKey === query.categoryKey : true
    const tagMatches = query.tagKey ? post.tagKeys.includes(query.tagKey) : true
    return categoryMatches && tagMatches
  })

  return paginate(filtered, page, size)
}

export function getPopularPosts(limit = 3) {
  return [...getPublishedPosts()].sort((postA, postB) => postB.viewCount - postA.viewCount).slice(0, limit)
}

export function getAdminPosts(query: AdminPostQuery = {}): PageResponse<AdminPostSummary> {
  const page = query.page ?? 1
  const size = query.size ?? 10
  const includedStatuses: PostStatus[] = query.status
    ? [query.status]
    : ['draft', 'published']
  const keyword = query.keyword?.trim().toLowerCase()

  const filtered = mockPosts.filter(post => {
    const statusMatches = includedStatuses.includes(post.status)
    const categoryMatches = query.categoryKey ? post.categoryKey === query.categoryKey : true
    const tagMatches = query.tagKey ? post.tagKeys.includes(query.tagKey) : true
    const keywordMatches = keyword ? post.title.toLowerCase().includes(keyword) : true
    return statusMatches && categoryMatches && tagMatches && keywordMatches
  })

  return paginate(
    [...filtered].sort((postA, postB) => {
      return new Date(postB.createdAt).getTime() - new Date(postA.createdAt).getTime()
    }),
    page,
    size,
  )
}

export function getCategories(): CategorySummary[] {
  const counts = new Map<string, CategorySummary>()

  for (const post of getPublishedPosts()) {
    const current = counts.get(post.categoryKey)
    counts.set(post.categoryKey, {
      name: post.category,
      key: post.categoryKey,
      count: (current?.count ?? 0) + 1,
    })
  }

  return sortCounts([...counts.values()])
}

export function getTags(): TagSummary[] {
  const counts = new Map<string, TagSummary>()

  for (const post of getPublishedPosts()) {
    post.tags.forEach((tag, index) => {
      const key = post.tagKeys[index]
      const current = counts.get(key)
      counts.set(key, {
        name: tag,
        key,
        count: (current?.count ?? 0) + 1,
      })
    })
  }

  return sortCounts([...counts.values()])
}

export function getAdminCategories(): CategorySummary[] {
  const counts = new Map<string, CategorySummary>()

  for (const post of mockPosts) {
    const current = counts.get(post.categoryKey)
    counts.set(post.categoryKey, {
      name: post.category,
      key: post.categoryKey,
      count: (current?.count ?? 0) + 1,
    })
  }

  return sortCounts([...counts.values()])
}

export function addCategory(name: string) {
  const trimmedName = name.trim()
  const key = toFilterKey(trimmedName)
  const existing = getAdminCategories().find(category => category.key === key)

  if (existing) {
    throw new Error('이미 존재하는 카테고리입니다.')
  }

  return {
    name: trimmedName,
    key,
    count: 0,
  }
}

export function savePost(request: AdminPostRequest, id?: number) {
  validatePostRequest(request)

  if (id) {
    const index = mockPosts.findIndex(post => post.id === id)
    if (index < 0) throw new Error('글을 찾을 수 없습니다.')

    const previous = mockPosts[index]
    const updatedPost = normalizePostRequest(request, previous)
    mockPosts.splice(index, 1, updatedPost)
    return updatedPost
  }

  const nextId = Math.max(...mockPosts.map(post => post.id)) + 1
  const post = normalizePostRequest(request, {
    id: nextId,
    slug: createUniqueSlug(request.title),
    createdAt: now(),
    viewCount: 0,
  })
  mockPosts.unshift(post)
  return post
}

export function updatePostStatus(id: number, status: PostStatus) {
  const post = mockPosts.find(item => item.id === id)
  if (!post) throw new Error('글을 찾을 수 없습니다.')

  post.status = status
  post.updatedAt = now()
  return post
}

function isPublished(post: AdminPostDetail): post is AdminPostDetail & { status: 'published' } {
  return post.status === 'published'
}

function getPublishedPosts() {
  return mockPosts.filter(isPublished)
}

function paginate<T>(items: T[], page: number, size: number): PageResponse<T> {
  const start = (page - 1) * size

  return {
    items: items.slice(start, start + size),
    page,
    size,
    totalCount: items.length,
    totalPages: Math.max(1, Math.ceil(items.length / size)),
  }
}

function sortCounts<T extends { name: string; count: number }>(items: T[]) {
  return items.sort((itemA, itemB) => itemB.count - itemA.count || itemA.name.localeCompare(itemB.name))
}

function normalizePostRequest(
  request: AdminPostRequest,
  previous: Pick<AdminPostDetail, 'id' | 'createdAt' | 'slug' | 'viewCount'> & Partial<AdminPostDetail>,
): AdminPostDetail {
  const categoryKey = toFilterKey(request.category)
  const normalizedTags = normalizeTags(request.tags)
  const contentText = toPlainText(request.content)

  return {
    id: previous.id,
    title: request.title.trim(),
    slug: previous.slug,
    description: createDescription(contentText),
    category: request.category.trim(),
    categoryKey,
    tags: normalizedTags.map(tag => tag.name),
    tagKeys: normalizedTags.map(tag => tag.key),
    createdAt: previous.createdAt,
    updatedAt: now(),
    readingTime: Math.max(1, Math.ceil(contentText.replace(/\s/g, '').length / 500)),
    viewCount: previous.viewCount,
    author,
    featured: request.featured,
    accent: previous.accent ?? pickAccent(categoryKey),
    status: request.status,
    content: request.content,
  }
}

function validatePostRequest(request: AdminPostRequest) {
  if (!request.title.trim()) throw new Error('제목을 입력해 주세요.')
  if (!request.category.trim()) throw new Error('카테고리를 선택해 주세요.')
  if (!request.content.trim()) throw new Error('본문을 입력해 주세요.')
  if (!request.tags.length) throw new Error('태그는 최소 1개 이상 입력해야 합니다.')
  if (request.tags.length > 10) throw new Error('태그는 최대 10개까지 입력할 수 있습니다.')
}

function normalizeTags(tags: string[]) {
  const tagMap = new Map<string, string>()

  for (const tag of tags) {
    const name = tag.trim()
    if (!name) continue
    tagMap.set(toFilterKey(name), name)
  }

  return [...tagMap.entries()].map(([key, name]) => ({ key, name }))
}

function toFilterKey(value: string) {
  const normalized = value
    .trim()
    .toLowerCase()
    .replace(/[^a-z0-9가-힣\s-]/g, '')
    .replace(/\s+/g, '-')
    .replace(/-+/g, '-')

  return normalized || `item-${Date.now().toString(36)}`
}

function createUniqueSlug(title: string) {
  const baseSlug = toFilterKey(title).slice(0, 80) || `post-${new Date().toISOString().slice(0, 10)}`
  const existingSlugs = new Set(mockPosts.map(post => post.slug))

  if (!existingSlugs.has(baseSlug)) return baseSlug

  let index = 2
  while (existingSlugs.has(`${baseSlug}-${index}`)) {
    index += 1
  }

  return `${baseSlug}-${index}`
}

function toPlainText(markdown: string) {
  return markdown
    .replace(/```[\s\S]*?```/g, ' ')
    .replace(/`([^`]+)`/g, '$1')
    .replace(/[#>*_[\]()-]/g, ' ')
    .replace(/\s+/g, ' ')
    .trim()
}

function createDescription(contentText: string) {
  return contentText.slice(0, 300)
}

function pickAccent(key: string) {
  const accents = ['mint', 'purple', 'blue'] as const
  return accents[Math.abs([...key].reduce((sum, char) => sum + char.charCodeAt(0), 0)) % accents.length]
}

function now() {
  return new Date().toISOString()
}

export type { AdminPostDetail, AdminPostRequest, AdminPostSummary, PostDetail, PostSummary }
