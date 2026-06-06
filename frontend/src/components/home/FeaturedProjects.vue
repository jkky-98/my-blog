<template>
  <section id="projects" class="content-section content-section--compact">
    <v-container>
      <div class="section-heading">
        <h2>Projects</h2>
        <span>정적 목업 이후 API와 연결될 작업들</span>
      </div>

      <v-row>
        <v-col v-for="project in projects" :key="project.id" cols="12" md="4">
          <v-card class="project-card" rounded="lg" elevation="0">
            <v-card-text>
              <div class="project-card__status">{{ project.status }}</div>
              <h3>{{ project.name }}</h3>
              <p>{{ project.description }}</p>
              <div class="project-card__stack">
                <TagBadge v-for="item in project.stack" :key="item" :label="item" />
              </div>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </section>
</template>

<script setup lang="ts">
  import TagBadge from '@/components/blog/TagBadge.vue'
  import { projects } from '@/data/projects'
</script>
