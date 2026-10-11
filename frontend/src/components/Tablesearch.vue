<template>
<el-form ref="ruleformRef" :model="formData" label-width="80px">
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
