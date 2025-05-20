<template>
    <a-modal :open="open" :title="title" @cancel="closeModal" style="top: 20px">
      <template #footer>
        <a-popconfirm title="Bạn có chắc chắn muốn lưu thay đổi?" @confirm="handleSubmit" ok-text="Đồng ý" cancel-text="Huỷ">
          <a-button type="primary">Xác nhận</a-button>
        </a-popconfirm>
        <a-button @click="closeModal">Huỷ</a-button>
      </template>
  
      <a-form :model="facility" ref="facilityForm" name="facilityForm" autocomplete="off">
        <a-form-item label="Tên cơ sở" name="facilityName" :rules="rules.facilityName">
          <a-input v-model:value="facility.facilityName" />
        </a-form-item>
      </a-form>
    </a-modal>
  </template>
  
  <script setup lang="ts">
  import { ref, watch, defineProps, defineEmits } from 'vue';
  import { getFacilityById, addFacility, updateFacility } from '@/services/api/facility.api';
  import { toast } from 'vue3-toastify';
  import dayjs from 'dayjs';
  
  const props = defineProps<{ open: boolean; facilityId: string | null; title: string }>();
  const emit = defineEmits(['close', 'success']);
  
  const facility = ref({
    facilityCode: '',
    facilityName: '',
    facilityStatus: 1,
  });
  
  const facilityForm = ref();
  
  const rules = {
    facilityName: [{ required: true, message: 'Tên cơ sở không được để trống!'},
    {min: 3, max: 50, message: "Tên cơ sở phải từ 3 - 50 ký tự!"},
    { pattern: /^[a-zA-ZÀ-Ỹà-ỹ0-9\s\u00C0-\u1EF9]+$/, message: "Tên cơ sở không được chứa ký tự đặc biệt!" }],
  };

  const fetchFacilityDetails = async (id: string) => {
    try {
      const response = await getFacilityById(id);
      facility.value = {
        facilityCode: response.data.facilityCode,
        facilityName: response.data.facilityName,
        facilityStatus: response.data.facilityStatus,
      };
    } catch (error) {
      console.error('Lỗi khi lấy thông tin cơ sở:', error);
    }
  };
  
  watch(
    () => props.facilityId,
    async (newId) => {
      if (newId) {
        await fetchFacilityDetails(newId);
      } else {
        facility.value = { facilityCode: '', facilityName: '', facilityStatus: 1 };
      }
    },
    { immediate: true }
  );
  
  const closeModal = () => emit('close');
  
  const resetForm = () => {
    facility.value = { facilityCode: '', facilityName: '', facilityStatus: 1 };
    facilityForm.value?.resetFields();
  };
  
  const handleSubmit = async () => {
    try {
      await facilityForm.value.validate();
      const formData = {
        facilityCode: facility.value.facilityCode,
        facilityName: facility.value.facilityName,
        facilityStatus: facility.value.facilityStatus,
        createdDate: dayjs().valueOf(),
      };
  
      if (props.facilityId) {
        await updateFacility(formData, props.facilityId);
        toast.success('Cập nhật thành công!')
      } else {
        await addFacility(formData);
        toast.success('Thêm thành công!')
      }
      resetForm();
      closeModal();
      emit('success');
    } catch (error) {
      if (props.facilityId) {
        toast.error('Cập nhật thất bại')
      } else {
        toast.error('Thêm thất bại')
      }
    }
  };
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