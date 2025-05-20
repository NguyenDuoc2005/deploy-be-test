<template>
  <a-modal :open="props.open" title="Thêm công việc mới" @close="closeTodoModal">
    <template #footer>
      <a-button @click="closeTodoModal">Hủy</a-button>
      <a-popconfirm title="Xác nhận tạo công việc mới" @confirm="handleSubmit">
        <a-button type="primary">Xác nhận</a-button>
      </a-popconfirm>
    </template>

    <div>
      <a-input v-model:value="nameTodo" placeholder="Nhập tên công việc"></a-input>
    </div>
  </a-modal>
</template>

<script lang="ts" setup>
import { dataCreateTodo, todoWebSocket } from '@/services/api/manage/todo/todo.socket.api';
import { debounce } from 'lodash';
import { onMounted, ref } from 'vue';
import { toast } from 'vue3-toastify';

const props = defineProps<{
  open: boolean,
  idProject: string | null,
}>();

const nameTodo = ref<string>('');

const emit = defineEmits(['close', 'success']);

const handleSubmit = () => {
  const payload: dataCreateTodo = {
    name: nameTodo.value,
    code: nameTodo.value,
    idProject: props.idProject as string
  }

  console.log('📤 Payload gửi đi: ', payload);
  todoWebSocket.sendMessageCreateTodo(payload);
  nameTodo.value = '';
  toast.success('Thêm mới thành công');
  emit('success');
  closeTodoModal();
}

const closeTodoModal = () => {
  emit('close');
}

const handleSuccessCreateNewTodo = debounce(() => {
  emit('success');
}, 200);

onMounted(() => {
  todoWebSocket.subscribeCreateTodo(handleSuccessCreateNewTodo);
  todoWebSocket.subscribeDeleteTodo(handleSuccessCreateNewTodo);
})
</script>
<style scoped>
.ant-btn {
  background-color: var(--selected-header) !important;
  color: var(--selected-text) !important;
  border-color: var(--selected-header) !important;
}

.ant-btn:hover,
.ant-btn:focus {
  background-color: var(--selected-header-hover) !important;
  border-color: var(--selected-header-hover) !important;
}
</style>