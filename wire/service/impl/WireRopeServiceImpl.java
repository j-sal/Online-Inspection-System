package com.hbsi.wire.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hbsi.dao.DiameterDeviationMapper;
import com.hbsi.dao.EntrustmentDataMapper;
import com.hbsi.dao.EntrustmentMapper;
import com.hbsi.dao.ReverseBendingMapper;
import com.hbsi.dao.SampleRecordMapper;
import com.hbsi.dao.SteelWireDataMapper;
import com.hbsi.dao.TensileStrengthMapper;
import com.hbsi.dao.UserMapper;
import com.hbsi.dao.WireAttributesGbt201182017Mapper;
import com.hbsi.dao.WireDataMapper;
import com.hbsi.dao.WireRopeMapper;
import com.hbsi.domain.DiameterDeviation;
import com.hbsi.domain.Entrustment;
import com.hbsi.domain.EntrustmentData;
import com.hbsi.domain.ReverseBending;
import com.hbsi.domain.SampleRecord;
import com.hbsi.domain.SteelWireData;
import com.hbsi.domain.TensileStrength;
import com.hbsi.domain.WireAttributesGbt201182017;
import com.hbsi.domain.WireData;
import com.hbsi.domain.WireRope;
import com.hbsi.entity.WireRopeData;
import com.hbsi.exception.BaseException;
import com.hbsi.exception.ExceptionEnum;
import com.hbsi.response.PageResult;
import com.hbsi.response.Response;
import com.hbsi.steel.entity.SteelWireRecord;
import com.hbsi.thickwire.service.ThickWireService;
import com.hbsi.user.service.UserAuth;
import com.hbsi.util.Arith;
import com.hbsi.wire.service.WireRopeService;
import com.hbsi.wire.service.WireRopeServiceGB8918;
import com.hbsi.wire.service.WireRopeServiceMT716;
import com.hbsi.wire.service.Ybt53592010Service;


@Service
public class WireRopeServiceImpl implements WireRopeService{
	private Logger logger=LoggerFactory.getLogger(WireRopeService.class);
	@Autowired
	private WireRopeMapper wireRopeMapper;
	@Autowired
	private EntrustmentMapper entrustmentMapper;
	@Autowired
	private WireDataMapper wireDataMapper;
	@Autowired
	private SteelWireDataMapper steelWireDataMapper;
	/* GBT20118-2017 Standard Attributes */
	@Autowired
	private WireAttributesGbt201182017Mapper wireAttrMapperGBT201182017;
	/* Diameter allowable error */
	@Autowired
	private DiameterDeviationMapper diameterDeviationMapper;

	/* Tensile strength allowable value */
	@Autowired
	private TensileStrengthMapper tensileStrengthMapper;
	/* Number of twists and bends */
	@Autowired
	private ReverseBendingMapper reverseBendingMapper;
	@Autowired
	private EntrustmentDataMapper entDataMapper;
	
	/* Determination for/from each standard */
	@Resource
	private WireRopeService wireRopeService;//[GB/T 20118-2017]
	@Resource
	private WireRopeServiceGB8918 wireRopeServiceGB8918;//[GB 8918-2006]
	@Resource
	private WireRopeServiceMT716 wireRopeServiceMT716;//[MT 716-2005]
	@Resource
	private ThickWireService thickWireService;//[GB/T 20067-2017]
	@Resource
	private Ybt53592010Service ybt53592010Service;//[YB/T5359-2010]
	@Autowired
	private UserMapper userMapper;
	
