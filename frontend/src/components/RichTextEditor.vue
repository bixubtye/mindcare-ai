<template>
    <div class="rich-text-editor" :style="{ '--editor-height': height }">
        <Toolbar
            :editor="editorRef"
            :defaultConfig="toolbarConfig"
            :mode="mode"
            class="editor-toolbar"
        />
        <Editor
            v-model="valueHtml"
            :style="{ height, overflowY: 'hidden' }"
            :defaultConfig="editorConfig"
            :mode="mode"
            @onCreated="handleCreated"
            @onChange="handleChange"
            class="editor-content"
        />
    </div>
</template>

<script setup>
import '@wangeditor/editor/dist/css/style.css'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { onBeforeUnmount, ref, shallowRef, watch } from 'vue'

const props = defineProps({
    modelValue: {
        type: String,
        default: ''
    },
    mode: {
        type: String,
        default: 'default'
    },
    height: {
        type: String,
        default: '400px'
    },
    placeholder: {
        type: String,
        default: '请输入内容...'
    }
})

const emit = defineEmits(['update:modelValue', 'change', 'created'])

const editorRef = shallowRef()
const valueHtml = ref(props.modelValue)

watch(() => props.modelValue, (val) => {
    if (val !== valueHtml.value) {
        valueHtml.value = val
    }
})

const handleCreated = (editor) => {
    editorRef.value = editor
    emit('created', editor)
}

const handleChange = (editor) => {
    valueHtml.value = editor.getHtml()
    emit('update:modelValue', valueHtml.value)
    emit('change', editor)
}

const toolbarConfig = {
    excludeKeys: ['group-video']
}

const editorConfig = {
    placeholder: props.placeholder,
    MENU_CONF: {
        fontSize: {
            fontSizeList: ['12px', '13px', '14px', '15px', '16px', '18px', '20px', '24px', '28px', '32px']
        }
    }
}

onBeforeUnmount(() => {
    if (editorRef.value) {
        editorRef.value.destroy()
        editorRef.value = null
    }
})
</script>

<style scoped>
.rich-text-editor {
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    overflow: hidden;
}
.editor-toolbar {
    border-bottom: 1px solid #dcdfe6;
}
.editor-content {
    height: var(--editor-height);
    overflow-y: auto;
}
</style>
