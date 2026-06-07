<template>
  <article class="markdown-content" v-html="renderedContent" />
</template>

<script setup lang="ts">
  import MarkdownIt from 'markdown-it'
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

  const markdown = new MarkdownIt({
    breaks: true,
    html: false,
    linkify: true,
    typographer: true,
  })

  markdown.renderer.rules.heading_open = (tokens, index, options, env, self) => {
    const token = tokens[index]
    const nextToken = tokens[index + 1]

    if (token.tag === 'h2' || token.tag === 'h3') {
      token.attrSet('id', slugify(nextToken?.content ?? ''))
    }

    return self.renderToken(tokens, index, options)
  }

  const renderedContent = computed(() => {
    return markdown.render(props.content)
  })
</script>
