<template>
  <div class="index clear-float">
    <div class="menu">
      <el-menu
        class="el-menu-vertical-demo jie"
        :default-active="defaultActive"
        background-color="#303133"
        text-color="#C0C4CC"
        active-text-color="#DCDFE6"
        style="min-height:100%;"
        router
      >
        <div class="header-logo">
          <div class="logo"></div>
        </div>
        <el-menu-item index="homepage">
          <i class="iconfont">&#xe501;</i>
          &nbsp;&nbsp;Home
        </el-menu-item>

        <el-submenu index="2">
          <template slot="title">
            <i class="iconfont">&#xe78e;</i>
            &nbsp;&nbsp;data processing
          </template>
          <el-menu-item index="weituodan">order</el-menu-item>
          <el-menu-item index="shiyanjilu">View test record</el-menu-item>
          <el-menu-item index="cuowuchuli">Error handling list</el-menu-item>
        </el-submenu>


          <el-menu-item index="SteelWire"> <i class="iconfont">&#xe628;</i>&nbsp;&nbsp;&nbsp;Wire report</el-menu-item>



          <el-menu-item index="menzhong"> <i class="iconfont">&#xe632;</i>&nbsp;&nbsp;&nbsp;Wire rope</el-menu-item>


        <el-submenu index="5">
          <template slot="title">
            <i class="iconfont">&#xe66f;</i>
            &nbsp;&nbsp;User Management
          </template>
          <el-menu-item index="deluser" v-if="xianshi">&nbsp;&nbsp;user list</el-menu-item>
          <el-menu-item index="picihao">&nbsp;&nbsp;Machine management</el-menu-item>
          <el-menu-item index="baogaotongji" v-if="xianshi">&nbsp;&nbsp;Report statistics</el-menu-item>
          <el-menu-item index="alterpassword">&nbsp;&nbsp;change Password</el-menu-item>
        </el-submenu>

        <el-menu-item index="/" @click="exit">
          <i class="iconfont">&#xe501;</i>

          <span>&nbsp;&nbsp;Log out</span>
        </el-menu-item>
      </el-menu>
    </div>

    <div class="body">
      <router-view class="router-view"/>
    </div>
  </div>
</template>

<script>
export default {
  name: "Home",
  data() {
    return {
      xianshi: true,
      offsetHeight: 1,
      height: {
        height: ""
      }
    };
  },
  computed: {
    defaultActive: function() {
      return this.$route.path.replace("/", "");
    }
  },
  methods: {
    gethome() {
      console.log("-----------------------------------");
      console.log(localStorage.userRank);
      if (localStorage.userRank == "administrator") {
        this.xianshi = true;
        console.log("Hello, I am an administrator.");
      } else {
        this.xianshi = false;
        console.log("Hello, I am a regular user.");
      }
    },

    // Exit small button
    exit() {
      console.log("Hello, I am a small button to quit, clear localStorage");
      console.log(localStorage.id);
      console.log(localStorage.username);
      console.log(localStorage.userRank);
      console.log(localStorage.userClass);
      localStorage.id = "";
      localStorage.username = "";
      localStorage.userRank = "";
      localStorage.userClass = "";
      console.log("id" + localStorage.id);
      console.log("username" + localStorage.username);
      console.log("userRank" + localStorage.userRank);
      console.log("userClass" + localStorage.userClass);
    }
  },
  mounted() {
    this.gethome();
    this.height.height = document.body.offsetHeight;
    // console.log("12313")
    // console.log(document.body.offsetHeight)
  }
};
</script>
<style>
</style>


<style lang='less' scoped>
.clear-float::after {
  content: "";
  display: block;
  height: 0;
  clear: both;
  visibility: hidden;
}

.index {
  position: relative;
  width: 100%;
  height: 100%;
  background: #f2f2f2;
  overflow: hidden;
  .menu {
    height: 100%;
    width: 199px;
    float: left;
    background: #303133;
    overflow-y: scroll;
    overflow-x: hidden;
    background-color: red;
    .header-logo {
      height: 150px;
      width: 100%;
      background: #303133;
      overflow: auto;
      .logo {
        margin: auto;
        margin-top: 60px;
        height: 50px;
        width: 110px;
        background-image: url("../assets/img/J&L.png");
        background-size: 100% 100%;
      }
    }
  }
  .body {
    width: calc(100% - 200px);
    height: 100%;
    background: #f2f2f2;
    float: right;
    .header {
      min-width: 1160px;
      height: 50px;
      margin: auto;
      background: #f2f2f2;
      // box-shadow: 0  3px 20px rgba(0, 0,0, 0.2);
      z-index: 10;
      .breadcrumb {
        line-height: 50px;
        font-size: 18px;
        padding-left: 20px;
        color: #555;
        background-color: #f2f2f2;
        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
      }
    }

    .router-view {
      // height: 100%;
      position: relative;
      background: #f2f2f2;
      height: 100%;
      overflow: scroll;
    }
  }
}
.iconfont {
  width: 25px;
  height: 25px;
}
::-webkit-scrollbar {
  display: none;
}
</style>
