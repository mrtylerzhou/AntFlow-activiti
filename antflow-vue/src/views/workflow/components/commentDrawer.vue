<template>
    <el-drawer v-model="visible" title="流程沟通" direction="rtl" :size="600"
        :destroy-on-close="false" :with-header="true">
        <div v-loading="loading" class="comment-drawer">
            <!-- 消息列表 -->
            <div class="msg-list">
                <el-empty v-if="!loading && roots.length === 0" description="暂无沟通记录" />
                <template v-else>
                    <div v-for="root in roots" :key="root.id" class="msg-root">
                        <div class="msg-item" :class="{ mine: isMine(root) }">
                            <div class="msg-avatar">{{ avatarText(root) }}</div>
                            <div class="msg-body">
                                <div class="msg-head">
                                    <span class="msg-user">{{ displayName(root) }}</span>
                                    <span class="msg-time">{{ formatTime(root.createTime) }}</span>
                                </div>
                                <div v-if="isWithdrawn(root)" class="msg-content withdrawn">消息已撤回</div>
                                <div v-else class="msg-content" v-html="renderContent(root.content)"></div>
                                <div v-if="!isWithdrawn(root)" class="msg-actions">
                                    <el-button link type="primary" size="small" @click="startReply(root)">回复</el-button>
                                    <el-button v-if="isMine(root)" link type="danger" size="small" @click="withdraw(root)">撤回</el-button>
                                </div>

                                <!-- 回复(二级) -->
                                <div v-if="repliesOf(root).length" class="msg-replies">
                                    <div v-for="r in repliesOf(root)" :key="r.id" class="msg-item reply"
                                        :class="{ mine: isMine(r) }">
                                        <div class="msg-avatar">{{ avatarText(r) }}</div>
                                        <div class="msg-body">
                                            <div class="msg-head">
                                                <span class="msg-user">{{ displayName(r) }}</span>
                                                <span class="msg-reply-to">回复 @{{ r.replyToUserName || r.replyToUser || '-' }}</span>
                                                <span class="msg-time">{{ formatTime(r.createTime) }}</span>
                                            </div>
                                            <div v-if="isWithdrawn(r)" class="msg-content withdrawn">消息已撤回</div>
                                            <div v-else class="msg-content" v-html="renderContent(r.content)"></div>
                                            <div v-if="!isWithdrawn(r)" class="msg-actions">
                                                <el-button link type="primary" size="small" @click="startReply(r)">回复</el-button>
                                                <el-button v-if="isMine(r)" link type="danger" size="small" @click="withdraw(r)">撤回</el-button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </template>
            </div>

            <!-- 输入区 -->
            <div class="msg-input">
                <div v-if="replyTarget" class="reply-hint">
                    回复 @{{ replyTarget.createUserName || replyTarget.createUser }}
                    <el-icon class="reply-close" @click="cancelReply"><Close /></el-icon>
                </div>
                <div class="input-row">
                    <el-popover placement="top-start" :width="220" trigger="click" v-model:visible="mentionVisible">
                        <template #reference>
                            <el-button class="mention-btn" link type="primary">@</el-button>
                        </template>
                        <div class="mention-panel">
                            <el-scrollbar max-height="200px">
                                <div v-for="p in participants" :key="p.userId" class="mention-item"
                                    @click="insertMention(p)">
                                    {{ p.userName }}
                                </div>
                                <el-empty v-if="participants.length === 0" :image-size="40" description="暂无可提及的参与者" />
                            </el-scrollbar>
                        </div>
                    </el-popover>
                    <el-input v-model="inputText" type="textarea" :rows="2" resize="none"
                        :placeholder="replyTarget ? `回复 @${replyTarget.createUserName || replyTarget.createUser}...` : '输入沟通内容，点击 @ 提及参与者'"
                        @keydown.enter.exact.prevent="send" />
                    <el-button type="primary" class="send-btn" :loading="sending" :disabled="!canSend" @click="send">发送</el-button>
                </div>
            </div>
        </div>
    </el-drawer>
</template>

<script setup>
import { computed, ref, watch } from 'vue';
import { Close } from '@element-plus/icons-vue';
import Cookies from 'js-cookie';
import { getProcessComments, saveProcessComment, withdrawProcessComment, getBpmVerifyInfoVos } from '@/api/workflow/index';
import { parseTime } from '@/utils/ruoyi';