	/**
	 * Query list of order numbers
	 */
	public Response<List<String>> selectWireEntList(String judgeStandard,String enstrustmentNumber){
		WireRope wireRope=new WireRope();
		wireRope.setJudgeStandard(judgeStandard);
		wireRope.setEnstrustmentNumber(enstrustmentNumber);
		List<String> list=wireRopeMapper.selectEntList(wireRope);
		return new Response<List<String>>(list);
	}
	/**
	 * Query and create wire rope reports
	 */
	@Override
	public Response<WireRope> selectOrCreateWR(String enstrustmentNumber,String standardNumber,Integer sampleBatch,String userName) {
		UserAuth userAuth = (user) -> "administrator".equals(user.getUserRank());
		if(standardNumber==null||"".equals(standardNumber)) {
			logger.debug("Detection criteria cannot be empty");
			return new Response<>("00001111", "Please select the criteria", null);
		}
		if(enstrustmentNumber!=null&&!"".equals(enstrustmentNumber)) {
//			Entrustment en1=new Entrustment();
//			en1.setEntrustmentNumber(enstrustmentNumber);
			Entrustment e1=entrustmentMapper.selectEntrustmentNumber(enstrustmentNumber);
			if(e1==null||e1.getEntrustmentNumber()==null) {
				logger.debug("The order number does not exist");
				return new Response<>("00001111", "The order number you entered does not exist", null);
			}
		}else {
			if(sampleBatch!=null&&sampleBatch!=0) {
				Entrustment e1= entrustmentMapper.selectEntrustByBatchNum(sampleBatch);
				if(e1==null||e1.getEntrustmentNumber()==null||"".equals(e1.getEntrustmentNumber())) {
					logger.debug("The order number does not exist");
					return new Response<>("00001111", "The batch number has not been added to the order or the batch number does not exist.", null);
				}else {
					enstrustmentNumber=e1.getEntrustmentNumber();
				}
			}
		}
		WireRope wireRope=wireRopeMapper.selectByEnsNum(enstrustmentNumber);
		if(wireRope==null) {
//			Entrustment e=new Entrustment();
//			e.setEntrustmentNumber(enstrustmentNumber);
			Entrustment en=entrustmentMapper.selectEntrustmentNumber(enstrustmentNumber);
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM");
			SimpleDateFormat sd=new SimpleDateFormat("yyMM");
			wireRope=new WireRope();
			wireRope.setEnstrustmentNumber(enstrustmentNumber);
			wireRope.setStockSplitMethod("Partial test");
			Date date = new Date();
			wireRope.setReportDate(sdf1.format(date));
			String batch=enstrustmentNumber.substring(enstrustmentNumber.length()-3);
			wireRope.setReportNumber("L-"+sdf.format(date)+"-"+batch);
			wireRope.setRecorderNumber("DG"+sd.format(date)+batch);
			wireRope.setJudgeStandard(standardNumber);
			try {
				int i=wireRopeMapper.insertSelective(wireRope);
			} catch (Exception ex) {
				logger.error("Add wire rope report error:{}",ex.getLocalizedMessage());
				throw new BaseException(ExceptionEnum.WIREROPE_SAVE_ERROR);
			}
		}else if("100".equals(wireRope.getState())&&!userAuth.isAdmin(userMapper.selectByUsername(userName))){
			logger.debug("The report has been submitted！Please contact the administrator");
			return new Response<>("00001112", "The report has been submitted! Please contact the administrator", null);
		}else {
			if("100".equals(wireRope.getState())) {
				return new Response<WireRope>("00001111", "The report has been submitted", null);
			}else {
				List<WireData> list=wireDataMapper.selectByEnNum(enstrustmentNumber);
				wireRope.setWireDataList(list);
			}
		}
		return new Response<WireRope>(wireRope);
	}
	/**
	 * Transfer data
	 * First, check if there is any data in the wire rope data. If not,
	 * take out the wire data and add it to the wire rope data to return.
	 */
	@Override
	public Response<List<WireData>> selectWireData(WireRope wireRope) {
		String entrustmentNumber=wireRope.getEnstrustmentNumber();
		if(entrustmentNumber==null||"".equals(entrustmentNumber)) {
			return new Response<List<WireData>>("00001111", "Order number not entered", null);
		}
		WireRope wr=wireRopeMapper.selectByEnsNum(entrustmentNumber);
		if(wr!=null) {
			exchange(wr, wireRope);
			System.out.println("***"+wr);
		}else {
			wr=wireRope;
		}
		List<WireData> wireList=wireDataMapper.selectByEnNum(entrustmentNumber);
		if(wireList!=null&&wireList.size()>0) {
			int i=wireDataMapper.deleteByEntrustment(entrustmentNumber);
			if(i>=0) {
				wireList=null;
			}
		}
		if(wireList==null||wireList.size()==0) {
			wireList=new ArrayList<WireData>();
			List<SteelWireData> slist=steelWireDataMapper.selectByEnNum(entrustmentNumber);
			System.out.println(slist);
			if(slist==null||slist.size()==0) {
				logger.error("no data");
			}else {
				List<String[]> l=new ArrayList<>();
				String cl=wireRope.getTrialClass();
				if(cl==null||"".equals(cl)) {
					cl=wr.getTrialClass();
				}
				if(cl!=null&&!"".equals(cl)) {
					String[] str=cl.split(",");
					for(int i=0;i<str.length;i++) {
						String s=str[i];
						String[] ss=s.split("\\*");
						String[] sa=ss[1].split("/");
						ss[1]=sa[0];
						l.add(ss);
					}
				}else {
					return new Response<List<WireData>>("00001111", "Please enter the test wire category", null);
				}
				int n=1;//Number of steel wires
				int m=0;
				for(SteelWireData s:slist) {
					WireData w=new WireData();
					if(l.size()>0) {
						String[] nd=l.get(m);
						w.setnDiamete(Double.parseDouble(nd[0]));
						Integer num=Integer.parseInt(nd[1]);
						if(n==num) {
							n=1;
							m++;
						}else {
							n++;
						}
					}
					w.setDiamete(s.getfDiamete1());
					w.setBreakTensile(s.getBreakTensile1());
					w.setTensileStrength(s.getTensileStrength());
					w.setKnotTension(s.getKnotTension());
					w.setKnotRate(s.getKnotRate());
					w.setTurnTimes(s.getTorsionTimes());
					w.setWindingTimes(s.getBendingTimes());
					w.setEntrustmentNumber(entrustmentNumber);
					wireList.add(w);
					
				}
//				List<WireData> list=wireDataMapper.selectByEnNum(wireRope.getEnstrustmentNumber());
				try {
					int j=wireRopeMapper.updateByenstrustmentNumber(wireRope);
//					if(list==null||list.size()==0) {
						int i=wireDataMapper.insertWireDataBatch(wireList);
//					}else {
						System.out.println("************");
//						int i=wireDataMapper.updateWireDataBatch(wireList);
//					}
					
				} catch (Exception e) {
					logger.error("Adding wire rope data failed,{}",e.getLocalizedMessage());
					e.printStackTrace();
					throw new BaseException(ExceptionEnum.WIREROPE_DATA_SAVE_ERROR);
				}
			}
		}
		return new Response<List<WireData>>(wireList);
	}
	@Autowired
	private SampleRecordMapper sampleRecordMapper;
	/**
	 * Transfer data, extract data from the experimental data table
	 */
	public Response<List<WireData>> selectWireDataList(WireRope wireRope){
		String entrustmentNumber=wireRope.getEnstrustmentNumber();
		if(entrustmentNumber==null||"".equals(entrustmentNumber)) {
			return new Response<List<WireData>>("00001111", "Order number not entered", null);
		}
		WireRope wr=wireRopeMapper.selectByEnsNum(entrustmentNumber);
		if(wr!=null) {
			exchange(wr, wireRope);
			System.out.println("***"+wr);
		}else {
			wr=wireRope;
		}
		
		/**
		 * Query the record table according to the order number
		 * If there is no wire rope data sheet, query and combine
		 * the test record table.
		 */			
		Entrustment ee = entrustmentMapper.selectEntrustmentNumber(entrustmentNumber);
			if(ee.getSampleBatch()==null || ee.getSampleBatch()==0) {
				//Query order number and batch number
				logger.info("Batch number does not exist");
				return new Response<>("00005290", "Batch number does not exist", null);
			}
			/* Query the experimental data table according to
			 * the order number (change to batch number)
			 */
			List<SampleRecord> sampleList = sampleRecordMapper.selectData(ee.getSampleBatch());
//			List<SampleRecord> sampleList=sampleRecordMapper.selectSampleData(entrustmentNumber);
			if(sampleList.size() <= 0) {
				logger.info("This experiment record does not exist on the order");
				return new Response<>("00005290", "This experiment record does not exist on the order", null);
			}
			//Combine wire strand test data into wire rope data
			List<WireData> list=comboWireData(sampleList,entrustmentNumber);
			/**
			 * Split wire nominal diameter
			 */
			List<String[]> l=new ArrayList<>();
			String cl=wireRope.getTrialClass();
			if(cl==null||"".equals(cl)) {
				cl=wr.getTrialClass();
			}
			if(cl!=null&&!"".equals(cl)) {
				String[] str=cl.split(",");
				for(int i=0;i<str.length;i++) {
					String s=str[i];
					String[] ss=s.split("\\*");
					String[] sa=ss[1].split("/");
					ss[1]=sa[0];
					l.add(ss);
				}
			}else {
				return new Response<List<WireData>>("00001111", "请输入试验钢丝类别", null);
			}
			int size=0;//Total number of test wires
			for(int i=0;i<l.size();i++) {
				String[] nd=l.get(i);
				Integer num1=Integer.parseInt(nd[1]);
				size+=num1;
			}
			if(list!=null&&list.size()>0) {
				if(list.size()!=size) {
					logger.debug("The test wire type is incorrectly filled, please check");
					return new Response<List<WireData>>("00001111", "The test wire type is incorrectly filled, please check", null);
				}else {
					/**
					 * First look in the wire rope data
					 * table if there is preexisting data,
					 * delete it first.
					 */
					List<WireData> wireList=wireDataMapper.selectByEnNum(entrustmentNumber);
					if(wireList!=null&&wireList.size()>0) {
						int i=wireDataMapper.deleteByEntrustment(entrustmentNumber);
						if(i>=0) {
							wireList=null;
						}
					}
					if(wireList==null||wireList.size()<=0) {
						wireList=new ArrayList<WireData>();
						int n=1;//Number of wire strands per diameter
						int m=0;
						 for(int i=0;i<list.size();i++) {
								WireData wireData=new WireData();
								WireData s1=null;
								if(l.size()>0) {
									String[] nd=l.get(m);
									double diamate=Double.parseDouble(nd[0]);
									if(diamate>0.5) {
										s1=list.get(i);
										s1.setnDiamete(diamate);
										Integer num=Integer.parseInt(nd[1]);
										if(n==num) {
											n=1;
											m++;
										}else {
											n++;
										}
									}else {
										s1=list.get(i);
										WireData s2=list.get(i+1);
										s1.setnDiamete(diamate);
										s1.setKnotTension(s2.getBreakTensile1());
										Integer num=Integer.parseInt(nd[1]);
										i++;
										if(n==num) {
											n=1;
											m++;
										}else {
											n++;
										}
									}
								}
								double d = (Arith.div(s1.getBreakTensile1(), Arith.mul(Math.PI, Arith.mul(s1.getnDiamete1()/2, s1.getnDiamete1()/2))))*1000;
								s1.setTensileStrength((int) Math.round(d));
								wireData=s1;
								wireList.add(wireData);
							}
						try {
							int j=wireRopeMapper.updateByenstrustmentNumber(wireRope);
//							if(list==null||list.size()==0) {
								int i=wireDataMapper.insertWireDataBatch(wireList);
//							}else {
								System.out.println("************");
//								int i=wireDataMapper.updateWireDataBatch(wireList);
//							}
							return new Response<List<WireData>>(wireList);
						} catch (Exception e) {
							logger.error("添加钢丝绳数据失败,{}",e.getLocalizedMessage());
							e.printStackTrace();
							throw new BaseException(ExceptionEnum.WIREROPE_DATA_SAVE_ERROR);
						}
					}
				}
				return new Response<List<WireData>>(null);
			}else {
				logger.debug("试验数据不存在");
				return new Response<List<WireData>>("00001111", "试验数据不存在", null);
			}
			
//		}
		
				 
	}
	/**
	 * Combine test record data into wire rope data
	 * @param list
	 * @return
	 */
	private List<WireData> comboWireData(List<SampleRecord> list,String entrustmentNumber){
		Map<Integer, WireData> map=new HashMap<Integer,WireData>();
		for(SampleRecord record:list) {
			WireData steelWireData=null;
			if(!map.containsKey(record.getSampleNumber())) {
				steelWireData=new WireData();
			}else {
				steelWireData=map.get(record.getSampleNumber());
			}
			if("pull".equals(record.getType())) {
				steelWireData.setBreakTensile(record.getExperimentalData());
				steelWireData.setMachineNumber(record.getMachineNumber());
				steelWireData.setMachineType(record.getMachineType());
			}else if("bending".equals(record.getType())) {
				steelWireData.setWindingTimes(record.getExperimentalData().intValue());
			}else if("twisting".equals(record.getType())) {
				steelWireData.setTurnTimes(record.getExperimentalData().intValue());
			}
			steelWireData.setEntrustmentNumber(entrustmentNumber);
			map.put(record.getSampleNumber(), steelWireData);
		}
		List<WireData> slist=new ArrayList<WireData>(map.values());
		return slist;
	}
	
