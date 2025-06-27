<template>
  <div class="message" style="font-size: 20px;padding-top: 2px;">
    <el-tooltip content="消息通知" placement="bottom" effect="dark">
      <div>
        <el-popover placement="bottom" :width="310" aria-hidden="true" trigger="click">
          <template #reference>
            <el-icon>
              <el-badge :value="5" class="item">
                <Bell />
              </el-badge>
            </el-icon>
          </template>
          <el-tabs v-model="activeName">
            <el-tab-pane label="通知(5)" name="first">
              <div class="message-list">
                <div class="message-item">
                  <img src="@/assets/images/work/msg01.png" alt="" class="message-icon" />
                  <div class="message-content">
                    <span class="message-title">AntFlow 点个星星 🧡</span>
                    <span class="message-date">一分钟前</span>
                  </div>
                </div>
                <div class="message-item">
                  <img src="@/assets/images/work/msg02.png" alt="" class="message-icon" />
                  <div class="message-content">
                    <span class="message-title">AntFlow 点个星星 💙</span>
                    <span class="message-date">一小时前</span>
                  </div>
                </div>
                <div class="message-item">
                  <img src="@/assets/images/work/msg03.png" alt="" class="message-icon" />
                  <div class="message-content">
                    <span class="message-title"> AntFlow 点个星星 💚</span>
                    <span class="message-date">半天前</span>
                  </div>
                </div>

              </div>
            </el-tab-pane>
            <el-tab-pane label="消息(0)" name="second">
              <div class="message-empty">
                <img src="@/assets/images/notData.png" alt="notData" />
                <div>暂无消息</div>
              </div>
            </el-tab-pane>
            <el-tab-pane label="代办(0)" name="third">
              <div class="message-empty">
                <img src="@/assets/images/notData.png" alt="notData" />
                <div>暂无代办</div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-popover>
      </div>
    </el-tooltip>
  </div>
</template>

<script setup lang="js">
const activeName = ref("first");
</script>

<style scoped lang="scss">
.message-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 260px;
  line-height: 45px;
}

.message-list {
  display: flex;
  flex-direction: column;

  .message-item {
    display: flex;
    align-items: center;
    padding: 20px 0;
    border-bottom: 1px solid var(--el-border-color-light);

    &:last-child {
      border: none;
    }

    .message-icon {
      width: 40px;
      height: 40px;
      margin: 0 20px 0 5px;
    }

    .message-content {
      display: flex;
      flex-direction: column;

      .message-title {
        margin-bottom: 5px;
      }

      .message-date {
        font-size: 12px;
        color: var(--el-text-color-secondary);
      }
    }
  }
}
</style>
