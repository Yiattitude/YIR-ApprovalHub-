<template>
  <div>
    <h2>我的申请</h2>
    <el-button type="primary" @click="loadApplications" :loading="loading" style="margin-bottom: 20px">
      刷新
    </el-button>
    <el-table :data="applications" style="width: 100%" v-loading="loading">
      <el-table-column prop="applicationNo" label="申请编号" width="200"></el-table-column>
      <el-table-column prop="applicationType" label="申请类型" width="120">
        <template #default="scope">
          {{ scope.row.applicationType === 'LEAVE' ? '请假' : '报销' }}
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" width="200"></el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="180">
        <template #default="scope">
          {{ formatDateTime(scope.row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="viewDetail(scope.row)">查看</el-button>
          <el-button size="small" type="primary" @click="submitApplication(scope.row)" 
                     v-if="scope.row.status === 'DRAFT'">提交</el-button>
          <el-button size="small" type="warning" @click="withdrawApplication(scope.row)" 
                     v-if="scope.row.status === 'PENDING'">撤回</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import api from '../services/api';

export default {
  name: 'MyApplications',
  setup() {
    const router = useRouter();
    const applications = ref([]);
    const loading = ref(false);
    let refreshInterval = null;

    const loadApplications = async () => {
      loading.value = true;
      try {
        const response = await api.getMyApplications();
        applications.value = response.data.data;
      } catch (error) {
        ElMessage.error('加载申请列表失败');
      } finally {
        loading.value = false;
      }
    };

    const viewDetail = (application) => {
      router.push(`/application/${application.id}`);
    };

    const submitApplication = async (application) => {
      try {
        await ElMessageBox.confirm('确认提交该申请吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });
        
        await api.submitApplication(application.id);
        ElMessage.success('申请已提交');
        loadApplications();
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error(error.response?.data?.message || '提交失败');
        }
      }
    };

    const withdrawApplication = async (application) => {
      try {
        await ElMessageBox.confirm('确认撤回该申请吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });
        
        await api.withdrawApplication(application.id);
        ElMessage.success('申请已撤回');
        loadApplications();
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error(error.response?.data?.message || '撤回失败');
        }
      }
    };

    const getStatusType = (status) => {
      const typeMap = {
        'DRAFT': 'info',
        'PENDING': 'warning',
        'APPROVED': 'success',
        'REJECTED': 'danger',
        'WITHDRAWN': 'info'
      };
      return typeMap[status] || 'info';
    };

    const getStatusText = (status) => {
      const textMap = {
        'DRAFT': '草稿',
        'PENDING': '审批中',
        'APPROVED': '已通过',
        'REJECTED': '已拒绝',
        'WITHDRAWN': '已撤回'
      };
      return textMap[status] || status;
    };

    const formatDateTime = (dateTime) => {
      if (!dateTime) return '-';
      return new Date(dateTime).toLocaleString('zh-CN');
    };

    onMounted(() => {
      loadApplications();
      refreshInterval = setInterval(() => {
        loadApplications();
      }, 30000);
    });

    onUnmounted(() => {
      if (refreshInterval) {
        clearInterval(refreshInterval);
      }
    });

    return {
      applications,
      loading,
      loadApplications,
      viewDetail,
      submitApplication,
      withdrawApplication,
      getStatusType,
      getStatusText,
      formatDateTime
    };
  }
};
</script>

<style scoped>
h2 {
  margin-bottom: 20px;
}
</style>
