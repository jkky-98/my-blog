<template>
  <section class="page-section">
    <v-container>
      <v-row>
        <v-col cols="12" md="8">
          <div class="page-title">
            <span>{{ selectedLabel }}</span>
            <h1>Posts</h1>
            <p>{{ pageDescription }}</p>
          </div>

          <v-row>
            <v-col v-for="post in visiblePosts" :key="post.id" cols="12" sm="6">
              <PostCard :post="post" />
            </v-col>
          </v-row>

          <div v-if="visiblePosts.length < page.totalCount" class="load-more">
            <v-btn rounded="pill" color="primary" variant="flat" @click="loadMore">
              더 보기
            </v-btn>
          </div>

          <v-empty-state
            v-if="!visiblePosts.length"
            headline="No posts"
            text="선택한 필터에 해당하는 글이 없습니다."
            title="글이 없습니다"
          />
        </v-col>

        <v-col cols="12" md="4">
          <div class="sticky-sidebar">
            <div class="side-panel">
              <div class="panel-title">Popular !</div>
              <RouterLink
                v-for="post in popularPosts"
                :key="post.id"
                :to="`/posts/${post.slug}`"
                class="popular-item"
              >
                <div class="popular-item__thumb" :class="`popular-item__thumb--${post.accent}`" />
                <div>
                  <strong>{{ post.title }}</strong>
                  <PostMeta
                    :created-at="post.createdAt"
                    :reading-time="post.readingTime"
                    :view-count="post.viewCount"
                  />
                </div>
              </RouterLink>
            </div>

            <div class="side-panel">
              <div class="panel-title">Category</div>
              <div class="category-list">
                <RouterLink
                  :class="{ 'category-list__item--active': !selectedCategoryKey && !selectedTagKey }"
                  :to="{ name: 'posts' }"
                  class="category-list__item"
                >
                  <span>&gt; All</span>
                  <span>({{ publicTotalCount }})</span>
                </RouterLink>
                <RouterLink
                  v-for="category in categories"
                  :key="category.key"
                  :class="{ 'category-list__item--active': selectedCategoryKey === category.key }"
                  :to="{ name: 'posts', query: { categoryKey: category.key } }"
                  class="category-list__item"
                >
                  <span>&gt; {{ category.name }}</span>
                  <span>({{ category.count }})</span>
                </RouterLink>
              </div>
            </div>

            <div class="side-panel">
              <div class="panel-title">Tags</div>
              <div class="tag-filter-list">
                <RouterLink
                  v-for="tag in tags"
                  :key="tag.key"
                  :class="{ 'tag-filter-list__item--active': selectedTagKey === tag.key }"
                  :to="{ name: 'posts', query: { tagKey: tag.key } }"
                  class="tag-filter-list__item"
                >
                  #{{ tag.name }} <span>{{ tag.count }}</span>
                </RouterLink>
              </div>
            </div>
          </div>
        </v-col>
      </v-row>
    </v-container>
  </section>
</template>

<script setup lang="ts">
  import { computed, ref, watch } from 'vue'
  import { useRoute } from 'vue-router'

  import PostMeta from '@/components/blog/PostMeta.vue'
  import PostCard from '@/components/blog/PostCard.vue'
  import { getCategories, getPopularPosts, getPublicPosts, getTags } from '@/data/posts'

  const route = useRoute()
  const pageSize = 6
  const pageNumber = ref(1)

  const selectedCategoryKey = computed(() => {
    return typeof route.query.categoryKey === 'string' ? route.query.categoryKey : ''
  })

  const selectedTagKey = computed(() => {
    return typeof route.query.tagKey === 'string' ? route.query.tagKey : ''
  })

  watch([selectedCategoryKey, selectedTagKey], () => {
    pageNumber.value = 1
  })

  const page = computed(() => {
    return getPublicPosts({
      categoryKey: selectedCategoryKey.value,
      tagKey: selectedTagKey.value,
      page: 1,
      size: pageNumber.value * pageSize,
    })
  })

  const visiblePosts = computed(() => page.value.items)
  const popularPosts = getPopularPosts(3)
  const categories = getCategories()
  const tags = getTags()
  const publicTotalCount = getPublicPosts({ size: 1 }).totalCount

  const selectedLabel = computed(() => {
    const category = categories.find(item => item.key === selectedCategoryKey.value)
    const tag = tags.find(item => item.key === selectedTagKey.value)

    if (category) return `Category / ${category.name}`
    if (tag) return `Tag / ${tag.name}`
    return 'Category / All'
  })

  const pageDescription = computed(() => {
    const category = categories.find(item => item.key === selectedCategoryKey.value)
    const tag = tags.find(item => item.key === selectedTagKey.value)

    if (category) return `${category.name} 주제로 남긴 기록.`
    if (tag) return `${tag.name} 태그로 묶은 기록.`
    return '백엔드, 프론트엔드, 디자인을 만들며 남긴 기록.'
  })

  function loadMore() {
    pageNumber.value += 1
  }
</script>
