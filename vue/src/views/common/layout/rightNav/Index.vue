<template>
    <div id="rightNav">
        <div class="header">
            <div class="collapse" @click="collapseChange">
                <i v-if="menuCollapse" class="el-icon-caret-right"></i>
                <i v-else class="el-icon-caret-left"></i>
            </div>
            <div class="tabs">
                <wei-tabs></wei-tabs>
            </div>
            <div class="user">
                <el-dropdown @command="handleCommand">
                    <div class="el-dropdown-link">
                        <span>{{$store.state.userInfo['realName'] || $store.state.userInfo['username']}}</span>
                        <i class="el-icon-arrow-down el-icon--right"></i>
                    </div>
                    <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item>{{$store.state.role.name}}</el-dropdown-item>
                        <el-dropdown-item command="logout">退出</el-dropdown-item>
                    </el-dropdown-menu>
                </el-dropdown>
            </div>
        </div>
    </div>
</template>

<script>
    export default {
        name: "Index",
        components: {
            'wei-tabs': () => import('./Tabs.vue')
        },
        props: {
            menuCollapse: {
                type: Boolean,
                default: false
            }
        },
        methods: {
            handleCommand(command) {
                switch (command) {
                    case 'logout': {
                        this.logout();
                    }
                        break;
                    default: {

                    }
                }
            },
            collapseChange() {
                this.$emit('collapseChange');
            },
            logout() {
                const that = this;
                this.$axios({
                    url: that.$global.URL.logout,
                    success() {
                        that.$globalFun.errorMsg('注销成功，即将返回登录页面');
                        that.$store.dispatch('resetState');
                        sessionStorage.setItem('loginStatus', 'logout');
                        let timer = setTimeout(() => {
                            clearTimeout(timer);
                            that.$router.replace('login');
                        }, 3000);
                    }
                });
            }
        }
    }
</script>

<style lang="less" scoped>
    #rightNav {
        .header {
            overflow: hidden;
            border-bottom: 1px solid #e2e2e2;
            height: 44px;
            line-height: 44px;
            .collapse {
                float: left;
                width: 50px;
                font-size: 37px;
                text-align: center;
            }
            .tabs {
                float: left;
                max-width: 77%;
            }
            .user {
                float: right;
                margin-right: 20px;
            }
        }
    }
</style>