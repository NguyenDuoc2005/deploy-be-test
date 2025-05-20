<template>
    <a-modal :open="open" :title="title" @cancel="closeModal" style="top: 20px">
        <template #footer>
            <a-popconfirm title="Bạn có chắc chắn muốn lưu thay đổi?" @confirm="handleSubmit" ok-text="Đồng ý" cancel-text="Huỷ">
                <a-button type="primary">Xác nhận</a-button>
            </a-popconfirm>
            <a-button @click="closeModal">Huỷ</a-button>
        </template>

        <a-form :model="departmentFacility" ref="departmentFacilityForm" name="departmentFacilityForm" autocomplete="off">
            <a-form-item label="Tên cơ sở" name="facilityId" >
                <a-select 
                    v-model:value="departmentFacility.facilityId" 
                    :options="facilityOptions"
                    placeholder="Chọn cơ sở"
                    show-search
                />
            </a-form-item>
        </a-form>
    </a-modal>
</template>

<script setup lang="ts">
import { ref, watch, defineProps, defineEmits, onMounted } from 'vue'
import { getDFListFacility, getDepartmentFacility, addDepartmentFacility, updateDepartmentFacility } from '@/services/api/departmentfacility.api'
import { toast } from 'vue3-toastify'
import { useRoute } from 'vue-router';

const props = defineProps<{ open: boolean; departmentFacilityId: string | null; title: string }>()
const emit = defineEmits(['close', 'success'])

const route = useRoute()
const departmentId = ref<string>(route.query.departmentId as string || '')

const departmentFacility = ref({
    departmentFacilityId: '',
    facilityId: '',
})

const departmentFacilityForm = ref()
const facilityOptions = ref<{ label: string; value: string }[]>([])

// Fetch danh sách cơ sở
const fetchFacilities = async () => {
    try {
        const response = await getDFListFacility()
        facilityOptions.value = response.data.map(facility => ({
            label: facility.facilityName,
            value: facility.facilityId,
        }))
    } catch (error) {
        console.error('Lỗi khi lấy danh sách cơ sở:', error)
    }
}

// Fetch chi tiết departmentFacility
const fetchDepartmentFacilityDetails = async (id: string) => {
    try {
        const response = await getDepartmentFacility(id)
        departmentFacility.value = {
            departmentFacilityId: response.data.departmentFacilityId,
            facilityId: response.data.facilityId,
        }
    } catch (error) {
        console.error('Lỗi khi lấy thông tin cơ sở bộ môn:', error)
    }
}

// Theo dõi khi ID thay đổi để load data
watch(
    () => props.departmentFacilityId,
    async (newId) => {
        if (newId) {
            await fetchDepartmentFacilityDetails(newId)
        } else {
            departmentFacility.value = { departmentFacilityId: '', facilityId: '' }
        }
    },
    { immediate: true }
)

const closeModal = () => emit('close')

const handleSubmit = async () => {
    try {
        await departmentFacilityForm.value.validate();

        if (!departmentId.value) {
            toast.error('Thiếu departmentId, vui lòng kiểm tra lại!');
            return;
        }

        const formData = {
            departmentId: departmentId.value,
            facilityId: departmentFacility.value.facilityId,
        };

        console.log("🔍 Dữ liệu gửi lên API:", JSON.stringify(formData, null, 2));

        if (props.departmentFacilityId) {
            await updateDepartmentFacility(formData, props.departmentFacilityId);
            toast.success('Cập nhật thành công!')
        } else {
            await addDepartmentFacility(formData);
            toast.success('Thêm thành công!')
        }

        closeModal();
        emit('success');
    } catch (error) {
        if (props.departmentFacilityId) {
        toast.error('Cập nhật thất bại')
      } else {
        toast.error('Thêm thất bại')
      }
    }
};


// Gọi API khi component được mounted
onMounted(fetchFacilities)
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