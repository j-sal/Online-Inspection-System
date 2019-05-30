package com.hbsi.wire.controller;

import java.util.List;

import javax.annotation.Resource;

import org.nutz.mvc.annotation.GET;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * Wire rope standard interface
 *
 */

import com.hbsi.domain.WireStandard;
import com.hbsi.response.Response;
import com.hbsi.wire.service.StandardService;
@RestController
@RequestMapping("standard")
public class WireStandardController {
	@Resource
	private StandardService standardService;
	/**
	 * Query all standard lists
	 * @return
	 */
	@GetMapping("selectStandardList")
	public Response<List<WireStandard>> selectStandardList(){
		return standardService.selectStandardList();
	}
}
