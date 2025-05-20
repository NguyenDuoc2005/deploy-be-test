<template>
    <DivCustom label="Bộ lọc">
        <template #icon>
        <FilterOutlined />
      </template>
      <div class="flex items-center space-x-2">
  
        <!-- Bộ lọc theo ngày -->
        <a-date-picker
          v-model:value="localDate"
          format="YYYY-MM-DD"
          class="w-4/12"
          placeholder="Chọn ngày báo cáo"
          @change="handleDateChange"
        />
  
        <a-tooltip title="Làm mới bộ lọc">
          <a-button @click="resetFilters" class="p-2 flex items-center justify-center">
            <ReloadOutlined />
          </a-button>
        </a-tooltip>
      </div>
    </DivCustom>
  </template>
  

  <script setup lang="ts">
  import { ref, watch } from 'vue';
  import DivCustom from '@/components/custom/Div/DivCustom.vue';
  import { FilterOutlined, ReloadOutlined } from '@ant-design/icons-vue';
  import dayjs from 'dayjs'; // đảm bảo đã cài: `npm install dayjs`
  
  const props = defineProps<{reportTime: number | null }>();
  const emit = defineEmits(['update:reportTime']);
  
  const localDate = ref(props.reportTime ? dayjs(props.reportTime) : null);

  
  // Khi người dùng chọn hoặc xóa ngày
  const handleDateChange = (date: any) => {
    const timestamp = date ? dayjs(date).startOf('day').valueOf() : null;
    console.log('📅 Ngày được chọn:', date?.format('YYYY-MM-DD'), '| Timestamp:', timestamp);

    emit('update:reportTime', timestamp);
  };
  
  // Đặt lại cả tìm kiếm & ngày
  const resetFilters = () => {
    localDate.value = null;
    emit('update:reportTime', null);
  };
  </script>
  
