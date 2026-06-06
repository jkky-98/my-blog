export type PostSummary = {
  id: number
  title: string
  slug: string
  description: string
  tags: string[]
  createdAt: string
  readingTime: number
  author: string
  featured?: boolean
  accent: 'mint' | 'purple' | 'blue'
  content: string
}

export const posts: PostSummary[] = [
  {
    id: 1,
    title: 'Spring Boot에서 Redis 캐시를 적용하며 배운 것',
    slug: 'spring-boot-redis-cache',
    description: '조회수 캐싱을 구현하며 겪은 고민과 설계 과정을 정리했다.',
    tags: ['Spring Boot', 'Redis', 'Backend'],
    createdAt: '2026-06-06',
    readingTime: 5,
    author: 'Jin',
    featured: true,
    accent: 'mint',
    content: `# Spring Boot에서 Redis 캐시를 적용하며 배운 것

조회수가 많은 글 목록을 매번 DB에서 계산하면 단순한 블로그에서도 응답 시간이 흔들릴 수 있다. 이번 목업에서는 실제 API 연결 전이지만, 백엔드에서 어떤 데이터를 내려줄지 상상하며 화면 구조를 잡았다.

## 왜 Redis였나

Redis는 조회수가 자주 변하는 데이터를 빠르게 읽고 쓰기 좋다. 특히 인기 글, 태그별 카운트, 최근 본 글처럼 화면에서 반복 노출되는 값은 캐시 계층을 두면 훨씬 단순해진다.

## 설계하면서 본 지점

- DB에는 최종적으로 신뢰할 수 있는 값을 남긴다.
- Redis에는 자주 바뀌는 조회수와 인기글 랭킹을 둔다.
- 프론트엔드는 API 응답 형태가 바뀌어도 카드 컴포넌트가 흔들리지 않도록 만든다.

## 예시 코드

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
    tags: ['Project', 'Vue', 'Design'],
    createdAt: '2026-06-07',
    readingTime: 4,
    author: 'Jin',
    featured: true,
    accent: 'purple',
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
    tags: ['Vue', 'Frontend', 'Architecture'],
    createdAt: '2026-06-10',
    readingTime: 6,
    author: 'Jin',
    accent: 'blue',
    content: `# Vue 3 Composition API로 화면 상태 정리하기

Composition API는 단순히 setup 문법을 쓰는 것이 아니라, 화면의 관심사를 더 작게 나누기 위한 도구다.

## 페이지에서 할 일

페이지 컴포넌트는 데이터를 고르고 배치하는 역할에 집중한다. 카드, 메타 정보, 태그 같은 표현은 하위 컴포넌트에 맡긴다.

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
    tags: ['Design', 'CSS', 'UX'],
    createdAt: '2026-06-12',
    readingTime: 3,
    author: 'Jin',
    accent: 'mint',
    content: `# 읽기 좋은 글 상세 페이지의 폭과 여백

글 상세 화면은 많은 정보를 담기보다 읽는 리듬을 유지하는 것이 중요하다.

## 본문 폭

너무 넓은 본문은 시선 이동이 커진다. 데스크탑에서도 본문 폭을 제한하고, 사이드바는 보조 정보로만 둔다.

## 타이포그래피

제목은 크게, 본문은 과하게 작지 않게 잡는다. 코드블럭은 배경 대비와 패딩을 충분히 둔다.

## 반응형

태블릿 이하에서는 사이드바를 본문 아래로 보내 한 방향으로 읽히게 만든다.`,
  },
]

export const popularPosts = posts.slice(0, 3)

export function findPostBySlug(slug: string) {
  return posts.find(post => post.slug === slug)
}
