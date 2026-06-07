<template>
  <section class="admin-page">
    <v-container fluid>
      <div class="admin-toolbar">
        <div>
          <span>Admin</span>
          <h1>{{ isEdit ? 'Edit Post' : 'New Post' }}</h1>
        </div>
        <div class="admin-toolbar__actions">
          <v-btn rounded="pill" variant="tonal" to="/admin/posts">목록</v-btn>
          <v-btn rounded="pill" variant="tonal" @click="saveDraft">초안 저장</v-btn>
          <v-btn color="primary" rounded="pill" @click="publishDialog = true">공개하기</v-btn>
        </div>
      </div>

      <v-alert v-if="errorMessage" class="mb-4" type="error" variant="tonal">
        {{ errorMessage }}
      </v-alert>

      <v-progress-linear v-if="loading" class="mb-4" color="primary" indeterminate />

      <div v-else class="editor-grid">
        <section class="editor-panel">
          <v-text-field
            v-model="form.title"
            :error-messages="fieldErrors.title"
            density="comfortable"
            label="제목"
            variant="outlined"
            @update:model-value="dirty = true"
          />

          <div class="category-row">
            <v-select
              v-model="form.category"
              :items="categoryItems"
              :error-messages="fieldErrors.category"
              density="comfortable"
              label="카테고리"
              variant="outlined"
              @update:model-value="dirty = true"
            />
            <v-btn icon="mdi-plus" variant="tonal" aria-label="카테고리 추가" @click="categoryDialog = true" />
          </div>

          <v-text-field
            v-model="tagInput"
            :error-messages="fieldErrors.tags"
            density="comfortable"
            hint="쉼표로 구분해서 입력"
            label="태그"
            persistent-hint
            variant="outlined"
            @update:model-value="dirty = true"
          />

          <v-checkbox
            v-model="form.featured"
            color="primary"
            hide-details
            label="홈 대표 후보로 사용"
            @update:model-value="dirty = true"
          />

          <v-textarea
            v-model="form.content"
            :error-messages="fieldErrors.content"
            auto-grow
            class="markdown-editor"
            label="Markdown 본문"
            rows="22"
            variant="outlined"
            @update:model-value="dirty = true"
          />
        </section>

        <section class="editor-preview">
          <div class="preview-meta">
            <span>{{ form.category || 'Category' }}</span>
            <strong>{{ form.title || '제목을 입력하세요' }}</strong>
            <small>{{ previewTags }}</small>
          </div>
          <MarkdownContent :content="form.content || '# 미리보기\n\n본문을 입력하면 이곳에 렌더링됩니다.'" />
        </section>
      </div>
    </v-container>

    <v-dialog v-model="categoryDialog" max-width="420">
      <v-card rounded="lg">
        <v-card-title>카테고리 추가</v-card-title>
        <v-card-text>
          <v-text-field
            v-model="newCategory"
            autofocus
            label="새 카테고리"
            variant="outlined"
            @keyup.enter="addNewCategory"
          />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="categoryDialog = false">취소</v-btn>
          <v-btn color="primary" variant="flat" @click="addNewCategory">추가</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="publishDialog" max-width="420">
      <v-card rounded="lg">
        <v-card-title>공개하기</v-card-title>
        <v-card-text>이 글을 공개할까요? 공개 상세 화면에서 바로 볼 수 있습니다.</v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="publishDialog = false">취소</v-btn>
          <v-btn color="primary" variant="flat" @click="publish">공개하기</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </section>
</template>

