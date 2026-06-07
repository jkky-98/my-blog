<template>
  <section class="page-section post-detail-page">
    <v-container>
      <template v-if="post">
        <v-row>
          <v-col cols="12" lg="8">
            <article class="post-detail">
              <div class="post-detail__category">
                Category : {{ post.category }}
              </div>
              <h1>{{ post.title }}</h1>
              <PostMeta
                :author="post.author"
                :created-at="post.createdAt"
                :reading-time="post.readingTime"
                :view-count="post.viewCount"
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
                    <PostMeta
                      :created-at="item.createdAt"
                      :reading-time="item.readingTime"
                      :view-count="item.viewCount"
                    />
                  </div>
                </RouterLink>
              </div>
            </div>
          </v-col>
        </v-row>
      </template>

      <v-empty-state
        v-else
        :headline="emptyState.headline"
        :text="emptyState.text"
        :title="emptyState.title"
      >
        <template #actions>
          <v-btn color="primary" rounded="pill" to="/posts">목록으로</v-btn>
        </template>
      </v-empty-state>
    </v-container>
  </section>
</template>

<script setup lang="ts">
  import { computed, ref, watch } from 'vue'

  import MarkdownContent from '@/components/blog/MarkdownContent.vue'
  import PostMeta from '@/components/blog/PostMeta.vue'
  import TableOfContents from '@/components/blog/TableOfContents.vue'
  import { fetchPostDetail } from '@/api/posts'
  import { getPopularPosts } from '@/data/posts'
  import type { PostDetail } from '@/types/blog'

  const props = defineProps<{
    slug: string
  }>()

  const post = ref<PostDetail>()
  const errorCode = ref<'POST_NOT_FOUND' | 'POST_NOT_PUBLIC'>()
  const popularPosts = getPopularPosts(3)

  const emptyState = computed(() => {
    if (errorCode.value === 'POST_NOT_PUBLIC') {
      return {
        headline: 'Private post',
        title: '비공개 글이거나 삭제된 글입니다',
        text: '공개되지 않은 글은 목록과 상세 화면에서 볼 수 없습니다.',
      }
    }

    return {
      headline: 'Post not found',
      title: '없는 글입니다',
      text: '요청한 글을 찾을 수 없습니다.',
    }
  })

  watch(
    () => props.slug,
    async slug => {
      const result = await fetchPostDetail(slug)
      post.value = result.post
      errorCode.value = result.errorCode
    },
    { immediate: true },
  )
</script>
