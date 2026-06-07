<template>
  <section class="admin-page">
    <v-container>
      <div class="admin-toolbar">
        <div>
          <span>Admin</span>
          <h1>Posts</h1>
        </div>
        <div class="admin-toolbar__actions">
          <v-btn rounded="pill" variant="tonal" @click="handleLogout">로그아웃</v-btn>
          <v-btn color="primary" rounded="pill" to="/admin/posts/new">새 글</v-btn>
        </div>
      </div>

      <div class="admin-filter">
        <v-select
          v-model="status"
          :items="statusItems"
          density="compact"
          hide-details
          label="상태"
          variant="outlined"
        />
        <v-select
          v-model="categoryKey"
          :items="categoryItems"
          density="compact"
          hide-details
          label="카테고리"
          variant="outlined"
        />
        <v-select
          v-model="tagKey"
          :items="tagItems"
          density="compact"
          hide-details
          label="태그"
          variant="outlined"
        />
        <v-text-field
          v-model="keywordInput"
          density="compact"
          hide-details
          label="제목 검색"
          prepend-inner-icon="mdi-magnify"
          variant="outlined"
          @keyup.enter="applyKeyword"
        />
        <v-btn rounded="pill" variant="tonal" @click="applyKeyword">검색</v-btn>
      </div>

      <v-table class="admin-table">
        <thead>
          <tr>
            <th>제목</th>
            <th>상태</th>
            <th>카테고리</th>
            <th>태그</th>
            <th>조회</th>
            <th>수정일</th>
            <th>액션</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="post in page.items" :key="post.id">
            <td>
              <strong>{{ post.title }}</strong>
              <small>{{ post.slug }}</small>
            </td>
            <td>
              <v-chip :color="statusColor(post.status)" size="small" variant="tonal">
                {{ statusLabel(post.status) }}
              </v-chip>
            </td>
            <td>{{ post.category }}</td>
            <td>{{ post.tags.join(', ') }}</td>
            <td>{{ post.viewCount }}</td>
            <td>{{ formatDate(post.updatedAt) }}</td>
            <td>
              <div class="admin-table__actions">
                <v-btn
                  icon="mdi-pencil"
                  size="small"
                  :to="`/admin/posts/${post.id}/edit`"
                  variant="text"
                  aria-label="수정"
                />
                <v-btn
                  icon="mdi-open-in-new"
                  size="small"
                  :disabled="post.status !== 'published'"
                  :to="post.status === 'published' ? `/posts/${post.slug}` : undefined"
                  variant="text"
                  aria-label="공개 보기"
                />
                <v-btn
                  icon="mdi-eye-off"
                  size="small"
                  variant="text"
                  aria-label="숨김 처리"
                  @click="askHide(post.id)"
                />
              </div>
            </td>
          </tr>
        </tbody>
      </v-table>

      <v-empty-state
        v-if="!page.items.length"
        headline="No posts"
        text="조건에 맞는 글이 없습니다."
        title="목록이 비어 있습니다"
      />

      <v-pagination
        v-if="page.totalPages > 1"
        v-model="pageNumber"
        class="admin-pagination"
        :length="page.totalPages"
        rounded="circle"
      />
    </v-container>

    <v-dialog v-model="hideDialog" max-width="420">
      <v-card rounded="lg">
        <v-card-title>숨김 처리</v-card-title>
        <v-card-text>이 글을 숨김 처리할까요? 공개 화면에서 보이지 않습니다.</v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="hideDialog = false">취소</v-btn>
          <v-btn color="error" variant="flat" @click="hidePost">숨김 처리</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </section>
</template>

<script setup lang="ts">
  import { computed, ref } from 'vue'
  import { useRouter } from 'vue-router'

  import { logout } from '@/api/auth'
  import { patchPostStatus } from '@/api/adminPosts'
  import { getAdminCategories, getAdminPosts, getTags } from '@/data/posts'
  import type { PostStatus } from '@/types/blog'

  const router = useRouter()
  const status = ref<PostStatus | ''>('')
  const categoryKey = ref('')
  const tagKey = ref('')
  const keyword = ref('')
  const keywordInput = ref('')
  const pageNumber = ref(1)
  const refreshKey = ref(0)
  const hideDialog = ref(false)
  const hideTargetId = ref<number>()

  const statusItems = [
    { title: '기본(draft + published)', value: '' },
    { title: '초안', value: 'draft' },
    { title: '공개', value: 'published' },
    { title: '숨김', value: 'hidden' },
  ]

  const categoryItems = computed(() => {
    refreshKey.value
    return [
      { title: '전체', value: '' },
      ...getAdminCategories().map(category => ({ title: category.name, value: category.key })),
    ]
  })

  const tagItems = computed(() => {
    refreshKey.value
    return [
      { title: '전체', value: '' },
      ...getTags().map(tag => ({ title: tag.name, value: tag.key })),
    ]
  })

  const page = computed(() => {
    refreshKey.value
    return getAdminPosts({
      status: status.value,
      categoryKey: categoryKey.value,
      tagKey: tagKey.value,
      keyword: keyword.value,
      page: pageNumber.value,
      size: 10,
    })
  })

  function applyKeyword() {
    keyword.value = keywordInput.value
    pageNumber.value = 1
  }

  function askHide(id: number) {
    hideTargetId.value = id
    hideDialog.value = true
  }

  async function hidePost() {
    if (typeof hideTargetId.value === 'number') {
      await patchPostStatus(hideTargetId.value, 'hidden')
    }

    refreshKey.value += 1
    hideDialog.value = false
    hideTargetId.value = undefined
  }

  async function handleLogout() {
    await logout()
    await router.push('/admin/login')
  }

  function statusLabel(value: PostStatus) {
    return {
      draft: '초안',
      published: '공개',
      hidden: '숨김',
    }[value]
  }

  function statusColor(value: PostStatus) {
    return {
      draft: 'secondary',
      published: 'primary',
      hidden: 'error',
    }[value]
  }

  function formatDate(value: string) {
    return new Intl.DateTimeFormat('ko-KR', {
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
    }).format(new Date(value))
  }
</script>
