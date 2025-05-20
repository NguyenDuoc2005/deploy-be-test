<template>
    <div v-if="visible">
        <a-modal :zIndex="10000" v-model:open="visible" title="Chọn loại công việc" :footer="null" width="400px">
            <!-- Select với tính năng tìm kiếm -->
            <a-select 
                :getPopupContainer="trigger => trigger.parentElement" 
                v-model:value="selectedJobId"
                :options="jobTypes.map(item => ({ value: item.id, label: item.type }))"
                placeholder="Chọn hoặc thêm loại công việc" 
                allow-clear 
                show-search 
                class="w-full mb-2"
                :filterOption="(input, option) => option.label.toLowerCase().includes(input.toLowerCase())"
                @search="onSearch" 
                @change="onSelectJob" 
            />

            <div class="mt-2">
                <div class="text-sm mb-1 text-gray-500">Các loại đã có:</div>
                <div class="flex flex-wrap gap-2">
                    <a-tag v-for="(job, index) in jobTypes" :key="job.id" closable @close="removeJobType(job.id)">
                        {{ job.type }}
                    </a-tag>
                </div>
            </div>

            <div class="mt-4 flex items-center gap-2">
                <a-input v-model:value="newJob" placeholder="Nhập loại công việc mới" class="flex-1"
                    @keydown.enter.prevent="addJobType" />
                <a-button type="primary" @click="addJobType">Thêm mới</a-button>
            </div>
        </a-modal>
    </div>
</template>

<script setup>
import { TODO_ID_STORAGE_KEY } from '@/constants/key'
import { changeStatusTypeTodo, createTypeTodo, getAllTypeTodo } from '@/services/api/member/projectdetail/typetodo.api'
import { webSocketTypeTodo } from '@/services/service/member/typetodo.socket'
import { localStorageAction } from '@/utils/storage'
import { ref, computed, defineProps, defineEmits, watch } from 'vue'

const props = defineProps({
    open: {
        type: Boolean,
        default: false
    },
    idType : String ,
    type : String 
})

const emit = defineEmits(['update:open', 'select'])

const visible = computed({
    get: () => props.open,
    set: (val) => emit('update:open', val)
})

const selectedJobId = ref(null)
const jobTypes = ref([]) 
const newJob = ref('')

const fetchTypeTodo = async () => {
    try {
        const response = await getAllTypeTodo()
        jobTypes.value = response?.data || []
        console.log('kiểu công việc:', jobTypes.value)
    } catch (error) {
        console.error('Lỗi khi tải danh sách thể loại:', error)
    }
}

// onMounted(fetchTypeTodo)
const type = ref(null);

watch(() => props.open, (val) => {
    visible.value = val;
    if (val) {
        fetchTypeTodo().then(() => {
            const matchedJob = jobTypes.value.find(job => job.id === props.idType);
            if (matchedJob) {
                selectedJobId.value = matchedJob.id;
            } else {
                selectedJobId.value = null;
            }
        });
    }
});


const addJobType = async () => {
    const trimmed = newJob.value.trim()

    const payload = {
        type: trimmed
    }
    webSocketTypeTodo.sendUpdateTypeTodo(payload);
    webSocketTypeTodo.getUpdateTypeTodo(fetchTypeTodo);
    newJob.value = ''
}

const removeJobType = async (idToRemove) => {

    const payload = {
        typeId: idToRemove
    }
    webSocketTypeTodo.sendChangeTypeTodo(payload)
    webSocketTypeTodo.getChangeStatusTypeTodo(fetchTypeTodo)
}

const onSearch = (value) => {
    if (value && !jobTypes.value.some(j => j.type === value)) {
        newJob.value = value
    }
}

const onSelectJob = (value) => {
    const payload ={
        idType: value, 
        idTodo: localStorageAction.get(TODO_ID_STORAGE_KEY)
    }
    webSocketTypeTodo.sendJoinTypeTodo(payload);
    webSocketTypeTodo.getJoinTypeTodo(fetchTypeTodo)
}

</script>

<style scoped>
.a-select-dropdown {
    z-index: 10001 !important;
}
</style>
