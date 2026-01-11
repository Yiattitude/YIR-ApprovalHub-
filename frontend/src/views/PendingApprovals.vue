<template>
  <div>
    <h2>待审批任务</h2>
    <el-table :data="tasks" style="width: 100%" v-loading="loading">
      <el-table-column prop="application.applicationNo" label="申请编号" width="200"></el-table-column>
      <el-table-column prop="application.applicationType" label="申请类型" width="120">
        <template #default="scope">
          {{ scope.row.application.applicationType === 'LEAVE' ? '请假' : '报销' }}
        </template>
      </el-table-column>
      <el-table-column prop="application.title" label="标题" width="200"></el-table-column>
      <el-table-column prop="stepName" label="审批步骤" width="120"></el-table-column>
      <el-table-column prop="application.applicant.fullName" label="申请人" width="120"></el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="180">
        <template #default="scope">
          {{ formatDateTime(scope.row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="viewDetail(scope.row)">查看详情</el-button>
          <el-button size="small" type="success" @click="handleApprove(scope.row)">同意</el-button>
          <el-button size="small" type="danger" @click="handleReject(scope.row)">拒绝</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="decisionForm" ref="decisionFormRef" label-width="80px">
        <el-form-item label="审批意见">
          <el-input v-model="decisionForm.comment" type="textarea" :rows="4" 
                    placeholder="请输入审批意见（可选）"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmDecision" :loading="processing">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import api from '../services/api';

export default {
  name: 'PendingApprovals',
  setup() {
    const router = useRouter();
    const tasks = ref([]);
    const loading = ref(false);
    const dialogVisible = ref(false);
    const dialogTitle = ref('');
    const processing = ref(false);
    const currentTask = ref(null);
    const decisionFormRef = ref(null);

    const decisionForm = reactive({
      action: '',
      comment: ''
    });

    const loadTasks = async () => {
      loading.value = true;
      try {
        const response = await api.getMyPendingTasks();
        tasks.value = response.data.data;
      } catch (error) {
        ElMessage.error('加载待审批任务失败');
      } finally {
        loading.value = false;
      }
    };

    const viewDetail = (task) => {
      router.push(`/application/${task.application.id}`);
    };

    const handleApprove = (task) => {
      currentTask.value = task;
      decisionForm.action = 'APPROVE';
      decisionForm.comment = '';
      dialogTitle.value = '同意审批';
      dialogVisible.value = true;
    };

    const handleReject = (task) => {
      currentTask.value = task;
      decisionForm.action = 'REJECT';
      decisionForm.comment = '';
      dialogTitle.value = '拒绝审批';
      dialogVisible.value = true;
    };

    const confirmDecision = async () => {
      processing.value = true;
      try {
        await api.processApprovalTask(currentTask.value.id, decisionForm);
        ElMessage.success('审批处理成功');
        dialogVisible.value = false;
        loadTasks();
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '处理失败');
      } finally {
        processing.value = false;
      }
    };

    const formatDateTime = (dateTime) => {
      if (!dateTime) return '-';
      return new Date(dateTime).toLocaleString('zh-CN');
    };

    onMounted(() => {
      loadTasks();
    });

    return {
      tasks,
      loading,
      dialogVisible,
      dialogTitle,
      processing,
      decisionForm,
      decisionFormRef,
      viewDetail,
      handleApprove,
      handleReject,
      confirmDecision,
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
