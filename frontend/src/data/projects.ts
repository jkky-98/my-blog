export type ProjectSummary = {
  id: number
  name: string
  description: string
  stack: string[]
  status: 'Design' | 'Mockup' | 'Planning'
}

export const projects: ProjectSummary[] = [
  {
    id: 1,
    name: 'Private Blog Platform',
    description: 'Vue 3 프론트와 Spring Boot API를 연결하는 개인 블로그 플랫폼.',
    stack: ['Vue', 'Vuetify', 'Spring Boot'],
    status: 'Mockup',
  },
  {
    id: 2,
    name: 'Developer Portfolio Kit',
    description: '프로젝트, 글, 실험 기록을 한 화면에서 보여주는 포트폴리오 구성.',
    stack: ['TypeScript', 'Design System', 'SEO'],
    status: 'Design',
  },
  {
    id: 3,
    name: 'Backend Lab Notes',
    description: '캐시, DB, 배포 기록을 글과 코드 조각으로 정리하는 실험 노트.',
    stack: ['Redis', 'MySQL', 'Docker'],
    status: 'Planning',
  },
]
