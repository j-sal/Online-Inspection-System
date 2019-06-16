<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style lang="less" >
@import './assets/gongyong'; /*Introducing public style*/

#app{
    width: 100%;
    height: 100%;
    background: #f2f2f2;
    // Control browser size
    // min-width: 1280px;
    // max-width: 1920px;
}


// el-button el-button--default
.el-message-box__title{
    width: 100%;
    height: 40px;
    line-height: 40px;
    color: #545454;
    font-weight: bold;
    font-size: 25px;
}
.el-button--small{
    color: #999999;
    width: 80px;
    height: 35px;
    margin-right: 5px;
    margin-top: 40px;
    font-size: 13px;
    border-radius: 7px;
    background-color: #ffffff;
    border: 1px solid #979797;
}
// .el-button--primary{
//     color: #fff;
//     width: 80px;
//     height: 35px;
//     margin-right: 5px;
//     margin-top: 40px;
//     font-size: 13px;
//     border-radius: 7px;
//     background-color: #80BC7C;
//     border: none;
// }
//lianxi
.el-pagination.is-background .el-pager li:not(.disabled).active {
    background-color: #409EFF;
    color: #fff;
    border-radius: 50%;
    width: 50px;
    height: 50px;
    line-height: 50px;
    margin: auto;
    margin-left: 14px;
}
.el-pagination.is-background .btn-next, .el-pagination.is-background .btn-prev, .el-pagination.is-background .el-pager li {
    margin: 0 5px;
    background-color: #C28F6C;
    color: #606266;
    min-width: 30px;
    border-radius: 50%;
    width: 50px;
    height: 50px;
    line-height: 50px;
    margin: auto;
    margin-left: 14px;
}

//home
.el-submenu .el-menu-item {
    height: 50px;
    line-height: 50px;
    padding: 0 45px;
    min-width: 0px;
}

.el-submenu .el-menu-item {
    height: 50px;
    font-weight: bolder;
    font-size: 14px;
    line-height: 50px;
    padding: 0 45px;
    min-width: 0;
}
.el-menu-item {

    font-weight: bolder;
    font-size: 14px;

}

.is-active{
  font-size: 14px;
  color: #C28F6C !important;
  &::before{
    display: inline-block;
    content: "";
    width: 5px;
    height: 35px;
    background: #C28F6C;
    position: absolute;
    left: 0;
    margin-top: 10px;
  }
}

// menu
.el-submenu__title{
  font-size: 14px;
  color: #CECECE;
  font-weight: bolder;

}

// .element.style{
//     min-height:calc(100% - 120px)
// }

// Pagination


</style>
