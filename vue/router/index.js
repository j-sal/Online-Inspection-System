import Vue from 'vue'
import Router from 'vue-router'
import Login from '@/components/Login'
import Home from '@/components/Home'
import weituodan from '@/components/weituodan'
import SteelWire from '@/components/SteelWire'
import alterpassword from '@/components/alterpassword'
import homepage from '@/components/homepage'
import shiyanjilu from '@/components/shiyanjilu'
import deluser from '@/components/deluser'
import cuowuchuli from '@/components/cuowuchuli'
import menzhong from '@/components/menzhong'
import One from '../components/one'
import Two from '../components/two'
import Jilubiao from '../components/jilubiao'
import Picihao from '../components/picihao'
import Baogaotongji from '../components/baogaotongji'




Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',  
      component: Login
    },
    {
      path: '/home',
      component: Home,
      redirect: '/homepage',
      children:[
       {
       path:'/weituodan',
       component:weituodan
       },
       {
        path:'/SteelWire',
        component:SteelWire
       },
       {
         path:"/alterpassword",
         component:alterpassword
       },
       {
         path:"/homepage",
         component:homepage
       },
       {
         path:"/shiyanjilu",
         component:shiyanjilu
       },
       {
         path:"/deluser",
         component:deluser
       },
       {
         path:"/cuowuchuli",
         component:cuowuchuli
       },
       {
         path:"/menzhong",
         component:menzhong
       },{
        path:'/picihao',
        component:Picihao
      },{
        path:'/baogaotongji',
        component:Baogaotongji
      }
      
    ]
    },,
    {
     path:"/one",
     name:'one',
     component:One
   },{
     path:'/two',
     name:'two',
     component:Two

   },{
     path:'/jilubiao',
     name:'/Jilubiao',
     component:Jilubiao
   },
    
  ]
})
