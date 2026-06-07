<template>
  <section class="admin-page admin-page--center">
    <v-card class="admin-login" rounded="lg" elevation="0">
      <v-card-text>
        <div class="admin-login__title">
          <span>Admin</span>
          <h1>Login</h1>
        </div>

        <v-form @submit.prevent="submit">
          <v-text-field
            v-model="username"
            autocomplete="username"
            density="comfortable"
            label="아이디"
            variant="outlined"
          />
          <v-text-field
            v-model="password"
            autocomplete="current-password"
            density="comfortable"
            label="비밀번호"
            type="password"
            variant="outlined"
          />
          <v-alert v-if="errorMessage" class="mb-4" type="error" variant="tonal">
            {{ errorMessage }}
          </v-alert>
          <v-btn block color="primary" rounded="pill" type="submit">로그인</v-btn>
        </v-form>
      </v-card-text>
    </v-card>
  </section>
</template>

<script setup lang="ts">
  import { ref } from 'vue'
  import { useRoute, useRouter } from 'vue-router'

  import { login } from '@/api/auth'

  const router = useRouter()
  const route = useRoute()
  const username = ref('admin')
  const password = ref('password')
  const errorMessage = ref(route.query.expired ? '로그인이 만료되었습니다' : '')

  async function submit() {
    errorMessage.value = ''

    try {
      await login(username.value, password.value)
      await router.push('/admin/posts')
    } catch (error) {
      errorMessage.value = error instanceof Error
        ? error.message
        : '아이디 또는 비밀번호가 올바르지 않습니다.'
    }
  }
</script>