const props = defineProps({
    modelValue: { type: Boolean, default: false },
    processNumber: { type: String, default: '' },
});
const emits = defineEmits(['update:modelValue']);

const visible = computed({
    get: () => props.modelValue,
    set: (v) => emits('update:modelValue', v),
});

const loading = ref(false);
const sending = ref(false);
const messages = ref([]);
const inputText = ref('');
const replyTarget = ref(null);
const mentionVisible = ref(false);
const participants = ref([]);

const currentUserId = Cookies.get('userId');

// 根消息: parentId 为 null
const roots = computed(() =>
    messages.value
        .filter(m => (m.parentId == null || m.parentId === 0))
        .slice()
        .sort(byTime)
);
const replyMap = computed(() => {
    const map = new Map();
    for (const m of messages.value) {
        if (m.rootId != null) {
            if (!map.has(m.rootId)) map.set(m.rootId, []);
            map.get(m.rootId).push(m);
        }
    }
    for (const list of map.values()) list.sort(byTime);
    return map;
});
function repliesOf(root) {
    return replyMap.value.get(root.id) || [];
}
function byTime(a, b) {
    const ta = (a.createTime || '').toString();
    const tb = (b.createTime || '').toString();
    if (ta !== tb) return ta < tb ? -1 : 1;
    return (a.id || 0) - (b.id || 0);
}

const canSend = computed(() => inputText.value.trim().length > 0 && !sending.value);

function isMine(m) {
    return m.createUser && m.createUser === currentUserId;
}
function isWithdrawn(m) {
    return m.isDeleted === 1 || m.isDeleted === '1';
}
function displayName(m) {
    return (m.createUserName && m.createUserName.trim()) ? m.createUserName : (m.createUser || '-');
}
function avatarText(m) {
    const name = displayName(m);
    return name && name !== '-' ? name.substring(0, 1) : '?';
}
function formatTime(t) {
    if (!t) return '';
    return parseTime(t, '{y}-{m}-{d} {h}:{i}:{s}');
}

/**
 * 渲染正文: 先转义 HTML, 再把 @姓名 高亮(防 XSS).
 */
function renderContent(text) {
    if (!text) return '';
    let s = String(text)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
    const names = participants.value
        .map(p => p.userName)
        .filter(Boolean)
        .sort((a, b) => b.length - a.length);
    for (const n of names) {
        const escaped = n.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
        s = s.replace(new RegExp('@' + escaped, 'g'), `<span class="mention">@${n}</span>`);
    }
    return s;
}

// 回复
function startReply(msg) {
    replyTarget.value = msg;
    // 聚焦输入框(简单起见仅置位, 由用户点击)
}

function cancelReply() {
    replyTarget.value = null;
}

// @提及
function insertMention(p) {
    inputText.value = (inputText.value ? inputText.value : '') + '@' + p.userName + ' ';
    mentionVisible.value = false;
}

// 发送
async function send() {
    if (!canSend.value) return;
    const text = inputText.value.trim();
    const mentions = parseMentions(text);
    sending.value = true;
    try {
        const res = await saveProcessComment({
            processNumber: props.processNumber,
            parentId: replyTarget.value ? replyTarget.value.id : null,
            content: text,
            attachment: null,
            mentions,
        });
        if (res && res.code === 200 && res.data) {
            // 整体重新赋值触发响应式(避免直接改元素属性不触发的问题)
            messages.value = [...messages.value, res.data];
        }
        inputText.value = '';
        replyTarget.value = null;
    } catch (e) {
        console.error('saveProcessComment failed', e);
    } finally {
        sending.value = false;
    }
}

/**
 * 从正文解析 @姓名 得到 mentions (精确匹配参与者姓名).
 */
function parseMentions(text) {
    const list = [];
    const seen = new Set();
    for (const p of participants.value) {
        if (!p.userName) continue;
        if (text.includes('@' + p.userName) && !seen.has(p.userId)) {
            seen.add(p.userId);
            list.push({ userId: p.userId, userName: p.userName });
        }
    }
    return list;
}

