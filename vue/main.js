// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import ElementUI from 'element-ui';
// import 'element-ui/lib/theme-chalk/index.css';
import './assets/element-variables.scss';
import BScroll from 'better-scroll';
import './assets/iconfont/iconfont'
import betterscroll from 'better-scroll'
import './assets/iconfont/iconfont.css'
import Vuex from 'vuex'
Vue.config.productionTip = false
import Print from 'vue-print-nb'
Vue.use(ElementUI);
Vue.use(Print);  //Register
Vue.use(Vuex)
import axios from 'axios'

Vue.prototype.axios = axios;
axios.defaults.baseURL = "http://127.0.0.1:8080"

/* eslint-disable no-new */




// Register a global custom directive `v-focus`
Vue.directive('focus', {
  // When the bound element is inserted into the DOM:
  inserted: function (el) {
    // Focusing element
    el.focus();
    console.log("Get the focus");
  }
})

new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>',
  render: h => h(App)
})
