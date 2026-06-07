<template>
  <aside class="toc">
    <div class="section-label">On this page</div>
    <a
      v-for="heading in headings"
      :key="heading.id"
      :class="{ 'toc__link--nested': heading.level === 3 }"
      :href="`#${heading.id}`"
    >
      {{ heading.text }}
    </a>
  </aside>
</template>

<script setup lang="ts">
  import { computed } from 'vue'

  const props = defineProps<{
    content: string
  }>()

  function slugify(value: string) {
    return value
      .toLowerCase()
      .replace(/[^a-z0-9가-힣\s-]/g, '')
      .trim()
      .replace(/\s+/g, '-')
  }

  const headings = computed(() => {
    return props.content
      .split('\n')
      .filter(line => line.startsWith('## ') || line.startsWith('### '))
      .map(line => {
        const level = line.startsWith('### ') ? 3 : 2
        const text = line.replace(/^#{2,3}\s/, '')
        return {
          id: slugify(text),
          level,
          text,
        }
      })
  })
</script>