// 撤回
async function withdraw(msg) {
    try {
        const res = await withdrawProcessComment(msg.id);
        if (res && res.code === 200) {
            messages.value = messages.value.map(m => m.id === msg.id ? { ...m, isDeleted: 1 } : m);
        }
    } catch (e) {
        console.error('withdrawProcessComment failed', e);
    }
}

// 加载消息 + 参与者
async function load() {
    if (!props.processNumber) {
        messages.value = [];
        return;
    }
    loading.value = true;
    try {
        const res = await getProcessComments(props.processNumber);
        if (res && res.code === 200) {
            messages.value = Array.isArray(res.data) ? res.data : [];
        } else {
            messages.value = [];
        }
    } catch (e) {
        console.error('getProcessComments failed', e);
        messages.value = [];
    } finally {
        loading.value = false;
    }
}

async function loadParticipants() {
    if (!props.processNumber) return;
    try {
        const res = await getBpmVerifyInfoVos({ processNumber: props.processNumber });
        const list = res && res.code === 200 && Array.isArray(res.data) ? res.data : [];
        const map = new Map();
        for (const it of list) {
            const uid = it.verifyUserId;
            const uname = it.verifyUserName || it.verifyUserId;
            if (uid && !map.has(uid)) {
                map.set(uid, { userId: uid, userName: uname });
            }
        }
        participants.value = Array.from(map.values());
    } catch (e) {
        console.error('getBpmVerifyInfoVos failed', e);
        participants.value = [];
    }
}

watch(() => props.modelValue, (v) => {
    if (v) {
        load();
        loadParticipants();
    }
});
watch(() => props.processNumber, () => {
    if (props.modelValue) {
        load();
        loadParticipants();
    }
});
</script>

<style lang="scss" scoped>
.comment-drawer {
    display: flex;
    flex-direction: column;
    height: 100%;
    padding: 0 4px;
}

.msg-list {
    flex: 1;
    overflow-y: auto;
    padding: 4px 8px 8px;
}

.msg-root {
    margin-bottom: 12px;
}

.msg-item {
    display: flex;
    gap: 8px;
    margin-bottom: 8px;

    .msg-avatar {
        flex-shrink: 0;
        width: 32px;
        height: 32px;
        line-height: 32px;
        text-align: center;
        border-radius: 50%;
        background-color: #409eff;
        color: #fff;
        font-size: 14px;
        user-select: none;
    }

    &.mine .msg-avatar {
        background-color: #909399;
    }

    .msg-body {
        flex: 1;
        min-width: 0;
    }

    .msg-head {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 12px;
        color: #909399;
    }

    .msg-user {
        font-weight: 600;
        color: #303133;
        font-size: 13px;
    }

    .msg-reply-to {
        color: #409eff;
    }

    .msg-time {
        margin-left: auto;
    }

    .msg-content {
        margin-top: 4px;
        font-size: 14px;
        color: #303133;
        white-space: pre-wrap;
        word-break: break-word;

        &.withdrawn {
            color: #c0c4cc;
            font-style: italic;
        }
    }

    .msg-actions {
        margin-top: 2px;
        padding: 0;
    }
}

.msg-replies {
    margin-top: 6px;
    padding-left: 12px;
    border-left: 2px solid #ebeef5;

    .msg-item {
        margin-bottom: 6px;
    }

    .msg-avatar {
        width: 26px;
        height: 26px;
        line-height: 26px;
        font-size: 12px;
    }
}

.msg-input {
    border-top: 1px solid #ebeef5;
    padding: 10px 8px 4px;
    background: #fff;

    .reply-hint {
        display: flex;
        align-items: center;
        font-size: 12px;
        color: #409eff;
        padding: 2px 4px 6px;

        .reply-close {
            margin-left: auto;
            cursor: pointer;
        }
    }

    .input-row {
        display: flex;
        align-items: flex-end;
        gap: 8px;

        .mention-btn {
            margin-bottom: 4px;
            font-size: 18px;
            font-weight: 600;
        }

        .send-btn {
            margin-bottom: 2px;
        }
    }
}

.mention-panel {
    .mention-item {
        padding: 8px 10px;
        cursor: pointer;
        border-radius: 4px;
        font-size: 13px;
        color: #303133;

        &:hover {
            background-color: #ecf5ff;
            color: #409eff;
        }
    }
}

:deep(.mention) {
    color: #409eff;
    font-weight: 600;
}
</style>
