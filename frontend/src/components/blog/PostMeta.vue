<template>
  <div class="post-meta">
    <span>{{ formattedDate }}</span>
    <span>{{ readingTime }}분 읽기</span>
    <span v-if="typeof viewCount === 'number'">조회 {{ formattedViewCount }}</span>
    <span v-if="author">by {{ author }}</span>
  </div>
</template>

<script setup lang="ts">
  import { computed } from 'vue'

  const props = defineProps<{
    createdAt: string
    readingTime: number
    viewCount?: number
    author?: string
  }>()

  const formattedDate = computed(() => {
    return new Intl.DateTimeFormat('ko-KR', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    }).format(new Date(props.createdAt))
  })

  const formattedViewCount = computed(() => {
    return new Intl.NumberFormat('ko-KR').format(props.viewCount ?? 0)
  })
</script>
