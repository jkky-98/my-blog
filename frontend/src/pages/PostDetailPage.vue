<template>
  <section class="page-section post-detail-page">
    <v-container>
      <template v-if="post">
        <v-row>
          <v-col cols="12" lg="8">
            <article class="post-detail">
              <div class="post-detail__category">
                Category : {{ post.tags.join(', ') }}
              </div>
              <h1>{{ post.title }}</h1>
              <PostMeta
                :author="post.author"
                :created-at="post.createdAt"
                :reading-time="post.readingTime"
              />
              <p class="post-detail__description">{{ post.description }}</p>
              <MarkdownContent :content="post.content" />
            </article>
          </v-col>

          <v-col cols="12" lg="4">
            <div class="sticky-sidebar">
              <TableOfContents :content="post.content" />

              <div class="side-panel">
                <div class="panel-title">Popular !</div>
                <RouterLink
                  v-for="item in popularPosts"
                  :key="item.id"
                  :to="`/posts/${item.slug}`"
                  class="popular-item"
                >
                  <div class="popular-item__thumb" :class="`popular-item__thumb--${item.accent}`" />
                  <div>
                    <strong>{{ item.title }}</strong>
                    <PostMeta :created-at="item.createdAt" :reading-time="item.readingTime" />
                  </div>
                </RouterLink>
              </div>
            </div>
          </v-col>
        </v-row>
      </template>

      <v-empty-state
        v-else
        headline="Post not found"
        text="요청한 글을 찾을 수 없습니다."
        title="없는 글입니다"
      >
        <template #actions>
          <v-btn color="primary" rounded="pill" to="/posts">목록으로</v-btn>
        </template>
      </v-empty-state>
    </v-container>
  </section>
</template>

<script setup lang="ts">
  import { computed } from 'vue'

  import MarkdownContent from '@/components/blog/MarkdownContent.vue'
  import PostMeta from '@/components/blog/PostMeta.vue'
  import TableOfContents from '@/components/blog/TableOfContents.vue'
  import { findPostBySlug, popularPosts } from '@/data/posts'

  const props = defineProps<{
    slug: string
  }>()

  const post = computed(() => findPostBySlug(props.slug))
</script>