	private String validateWireRope(WireRope wireRope) {
		if(wireRope.getEnstrustmentNumber()==null||"".equals(wireRope.getEnstrustmentNumber())) {
			return "The order number is empty, please enter the order number";
		}
		if(wireRope.getSpecification()==null||"".equals(wireRope.getSpecification())) {
			return "Specifications cannot be empty, please fill them in";
		}
		 
		if(!Pattern.matches("^\\d+(\\.\\d+)?$", wireRope.getSpecification())) {
			return "Specifications should be numeric";
		}
		if(wireRope.getStructure()==null||"".equals(wireRope.getStructure())) {
			return "The structure cannot be empty, please enter or select the structure";
		}
		if(wireRope.getSurfaceState()==null||"".equals(wireRope.getSurfaceState())) {
			return "Surface state cannot be empty";
		}
		if(wireRope.getStrengthLevel()==null||"".equals(wireRope.getStrengthLevel())) {
			return "Strength level cannot be empty";
		}
		List<WireData> list=wireRope.getWireDataList();
		if(list==null||list.size()==0) {
			return "The wire rope's strand's data is empty";
		}
		for(WireData d:list) {
			if(d.getDiamete1()==null||d.getDiamete1()==0) {
				return "The item in your wire data is empty or 0";
			}
		}
		return "SUCCESS";
	}
	