<script setup lang="ts">
  import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
  import { onBeforeRouteLeave, useRoute, useRouter } from 'vue-router'

  import { createPost, fetchAdminPostDetail, updatePost } from '@/api/adminPosts'
  import { createCategory } from '@/api/categories'
  import MarkdownContent from '@/components/blog/MarkdownContent.vue'
  import { getAdminCategories } from '@/data/posts'
  import type { AdminPostRequest } from '@/types/blog'

  const route = useRoute()
  const router = useRouter()
  const postId = computed(() => Number(route.params.id))
  const isEdit = computed(() => Number.isFinite(postId.value))
  const dirty = ref(false)
  const loading = ref(false)
  const errorMessage = ref('')
  const publishDialog = ref(false)
  const categoryDialog = ref(false)
  const newCategory = ref('')
  const categoryRefreshKey = ref(0)
  const tagInput = ref('')
  const fieldErrors = reactive<Record<string, string>>({})

  const form = reactive<AdminPostRequest>({
    title: '',
    category: '',
    tags: [],
    featured: false,
    status: 'draft',
    content: '',
  })

  const categoryItems = computed(() => {
    categoryRefreshKey.value
    return getAdminCategories().map(category => category.name)
  })
  const previewTags = computed(() => parseTags(tagInput.value).map(tag => `#${tag}`).join(' '))

  onMounted(async () => {
    if (!isEdit.value) return

    loading.value = true
    try {
      const post = await fetchAdminPostDetail(postId.value)
      form.title = post.title
      form.category = post.category
      form.tags = post.tags
      form.featured = Boolean(post.featured)
      form.status = post.status
      form.content = post.content
      tagInput.value = post.tags.join(', ')
    } finally {
      loading.value = false
    }
  })

  window.addEventListener('beforeunload', beforeUnload)

  onBeforeUnmount(() => {
    window.removeEventListener('beforeunload', beforeUnload)
  })

  onBeforeRouteLeave(() => {
    if (!dirty.value) return true
    return window.confirm('저장하지 않은 변경이 있습니다. 페이지를 벗어날까요?')
  })

  async function saveDraft() {
    await submit('draft')
  }

  async function publish() {
    publishDialog.value = false
    await submit('published')
  }

  async function submit(status: 'draft' | 'published') {
    errorMessage.value = ''
    clearFieldErrors()

    try {
      form.status = status
      form.tags = parseTags(tagInput.value)
      validateForm()

      const savedPost = isEdit.value
        ? await updatePost(postId.value, form)
        : await createPost(form)

      dirty.value = false

      if (status === 'published') {
        await router.push(`/posts/${savedPost.slug}`)
      } else if (!isEdit.value) {
        await router.replace(`/admin/posts/${savedPost.id}/edit`)
      }
    } catch (error) {
      errorMessage.value = error instanceof Error ? error.message : '입력값을 확인해 주세요.'
    }
  }

  async function addNewCategory() {
    try {
      const category = await createCategory(newCategory.value)
      form.category = category.name
      categoryRefreshKey.value += 1
      newCategory.value = ''
      categoryDialog.value = false
      dirty.value = true
    } catch (error) {
      errorMessage.value = error instanceof Error ? error.message : '카테고리를 추가하지 못했습니다.'
    }
  }

  function validateForm() {
    if (!form.title.trim()) fieldErrors.title = '제목을 입력해 주세요.'
    if (!form.category.trim()) fieldErrors.category = '카테고리를 선택해 주세요.'
    if (!form.content.trim()) fieldErrors.content = '본문을 입력해 주세요.'
    if (!form.tags.length) fieldErrors.tags = '태그는 최소 1개 이상 입력해야 합니다.'
    if (form.tags.length > 10) fieldErrors.tags = '태그는 최대 10개까지 입력할 수 있습니다.'

    if (Object.keys(fieldErrors).length) {
      throw new Error('입력값을 확인해 주세요.')
    }
  }

  function parseTags(value: string) {
    const uniqueTags = new Map<string, string>()

    value
      .split(',')
      .map(tag => tag.trim())
      .filter(Boolean)
      .forEach(tag => uniqueTags.set(tag.toLowerCase(), tag))

    return [...uniqueTags.values()]
  }

  function clearFieldErrors() {
    Object.keys(fieldErrors).forEach(key => {
      delete fieldErrors[key]
    })
  }

  function beforeUnload(event: BeforeUnloadEvent) {
    if (!dirty.value) return

    event.preventDefault()
    event.returnValue = ''
  }
</script>
