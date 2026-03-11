<template>
  <div class="row justify-center q-mt-xl">
    <div class="col-12 col-md-8 col-lg-6">
      <q-card flat bordered class="text-center q-pa-lg">
        <q-card-section>
          <q-icon name="how_to_vote" size="80px" color="primary" />
          <div class="text-h3 text-weight-bold q-mt-md text-primary">
            {{ t$('home.title') }}
          </div>
          <div class="text-subtitle1 text-grey-7 q-mt-sm">
            {{ t$('home.subtitle') }}
          </div>
        </q-card-section>

        <q-card-section v-if="authenticated">
          <q-banner rounded class="bg-positive text-white">
            <template v-slot:avatar>
              <q-icon name="check_circle" />
            </template>
            <span v-if="username">{{ t$('home.logged.message', { username }) }}</span>
          </q-banner>
        </q-card-section>

        <q-card-section v-if="!authenticated" class="q-gutter-y-md">
          <q-banner rounded class="bg-warning text-dark">
            <template v-slot:avatar>
              <q-icon name="info" />
            </template>
            <span>{{ t$('global.messages.info.authenticated.prefix') }}</span>
            <a href="javascript:void(0)" class="text-weight-bold text-dark" @click="showLogin()">
              {{ t$('global.messages.info.authenticated.link') }}
            </a>
            <span v-html="t$('global.messages.info.authenticated.suffix')"></span>
          </q-banner>

          <div class="row justify-center q-gutter-md q-mt-lg">
            <q-btn
              color="primary"
              icon="login"
              :label="t$('global.messages.info.authenticated.link')"
              size="lg"
              no-caps
              unelevated
              @click="showLogin()"
            />
            <q-btn
              outline
              color="primary"
              icon="person_add"
              :label="t$('global.messages.info.register.link')"
              size="lg"
              no-caps
              to="/register"
            />
          </div>
        </q-card-section>
      </q-card>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { type ComputedRef, inject } from 'vue';
import { useI18n } from 'vue-i18n';
import { useLoginModal } from '@/account/login-modal';

const { t: t$ } = useI18n();
const { showLogin } = useLoginModal();
const authenticated = inject<ComputedRef<boolean>>('authenticated');
const username = inject<ComputedRef<string>>('currentUsername');
</script>
