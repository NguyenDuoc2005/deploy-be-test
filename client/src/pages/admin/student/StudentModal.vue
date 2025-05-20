<template>
  <a-modal :open="open" :title="title" @cancel="closeModal" :maskClosable="true" :centered="true">
    <template #footer>
      <a-popconfirm
        title="Bạn có chắc chắn muốn lưu thay đổi?"
        @confirm="handleSubmit"
        ok-text="Đồng ý"
        cancel-text="Huỷ"
      >
        <a-button type="primary">Xác nhận</a-button>
      </a-popconfirm>
      <a-button @click="closeModal">Huỷ</a-button>
    </template>

    <a-form :model="student" ref="StudentForm" name="StudentForm" autocomplete="off">
      <a-form-item
        label="Mã sinh viên"
        name="code"
        :rules="rules.code"
        :labelCol="{ style: { width: '150px' } }"
      >
        <a-input v-model:value="student.code" />
      </a-form-item>
      <a-form-item
        label="Tên sinh viên"
        name="name"
        :rules="rules.name"
        :labelCol="{ style: { width: '150px' } }"
      >
        <a-input v-model:value="student.name" />
      </a-form-item>
      <a-form-item
        label="Email"
        name="email"
        :rules="rules.email"
        :labelCol="{ style: { width: '150px'} }"
      >
        <a-input v-model:value="student.email" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, watch, defineProps, defineEmits } from 'vue'
import { toast } from 'vue3-toastify'
import dayjs from 'dayjs'
import { addStudent, detailStudent, updateStudent } from '@/services/api/admin/student.api'

const props = defineProps<{ open: boolean; id: string | null; title: string }>()
const emit = defineEmits(['close', 'success'])

const student = ref({
  code: '',
  name: '',
  email: '',
  status: ''
})
const StudentForm = ref()

const rules = {
  code: [
    { required: true, message: 'Mã sinh viên không được để trống!' },
    { min: 3, max: 50, message: 'Mã sinh viên phải từ 3 - 50 ký tự!' },
    { pattern: /^[a-zA-Z0-9]+$/, message: "Mã sinh viên không được chứa ký tự đặc biệt!" }
  ],
  name: [
    { required: true, message: 'Tên sinh viên không được để trống!' },
    { min: 3, max: 50, message: 'Tên sinh viên phải từ 3 - 50 ký tự!' },
    { pattern: /^[a-zA-ZÀ-Ỹà-ỹ0-9\s\u00C0-\u1EF9]+$/, message: "Tên sinh viên không được chứa ký tự đặc biệt!" }
  ],
  email: [
    { required: true, message: 'Email không được để trống!' },
    {
      pattern: /^[A-Za-z0-9._%+-]+@(fpt\.edu\.vn|gmail\.com)$/,
      message: 'Email phải có định dạng @fpt.edu.vn hoặc @gmail.com!',
      trigger: ['blur', 'change']
    }
  ],
status: [{ required: true, message: 'Trạng thái không được để trống!' }]
}

const fetchStudentDetails = async (id: string) => {
  try {
    const response = await detailStudent(id)
    student.value = {
      code: response.data.code,
      name: response.data.name,
      email: response.data.email,
      status: response.data.status
    }
  } catch (error) {
    console.error('Lỗi khi lấy thông tin sinh viên:', error)
  }
}

watch(
  () => props.id,
  async (newId) => {
    if (newId) {
      await fetchStudentDetails(newId)
    } else {
      student.value = { code: '', name: '', status: '', email: '' }
    }
  },
  { immediate: true }
)

const closeModal = () => emit('close')

const resetForm = () => {
  student.value = {
    code: '',
    name: '',
    status: '',
    email: ''
  }
  StudentForm.value?.resetFields()
}

const handleSubmit = async () => {
  try {
    await StudentForm.value.validate()
    const formData = {
      id: props.id || '',
      code: student.value.code,
      name: student.value.name,
      stuaus: student.value.status,
      email: student.value.email,
      createdDate: dayjs().valueOf()
    }

    if (props.id) {
      await updateStudent(formData, props.id)
      toast.success('Cập nhật thành công!')
    } else {
      await addStudent(formData)
      toast.success('Thêm thành công!')
    }
    resetForm()
    closeModal()
    emit('success')
  } catch (error) {
    toast.error('Lưu thất bại, vui lòng kiểm tra lại dữ liệu!')
  }
}
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