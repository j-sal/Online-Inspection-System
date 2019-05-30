package com.hbsi.wire.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hbsi.domain.Entrustment;
import com.hbsi.domain.WireData;
import com.hbsi.domain.WireRope;
import com.hbsi.entrustment.service.EntrustmentService;
import com.hbsi.response.PageResult;
import com.hbsi.response.Response;
import com.hbsi.wire.service.WireRopeService;

/**
 * Wire rope report
 *
 */
@RestController
@RequestMapping("wireRope")
public class WireRopeController {
	@Resource
	private WireRopeService wireRopeService;
	@Autowired
	private EntrustmentService entrustmentService;
	/**
	 * Fuzzy query order list
	 * @param judgeStandard
	 * @param enstrustmentNumber
	 * @return
	 */
	@GetMapping("selectWireEntList")
	public Response<List<String>> selectWireEntList(String judgeStandard,String enstrustmentNumber){
		return wireRopeService.selectWireEntList(judgeStandard, enstrustmentNumber);
	}
	/**
	 * Query or open the wire rope report
	 * @param entrustmentNumber
	 * @return
	 */
	@GetMapping("seleteOrCreateWireRope")
	public Response<WireRope> seleteOrCreateWireRope(String entrustmentNumber,String standardNumber,Integer sampleBatch,String userName){
		return wireRopeService.selectOrCreateWR(entrustmentNumber,standardNumber,sampleBatch,userName);
	}
	
	/**
	 * Query or open the wire rope report by batch number
	 * @param entrustmentNumber
	 * @return
	 */
	@GetMapping("seleteOrCreateWireRopeBySampleBatch")
	public Response<WireRope> seleteOrCreateWireRope(Integer sampleBatch,String entrustmentNumber){
//		Response<Entrustment> response = entrustmentService.selectEntruByBatchNum(sampleBatch);
//		Entrustment data = (Entrustment) response.getData();
//		System.out.println(data);
//		return wireRopeService.selectOrCreateWR(data.getEntrustmentNumber(),standardNumber,sampleBatch);
		return wireRopeService.selectWireRope(entrustmentNumber, sampleBatch);
	}
	/**
	 * Transfer data
	 * @param entrustmentNumber
	 * @return
	 */
	@PostMapping("selectWireData")
	public Response<List<WireData>> selectWireData(@RequestBody WireRope wireRope){
		return wireRopeService.selectWireDataList(wireRope);
	}
	/**
	 * Judgement
	 * @param info
	 * @return
	 */
	@PostMapping("wireJudge")
	public Response<WireRope> wireJudge(@RequestBody WireRope wireRope){
//		System.out.println("*******8"+wireRope);
//		wireRope.setJudgeStandard("GB/T 20067-2017");
//		wireRope.setJudgeStandard("GB/T 20118-2017");
		return wireRopeService.judgeWireRopeAll(wireRope);
	}
	/**
	 * Adjust test standards
	 * @param enstrustmentNumber
	 * @param standardNumber
	 * @return
	 */
	@GetMapping("updateStandard")
	public Response<WireRope> updateStandard(String entrustmentNumber,String standardNumber){
		return wireRopeService.updateStandard(entrustmentNumber, standardNumber);
	}
	/**
	 * Adjustment diameter
	 * @param wireRope
	 * @return
	 */
	@PostMapping("updateDiamate")
	public Response<WireRope> updateDiamate(@RequestBody WireRope wireRope){
		return wireRopeService.updateDiamate(wireRope);
	}
	/**
	 * Submit data
	 * @param wireRope
	 * @return
	 */
	@PostMapping("saveWireRope")
	public Response<WireRope> saveWireRope(@RequestBody WireRope wireRope){
		return wireRopeService.saveWire(wireRope);
	}
	/**
	 * Query wire rope report list
	 * @param reportNumber
	 * @param judgeStandard
	 * @param offset
	 * @param limit
	 * @return
	 */
	@GetMapping("selectWireRopeList")
	public Response<PageResult> selectWireRopeList(String producerNumber,String judgeStandard,Integer offset,Integer limit){
		return wireRopeService.selectWireRopeList(producerNumber, judgeStandard, offset, limit);
	}
	
	/**
	 * According to the test wire category, return wire diameter array
	 * @param trialClass
	 * @return
	 */
	@GetMapping("getData")
	public Response<List<Double>> getData(String trialClass){
		if(trialClass==null) {
			return new Response<List<Double>>("00001111", "Test wire category empty", null);
		}else {
			List<Double> re=new ArrayList<>();
			try {
				List<String[]> l=new ArrayList<>();
				String[] str=trialClass.split(",");
				for(int i=0;i<str.length;i++) {
					String s=str[i];
					String[] ss=s.split("\\*");
					String[] sa=ss[1].split("/");
					ss[1]=sa[0];
					l.add(ss);
				}
				for(int i=0;i<l.size();i++) {
					String[] ss=l.get(i);
					if(ss!=null) {
						Integer t1=Integer.parseInt(ss[1]);
						int j=0;
						Double d=Double.parseDouble(ss[0]);
						while(j<t1) {
							re.add(d);
							j++;
						}
					}
				}
			} catch (Exception e) {
				return new Response<List<Double>>("00001111", "Test wire category input is incorrect", null);
			}
			return new Response<List<Double>>(re);
		}
	}
}
