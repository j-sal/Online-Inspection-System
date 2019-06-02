package com.hbsi.user.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * @description User management controller
 *
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hbsi.domain.User;
import com.hbsi.response.Response;
import com.hbsi.user.service.UserService;
@RestController
@RequestMapping("user")
public class UserController {
	@Resource
	private UserService userService;
	
	/**
	 * @description save user 
	 * @param user
	 * @return
	 */
	@PostMapping("saveUser")
	public Response<User> saveUser(@RequestBody User user){
		return userService.saveUser(user);
	}
	/**
	 * @description update user 
	 * @param user
	 * @return
	 */
	@PostMapping("updateUser")
	public Response<User> updateUser(@RequestBody User user){
		return userService.updateUser(user);
	}
	/**
	 * @description user login 
	 * @param user
	 * @return
	 * 传入参数  detail param: username,password
	 */
	@PostMapping("login")
	public Response<User> login(@RequestBody User user){
		return userService.login(user);
	}
	/**
	 * @description user list
	 * @return
	 */
	@GetMapping("selectUserList")
	public Response<List<User>> selectUserList(){
		return userService.selectUserList();
	}
	/**
	 * @description delete user
	 * @param userId
	 * @return
	 */
	@GetMapping("deleteUser")
	public Response<Integer> deleteUser(Integer userId){
		return userService.deleteUser(userId);
	}
	/**
	 * @description update user password 
	 * @param info
	 * @return
	 */
	@PostMapping("updateUserPassword")
	public Response<User> updateUserPassword(@RequestBody String info){
		/**
		 * The data format used for data interaction between the 
		 * frontend and the interface is a json string.
		 * Info is a json string, convert it to json object format, get
		 * the parameter value according to the key-value pair
		 * 
		 * The info format is：
		 * {"id":1,"oldPassword":"123456","newPassword":"123"}
		 * If the parameters in json correspond to the member variables
		 * in the entity class (User is an entity class), you can use
		 * the entity class to directly receive parameters, such as
		 * adding and modifying parameters in the function.
		 */
		JSONObject json=JSON.parseObject(info);
		String username=json.getString("username");
		String oldPassword=json.getString("oldPassword");
		String newPassword=json.getString("newPassword");
		return userService.updateUserPassword(username, oldPassword, newPassword);
	}
	/**
	 * @description reset user password
	 * @param userId
	 * @return
	 */
	@PostMapping("resetUserPassword")
	public Response<User> resetUserPassword(@RequestBody String info){
		JSONObject json=JSON.parseObject(info);
		String username=json.getString("username");
		String password=json.getString("password");
		return userService.resetUserPassword(username, password);
	}
	
	/**
	 * @description valid user
	 * @param info
	 * @return
	 */
	@PostMapping("validUser")
	public Response<User> validUser(@RequestBody String info){
		return userService.validUser(info);
	}
}
