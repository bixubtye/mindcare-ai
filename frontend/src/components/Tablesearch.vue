<template>
<el-form ref="ruleformRef" :model="formData" label-width="80px" class="table-search">
    <el-row :gutter="20">
        <el-col v-for="item in formItem" :key="item.prop" :xs="24" :sm="12" :md="8" :lg="8">
            <el-form-item :label="item.label" :prop="item.prop">
                <component :is="isType(item.type)" v-model="formData[item.prop]" :placeholder="item.placeholder">
                     <template v-if="item.type==='select'">
                    <el-option label="全部" value=""></el-option>
                    <el-option
                        v-for="opt in item.options"
                        :key="opt.value"
                        :label="opt.label"
                        :value="opt.value"
                    />
                </template>
                </component>
            </el-form-item>
        </el-col>
    </el-row>
    <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset(ruleformRef)">重置</el-button>
    </el-form-item>
</el-form>
</template>
<script setup>
import { ref, reactive } from 'vue'
const emit = defineEmits(['search'])
const formData = reactive({})
const ruleformRef = ref()
const props = defineProps({
    formItem: {
        type: Array,
        default: () => []
    }
})
const isType = (type) => {
    return {
        input: ElInput,
        select: ElSelect
    }[type]
}
const handleSearch = () => {
    emit('search', formData)
}
const handleReset = (formEL) => {
    if(!formEL) return
    formEL.resetFields()
    emit('search', formData)
}
</script>
<style scoped>
.table-search {
    padding: 18px 18px 2px;
    border: 1px solid var(--border-color);
    border-radius: 12px;
    background: #f8fbfa;
}
.table-search :deep(.el-form-item__label) {
    color: var(--text-secondary);
    font-weight: 600;
}
.table-search :deep(.el-select) {
    width: 100%;
}
@media (max-width: 768px) {
    .table-search { padding: 14px 12px 1px; }
}
</style>
