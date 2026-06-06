<template>
  <aside class="toc">
    <div class="section-label">On this page</div>
    <a v-for="heading in headings" :key="heading.id" :href="`#${heading.id}`">
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
      .filter(line => line.startsWith('## '))
      .map(line => {
        const text = line.replace('## ', '')
        return {
          id: slugify(text),
          text,
        }
      })
  })
</script>
