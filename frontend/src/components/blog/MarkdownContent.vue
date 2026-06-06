<template>
  <article class="markdown-content">
    <template v-for="(block, index) in blocks" :key="`${block.type}-${index}`">
      <h1 v-if="block.type === 'h1'" :id="block.id">
        {{ block.text }}
      </h1>
      <h2 v-else-if="block.type === 'h2'" :id="block.id">
        {{ block.text }}
      </h2>
      <pre v-else-if="block.type === 'code'"><code>{{ block.text }}</code></pre>
      <ul v-else-if="block.type === 'list'">
        <li v-for="item in block.items" :key="item">{{ item }}</li>
      </ul>
      <p v-else>{{ block.text }}</p>
    </template>
  </article>
</template>

<script setup lang="ts">
  import { computed } from 'vue'

  type MarkdownBlock =
    | { type: 'h1' | 'h2' | 'p' | 'code', text: string, id?: string }
    | { type: 'list', items: string[] }

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

  const blocks = computed<MarkdownBlock[]>(() => {
    const lines = props.content.trim().split('\n')
    const parsed: MarkdownBlock[] = []
    let paragraph: string[] = []
    let list: string[] = []
    let code: string[] = []
    let inCode = false

    const flushParagraph = () => {
      if (paragraph.length) {
        parsed.push({ type: 'p', text: paragraph.join(' ') })
        paragraph = []
      }
    }

    const flushList = () => {
      if (list.length) {
        parsed.push({ type: 'list', items: list })
        list = []
      }
    }

    for (const line of lines) {
      if (line.startsWith('```')) {
        if (inCode) {
          parsed.push({ type: 'code', text: code.join('\n') })
          code = []
          inCode = false
        } else {
          flushParagraph()
          flushList()
          inCode = true
        }
        continue
      }

      if (inCode) {
        code.push(line)
        continue
      }

      if (!line.trim()) {
        flushParagraph()
        flushList()
        continue
      }

      if (line.startsWith('# ')) {
        flushParagraph()
        flushList()
        const text = line.replace('# ', '')
        parsed.push({ type: 'h1', text, id: slugify(text) })
        continue
      }

      if (line.startsWith('## ')) {
        flushParagraph()
        flushList()
        const text = line.replace('## ', '')
        parsed.push({ type: 'h2', text, id: slugify(text) })
        continue
      }

      if (line.startsWith('- ')) {
        flushParagraph()
        list.push(line.replace('- ', ''))
        continue
      }

      paragraph.push(line.trim())
    }

    flushParagraph()
    flushList()

    return parsed
  })
</script>