	/**
	 * Wire rope sum determination, standard GB/T 20118-2017
	 * Save data first
	 * 1.Calculate the minimum wire breaking force
	 * 2.Calculate the sum of the measured tensile forces of the steel wire
	 * 3.Diameter determination
	 * 4.Tensile strength determination
	 * 5.Twisting number determination
	 * 6.Bending number determination
	 * 7.
	 */
	@Override
	public Response<WireRope> judgeWireRopeGbt201182017(WireRope wireRope) {
		
		/* Saving Data */
		if(wireRope==null) {
			logger.error("no data");
			return new Response<WireRope>("00001111", "Wire rope data is empty", null);
		}else {
			String m=validateWireRope(wireRope);
			
			if(!"SUCCESS".equals(m)) {
				return new Response<WireRope>("00001111", m, null);
			}
			/* Calculate the breaking force of the wire */
			double wireBreak=calWireBreakTensile(wireRope);
			if(wireBreak==-1) {
				logger.debug("Test wire category incorrectly filled");
				return new Response<WireRope>("00001111", "Test wire category incorrectly filled", null);
			}
			wireBreak=Arith.getValue(wireBreak);
			/**
			 * Calculate the sum of the minimum breaking force of
			 * the wire rope
			 * K*D*D*R0/1000
			 * */
			WireAttributesGbt201182017 att=wireAttrMapperGBT201182017.selectTanningByStructure(wireRope.getStructure());//Query tanning loss factor
			double minBreakForce=att.getMinimumBreakingForce();
			double diamate=Double.parseDouble(wireRope.getSpecification());//Wire rope diameter
			double strengthLevel=Double.parseDouble(wireRope.getStrengthLevel());
			 double breakValue=Arith.div(Arith.mul(Arith.mul(minBreakForce, Math.pow(diamate, 2)), (double)strengthLevel),1000.0);
//			double  standardBreak=Arith.getValue(breakValue);
			
			double wireBreakAll=breakValue*att.getTanningLossFactor();//The minimum breaking force of the wire rope
			wireBreakAll=Arith.getValue(wireBreakAll);
			wireRope.setMinBreakTensile(wireBreakAll);
			wireRope.setMeasureBreakTensile(wireBreak);
			/* Modify wire rope data */
			saveWireData(wireRope);
			Map<Double, int[]> resultMap=judgeWireRope(wireRope);
			//no more than 3% of wire exceeding regulations is allowed
			int min_strength=0;  
			int min_turn=0; 
			Integer num=wireRope.getWireDataList().size();//Total number of test wire strands
			/* Query the wire rope according to the structure to
			 * set the allowed number of low-value steel wires
			 */
			if((wireRope.getNonTrialClass()!=null&&wireRope.getNonTrialClass().length()!=0)||"Partial test".equals(wireRope.getStockSplitMethod())) {
				if(att.getPartialIntensityLow()==null||att.getPartialReverseLow()==null) {
					//Partial test
					min_strength=(int)(num*0.06);
					min_turn=(int)(num*0.05);
				}else {
					min_strength=att.getPartialIntensityLow();
					min_turn=att.getPartialReverseLow();
				}
			}else {
				//100% test
				min_strength=(int)(num*0.025);
				min_turn=(int)(num*0.025);
			}
			//The sb data is stored in evaluation
			StringBuilder sb=new StringBuilder();
			//The sb1 data is stored in remarks
			StringBuilder sb1=new StringBuilder();
			int[] all=resultMap.get(0d);
			if(all!=null&&all.length!=0) {
				if(all[0]>0) {
					sb.append("not qualified");
				}else if(all[1]>num*0.03) {
					sb.append("not qualified");
				} else if(all[2]>0) {
					sb.append("not qualified");
				}else if(all[3]>0) {
					sb.append("not qualified");
				}else if(all[5]>0) {
					sb.append("not qualified");
				}else if(all[7]>0) {
					sb.append("not qualified");
				}else if(all[1]>num*0.03) {
					sb.append("not qualified");
				}else if(all[4]>min_strength) {
					sb.append("not qualified");
				}else if(all[6]>min_turn||all[8]>min_turn) {
					sb.append("not qualified");
				} else if(all[14]>all[13]*0.05){//Steel wire strands of less than 0.5 require at least 95% compliance
					sb.append("not qualified");
				}else {
					sb.append("qualified");
				}
			}
			Iterator<Double> it=resultMap.keySet().iterator();
		/* [Failed] Φ4.05 steel wire [2 diameters are biased but within
		 * allowable range] Φ3.15 steel wire [1 diameter is unqualified]
		 */
			while(it.hasNext()) {
				double ndiamate=it.next();
				if(ndiamate!=0&&ndiamate!=0.0) {
					int[] re=resultMap.get(ndiamate);
					int i=0;
					for(;i<re.length;i++) {
						if(re[i]!=0) {
						/* If there is a low value or
						 * an unqualified, jump out of
						 * this loop
						 */
							break;
						}
					}
					if(i>=re.length) {
						continue;
						/* If there is no unqualified
						 * or low value under the
						 * nominal diameter, jump out
						 * of this cycle.
						 */
					}else {
						/* If there's a low value or an
						 * unqualified under the
						 * nominal diameter, it will be
						 * displayed.
						 */
						sb1.append("Φ").append(ndiamate).append("Steel wire[");
						if(re[0]>0) {
							sb1.append(" diameter has ").append(re[0]).append(" unqualified strands ");
						}
						if(re[1]>0) {
							sb1.append(" diameter has ").append(re[1]).append(" strands with deviation, but within the allowable range ");
						}
						if(re[3]>0) {
							sb1.append("Tensile strength has").append(re[3]).append(" unqualified strands ");
						}
						if(re[4]>0) {
							sb1.append("Tensile strength has").append(re[4]).append(" strands with low values ");
						}
						if(re[5]>0) {
							sb1.append("twisting has").append(re[5]).append(" unqualified strands ");
						}
						if(re[6]>0) {
							sb1.append("twisting has").append(re[6]).append(" strands with low values ");
						}
						if(re[7]>0) {
							sb1.append("Repeated bending has").append(re[7]).append(" unqualified strands ");
						}
						if(re[8]>0) {
							sb1.append("Repeated bending has").append(re[8]).append(" strands with low values ");
						}
						if(re[13]>0) {
							sb1.append("Knotted pull has").append(re[13]).append(" unqualified strands ");
						}						
					}
					sb1.append("]  ");
				}
			}
			System.out.println("*******"+sb.toString());
			wireRope.setEvaluation(sb.toString());
			wireRope.setMemo(sb1.toString());
			try {
				wireRopeMapper.updateRopeEvaluatuion(wireRope);
				return new Response<WireRope>(wireRope);
			} catch (Exception e) {
				logger.error("Failed saving wire rope judgment result,{}",e.getLocalizedMessage());
				throw new BaseException(ExceptionEnum.WIREROPE_SAVE_ERROR);
			}
		}
		
	}
	private Map<Double, int[]> judgeWireRope(WireRope wireRope){
		WireAttributesGbt201182017 att=wireAttrMapperGBT201182017.selectTanningByStructure(wireRope.getStructure());
		//Wire strand type
		String shape=att.getShape();
		String structure=wireRope.getStructure();
		if(structure.contains("V")) {
			shape="differently shaped section";
		}else {
			shape="round section";
		}
		List<WireData> dataList=wireRope.getWireDataList();
		
		WireRopeData wireRopeData=new WireRopeData();
		wireRopeData.setStandardNum(wireRope.getJudgeStandard());
		/**
		 * Judging the surface condition of the wire rope
		 */
		String surface1=wireRope.getSurfaceState();
		String surface="";//For torsional bending and determination
		if(surface1.contains("Glossy")) {
			surface="UB";
		}else if(surface1.contains("Class A")||surface1.contains("Level A")){
			surface="A";
		}else if(surface1.contains("Class B")||surface1.contains("Level B")){
			surface="UB";
		}else if(surface1.contains("Class AB")||surface1.contains("Level AB")){
			surface="AB";
		}else if(surface1.contains("Glossy and class B")||surface1.contains("Glossy and Level B")) {
			surface="UB";
		}
		String diaSurface=surface;//For diameter deviation
		if(surface.equals("UB")||surface.equals("AB")) {
			diaSurface="U,B,AB";
		}
		wireRopeData.setSurface(diaSurface);
		Integer strengthLevel=Integer.parseInt(wireRope.getStrengthLevel());//tensile strength
		/**
		 * Calculate the standard tensile strength allowable value:
		 * Query the minimum tensile strength value according to the
		 * standard number and tensile strength level
		 */
		TensileStrength t=new TensileStrength();
		t.setStandardNum(wireRope.getJudgeStandard());
		t.setRatedStrength(strengthLevel);
		List<TensileStrength> tensileList=tensileStrengthMapper.selectByStrengthLevel(t);
		//甲: first
		Integer first=0;
		//乙: second
		Integer second=0;
		//丙: third
		Integer third=0;
		/**
		 * If the nominal tensile strength is not in the specified
		 * data, it is calculated according to the note in Table 9 of
		 * P11. The column A is the nominal tensile strength drop of 50
		 * the column B is down 5%, and the column C is down by 10%.
		 */
		if(tensileList==null||tensileList.size()==0) {
			first=strengthLevel-50;
//			second=(int)(strengthLevel*(1-0.05));//Needs to be rounded to an integer
//			third=(int)(strengthLevel*(1-0.1));
			second=Arith.revision(strengthLevel*(1-0.05));
			third=Arith.revision(strengthLevel*(1-0.1));
		}else {
			for(TensileStrength t1:tensileList) {
				if("甲 first".equals(t1.getType())) {
					first=t1.getStrengthValue();
				}else if("乙 second".equals(t1.getType())) {
					second=t1.getStrengthValue();
				}else if("丙 third".equals(t1.getType())) {
					third=t1.getStrengthValue();
				}
			}
		}
		/**
		 * According to the test wire data, the nominal diameter
		 * combined data
		 */
		Map<Double, List<WireData>> dataMap=new HashMap<>();
		List<WireData> l=null;
		for(WireData wireData:dataList) {
			if(!dataMap.containsKey(wireData.getnDiamete1())) {
				l=new ArrayList<WireData>();
				l.add(wireData);
			}else {
				l=dataMap.get(wireData.getnDiamete1());
				l.add(wireData);
			}
			dataMap.put(wireData.getnDiamete1(), l);
		}
		/**
		 * Operate dataMap data and judge according to different
		 * diameters:
		 * Define a map that stores the number of unqualified wires and
		 * the number of low-value wires, with the nominal diameter as
		 * the key
		 * int diamaten=0;//Unqualified diameter int[0]
		 * int diamatem=0;//The number of diameters that are biased but
		 * within the allowable range int[1]
		 * int breakn=0;//Unsatisfactory number of broken tensile
		 * forces  int[2]   Do not judge the breaking force
		 * int strengthn=0;//Unsatisfactory tensile strength int[3]
		 * int strengthm=0;//Low tensile strength int[4]
		 * int turnn=0;//Unsatisfactory number of twists int[5]
		 * int turnm=0;//Low number of twists int[6]
		 * int windingn=0;//Unsatisfactory number of bending times
		 * int[7]
		 * int windingm=0;//Low bending times int[8]
		 * int[9] Tensile strength, the number of twists is low
		 * int[10] tensile strength, low bending times
		 * int[11] Both the number of twists and the number of bends
		 * are low
		 * int[12] Tensile strength, number of twists, number of bends
		 * int[13] The knotting tension of the steel wire less than 0.5
		 * does not meet the required number
		 */
		Map<Double, int[]> resultMap=new HashMap<Double,int[]>();
		Set<Double> diaSet=dataMap.keySet();
		Iterator<Double> it=diaSet.iterator();
		//Nominal diameter
		double ndiamate=0;
		//Minimum breaking force coefficient of steel wire
	
		DiameterDeviation dd=null;
		/**
		 * The maximum deviation of the wire diameter is allowed.
		 * Query according to the range of the nominal diameter:
		 * Query the lowest value condition of the number of torsional
		 * bending
		 */
		ReverseBending rb=new ReverseBending();
		rb.setStandardNum(wireRope.getJudgeStandard());
		rb.setRatedStrength(strengthLevel+"");
		rb.setSurfaceState(surface);
		int[] all=new int[15];
		/**
		 * For all wire determination results, all[13] represents less
		 * than 0.5 wire number all[14] represents less than 0.5 wire
		 * does not meet the required number
		 */
		while(it.hasNext()) {
			ndiamate=it.next();//Nominal diameter of steel wire
			wireRopeData.setNdiamete(ndiamate); 
			dd = diameterDeviationMapper.selectDiaByType(wireRopeData);
			//Maximum deviation allowed for wire diameter
		  
			//Query the number of torsional bending times corresponding to the nominal diameter
			rb.setfDiamete(ndiamate);
			List<ReverseBending> rlist=reverseBendingMapper.selectRBDataByCon(rb);
			int[] d=new int[2];
			for(ReverseBending r:rlist) {
				if("R".equals(r.getType())) {
					d[0]=r.getValueRob();
				}else if("B".equals(r.getType())) {
					d[1]=r.getValueRob();
				}
			}
			    
			List<WireData> dlist=dataMap.get(ndiamate);//Nominal diameter steel wire data
			int[] re=new int[14];
			/*
			 * The judgment result of the nominal diameter re[13]
			 * The number of knotting tensions whose nominal
			 * diameter is less than 0.5 does not match
			 */
			boolean f1=false;//Low tensile strength
			boolean f2=false;//Low number of twists
			boolean f3=false;//Low bending times
			
			for(WireData wireData:dlist) {
				wireData.setDiameteJudge("");
				wireData.setStrengthJudge("");
				wireData.setTensileJudge("");
				wireData.setTurnJudge("");
				wireData.setWindingJudge("");
				wireData.setZincJudge("");
				wireData.setKnotJudge("");
				/**
				 * Diameter determination
				 */
				double diamete=wireData.getDiamete1();//Wire diameter
				if(Math.abs(diamete-ndiamate)>dd.getValue()*1.5) {
					re[0]++;//Unqualified diameter plus 1
					all[0]++;
					wireData.setDiameteJudge("**");
				}else if(Math.abs(diamete-ndiamate)>dd.getValue()) {
					re[1]++;//Diameter allowed maximum deviation +1
					all[1]++;
					wireData.setDiameteJudge("*");
				}
				/**
				 * Tensile strength judgment
				 * First check the tensile strength table and
				 * judge according to page 11. If the query is
				 * empty, according to the Note in Table 9, the
				 * calculation allows the lowest tensile
				 * strength to be low.
				 */
				int minTurnTimes=d[0];//The minimum number of twists allowed
				int minWindTimes=d[1];//The minimum number of bends allowed
				if("Round section".equals(shape)) {
					//Tensile strength determination
					if(wireData.getTensileStrength()<third) {
						re[3]++;//not qualified
						all[3]++;
						wireData.setStrengthJudge("**");
					}else if(wireData.getTensileStrength()<first) {
						re[4]++;//Low value
						all[4]++;
						wireData.setStrengthJudge("*");
						f1=true;
					}
					//Wire diameter less than 0.5 knotting tension instead of twisting a bend
					if(ndiamate<0.5) {
						all[13]++;
						//The knotting tension should not be less than 50% of the nominal tensile strength of the steel wire.
						double knotTension=wireData.getKnotTension();//Test knotting tension
						double k1=Arith.mul(Arith.div(Arith.mul(Arith.mul(Math.PI, Math.pow(wireData.getnDiamete1()/2.0, 2)),(double)strengthLevel), 1000.0), 0.5);
						if(knotTension<k1) {
							re[13]++;
							all[14]++;
							wireData.setKnotJudge("**");
						}
						double la=Arith.div(Arith.mul(Arith.mul(Math.PI, Math.pow(wireData.getnDiamete1()/2.0, 2)),(double)strengthLevel), 1000.0);
						double knotRate=Arith.div(knotTension, la);
						wireData.setKnotRate(Double.valueOf(Math.round(Arith.mul(knotRate, 100.0))));

						int effectedNum = wireDataMapper.updateByPrimaryKeySelective(wireData);
						continue;
					}
					//Torsion number determination
					//Obtain the number of twists based on the nominal diameter of the wire
					
					if(wireData.getTurnTimes()<minTurnTimes*(1-0.2)) {
						re[5]++;
						all[5]++;
						wireData.setTurnJudge("**");
					}else if(wireData.getTurnTimes()<minTurnTimes) {
						re[6]++;
						all[6]++;
						wireData.setTurnJudge("*");
						f2=true;
					}
					//Bending number determination
					if(wireData.getWindingTimes()<minWindTimes) {
						re[7]++;
						all[7]++;
						wireData.setWindingJudge("**");
						f3=true;
					}
					
				}else if("Shaped section".equals(shape)) {
					if(wireData.getTensileStrength()<third) {
						re[3]++;
						all[3]++;
						wireData.setStrengthJudge("**");
					}else if(wireData.getTensileStrength()<second) {
						re[4]++;
						all[4]++;
						wireData.setStrengthJudge("*");
						f1=true;
					}
					
					/**
					 * Wire diameter less than 0.5 knotting
					 * tension instead of twisting a bend
					 */
					if(ndiamate<0.5) {
						all[13]++;
						//The knotting tension should not be less than 50% of the nominal tensile strength of the steel wire.
						double knotTension=wireData.getKnotTension();//Test knotting tension
						double k1=Arith.mul(Arith.div(Arith.mul(Arith.mul(Math.PI, Math.pow(wireData.getnDiamete1()/2.0, 2)),(double)strengthLevel), 1000.0), 0.5);
						if(knotTension<k1) {
							re[13]++;
							all[14]++;
							wireData.setKnotJudge("**");
						}
						double la=Arith.div(Arith.mul(Arith.mul(Math.PI, Math.pow(wireData.getnDiamete1()/2.0, 2)),(double)strengthLevel), 1000.0);
						double knotRate=Arith.div(knotTension, la);
						wireData.setKnotRate(Double.valueOf(Math.round(Arith.mul(knotRate, 100.0))));

						int effectedNum = wireDataMapper.updateByPrimaryKeySelective(wireData);
						continue;
					}
					
					/**
				 	 * Torsion number determination
				 	 */
					if(wireData.getTurnTimes()<minTurnTimes*(1-0.3)) {
						re[5]++;
						all[5]++;
						wireData.setTurnJudge("**");
					}else if(wireData.getTurnTimes()<minTurnTimes) {
						re[6]++;
						all[6]++;
						wireData.setTurnJudge("*");
						f2=true;
					}
					//Bending number determination
					if(wireData.getWindingTimes()<minWindTimes*(1-0.2)) {
						re[7]++;
						all[7]++;
						wireData.setWindingJudge("**");
					}else if(wireData.getWindingTimes()<minWindTimes) {
						re[8]++;
						all[8]++;
						wireData.setWindingJudge("*");
						f3=true;
					}
				}
				if(f1&f2&!f3) {
					re[9]++;
					all[9]++;
				}
				if(f1&f3&!f2) {
					re[10]++;
					all[10]++;
				}
				if(f2&f3&!f1) {
					re[11]++;
					all[11]++;
				}
				if(f1&f2&f3) {
					re[12]++;
					all[12]++;
				}
				try {
					int effectedNum = wireDataMapper.updateByPrimaryKeySelective(wireData);
					if(effectedNum <=0) {
						logger.error("The result of wireData was not saved successfully.");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			resultMap.put(ndiamate, re);
		}
		resultMap.put(0d, all);
		return resultMap;
	}
	
	
	
	/**
	 * Calculate the minimum breaking force of the wire
	 * @param wireRope
	 * @return
	 * The tensile force of the test wire + the calculated tensile force of
	 * the test wire
	 * The tensile force of the test wire is a sum of the medium tensile
	 * forces, * number of shares
	 * Calculated tensile force of steel wire without test =
	 * rope level * area
	 */
	public double calWireBreakTensile(WireRope wireRope) {
		/*Test wire tension*/
		String trialClass=wireRope.getTrialClass();
		String[] s=trialClass.split(",");
		List<double[]> dlist=new ArrayList<>();
		List<double[]> list=new ArrayList<>();
		//Obtain the nominal diameter, number of strands, and number of sections of the test wire
		for(int i=0;i<s.length;i++) {
			String s1=s[i];
			if(!(s1.contains("*")&&s1.contains("/"))) {
				return -1;
			}
			double[] d=new double[4];
			d[0]=Double.parseDouble(s1.substring(0, s1.indexOf("*")));
			d[1]=Double.parseDouble(s1.substring(s1.indexOf("*")+1,s1.indexOf("/")));
			d[2]=Double.parseDouble(s1.substring(s1.indexOf("/")+1));
			d[2]=d[2]/d[1];
			dlist.add(d);
		}
		//Calculate the tensile force of the test wire
		List<WireData> dataList=wireRope.getWireDataList();
		for(int i=0;i<dlist.size();i++) {
			double[] d=dlist.get(i);
			double f=0;
			for(WireData data:dataList) {
				if(data.getnDiamete1()==d[0]) {
					f+=data.getBreakTensile1();
				}
			}
			d[3]=f;
			list.add(d);
		}
		double trialF=0;//Test wire tension sum
		for(double[] d:list) {
			trialF+=d[2]*d[3];
		}
		//Calculate the sum of the tensile force of the test wire
		String nonTrial=wireRope.getNonTrialClass();
		double nonTrialF=0;//Do not test the sum of wire pull
		if(nonTrial!=null&&!"".equals(nonTrial)) {
			String[] nons=nonTrial.split(",");
			for(String ns:nons) {
				String[] nss=ns.split("\\*");
				double diamate=Double.parseDouble(nss[0]);
				int n=Integer.parseInt(nss[1]);
				nonTrialF+=Arith.div(Math.PI*Math.pow(diamate/2, 2)*(Integer.parseInt(wireRope.getStrengthLevel()))*n, 1000.0);
				
			}
		}
 		return (trialF+nonTrialF);
	} 
	/**
	 * Submit wire rope report data
	 */
	public Response<WireRope> saveWire(WireRope wireRope){
		if(wireRope==null) {
			logger.info("no data");
		}else {
			WireRope w=wireRopeMapper.selectByEnsNum(wireRope.getEnstrustmentNumber());
			exchange(w, wireRope);
			List<WireData> dataList=wireDataMapper.selectByEnNum(wireRope.getEnstrustmentNumber());
			List<WireData> list=wireRope.getWireDataList();
			List<WireData> dlist=new ArrayList<>();
			for(WireData d:dataList) {
				for(WireData l:list) {
					if(d.getId()==l.getId()||d.getId().equals(l.getId())) {
						wireDataChange(d, l);
						dlist.add(d);
						break;
					}
				}
			}
			try {
				
				if(w.getEvaluation()==null||"".equals(w.getEvaluation())) {
					int i=wireRopeMapper.updateByPrimaryKey(w);
					for (WireData wireData : dlist) {
						int j=wireDataMapper.updateByPrimaryKeySelective(wireData);
					}
					return new Response<WireRope>("00001111", "A comprehensive judgment hasn't been made; cannot submit a report", null);
				}else {
					w.setState("100");
					int i=wireRopeMapper.updateByPrimaryKey(w);
					for (WireData wireData : dlist) {
						int j=wireDataMapper.updateByPrimaryKeySelective(wireData);
					}
				}
			} catch (Exception e) {
				logger.error("There is an error in modifying the wire rope data.:{}",e.getLocalizedMessage());
				e.printStackTrace();
			}
		}
		return new Response<WireRope>(null);
	}
	
	/**
	 * Modify wire rope data
	 * @param wireRope
	 */
	public void saveWireData(WireRope wireRope) {
		WireRope w=wireRopeMapper.selectByEnsNum(wireRope.getEnstrustmentNumber());
		exchange(w, wireRope);
		List<WireData> dataList=wireDataMapper.selectByEnNum(wireRope.getEnstrustmentNumber());
		List<WireData> list=wireRope.getWireDataList();
		List<WireData> dlist=new ArrayList<>();
		System.out.println(dataList.size());
		System.out.println(list.size());
		for(WireData d:dataList) {
			for(WireData l:list) {
				if(d.getId()==l.getId()||d.getId().equals(l.getId())) {
					wireDataChange(d, l);
					dlist.add(d);
					break;
				}
			}
		}
		try {
			int i=wireRopeMapper.updateByPrimaryKey(w);
			for (WireData wireData : dlist) {
				int j=wireDataMapper.updateByPrimaryKeySelective(wireData);
			}
		} catch (Exception e) {
			logger.error("There is an error in modifying the wire rope data.:{}",e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	private void exchange(WireRope w,WireRope r) {
		if(r.getProducerNumber()!=null&&!"".equals(r.getProducerNumber())) {
			w.setProducerNumber(r.getProducerNumber());
		}
		if(r.getReportNumber()!=null&&!"".equals(r.getReportNumber())) {
			w.setReportNumber(r.getReportNumber());
		}
		if(r.getReportDate()!=null&&!"".equals(r.getReportDate())) {
			w.setReportDate(r.getReportDate());
		}
		if(r.getStructure()!=null&&!"".equals(r.getStructure())){
			w.setStructure(r.getStructure());
		}
		if(r.getSpecification()!=null&&!"".equals(r.getSpecification())) {
			w.setSpecification(r.getSpecification());
		}
		if(r.getStrengthLevel()!=null&&!"".equals(r.getStrengthLevel())) {
			w.setStrengthLevel(r.getStrengthLevel());
		}
		if(r.getSurfaceState()!=null&&!"".equals(r.getSurfaceState())) {
			w.setSurfaceState(r.getSurfaceState());
		}
		if(r.getTwistingMethod()!=null&&!"".equals(r.getTwistingMethod())) {
			w.setTwistingMethod(r.getTwistingMethod());
		}
		if(r.getMeasureBreakTensile()!=null) {
			w.setMeasureBreakTensile(r.getMeasureBreakTensile());
		}
		if(r.getMinBreakTensile()!=null) {
			w.setMinBreakTensile(r.getMinBreakTensile());
		}
		if(r.getDiamete()!=null) {
			w.setDiamete(r.getDiamete());
		}
		w.setDiameteNonRundness(r.getDiameteNonRundness());
		if(r.getStockSplitMethod()!=null) {
			w.setStockSplitMethod(r.getStockSplitMethod());
		}
		if(r.getTrialClass()!=null) {
			w.setTrialClass(r.getTrialClass());
		}
		if(r.getNonTrialClass()!=null) {
			w.setNonTrialClass(r.getNonTrialClass());
		}
		w.setCenterDiamete(r.getCenterDiamete());
		w.setCenterStrength(r.getCenterStrength());
		if(r.getEvaluation()!=null&&!"".equals(r.getEvaluation())) {
			w.setEvaluation(r.getEvaluation());
		}
		if(r.getMemo()!=null) {
			w.setMemo(r.getMemo());
		}
		if(r.getTemperature()!=null) {
			w.setTemperature(r.getTemperature());
		}
		if(r.getTestStandard()!=null) {
			w.setTestStandard(r.getTestStandard());
		}
		if(r.getRecorderNumber()!=null) {
			w.setRecorderNumber(r.getRecorderNumber());
		}
		if(r.getRecorderMemo()!=null) {
			w.setRecorderMemo(r.getRecorderMemo());
		}
		if(r.getJudgeStandard()!=null) {
			w.setJudgeStandard(r.getJudgeStandard());
		}
		
	}
	private void wireDataChange(WireData w,WireData d) {
		if(d.getnDiamete()!=null) {
			w.setnDiamete(d.getnDiamete1());
		}
		if(d.getDiamete1()!=null&&d.getDiamete1()!=0) {
			w.setDiamete(d.getDiamete1());
		}
	}
	/**
	 * Adjust standard number
	 */
	@Override
	public Response<WireRope> updateStandard(String enstrustmentNumber, String standardNumber) {
		WireRope wireRope=new WireRope();
		wireRope.setEnstrustmentNumber(enstrustmentNumber);
		wireRope.setTestStandard(standardNumber);
		try {
			int i=wireRopeMapper.updateStandard(wireRope);
			return new Response<WireRope>(wireRope);
		} catch (Exception e) {
			logger.debug("Adjusting the detection standard number failed,{}",e.getLocalizedMessage());
			throw new BaseException(ExceptionEnum.WIREROPE_STANDARD_UPDATE_ERROR);
		}
	}
	/**
	 * Adjust diameter
	 */
	public Response<WireRope> updateDiamate(WireRope wireRope) {
		if(wireRope==null) {
			logger.info("no data");
			return new Response<WireRope>("00001111", "Incoming data is empty", null);
		}else {

			List<WireData> dataList=wireRope.getWireDataList();
			try {
				int i=wireRopeMapper.updateTrialClass(wireRope);
				for (WireData wireData : dataList) {
					double d = (Arith.div(wireData.getBreakTensile1(), Arith.mul(Math.PI, Arith.mul(wireData.getnDiamete1()/2, wireData.getnDiamete1()/2))))*1000;
					wireData.setTensileStrength((int)(Math.round(d)));//tensile strength
					int j=wireDataMapper.updateByPrimaryKey(wireData);
				}
				dataList=wireDataMapper.selectByEnNum(wireRope.getEnstrustmentNumber());
				wireRope.setWireDataList(dataList);
				return new Response<WireRope>(wireRope);
			} catch (Exception e) {
				logger.error("There is an error in modifying the wire rope data:{}",e.getLocalizedMessage());
				throw new BaseException(ExceptionEnum.WIREROPE_DATA_SAVE_ERROR);
			}
		}
	}
	
	/**
	 * Comprehensive judgment
	 * @param judgeStandard
	 * @return
	 */
	@Override
	public Response<WireRope> judgeWireRopeAll(WireRope wireRope) {
		String judgeStandard = wireRope.getJudgeStandard();
		if (judgeStandard == null || "".equals(judgeStandard)) {
			logger.info("Judgement Standard is empty");
			return new Response<>("00001000", "Please select the Judgement Standard", null);
		}
		if (judgeStandard.contains("GB/T 20118-2017")) {
			return judgeWireRopeGbt201182017(wireRope);
		}
		if (judgeStandard.contains("GB/T 20067-2017")) {
			return thickWireService.judgeThickWire(wireRope);
		}
		if (judgeStandard.contains("GB 8918-2006")) {
			return wireRopeServiceGB8918.judgeWire(wireRope);
		}
		if (judgeStandard.contains("YB/T 5359-2010")) {
            return ybt53592010Service.judgeWireRopeYbt53592010(wireRope);
		}
		if(judgeStandard.contains("MT 716-2005 ")) {
			return wireRopeServiceMT716.judgeWire(wireRope);
		}
		if (judgeStandard.contains("API 9A 2011")) {

		}
		if (judgeStandard.contains("BS EN 12385-4-2002+A1-2008")) {

		}
		if (judgeStandard.contains("ISO 2408-2004")) {

		}
		if (judgeStandard.contains("YB 4542-2016")) {

		}
		if (judgeStandard.contains("YB/T 5343-2006")) {

		}
		if (judgeStandard.contains("YB/T 5259-2006")) {//Sealed wire rope

		}
		return new Response<WireRope>(wireRope);
	}
	/**
	 * Query wire rope data
	 */
	public Response<WireRope> selectWireRope(String entrustmentNumber,Integer sampleBatch){
		if(entrustmentNumber!=null&&!"".equals(entrustmentNumber)) {
			Entrustment e1=entrustmentMapper.selectEntrustmentNumber(entrustmentNumber);
			if(e1==null||e1.getEntrustmentNumber()==null) {
				logger.debug("The order number does not exist");
				return new Response<>("00001111", "The order number you entered does not exist", null);
			}
		}else {
			if(sampleBatch!=null&&sampleBatch!=0) {
				Entrustment e1= entrustmentMapper.selectEntrustByBatchNum(sampleBatch);
				if(e1==null||e1.getEntrustmentNumber()==null||"".equals(e1.getEntrustmentNumber())) {
					logger.debug("The order number does not exist");
					return new Response<>("00001111", "The batch number has not been added to the order, or the batch number does not exist.", null);
				}else {
					entrustmentNumber=e1.getEntrustmentNumber();
				}
			}
		}
		WireRope wireRope=wireRopeMapper.selectByEnsNum(entrustmentNumber);
		if(wireRope!=null) {
			if("100".equals(wireRope.getState())) {
				return new Response<WireRope>("00001111", "The report has been submitted", null);
			}else {
				List<WireData> list=wireDataMapper.selectByEnNum(entrustmentNumber);
				if(list!=null) {
					wireRope.setWireDataList(list);
				}else {
					wireRope.setWireDataList(null);
				}
				return new Response<WireRope>(wireRope);
			}
			
		}else {
			return new Response<>("00001111", "Wire rope report has not been created for the current order number or batch number", null);
		}
	}
	
	public Response<PageResult> selectWireRopeList(String producerNumber,String judgeStandard,Integer offset,Integer limit){
		if(offset==null) {
			offset=1;
		}
		if(limit==null) {
			limit=20;
		}
		WireRope w=new WireRope();
		w.setProducerNumber(producerNumber);
		w.setJudgeStandard(judgeStandard);
		PageHelper.startPage(offset, limit);
		List<WireRope> list=wireRopeMapper.selectWireRopeList(w);
		PageInfo<WireRope> p=new PageInfo<>(list);
		return new Response<PageResult>(new PageResult(limit, offset, p.getTotal(), list));
		
	}

}
