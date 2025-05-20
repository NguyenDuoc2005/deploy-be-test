import { router } from '@/routes/router'
import { VueQueryPlugin } from '@tanstack/vue-query'
import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/reset.css'
import { createPinia } from 'pinia'
import { createApp } from 'vue'
import './index.scss'
import 'vue3-toastify/dist/index.css'
import './index.css'
import 'jsvectormap/dist/jsvectormap.min.css'
import 'flatpickr/dist/flatpickr.min.css'
import './index.scss'
import './assets/style/style.css'
import 'vue3-toastify/dist/index.css'
import websocketPlugin from "@/services/configsocket/websocketPlugin";

import App from '@/App.vue'
(window as any).global = window
const app = createApp(App)

app.use(websocketPlugin);
app.use(router)
app.use(Antd)
app.use(VueQueryPlugin)
app.use(createPinia())


app.mount('#app')
