<template>
  <article class="markdown-content" v-html="renderedContent" />
</template>

<script setup lang="ts">
  import MarkdownIt from 'markdown-it'
  import { computed } from 'vue'

  const props = defineProps<{
    content: string
  }>()

  const markdown = new MarkdownIt({
    breaks: true,
    html: false,
    linkify: true,
    typographer: true,
  })

  const renderedContent = computed(() => {
    return markdown.render(props.content)
  })
</script>
