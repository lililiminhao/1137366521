/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.controller;

import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.cart.AbstractShoppingCart;
import com.lmf.common.cart.ShoppingCartEntry;
import com.lmf.common.enums.OwnerType;
import com.lmf.common.enums.ShoppingCartSourceEntryType;
import com.lmf.common.exceptions.OrderCreateException;
import com.lmf.common.exceptions.PermissionDeniedException;
import com.lmf.enterprise.entity.EnterpriseExclusiveProductPoolEntry;
import com.lmf.enterprise.entity.EnterpriseUserMap;
import com.lmf.enterprise.service.EnterpriseExclusiveProductPoolEntryService;
import com.lmf.enterprise.service.EnterprisePackageVoucherService;
import com.lmf.enterprise.service.EnterpriseUserMapService;
import com.lmf.enums.ProductStatus;
import com.lmf.integral.secuity.UserDetail;
import com.lmf.market.entity.Coupon;
import com.lmf.market.entity.FenxiaoUser;
import com.lmf.market.entity.Relationship;
import com.lmf.market.entity.UserCoupon;
import com.lmf.market.repository.RelationshipDao;
import com.lmf.market.repository.vo.GoodsPriceVO;
import com.lmf.market.repository.vo.ProductCalcVO;
import com.lmf.market.service.*;
import com.lmf.market.vo.UserOrderCoupon;
import com.lmf.order.entity.OrderEntry;
import com.lmf.order.entity.ShoppingOrder;
import com.lmf.order.enums.OrderEntrySourceType;
import com.lmf.order.enums.OrderStatus;
import com.lmf.order.repository.ShoppingOrderDao;
import com.lmf.order.repository.impl.ShoppingOrderDaoImpl;
import com.lmf.order.service.OrderService;
import com.lmf.order.vo.TransitionOrder;
import com.lmf.product.entity.Product;
import com.lmf.product.entity.ProductStorage;
import com.lmf.product.entity.StorageUnit;
import com.lmf.product.service.ProductService;
import com.lmf.product.service.ProductStorageService;
import com.lmf.product.service.StorageUnitService;
import com.lmf.product.vo.SimpleStorageSummary;
import com.lmf.sys.entity.GeoRegion;
import com.lmf.sys.entity.ShipmentFeeSettingsExt;
import com.lmf.sys.service.GeoRegionService;
import com.lmf.sys.service.ShipmentFeeSettingsExtService;
import com.lmf.system.sdk.service.SystemFinanceService;
import com.lmf.system.sdk.service.SystemProductService;
import com.lmf.website.entity.*;
import com.lmf.website.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.velocity.tools.generic.NumberTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author shenzhixiong
 */
@Controller
public class ShoppingCartController {
	@Autowired
	private ShoppingOrderDaoImpl test;

	@Autowired
	private ProductService productService;

	@Autowired
	private FenXiaoUserService fenXiaoUserService;

	@Autowired
	private FenXiaoProductService fenXiaoProductService;

	@Autowired
	private GeoRegionService geoRegionService;

	@Autowired
	private FenXiaoOrderService fenXiaoOrderService;

	@Autowired
	private ShoppingOrderDao shoppingOrderDao;

	@Autowired
	private WebsiteUserAddressService websiteUserAddressService;

	@Autowired
	private WebsiteUserService websiteUserService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private ProductStorageService productStorageService;

	@Autowired
	private StorageUnitService storageUnitService;

	@Autowired
	private SystemFinanceService systemFinanceService;

	@Autowired
	private SystemProductService systemProductService;

	@Autowired
	private EnterpriseUserMapService enterpriseUserMapService;

	@Autowired
	private EnterpriseExclusiveProductPoolEntryService productPoolEntryService;
	@Autowired
	private UserDefinedCateService userDefinedCateService;
	@Autowired
	private MobileCouponService mobileCouponService;
	@Autowired
	private UserCouponService userCouponService;
	@Autowired
	private WebsiteCustomBlockService websiteCustomBlockService;
	@Autowired
	private EnterpriseExclusiveProductPoolEntryService enterpriseExclusiveProductPoolEntryService;

	@Autowired
	private EnterprisePackageVoucherService enterprisePackageVoucherService;

	@Autowired
	private RelationshipDao relationshipDao;

	@Autowired
	private WebsiteAdministratorService websiteAdministratorService;

	@Autowired
	private ShipmentFeeSettingsExtService shipmentFeeSettingsExtService;

	private Logger logger = LoggerFactory.getLogger(ShoppingCartController.class);

	@RequestMapping(value = "/shoppingCart/add.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public @ResponseBody SimpleJsonResponse addToCart(@RequestParam("entry") int entryId,
													  @RequestParam("sentry") int sourceEntryId,
													  @RequestParam(value="fid",required=false) Integer fid,
													  @RequestParam(value = "stype", defaultValue = "product") ShoppingCartSourceEntryType sourceEntryType,
													  @RequestParam(value = "amount", defaultValue = "1") int amount,
													  AbstractShoppingCart shoppingCart,
													  UserDetail currentUser) {
		if (entryId < 0 || sourceEntryType == null) {
			return new SimpleJsonResponse(false, "");
		}
		if(fid == null){
			fid=0;
		}
		shoppingCart.increase(entryId, amount, sourceEntryType, sourceEntryId,fid);
		shoppingCart.flush();

		return new SimpleJsonResponse(true, "/shoppingCart/myCart.php");
	}

	@RequestMapping(value = "/shoppingCart/myCart.php", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	public String myShoppingCart(AbstractShoppingCart shoppingCart, Website website, Model model, WebsiteUser websiteUser) {

		List<ShoppingCartEntry> entries = shoppingCart.entries();
		//统计购物车的商品总数(TOTALAMOUNT),总金额(TOTALPRICE)
		Map<String, Object> resultMap = initSubmitOrderInfo(entries, website, 0.00, websiteUser);

		model.addAttribute("cartEntrys", entries);
		model.addAttribute("resultMap", resultMap);
		return "shopping/step1";
	}

	//@RequestMapping(value = "/shoppingCart/chooseEntry.php", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	public String chooseEntry(@RequestParam("isSelected[]") boolean[] isSelected,
							  @RequestParam(value ="fenxiaoUserId[]") int[] fenxiaoUserIds,
							  @RequestParam("entry[]") int[] entryIds,
							  @RequestParam("sentry[]") int[] sourceEntryIds,//产品ID
							  @RequestParam(value = "stype[]") ShoppingCartSourceEntryType[] sourceEntryTypes,
							  @RequestParam(value = "amount[]") int[] amounts,
							  //@RequestParam(value = "fenxiaoInfo",required = false) String fenxiaoInfo,//商品分销信息 (商品ID_分销商ID_商品数量,商品ID_分销商ID_商品数量)
							  @RequestParam(value = "returnUrl", required = false) String returnUrl,
							  @RequestParam(value = "addressId", required = false) Integer addressId,
							  @RequestParam(value = "productOwnerType[]") OwnerType[] productOwnerType,
							  AbstractShoppingCart shoppingCart,
							  WebsiteUser websiteUser,
							  Website website,
							  Integer couponId,
							  Model model,
							  HttpSession session) {
		//分销信息
		int fid = fenxiaoUserIds[0];
		if (websiteUser == null || websiteUser.getId() <= 0) {
			//如果当前未登陆,则跳转至登陆页面
			if (returnUrl != null && !returnUrl.isEmpty()) {
				return "redirect:/login.php?retUrl=" + returnUrl;
			} else {
				returnUrl = "/product/" + sourceEntryIds[0] + ".php?fid="+String.valueOf(fid);
				return "redirect:/login.php?retUrl=" + returnUrl;
			}
		}

		//建立分销等级关系
		Relationship r=relationshipDao.findbyuserID(websiteUser.getId().intValue());
		FenxiaoUser fx=fenXiaoUserService.findByUserId(websiteUser.getId().intValue());
		if(r==null&&websiteUser.getId() != fenxiaoUserIds[0]&&fenxiaoUserIds[0]!=0&&fx!=null&&fx.getParentID()!=0) {
			Relationship relationship = new Relationship();
			relationship.setUserID(websiteUser.getId());
			relationship.setFenxiaoID(fenxiaoUserIds[0]);
			relationship.setRelationship(1);
			relationshipDao.save(relationship);
		}
		if(r==null&&websiteUser.getId() != fenxiaoUserIds[0]&&fenxiaoUserIds[0]!=0&&fx==null) {
			Relationship relationship = new Relationship();
			relationship.setUserID(websiteUser.getId());
			relationship.setFenxiaoID(fenxiaoUserIds[0]);
			relationship.setRelationship(1);
			relationshipDao.save(relationship);
		}

		List<ShoppingCartEntry> entries = new ArrayList<>();
		List<OrderEntry> orderEntries = new ArrayList<>();
		for (int i = 0; i < entryIds.length; i++) {
			if (!isSelected[i]) {
				continue;
			}
			if (amounts[i] > 0) {
				ShoppingCartEntry entry = new ShoppingCartEntry();
				entry.setEntryId(entryIds[i]);
				entry.setSourceEntryId(sourceEntryIds[i]);
				entry.setSourceEntryType(sourceEntryTypes[i]);
				entry.setAmount(amounts[i]);
				entry.setFenxiaoUserId(fenxiaoUserIds[i]);
				entries.add(entry);

				OrderEntry orderEntry = new OrderEntry();
				orderEntry.setStorageUnitId(entryIds[i]);
				orderEntry.setSourceEntryType(OrderEntrySourceType.valueOf(sourceEntryTypes[i].name()));
				orderEntry.setSourceObjectId(sourceEntryIds[i]);
				orderEntry.setAmount(amounts[i]);
				orderEntries.add(orderEntry);
			}
		}

		//当前登陆用户所有地址信息
		WebsiteUserAddress websiteUserAddress;
		double shipmentFee = 0;
		if (addressId != null && addressId > 0) {
			websiteUserAddress = websiteUserAddressService.findOne(addressId);
			model.addAttribute("address", websiteUserAddress);
		} else {
			websiteUserAddress = websiteUserAddressService.findDefault(websiteUser);
			model.addAttribute("address", websiteUserAddress);
		}
		if(websiteUserAddress != null) {
			shipmentFee = orderService.getShipmentFee(websiteUserAddress.getProvince(), orderEntries);
			model.addAttribute("shipmentFee", shipmentFee);
		}
		//统计购物车的商品总数(TOTALAMOUNT),总金额(TOTALPRICE)
		Map<String, Object> resultMap = initSubmitOrderInfo(entries, website, shipmentFee, websiteUser);

		model.addAttribute("cartEntryList", entries);
//        model.addAttribute("fstr", fstr);
		model.addAttribute("resultMap", resultMap);
		model.addAttribute("provinces", geoRegionService.findAllProvince());

		//**********************************优惠券使用*****************************
		UserDetail currentUser = (UserDetail) session.getAttribute("currentUser");
		List<Coupon> coupons = new ArrayList<>();

		//第一步遍历商品,处理成想要的list
		//找到优惠券
		Set<Integer> catSet = new HashSet<>();
		for (int targetId :sourceEntryIds ) {
			//商品优惠券
			List<Coupon> mobileCoupons1 = mobileCouponService.findProductCouponBytargetId(targetId, currentUser.getUserId());
			coupons.addAll(mobileCoupons1);

			//分类优惠券
			Set<Integer> userCatsSet = userDefinedCateService.findProductCateIds(targetId);
			catSet.addAll(userCatsSet);
			for (Integer catId : userCatsSet) {
				List<Coupon> mobileCoupons = mobileCouponService.findProductCouponByType2(catId,
						currentUser.getUserId());
				coupons.addAll(mobileCoupons);
			}

			//专题优惠券
			List<WebsiteCustomBlock> blocks = websiteCustomBlockService.findCustomBlockByTargetId(targetId);
			if (blocks!=null){
				for (WebsiteCustomBlock block : blocks) {
					List<Coupon> mobileCoupons2 = mobileCouponService.findProductCouponByType3(block.getSpecialActivityId(),currentUser.getUserId());
					coupons.addAll(mobileCoupons2);
				}
			}

		}

		CopyOnWriteArrayList<UserOrderCoupon> userCoupons = new CopyOnWriteArrayList<>();
		Object a = resultMap.get("TOTALPRICE");
		String s = a.toString();
		Integer shopAmount = Integer.valueOf(s.substring(0,s.indexOf(".")));
		if (CollectionUtils.isNotEmpty(coupons)){
			for (Coupon coupon1 : coupons) {
				if (coupon1.getType()==1){
					userCoupons.addAll(userCouponService.selectByUserIdAndType1(currentUser.getUserId(),coupon1.getType(),shopAmount,coupon1.getId()));
				}else {
					userCoupons.addAll(userCouponService.selectByUserIdAndType(currentUser.getUserId(), coupon1.getType(), coupon1.getId()));
				}
			}
		}

		userCoupons = returnList(userCoupons);
		//处理3个list
		List<GoodsPriceVO> productList = new ArrayList<>();
		Map<Integer,GoodsPriceVO> map = new HashMap<>();
		Map<Integer,GoodsPriceVO> activityMap = new HashMap<>();
		for(Integer catid :catSet){
			GoodsPriceVO gvo = new GoodsPriceVO();
			gvo.setTargetId(catid);
			gvo.setTargetType(2);
			map.put(catid,gvo);
		}

		ProductCalcVO pvo = new ProductCalcVO();
		for (ShoppingCartEntry shoppingCartEntry :entries ) {
			//指定范围,1：指定商家可用。2：指定分类可用。3：指定商品可用。4：指定专题可用
			//商品
			GoodsPriceVO gvo = new GoodsPriceVO();
			Product product = productService.findOne(shoppingCartEntry.getSourceEntryId());
			gvo.setTargetId(shoppingCartEntry.getSourceEntryId());
			gvo.setAmount(product.getRetailPrice()*shoppingCartEntry.getAmount());
			gvo.setTargetType(3);
			productList.add(gvo);

			//分类
			Set<Integer> userCatsSet = userDefinedCateService.findProductCateIds(shoppingCartEntry.getSourceEntryId());
			for (Integer i : userCatsSet) {
				map.get(i).setAmount(map.get(i).getAmount()+product.getRetailPrice()*shoppingCartEntry.getAmount());
				map.get(i).setTargetType(2);
			}
			//专题
			List<WebsiteCustomBlock> blocks = websiteCustomBlockService.findCustomBlockByTargetId(shoppingCartEntry.getSourceEntryId());
			if (blocks!=null) {
				for (WebsiteCustomBlock block : blocks) {
					GoodsPriceVO avo = activityMap.get(block.getSpecialActivityId());
					if (avo == null) {
						avo = new GoodsPriceVO();
					}
					avo.setTargetId(block.getSpecialActivityId());
					avo.setAmount(avo.getAmount() + product.getRetailPrice()*shoppingCartEntry.getAmount());
					avo.setTargetType(4);
					activityMap.put(block.getSpecialActivityId(), avo);
				}
			}
		}
		pvo.setCatProductList(new ArrayList<GoodsPriceVO>(map.values()));
		pvo.setActivityProductList(new ArrayList<GoodsPriceVO>(activityMap.values()));
		pvo.setProductList(productList);

		double diff = 0;
		//计算最优优惠券
		UserOrderCoupon calcCoupon = calcBestConpon(pvo, userCoupons);
		if (couponId != null && couponId>0) {
			List<UserOrderCoupon> couponlist= userCouponService.selectByCouponId(couponId);
			calcCoupon = CollectionUtils.isEmpty(couponlist)?null:couponlist.get(0);
		}

		calcCoupon = calcCoupon == null ? new UserOrderCoupon(): calcCoupon;
		if (calcCoupon.getTargetType() != null && 2 == calcCoupon.getTargetType()) {
			diff = calCouponForGoods(pvo.getCatProductList(), calcCoupon);
		}
		if (calcCoupon.getTargetType() != null && 3 == calcCoupon.getTargetType()) {
			diff = calCouponForGoods(pvo.getProductList(), calcCoupon);
		}
		if (calcCoupon.getTargetType() != null && 4 == calcCoupon.getTargetType()) {
			diff = calCouponForGoods(pvo.getActivityProductList(), calcCoupon);
		}
		//如果差额为0，最优优惠券也不能使用
		if(0==getDiff(new CopyOnWriteArrayList<UserOrderCoupon>(), pvo, calcCoupon)){
			calcCoupon = new UserOrderCoupon();
		}
		if (couponId != null&&couponId == 0){
			UserOrderCoupon userOrderCoupon = new UserOrderCoupon();
			Object o = resultMap.get("TOTALPRICE");
			BigDecimal amount = new BigDecimal(o.toString());
			resultMap.put("TOTALPRICE",new NumberTool().format("#0.00",amount.doubleValue()));
			model.addAttribute("userCoupon",userOrderCoupon);
		}else {
			BigDecimal b = new BigDecimal(diff);
			Object o = resultMap.get("TOTALPRICE");
			BigDecimal amount = new BigDecimal(o.toString());
			resultMap.put("TOTALPRICE",new NumberTool().format("#0.00", amount.subtract(b).doubleValue()));
			model.addAttribute("userCoupon", calcCoupon);
		}

		for (UserOrderCoupon calcCoupon1 : userCoupons) {
			getDiff(userCoupons, pvo, calcCoupon1);
		}
		//增加提货券，这里如果券满足时强制用券
		//如果商品只有一个，且为提货券类商品，显示提货券数量
		//根据员工和产品ID查找专享价
		boolean isEnterpriseZoneProduct = false;
		Integer thisProductTotalVoucher = 0;
		if(entries!=null&&entries.size()==1) {
			EnterpriseExclusiveProductPoolEntry productPoolEntry = productPoolEntryService.selectProductPoolEntry(websiteUser, entries.get(0).getSourceEntryId());
			Product product = productService.findOne(entries.get(0).getSourceEntryId());
			//是否为企业专区大礼包产品,根据这个值显示立即购买
			if (productPoolEntry != null && websiteUser != null && websiteUser.getId() > 0) {
				isEnterpriseZoneProduct = userDefinedCateService.isEnterpriseZoneProduct(entries.get(0).getSourceEntryId());
				Map<Integer,String> catMap = userDefinedCateService.findEnterpriseCatMap(entries.get(0).getSourceEntryId());
				// 根据用户查询出可兑换券的数量，返回前台，做数量限制
				for(Map.Entry<Integer, String> entry:catMap.entrySet()) {
					thisProductTotalVoucher += enterprisePackageVoucherService.countUseableByUserPhoneAndProductName(entry.getValue(), websiteUser);
				}
				// 如果券不足则不使用
				if (thisProductTotalVoucher.intValue() < entries.get(0).getAmount()) {
					thisProductTotalVoucher = 0;
				}
			}
		}
		//如果使用了提货券，则实付款为0
		if(isEnterpriseZoneProduct&&thisProductTotalVoucher.intValue()>0) {
			resultMap.put("TOTALPRICE",new NumberTool().format("#0.00", 0d+shipmentFee));
			resultMap.put("allPrice",new NumberTool().format("#0.00", 0d+shipmentFee));
		}

		model.addAttribute("thisProductTotalVoucher", thisProductTotalVoucher);
		model.addAttribute("isEnterpriseZoneProduct", isEnterpriseZoneProduct);
		//增加提货券结束

		model.addAttribute("userCoupons", userCoupons);

		return "shopping/step2";
	}

	private double getDiff(CopyOnWriteArrayList<UserOrderCoupon> userCoupons, ProductCalcVO pvo,
						   UserOrderCoupon calcCoupon1) {
		double diff = 0;
		if (calcCoupon1.getTargetType() != null && 1 == calcCoupon1.getTargetType()) {
			diff = calCouponForGoods(pvo.getShopProductList(), calcCoupon1);
		}
		if (calcCoupon1.getTargetType() != null && 2 == calcCoupon1.getTargetType()) {
			diff = calCouponForGoods(pvo.getCatProductList(), calcCoupon1);
		}
		if (calcCoupon1.getTargetType() != null && 3 == calcCoupon1.getTargetType()) {
			diff = calCouponForGoods(pvo.getProductList(), calcCoupon1);
		}
		if (calcCoupon1.getTargetType() != null && 4 == calcCoupon1.getTargetType()) {
			diff = calCouponForGoods(pvo.getActivityProductList(), calcCoupon1);
		}
		if (diff == 0) {
			userCoupons.remove(calcCoupon1);
		}
		return diff;
	}

	private UserOrderCoupon calcBestConpon(ProductCalcVO productCalcVO, List<UserOrderCoupon> couponList) {
		Map<UserOrderCoupon, Double> couponCutMap = new HashMap<>();
		for (UserOrderCoupon userOrderCoupon : couponList) {
			/**
			 * 指定范围,1：指定商家可用。2：指定分类可用。3：指定商品可用。4：指定专题可用
			 */

			if (1 == userOrderCoupon.getTargetType()) {
				calCouponForGoods(productCalcVO.getShopProductList(), userOrderCoupon);
			}
			if (2 == userOrderCoupon.getTargetType()) {
				calCouponForGoods(productCalcVO.getCatProductList(), userOrderCoupon);
			}
			if (3 == userOrderCoupon.getTargetType()) {
				calCouponForGoods(productCalcVO.getProductList(), userOrderCoupon);
			}
			if (4 == userOrderCoupon.getTargetType()) {
				calCouponForGoods(productCalcVO.getActivityProductList(), userOrderCoupon);
			}
			double diff = 0;
			for (GoodsPriceVO goodsPriceVO : productCalcVO.getProductList()) {
				diff += goodsPriceVO.getDiff();
			}
			couponCutMap.put(userOrderCoupon, diff);
		}
		double max = 0;
		for (UserOrderCoupon orderCoupon : couponCutMap.keySet()) {
			max = (couponCutMap.get(orderCoupon) > max ? (max = couponCutMap.get(orderCoupon)) : max);
		}
		for (UserOrderCoupon orderCoupon : couponCutMap.keySet()) {
			if (couponCutMap.get(orderCoupon) == max) {
				return orderCoupon;
			}
		}
		return null;
	}


	private double calCouponForGoods(List<GoodsPriceVO> list, UserOrderCoupon coupon) {
		if(coupon==null) return 0;
		for (GoodsPriceVO goodsPriceVO : list) {
			goodsPriceVO.setDiff(0);
			//如果是商品优惠券
			if (goodsPriceVO.getTargetId().equals(coupon.getTargetId()) && goodsPriceVO.getTargetType().equals(coupon.getTargetType())) {
				//满减
				//1:满减. 2:直减 . 3:折扣
				if (coupon.getType() == 1) {
					if (goodsPriceVO.getAmount() >= coupon.getUsable().doubleValue() ) {
						BigDecimal diff = coupon.getAmount();
						goodsPriceVO.setDiff(diff.doubleValue());
					}
				}
				//直减
				if (coupon.getType() == 2) {
					BigDecimal diff = coupon.getAmount();
					if(diff.doubleValue()>goodsPriceVO.getAmount()){
						diff = new BigDecimal(goodsPriceVO.getAmount());
					}
					goodsPriceVO.setDiff(diff.doubleValue());
				}
				//折扣
				if (coupon.getType() == 3) {
					BigDecimal discutPrice = new BigDecimal(goodsPriceVO.getAmount()).multiply(coupon.getAmount()).multiply(new BigDecimal(0.1));
					goodsPriceVO.setDiff(goodsPriceVO.getAmount() - discutPrice.doubleValue());
				}
			}
		}
		double diffreturn=0;
		for (GoodsPriceVO goodsPriceVO : list) {
			diffreturn += goodsPriceVO.getDiff();
		}
		return diffreturn;
	}




	@RequestMapping(value = "/shoppingCart/getShipmentFee.php", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	public @ResponseBody SimpleJsonResponse getShipmentFee(@RequestParam("entry[]") int[] entryIds,
														   @RequestParam("sentry[]") int[] entrySourceIds,
														   @RequestParam(value = "stype[]") OrderEntrySourceType[] entrySourceTypes,
														   @RequestParam(value = "amount[]") int[] amounts,
														   @RequestParam("myAddressId") long addressId,
														   Website website) {
		List<OrderEntry> entryList = new ArrayList<>();
		for (int i = 0; i < entryIds.length; i++) {
			OrderEntry entry = new OrderEntry();
			entry.setStorageUnitId(entryIds[i]);
			entry.setSourceEntryType(entrySourceTypes[i]);
			entry.setSourceObjectId(entrySourceIds[i]);
			entry.setAmount(amounts[i]);
			entryList.add(entry);
		}
		WebsiteUserAddress websiteUserAddress = websiteUserAddressService.findOne(addressId);
		double shipmentFee = orderService.getShipmentFee(websiteUserAddress.getProvince(), entryList);
		return new SimpleJsonResponse(true, shipmentFee);
	}

	//@RequestMapping(value = "/shoppingCart/submitOrder.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public @ResponseBody SimpleJsonResponse submitOrder(@RequestParam("entry[]") int[] entryIds,
														@RequestParam("sentry[]") int[] entrySourceIds, //商品id
														@RequestParam(value = "fenxiaoUserId[]") int[] fenxiaoUserIds,
														@RequestParam(value = "stype[]") OrderEntrySourceType[] entrySourceTypes,
														@RequestParam(value = "amount[]") int[] amounts,
														@RequestParam(value = "myAddressId") Long addressId,
														@RequestParam(value = "userRemark", required = false) String userRemark,
														@RequestParam(value = "couponId",required = false) Integer couponId,
														//@RequestParam(value = "fenxiaoInfo",required = false) String fenxiaoInfo,//商品分销信息 (商品ID_分销商ID_商品数量,商品ID_分销商ID_商品数量)
														@RequestParam(value = "couponIdInit",required = false) Integer couponIdInit,
														@RequestParam("totalPrice") double needPrice,
														AbstractShoppingCart shoppingCart,
														UserDetail currentUser,
														Website website,
														HttpSession session,
														HttpServletRequest request) throws IOException {

		WebsiteUser websiteUser = websiteUserService.findOne(currentUser.getUserId());
		ShoppingOrder order = new ShoppingOrder();
		// Relationship relationship=new Relationship();
		if (websiteUser == null || websiteUser.getId() <= 0) {
			logger.info("SubmitOrder fail, websiteUser is null!");
			throw new PermissionDeniedException();
		}

		if(addressId == null || addressId < 1) {
			return new SimpleJsonResponse(false, "地址信息不存在或已经被删除,请您重新选择送货地址!");
		}

		WebsiteUserAddress address = websiteUserAddressService.findOne(addressId);
		if (address == null || address.getId() < 0) {
			return new SimpleJsonResponse(false, "地址信息不存在或已经被删除,请您重新选择送货地址!");
		}
		if (address.getUserId() != currentUser.getUserId()) {
			return new SimpleJsonResponse(false, "数据异常，请勿非法操作！");
		}
		if (address.getMobile() == null || address.getMobile().isEmpty()) {
			if (address.getPhone() == null || address.getPhone().isEmpty()) {
				return new SimpleJsonResponse(false, "手机号码和固定电话必须填写一个,请补全您的送货地址信息");
			}
		}
		if(address.getProvince() == null || address.getCity() == null || address.getCounty() == null) {
			return new SimpleJsonResponse(false, "当前地址已失效，请您删除后重新添加！");
		}
		if(address.getProvince().isEnabled() == false || address.getCity().isEnabled() == false || address.getCounty().isEnabled() == false) {
			return new SimpleJsonResponse(false, "当前地址已失效，请您删除后重新添加！");
		}
		if(address.getTown() != null && address.getTown().isEnabled() == false) {
			return new SimpleJsonResponse(false, "当前地址已失效，请您删除后重新添加！");
		}
		if (userRemark != null && userRemark.length() > 200) {
			return new SimpleJsonResponse(false, "买家留言最多可以输入200字！");
		}
		order.setReceiverName(address.getReceiverName());
		order.setReceiverMobile(address.getMobile());
		order.setReceiverPhone(address.getPhone());
		order.setReceiverAddr(address.getAddress());
		//身份证信息，跨境商品使用
		order.setReceiverIdentityCard(address.getIdentityCard());
		order.setProvince(address.getProvince());
		order.setCity(address.getCity());
		order.setCounty(address.getCounty());
		order.setTown(address.getTown());
		order.setUserRemark(userRemark);
		//检查自有产品库存，和供应商产品库存
		order.setUserId(currentUser.getUserId());
		order.setDeprecated(false);
		order.setRemoteIPAddr(request.getRemoteAddr());
		order.setNeedPay(needPrice);
		//存入优惠券id
		if (couponIdInit!= null){
			order.setCouponId(couponIdInit);
		}else if(couponId!=null){
			order.setCouponId(couponId);
		}

		List<OrderEntry> entryList = new ArrayList<>();
		for (int i = 0; i < entryIds.length; i++) {
			if (amounts[i] <= 0) {
				logger.info("SubmitOrder fail, amounts less than or equal to zero!");
				return new SimpleJsonResponse(false, "网络错误，请重新操作");
			}
			Map<Boolean, String> result = checkOrder(order, website, entryIds[i], amounts[i], entrySourceTypes[i], address);
			String message = result.get(false);
			if (message != null && !message.isEmpty()) {
				return new SimpleJsonResponse(false, message);
			}

			OrderEntry entry = new OrderEntry();
			entry.setStorageUnitId(entryIds[i]);
			entry.setSourceEntryType(entrySourceTypes[i]);
			entry.setSourceObjectId(entrySourceIds[i]);
			if (amounts[i] < 0) {
				return new SimpleJsonResponse(false, "订单异常，请重新操作");
			}
			entry.setAmount(amounts[i]);
			entryList.add(entry);
		}

		//如果是提货券
		//增加提货券，这里如果券满足时强制用券
		//如果商品只有一个，且为提货券类商品，显示提货券数量
		//根据员工和产品ID查找专享价
		boolean isEnterpriseZoneProduct = false;
		Integer thisProductTotalVoucher = 0;
		Product product = null;
		Integer productId =new Long(entryList.get(0).getSourceObjectId()).intValue();
		Map<Integer,String> catMap = userDefinedCateService.findEnterpriseCatMap(productId);
		if(entryList!=null&&entryList.size()==1) {
			EnterpriseExclusiveProductPoolEntry productPoolEntry = productPoolEntryService.selectProductPoolEntry(websiteUser, productId);
			product = productService.findOne(productId);
			//是否为企业专区大礼包产品,根据这个值显示立即购买
			if (productPoolEntry != null && websiteUser != null && websiteUser.getId() > 0) {
				isEnterpriseZoneProduct = userDefinedCateService.isEnterpriseZoneProduct(productId);
				// 根据用户查询出可兑换券的数量，返回前台，做数量限制
				for(Map.Entry<Integer, String> entry:catMap.entrySet()) {
					thisProductTotalVoucher += enterprisePackageVoucherService.countUseableByUserPhoneAndProductName(entry.getValue(), websiteUser);
				}
				// 如果券不足则不使用
				if (thisProductTotalVoucher.intValue() < entryList.get(0).getAmount()) {
					thisProductTotalVoucher = 0;
				}
			}
		}
		//如果使用了提货券，则实付款为0
		boolean isVoucher = false;
		if (isEnterpriseZoneProduct && thisProductTotalVoucher.intValue() > 0
				&& thisProductTotalVoucher.intValue() >= entryList.get(0).getAmount()) {
			order.setVoucherNum(entryList.get(0).getAmount());
			isVoucher=true;
		}
		//增加提货券结束
		//如果使用了提货券，则无法使用优惠券
		if(isVoucher) {
			order.setCouponId(null);
			couponIdInit = null;
			couponId = null;
		}

		ShoppingOrder myOrder;
		try {
			try {
				myOrder = orderService.create(order, entryList, website,needPrice);
				if(isVoucher && myOrder.getNeedPay()==0d) {
					shoppingOrderDao.setStatus(order, OrderStatus.waiting_shipment);
				}
				if(isVoucher) {
					int total = entryList.get(0).getAmount();
					for(Map.Entry<Integer, String> entry:catMap.entrySet()) {
						Integer count = enterprisePackageVoucherService.countUseableByUserPhoneAndProductName(entry.getValue(), websiteUser);
						if (total > 0 && count != null && count.intValue() > 0) {
							if (count.intValue() >= total) {
								enterprisePackageVoucherService.minusUseableByUserPhoneAndProductName(entry.getValue(),
										websiteUser, total, myOrder.getId());
								break;
							} else {
								enterprisePackageVoucherService.minusUseableByUserPhoneAndProductName(entry.getValue(),
										websiteUser, count, myOrder.getId());
								total = total - count;
							}
						}
					}
				}

			} catch(OrderCreateException e) {
				logger.info("", e);
				return new SimpleJsonResponse(false, e.getMessage());
			}
		} catch (Exception e) {
			logger.info("", e);
			return new SimpleJsonResponse(false, "订单提交失败");
		}

		if (couponIdInit != null){
			userCouponService.updateByStatus(couponIdInit);
		}else if(couponId!=null){
			userCouponService.updateByStatus(couponId);
		}

		fenXiaoOrderService.create(order.getKey(),fenxiaoUserIds,entrySourceIds,amounts);







		//订单生成成功后清空购物车
		for (int i = 0; i < entryIds.length; i++) {
			shoppingCart.decrease(entryIds[i], amounts[i], ShoppingCartSourceEntryType.product, entrySourceIds[i]);
		}
		shoppingCart.flush();
		currentUser.setIntegral(websiteUser.getIntegral() - (int) myOrder.getNeedPay());
		session.setAttribute("currentUser", currentUser);
		return new SimpleJsonResponse(true, myOrder.getId());

	}

	@RequestMapping(value = "/shoppingCart/success.php", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	public String successCreateOreder(@RequestParam("id") long id, UserDetail currentUser, Model model) {
		if (currentUser == null || currentUser.getUserId() < 0) {
			throw new PermissionDeniedException();
		}
		ShoppingOrder myOrder = orderService.findOne(id);
		if (currentUser.getUserId() != myOrder.getUserId()) {
			throw new PermissionDeniedException();
		}

		model.addAttribute("myOrder", myOrder);

		//订单支付信息  倒计时
		if (myOrder.getStatus() == OrderStatus.waiting_pay) {
			model.addAttribute("expiredTime", myOrder.getCreateTime().getTime() + 60 * 1000 * 60 * 24);
		}
		// 如果使用了提货券
		model.addAttribute("useVoucher",false);
		if(myOrder.getVoucherNum()!=null&&myOrder.getVoucherNum()>0&&myOrder.getNeedPay()==0d) {
			model.addAttribute("useVoucher",true);
		}

		return "shopping/step3";
	}

	@RequestMapping(value = "/shoppingCart/changeAmount.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public @ResponseBody SimpleJsonResponse changeAmount(@RequestParam("entry") int entryId,
														 @RequestParam("sentry") int sourceEntryId,
														 @RequestParam("stype") ShoppingCartSourceEntryType sourceEntryType,
														 @RequestParam("opeAmount") int opeAmount,
														 AbstractShoppingCart shoppingCart,
														 WebsiteUser websiteUser,
														 Website website,
														 HttpSession session) {
		if (opeAmount > 0) {
			shoppingCart.increase(entryId, opeAmount, sourceEntryType, sourceEntryId,-1);
		} else {
			shoppingCart.decrease(entryId, Math.abs(opeAmount), sourceEntryType, sourceEntryId);
		}

		shoppingCart.flush();

		Map<String, Object> resultMap = initSubmitOrderInfo(shoppingCart.entries(), website, 0.00, websiteUser);  //统计购物车的商品总数(TOTALAMOUNT),总金额(TOTALPRICE)

		return new SimpleJsonResponse(true, resultMap);
	}

	/**
	 * 判断商品库存
	 *
	 * @param website
	 * @param amount
	 * @param entryId
	 * @param entrySourceType
	 * @return
	 */
	private Map<Boolean, String> checkOrder(ShoppingOrder order, Website website, int entryId, int amount, OrderEntrySourceType entrySourceType, WebsiteUserAddress address) throws IOException {

		Map<Boolean, String> result = new HashMap();
		result.put(true, null);

		//检查库存是否充足
		if (entrySourceType == OrderEntrySourceType.product) {
			StorageUnit sku = storageUnitService.findOne(entryId);
			if (sku == null) {
				result.put(false, "商品不存在，请刷新后尝试");
				return result;
			}
			Product product = productService.findOne(sku.getProductId());
			if (product == null) {
				result.put(false, "商品不存在，请刷新后尝试");
				return result;
			}

			if (product.getStatus() != ProductStatus.selling) {
				result.put(false, "订单中含有未上架的商品，订单提交失败！");
			}

			if (product.getOwnerType() == OwnerType.enterprise || product.getOwnerType() == OwnerType.provider) {
				if (website.isEnabledErp()) {
					ProductStorage storage = productStorageService.findOne(entryId);
					if (amount > storage.getUseableNum()) {
						result.put(false, product.getName() + "已经售完，请重新选择商品！");
						return result;
					}
				}

				if(product.isEnableLimitedArea()) {
					if(order.getCity().getId() != 1213) {
						result.put(false, product.getName() + "只限制杭州区域发货，请您重新选择收货地址！");
						return result;
					}
				}

				if(product.isEnableOverseas()) {
					if(order.getReceiverIdentityCard() == null || order.getReceiverIdentityCard().trim().isEmpty()) {
						result.put(false, "您的订单中包含跨境商品请您先填写身份证信息！");
						return result;
					}
				}

			} else {
				try {
					String strAddress = address.getProvince().getId() + "_" + address.getCity().getId() + "_" + address.getCounty().getId();
					Map<String, Object> stockResult = systemProductService.productStock(product.getSystemProductId(), amount, strAddress, website);
					if (stockResult != null) {
						boolean storageResult = (boolean) stockResult.get("stock_status");
						if (!storageResult) {
							result.put(false, product.getName() + "已经售完，请重新选择商品！");
							return result;
						}
					}
				} catch (Exception e) {
					result.put(false, product.getName() + "已经售完，请重新选择商品！");
					return result;
				}

				try {
					Map<String, Object> financeResult = systemFinanceService.balanceRemain(website);
					if (financeResult != null) {
						double remain = (double) financeResult.get("REMAIN");
						double creditTotal = (double) financeResult.get("CREDIT_TOTAL");
						double creditUsed = (double) financeResult.get("CREDIT_USED");
						if ((remain + creditTotal - creditUsed) < product.getSystemPrice()) {
							result.put(false, "网站预存款不足，请通知管理员尽快充值！");
							return result;
						}
					} else {
						result.put(false, "网站预存款不足，请通知管理员尽快充值！");
						return result;
					}
				} catch (Exception e) {
					result.put(false, "网站预存款不足，请通知管理员尽快充值！");
					return result;
				}
			}
		}
		return result;
	}

	/**
	 * 统计购物车的商品总数(TOTALAMOUNT),总金额(TOTALPRICE),判定是否限定
	 *
	 * @param cartEntryList
	 * @return
	 */
	private Map<String, Object> initSubmitOrderInfo(List<ShoppingCartEntry> cartEntryList, Website website, double shimpentFee, WebsiteUser websiteUser) {
		Map<String, Object> resultMap = new HashMap<>();
		int totalAmount = 0;    //总数量
		double totalPrice = 0.00D;    //总金额

		boolean enableOverseas = false;
		boolean enableLimitedArea = false;
		//取出根据产品ID集合取出专享价
		List<Integer>  productIds = new ArrayList<>();
		if(cartEntryList == null || cartEntryList.isEmpty()) {
			resultMap.put("TOTALAMOUNT", totalAmount);
			resultMap.put("TOTALPRICE", new NumberTool().format("#0.00", totalPrice));
			resultMap.put("ENABLELIMITEDAREA", enableLimitedArea);
			resultMap.put("ENABLEOVERSEAS", enableOverseas);
			return resultMap;
		}
		for (ShoppingCartEntry shoppingCartEntry : cartEntryList) {
			productIds.add(shoppingCartEntry.getSourceEntryId());
		}
		Map<Integer, EnterpriseExclusiveProductPoolEntry> exclusiveProductPoolEntryMap = null;
		if(websiteUser != null && websiteUser.getId() != null && websiteUser.getId() > 0) {
			exclusiveProductPoolEntryMap = productPoolEntryService.selectProductPoolEntryMap(websiteUser, productIds);
		}
		//计算价格
		if (cartEntryList != null && !cartEntryList.isEmpty()) {
			for (ShoppingCartEntry entry : cartEntryList) {
				Product product = productService.findOne(entry.getSourceEntryId());
				if (product == null) {
					continue;
				}
				//赋值员工专享价
				if(exclusiveProductPoolEntryMap != null && !exclusiveProductPoolEntryMap.isEmpty()) {
					for (Integer productId : exclusiveProductPoolEntryMap.keySet()) {
						if(productId.equals(product.getId())){
							product.setRetailPrice(exclusiveProductPoolEntryMap.get(productId).getExclusivePrice());
						}
					}
				}

				if (product.getStatus() == ProductStatus.selling) {
					if(websiteUser != null && websiteUser.getId() != null && websiteUser.getId() > 0) {
						EnterpriseUserMap userMap = enterpriseUserMapService.getOneByUserId(websiteUser.getId());
						if(userMap != null && userMap.getProductPoolId() != null && userMap.getProductPoolId() > 0) {
							EnterpriseExclusiveProductPoolEntry poolEntry = enterpriseExclusiveProductPoolEntryService.selectOne(userMap.getProductPoolId(), product.getId());
							if(poolEntry != null) {
								product.setRetailPrice(poolEntry.getExclusivePrice());
							}
						}
					}
					if (website.isEnabledErp()) {
						boolean isCalculate = true;
						if (product.getOwnerType() != OwnerType.system && product.getOwnerType() != OwnerType.jingdong) {
							SimpleStorageSummary storageSummary = productStorageService.findStorageSummary(entry.getSourceEntryId());
							if (storageSummary.getUseableNum() <= 0) {
								isCalculate = false;
							}
						}
						if (isCalculate) {
							totalAmount += entry.getAmount();   //数量
							totalPrice += entry.getAmount() * product.getRetailPrice();
						}
					} else {
						totalAmount += entry.getAmount();   //数量
						totalPrice += entry.getAmount() * product.getRetailPrice();
					}
					//判断是否限定区域发货，当前固定杭州
					if(product.isEnableLimitedArea()) {
						enableLimitedArea = true;
					}
					//是否为境外商品
					if(product.isEnableOverseas()) {
						enableOverseas = true;
					}
				}
			}
		}

		resultMap.put("TOTALAMOUNT", totalAmount);
		resultMap.put("TOTALPRICE", new NumberTool().format("#0.00", totalPrice + shimpentFee));
		resultMap.put("ENABLELIMITEDAREA", enableLimitedArea);
		resultMap.put("ENABLEOVERSEAS", enableOverseas);
		return resultMap;
	}

	public CopyOnWriteArrayList<UserOrderCoupon> returnList(CopyOnWriteArrayList<UserOrderCoupon> userCoupons){
		for (int i =0; i<userCoupons.size()-1; i++){
			for (int j = userCoupons.size()-1; j > i; j--){
				if (userCoupons.get(j).getCouponId().equals(userCoupons.get(i).getCouponId())){
					userCoupons.remove(j);
				}
			}
		}
		return userCoupons;
	}

	@RequestMapping(value = "/shoppingCart/chooseEntry.php", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	public String chooseEntry2(
			@RequestParam("isSelected[]") boolean[] isSelected,
			@RequestParam(value = "fenxiaoUserId[]") int[] fenxiaoUserIds,
			@RequestParam("entry[]") int[] entryIds,
			@RequestParam("sentry[]") int[] sourceEntryIds,// 产品ID
			@RequestParam(value = "stype[]") ShoppingCartSourceEntryType[] sourceEntryTypes,
			@RequestParam(value = "amount[]") int[] amounts,
			// @RequestParam(value = "fenxiaoInfo",required = false) String
			// fenxiaoInfo,//商品分销信息 (商品ID_分销商ID_商品数量,商品ID_分销商ID_商品数量)
			@RequestParam(value = "returnUrl", required = false) String returnUrl,
			@RequestParam(value = "addressId", required = false) Integer addressId,
			@RequestParam(value = "productOwnerType[]") OwnerType[] productOwnerType,
			@RequestParam(value = "couponId[]", required = false) String[] couponId1,
			AbstractShoppingCart shoppingCart, WebsiteUser websiteUser,
			Website website, Model model, HttpSession session) {
		Map<Integer, Integer> couponInfo= null;
		if(couponId1 != null && couponId1.length>0){
			couponInfo = toMap(couponId1);
		}

		// 分销信息
		int fid = fenxiaoUserIds[0];
		if (websiteUser == null || websiteUser.getId() <= 0) {
			// 如果当前未登陆,则跳转至登陆页面
			if (returnUrl != null && !returnUrl.isEmpty()) {
				return "redirect:/login.php?retUrl=" + returnUrl;
			} else {
				returnUrl = "/product/" + sourceEntryIds[0] + ".php?fid="
						+ String.valueOf(fid);
				return "redirect:/login.php?retUrl=" + returnUrl;
			}
		}

		// 建立分销等级关系
		Relationship r = relationshipDao.findbyuserID(websiteUser.getId()
				.intValue());
		FenxiaoUser fx = fenXiaoUserService.findByUserId(websiteUser.getId()
				.intValue());
		if (r == null && websiteUser.getId() != fenxiaoUserIds[0]
				&& fenxiaoUserIds[0] != 0 && fx != null
				&& fx.getParentID() != 0) {
			Relationship relationship = new Relationship();
			relationship.setUserID(websiteUser.getId());
			relationship.setFenxiaoID(fenxiaoUserIds[0]);
			relationship.setRelationship(1);
			relationshipDao.save(relationship);
		}
		if (r == null && websiteUser.getId() != fenxiaoUserIds[0]
				&& fenxiaoUserIds[0] != 0 && fx == null) {
			Relationship relationship = new Relationship();
			relationship.setUserID(websiteUser.getId());
			relationship.setFenxiaoID(fenxiaoUserIds[0]);
			relationship.setRelationship(1);
			relationshipDao.save(relationship);
		}
		// 当前登陆用户所有地址信息
		WebsiteUserAddress websiteUserAddress;
		if (addressId != null && addressId > 0) {
			websiteUserAddress = websiteUserAddressService.findOne(addressId);
			//身份确认，防止抓取别的用户的地址信息
			if(websiteUserAddress.getUserId() != websiteUser.getId()){
				model.addAttribute("address", null);
			}else{
				model.addAttribute("address", websiteUserAddress);
			}

		} else {
			websiteUserAddress = websiteUserAddressService
					.findDefault(websiteUser);
			model.addAttribute("address", websiteUserAddress);
		}
		model.addAttribute("provinces", geoRegionService.findAllProvince());

		//按照商品的归属者将商品拆开
		Map<Integer, List<TransitionOrder>> map1 = excute(isSelected,fenxiaoUserIds,entryIds,sourceEntryIds,sourceEntryTypes,amounts,productOwnerType);

		List<Integer> ordersSize = new ArrayList<Integer>();
		model.addAttribute("ordersSize",ordersSize);
		List<Map<String, Object>> infos = new ArrayList<Map<String,Object>>();
		//BigDecimal totalPrice = new BigDecimal(0);
		double totalPrice = 0;
		int totalAmount = 0;

		// 生成每个归属者的信息
		for (Map.Entry<Integer, List<TransitionOrder>> entry : map1.entrySet()) {

			Map<String, Object> info = new HashMap<String, Object>();
			Integer couponId = null;
			if (couponInfo!= null && couponInfo.size()>0){
				couponId = couponInfo.get(entry.getKey());
			}
			info.put("ownerId",entry.getKey());
			ordersSize.add(entry.getKey());
			List<Integer> sourceEntryIds1 = new ArrayList<>();
			List<ShoppingCartEntry> entries = new ArrayList<>();
			List<OrderEntry> orderEntries = new ArrayList<>();
			for (TransitionOrder transitionOrder : entry.getValue()) {
				if (!transitionOrder.isSelected()) {
					continue;
				}
				if (transitionOrder.getAmount() > 0) {
					ShoppingCartEntry shoppingCartEntry = new ShoppingCartEntry();
					shoppingCartEntry.setEntryId(transitionOrder.getEntryId());
					shoppingCartEntry.setSourceEntryId(transitionOrder
							.getSourceEntryId());
					shoppingCartEntry.setSourceEntryType(transitionOrder
							.getSourceEntryType());
					shoppingCartEntry.setAmount(transitionOrder.getAmount());
					shoppingCartEntry.setFenxiaoUserId(transitionOrder
							.getFenxiaoUserId());
					entries.add(shoppingCartEntry);

					OrderEntry orderEntry = new OrderEntry();
					orderEntry.setStorageUnitId(transitionOrder.getEntryId());
					orderEntry.setSourceEntryType(OrderEntrySourceType
							.valueOf(transitionOrder.getSourceEntryType()
									.name()));
					orderEntry.setSourceObjectId(transitionOrder.getSourceEntryId());
					orderEntry.setAmount(transitionOrder.getAmount());
					orderEntries.add(orderEntry);
				}
				sourceEntryIds1.add(transitionOrder.getSourceEntryId());
			}

			// 邮费
			double shipmentFee = 0;
			if (websiteUserAddress != null) {
				shipmentFee = orderService.getShipmentFee(
						websiteUserAddress.getProvince(), orderEntries);
				info.put("shipmentFee", shipmentFee);
				//model.addAttribute("shipmentFee"+ownerId, shipmentFee);
			}
			// 统计购物车的商品总数(TOTALAMOUNT),总金额(TOTALPRICE)
			Map<String, Object> resultMap = initSubmitOrderInfo(entries,
					website, shipmentFee, websiteUser);
			info.put("cartEntryList", entries);
			//model.addAttribute("cartEntryList"+ownerId, entries);
			info.put("resultMap", resultMap);
			//model.addAttribute("resultMap"+ownerId, resultMap);

			// **********************************优惠券使用*****************************
			UserDetail currentUser = (UserDetail) session
					.getAttribute("currentUser");
			List<Coupon> coupons = new ArrayList<>();

			// 第一步遍历商品,处理成想要的list
			// 找到优惠券
			Set<Integer> catSet = new HashSet<>();
			//商家优惠券
			coupons.addAll(mobileCouponService
					.findProductCouponByType1(entry.getKey(),currentUser.getUserId()));



			for (int targetId : sourceEntryIds1) {
				// 商品优惠券
				List<Coupon> mobileCoupons1 = mobileCouponService
						.findProductCouponBytargetId(targetId,
								currentUser.getUserId());
				coupons.addAll(mobileCoupons1);

				// 分类优惠券
				Set<Integer> userCatsSet = userDefinedCateService
						.findProductCateIds(targetId);
				catSet.addAll(userCatsSet);
				for (Integer catId : userCatsSet) {
					List<Coupon> mobileCoupons = mobileCouponService
							.findProductCouponByType2(catId,
									currentUser.getUserId());
					coupons.addAll(mobileCoupons);
				}

				// 专题优惠券
				List<WebsiteCustomBlock> blocks = websiteCustomBlockService
						.findCustomBlockByTargetId(targetId);
				if (blocks != null) {
					for (WebsiteCustomBlock block : blocks) {
						List<Coupon> mobileCoupons2 = mobileCouponService
								.findProductCouponByType3(
										block.getSpecialActivityId(),
										currentUser.getUserId());
						coupons.addAll(mobileCoupons2);
					}
				}

			}

			CopyOnWriteArrayList<UserOrderCoupon> userCoupons = new CopyOnWriteArrayList<>();
			Object a = resultMap.get("TOTALPRICE");
			String s = a.toString();
			Integer shopAmount = Integer
					.valueOf(s.substring(0, s.indexOf(".")));
			if (CollectionUtils.isNotEmpty(coupons)) {
				for (Coupon coupon1 : coupons) {
					if (coupon1.getType() == 1) {
						userCoupons.addAll(userCouponService
								.selectByUserIdAndType1(
										currentUser.getUserId(),
										coupon1.getType(), shopAmount,
										coupon1.getId()));
					} else {
						userCoupons.addAll(userCouponService
								.selectByUserIdAndType(currentUser.getUserId(),
										coupon1.getType(), coupon1.getId()));
					}
				}
			}

			userCoupons = returnList(userCoupons);
			// 处理3个list
			List<GoodsPriceVO> productList = new ArrayList<>();
			Map<Integer, GoodsPriceVO> shopMap = new HashMap<>();
			Map<Integer, GoodsPriceVO> map = new HashMap<>();
			Map<Integer, GoodsPriceVO> activityMap = new HashMap<>();
			for (Integer catid : catSet) {
				GoodsPriceVO gvo = new GoodsPriceVO();
				gvo.setTargetId(catid);
				gvo.setTargetType(2);
				map.put(catid, gvo);
			}

			ProductCalcVO pvo = new ProductCalcVO();
			for (ShoppingCartEntry shoppingCartEntry : entries) {
				Product product = productService.findOne(shoppingCartEntry
						.getSourceEntryId());
				// 指定范围,1：指定商家可用。2：指定分类可用。3：指定商品可用。4：指定专题可用


				//商家
				GoodsPriceVO svo = shopMap.get(entry.getKey());
				if (svo == null) {
					svo = new GoodsPriceVO();
				}
				svo.setTargetId(entry.getKey());
				svo.setAmount(svo.getAmount()
						+ product.getRetailPrice()
						* shoppingCartEntry.getAmount());
				svo.setTargetType(1);
				shopMap.put(entry.getKey(), svo);



				// 商品
				GoodsPriceVO gvo = new GoodsPriceVO();

				gvo.setTargetId(shoppingCartEntry.getSourceEntryId());
				gvo.setAmount(product.getRetailPrice()
						* shoppingCartEntry.getAmount());
				gvo.setTargetType(3);
				productList.add(gvo);

				// 分类
				Set<Integer> userCatsSet = userDefinedCateService
						.findProductCateIds(shoppingCartEntry
								.getSourceEntryId());
				for (Integer i : userCatsSet) {
					map.get(i).setAmount(
							map.get(i).getAmount() + product.getRetailPrice()
									* shoppingCartEntry.getAmount());
					map.get(i).setTargetType(2);
				}
				// 专题
				List<WebsiteCustomBlock> blocks = websiteCustomBlockService
						.findCustomBlockByTargetId(shoppingCartEntry
								.getSourceEntryId());
				if (blocks != null) {
					for (WebsiteCustomBlock block : blocks) {
						GoodsPriceVO avo = activityMap.get(block
								.getSpecialActivityId());
						if (avo == null) {
							avo = new GoodsPriceVO();
						}
						avo.setTargetId(block.getSpecialActivityId());
						avo.setAmount(avo.getAmount()
								+ product.getRetailPrice()
								* shoppingCartEntry.getAmount());
						avo.setTargetType(4);
						activityMap.put(block.getSpecialActivityId(), avo);
					}
				}
			}
			pvo.setCatProductList(new ArrayList<GoodsPriceVO>(map.values()));
			pvo.setActivityProductList(new ArrayList<GoodsPriceVO>(activityMap
					.values()));
			pvo.setProductList(productList);
			pvo.setShopProductList(new ArrayList<GoodsPriceVO>(shopMap.values()));

			double diff = 0;
			// 计算最优优惠券
			UserOrderCoupon calcCoupon = calcBestConpon(pvo, userCoupons);
			//如果已被前面订单使用 则不能使用
			if(calcCoupon!= null && isUsedCoupon(calcCoupon.getCouponId(),infos)){
				calcCoupon = new UserOrderCoupon();
			}

			if (couponId != null && couponId > 0) {
				List<UserOrderCoupon> couponlist = userCouponService
						.selectByCouponId(couponId);
				calcCoupon = CollectionUtils.isEmpty(couponlist) ? null
						: couponlist.get(0);
			}

			calcCoupon = calcCoupon == null ? new UserOrderCoupon()
					: calcCoupon;

			if (calcCoupon.getTargetType() != null
					&& 1 == calcCoupon.getTargetType()) {
				diff = calCouponForGoods(pvo.getShopProductList(), calcCoupon);
			}
			if (calcCoupon.getTargetType() != null
					&& 2 == calcCoupon.getTargetType()) {
				diff = calCouponForGoods(pvo.getCatProductList(), calcCoupon);
			}
			if (calcCoupon.getTargetType() != null
					&& 3 == calcCoupon.getTargetType()) {
				diff = calCouponForGoods(pvo.getProductList(), calcCoupon);
			}
			if (calcCoupon.getTargetType() != null
					&& 4 == calcCoupon.getTargetType()) {
				diff = calCouponForGoods(pvo.getActivityProductList(),
						calcCoupon);
			}
			// 如果差额为0，最优优惠券也不能使用
			if (0 == getDiff(new CopyOnWriteArrayList<UserOrderCoupon>(), pvo,
					calcCoupon)) {
				calcCoupon = new UserOrderCoupon();
			}
			if (couponId != null && couponId == 0) {
				UserOrderCoupon userOrderCoupon = new UserOrderCoupon();
				Object o = resultMap.get("TOTALPRICE");
				BigDecimal amount = new BigDecimal(o.toString());
				resultMap.put("TOTALPRICE",
						new NumberTool().format("#0.00", amount.doubleValue()));
				//已经选择的优惠券
				userOrderCoupon.setRealMoney(0d);
				info.put("userCoupon", userOrderCoupon);
				//model.addAttribute("userCoupon"+ownerId, userOrderCoupon);
			} else {
				BigDecimal b = new BigDecimal(diff);
				Object o = resultMap.get("TOTALPRICE");
				BigDecimal amount = new BigDecimal(o.toString());
				resultMap.put("TOTALPRICE", new NumberTool().format("#0.00",
						amount.subtract(b).doubleValue()));
				calcCoupon.setRealMoney(diff);
				//已经选择的优惠券
				info.put("userCoupon", calcCoupon);
				//model.addAttribute("userCoupon"+ownerId, calcCoupon);
			}
			for (UserOrderCoupon calcCoupon1 : userCoupons) {
				getDiff(userCoupons, pvo, calcCoupon1);
			}
			// 增加提货券，这里如果券满足时强制用券
			// 如果商品只有一个，且为提货券类商品，显示提货券数量
			// 根据员工和产品ID查找专享价
			boolean isEnterpriseZoneProduct = false;
			Integer thisProductTotalVoucher = 0;
			if (entries != null && entries.size() == 1) {
				EnterpriseExclusiveProductPoolEntry productPoolEntry = productPoolEntryService
						.selectProductPoolEntry(websiteUser, entries.get(0)
								.getSourceEntryId());
				// 是否为企业专区大礼包产品,根据这个值显示立即购买
				if (productPoolEntry != null && websiteUser != null
						&& websiteUser.getId() > 0) {
					isEnterpriseZoneProduct = userDefinedCateService
							.isEnterpriseZoneProduct(entries.get(0)
									.getSourceEntryId());
					Map<Integer, String> catMap = userDefinedCateService
							.findEnterpriseCatMap(entries.get(0)
									.getSourceEntryId());
					// 根据用户查询出可兑换券的数量，返回前台，做数量限制
					for (Map.Entry<Integer, String> entry1 : catMap.entrySet()) {
						thisProductTotalVoucher += enterprisePackageVoucherService
								.countUseableByUserPhoneAndProductName(
										entry1.getValue(), websiteUser);
					}
					// 如果券不足则不使用
					if (thisProductTotalVoucher.intValue() < entries.get(0)
							.getAmount()) {
						thisProductTotalVoucher = 0;
					}
				}
			}
			// 如果使用了提货券，则实付款为0
			if (isEnterpriseZoneProduct
					&& thisProductTotalVoucher.intValue() > 0) {
				resultMap.put("TOTALPRICE",
						new NumberTool().format("#0.00", 0d + shipmentFee));
				resultMap.put("allPrice",
						new NumberTool().format("#0.00", 0d + shipmentFee));
			}

			info.put("thisProductTotalVoucher", thisProductTotalVoucher);
			//model.addAttribute("thisProductTotalVoucher"+ownerId,thisProductTotalVoucher);

			info.put("isEnterpriseZoneProduct", isEnterpriseZoneProduct);
			//model.addAttribute("isEnterpriseZoneProduct"+ownerId,isEnterpriseZoneProduct);
			// 增加提货券结束

			//所有可用优惠券
			info.put("userCoupons", userCoupons);
			//model.addAttribute("userCoupons"+ownerId, userCoupons);
			infos.add(info);
			totalPrice = totalPrice + Double.parseDouble(resultMap.get("TOTALPRICE").toString());
			totalAmount = totalAmount + Integer.parseInt(resultMap.get("TOTALAMOUNT").toString());
		}
		model.addAttribute("totalPrice",totalPrice);
		model.addAttribute("totalAmount",totalAmount);
		model.addAttribute("infos",infos);
		model.addAttribute("ordersSize",ordersSize);
		return "shopping/step2";
	}

	private boolean isUsedCoupon(Integer couponId,
								 List<Map<String, Object>> infos) {
		if(couponId == null || CollectionUtils.isEmpty(infos)){
			return false;
		}
		for (Map<String, Object> info : infos) {

			UserOrderCoupon userOrderCoupon = (UserOrderCoupon) info.get("userCoupon");
			if(userOrderCoupon != null && couponId.equals(userOrderCoupon.getCouponId())){
				return true;
			}
		}
		return false;
	}

	private Map<Integer, Integer> toMap(String[] couponId) {
		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		for (String string : couponId) {
			String[] strings = string.split("-");
			result.put(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]));
		}
		return result;
	}

	private Map<Integer, List<TransitionOrder>> excute(boolean[] isSelected,
													   int[] fenxiaoUserIds, int[] entryIds, int[] sourceEntryIds,
													   ShoppingCartSourceEntryType[] sourceEntryTypes, int[] amounts,
													   OwnerType[] productOwnerType) {
		Map<Integer, List<TransitionOrder>> map = new HashMap<Integer, List<TransitionOrder>>();
		for (int i = 0;i<entryIds.length;i++) {
			if(!isSelected[i]){
				continue;
			}
			Product product = productService.findOne(sourceEntryIds[i]);
			TransitionOrder transitionOrder = new TransitionOrder(isSelected[i], fenxiaoUserIds[i], entryIds[i], sourceEntryIds[i], sourceEntryTypes[i], amounts[i], productOwnerType[i]);
			//拆单需要，系统产品，和京东产品视为同一类型。 来推送到系统。
			if(OwnerType.jingdong == product.getOwnerType() || OwnerType.system == product.getOwnerType()) {
				product.setOwnerType(OwnerType.system);
			}
			if(product.getOwnerType() == OwnerType.provider) {
				List<TransitionOrder> transitionOrders = map.get(product.getOwnerId());
				if(transitionOrders == null){
					transitionOrders = new ArrayList<>();
					map.put(product.getOwnerId(), transitionOrders);
				}
				transitionOrders.add(transitionOrder);
			}else if(product.getOwnerType() == OwnerType.enterprise){
				List<TransitionOrder> transitionOrders = map.get(0);
				if(transitionOrders == null){
					transitionOrders = new ArrayList<>();
					map.put(0, transitionOrders);
				}
				transitionOrders.add(transitionOrder);
			} else {
				List<TransitionOrder> transitionOrders = map.get(-1);
				if(transitionOrders == null){
					transitionOrders = new ArrayList<>();
					map.put(-1, transitionOrders);
				}
				transitionOrders.add(transitionOrder);
			}
		}

		return map;
	}

	@Transactional
	@RequestMapping(value = "/shoppingCart/submitOrder.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public @ResponseBody SimpleJsonResponse submitOrder2(@RequestParam("entry[]") int[] entryIds,
														 @RequestParam("sentry[]") int[] entrySourceIds, //商品id
														 @RequestParam(value = "fenxiaoUserId[]") int[] fenxiaoUserIds,
														 @RequestParam(value = "stype[]") OrderEntrySourceType[] entrySourceTypes,
														 @RequestParam(value = "couponMoney[]",required = false) double[] couponMoneys,//优惠券 优惠掉的金额
														 @RequestParam(value = "amount[]") int[] amounts,
														 @RequestParam(value = "myAddressId") Long addressId,
														 @RequestParam(value = "userRemark", required = false) String[] userRemark1,
														 @RequestParam(value = "couponId[]",required = false) Integer[] couponId1,
														 //@RequestParam(value = "fenxiaoInfo",required = false) String fenxiaoInfo,//商品分销信息 (商品ID_分销商ID_商品数量,商品ID_分销商ID_商品数量)
														 @RequestParam(value = "couponIdInit[]",required = false) Integer[] couponIdInit1,
														 @RequestParam("totalPrice") double needPrice,
														 AbstractShoppingCart shoppingCart,
														 UserDetail currentUser,
														 Website website,
														 HttpSession session,
														 HttpServletRequest request) throws IOException {


		WebsiteUser websiteUser = websiteUserService.findOne(currentUser.getUserId());
		ShoppingOrder order = new ShoppingOrder();
		// Relationship relationship=new Relationship();
		if (websiteUser == null || websiteUser.getId() <= 0) {
			logger.info("SubmitOrder fail, websiteUser is null!");
			throw new PermissionDeniedException();
		}

		if(addressId == null || addressId < 1) {
			return new SimpleJsonResponse(false, "地址信息不存在或已经被删除,请您重新选择送货地址!");
		}

		WebsiteUserAddress address = websiteUserAddressService.findOne(addressId);
		if (address == null || address.getId() < 0) {
			return new SimpleJsonResponse(false, "地址信息不存在或已经被删除,请您重新选择送货地址!");
		}
		if (address.getUserId() != currentUser.getUserId()) {
			return new SimpleJsonResponse(false, "数据异常，请勿非法操作！");
		}
		if (address.getMobile() == null || address.getMobile().isEmpty()) {
			if (address.getPhone() == null || address.getPhone().isEmpty()) {
				return new SimpleJsonResponse(false, "手机号码和固定电话必须填写一个,请补全您的送货地址信息");
			}
		}
		if(address.getProvince() == null || address.getCity() == null || address.getCounty() == null) {
			return new SimpleJsonResponse(false, "当前地址已失效，请您删除后重新添加！");
		}
		if(address.getProvince().isEnabled() == false || address.getCity().isEnabled() == false || address.getCounty().isEnabled() == false) {
			return new SimpleJsonResponse(false, "当前地址已失效，请您删除后重新添加！");
		}
		if(address.getTown() != null && address.getTown().isEnabled() == false) {
			return new SimpleJsonResponse(false, "当前地址已失效，请您删除后重新添加！");
		}

		List<Integer> couponid = new ArrayList<>();
		if(couponIdInit1!=null && couponIdInit1.length>0)
		{
			for (Integer couponId : couponIdInit1) {
				List<UserCoupon> list = userCouponService.selectByUserAndCoupon(websiteUser.getId(), couponId);
				for (UserCoupon check : list ) {
					if( 1 == check.getStatus()){
						couponid.add(check.getCouponId());
					}
				}
			}
		}


		if (userRemark1 != null && userRemark1.length > 0) {
			int i = 0;
			for (String s : userRemark1) {
				i += s.length();
				if (i > 200) {
					return new SimpleJsonResponse(false, "买家留言最多可以输入200字！");
				}
			}
		}
		order.setReceiverName(address.getReceiverName());
		order.setReceiverMobile(address.getMobile());
		order.setReceiverPhone(address.getPhone());
		order.setReceiverAddr(address.getAddress());
		//身份证信息，跨境商品使用
		order.setReceiverIdentityCard(address.getIdentityCard());
		order.setProvince(address.getProvince());
		order.setCity(address.getCity());
		order.setCounty(address.getCounty());
		order.setTown(address.getTown());
//		if(userRemark1.length>1){
//			order.setRemark(null);
//		}else {
//			order.setRemark(userRemark1[0]);
//		}
		//检查自有产品库存，和供应商产品库存
		order.setUserId(currentUser.getUserId());
		order.setDeprecated(false);
		order.setRemoteIPAddr(request.getRemoteAddr());
		//order.setNeedPay(needPrice);

		//修改needPay

		//当前登陆用户所有地址信息
		WebsiteUserAddress websiteUserAddress;

		if (addressId != null && addressId > 0) {
			websiteUserAddress = websiteUserAddressService.findOne(addressId);
		} else {
			websiteUserAddress = websiteUserAddressService.findDefault(websiteUser);
		}

		List<ShoppingCartEntry> entries = new ArrayList<>();
		List<OrderEntry> orderEntries = new ArrayList<>();
		double shipmentFee2 = 0;
		List<Integer> providerid = new ArrayList<>();
		for (int i = 0; i < entryIds.length; i++) {
			if (amounts[i] > 0) {
				ShoppingCartEntry entry = new ShoppingCartEntry();
				entry.setEntryId(entryIds[i]);
				entry.setSourceEntryId(entrySourceIds[i]);
				entry.setSourceEntryType(null);
				entry.setAmount(amounts[i]);
				entries.add(entry);

				OrderEntry orderEntry = new OrderEntry();
				orderEntry.setStorageUnitId(entryIds[i]);
				orderEntry.setSourceEntryType(OrderEntrySourceType.valueOf(entrySourceTypes[i].name()));
				orderEntry.setSourceObjectId(entrySourceIds[i]);
				orderEntry.setAmount(amounts[i]);

				Product product = productService.findOne(entrySourceIds[i]);
				if(CollectionUtils.isEmpty(orderEntries))
				{
					orderEntries.add(orderEntry);
					shipmentFee2 = shipmentFee2+getShipmentFee(websiteUserAddress.getProvince(), orderEntry);
					if(product.getOwnerType() == OwnerType.provider ){
						WebsiteAdministrator provider = websiteAdministratorService.findOne(product.getOwnerId());
						providerid.add(provider.getId());
					}
				}else {
					if(product.getOwnerType() == OwnerType.provider ){
						WebsiteAdministrator provider = websiteAdministratorService.findOne(product.getOwnerId());
						boolean flag = true;
						for (Integer pid : providerid) {
							if(pid.equals(provider.getId()))
							{
								flag=false;
							}
						}
						if(flag)
						{
							orderEntries.add(orderEntry);
							shipmentFee2 = shipmentFee2+getShipmentFee(websiteUserAddress.getProvince(), orderEntry);
							providerid.add(provider.getId());
						}
					}
				}



			}
		}




//        if(websiteUserAddress != null) {
//        		shipmentFee2 = orderService.getShipmentFee(websiteUserAddress.getProvince(), orderEntries);
//        }

		//统计购物车的商品总数(TOTALAMOUNT),总金额(TOTALPRICE)
		Map<String, Object> resultMap = initSubmitOrderInfo(entries, website, shipmentFee2, websiteUser);

		//找到优惠券
		Set<Integer> catSet = new HashSet<>();
		for (int targetId :entrySourceIds ) {

			//分类优惠券
			Set<Integer> userCatsSet = userDefinedCateService.findProductCateIds(targetId);
			catSet.addAll(userCatsSet);
		}
		//处理3个list
		List<GoodsPriceVO> productList = new ArrayList<>();
		Map<Integer,GoodsPriceVO> map = new HashMap<>();
		Map<Integer,GoodsPriceVO> activityMap = new HashMap<>();
		Map<Integer, GoodsPriceVO> shopMap = new HashMap<>();
		for(Integer catid :catSet){
			GoodsPriceVO gvo = new GoodsPriceVO();
			gvo.setTargetId(catid);
			gvo.setTargetType(2);
			map.put(catid,gvo);
		}
		ProductCalcVO pvo = new ProductCalcVO();
		for (ShoppingCartEntry shoppingCartEntry :entries ) {

			Product product = productService.findOne(shoppingCartEntry.getSourceEntryId());

			//指定范围,1：指定商家可用。2：指定分类可用。3：指定商品可用。4：指定专题可用
			//商品
			GoodsPriceVO gvo = new GoodsPriceVO();
			gvo.setTargetId(shoppingCartEntry.getSourceEntryId());
			gvo.setAmount(product.getRetailPrice()*shoppingCartEntry.getAmount());
			gvo.setTargetType(3);
			productList.add(gvo);



			//商家
			GoodsPriceVO svo = shopMap.get(shoppingCartEntry.getSourceEntryId());
			if (svo == null) {
				svo = new GoodsPriceVO();
			}
			svo.setTargetId(product.getOwnerId());
			svo.setAmount(svo.getAmount()+ product.getRetailPrice()* shoppingCartEntry.getAmount());
			svo.setTargetType(1);
			if(shopMap.isEmpty())
			{
				shopMap.put(shoppingCartEntry.getSourceEntryId(), svo);
			}else {
				boolean flag2 = true;
				for (Integer i : shopMap.keySet()) {

					if(product.getOwnerId()!=null&&shopMap.get(i).getTargetId()!=null&&product.getOwnerId().equals(shopMap.get(i).getTargetId()))
					{
						flag2 = false;
					}
				}
				if(flag2)
				{
					shopMap.put(shoppingCartEntry.getSourceEntryId(), svo);
				}

			}



			//分类
			Set<Integer> userCatsSet = userDefinedCateService.findProductCateIds(shoppingCartEntry.getSourceEntryId());
			for (Integer i : userCatsSet) {
				map.get(i).setAmount(map.get(i).getAmount()+product.getRetailPrice()*shoppingCartEntry.getAmount());
				map.get(i).setTargetType(2);
			}

			//专题
			List<WebsiteCustomBlock> blocks = websiteCustomBlockService.findCustomBlockByTargetId(shoppingCartEntry.getSourceEntryId());
			if (blocks!=null) {
				for (WebsiteCustomBlock block : blocks) {
					GoodsPriceVO avo = activityMap.get(block.getSpecialActivityId());
					if (avo == null) {
						avo = new GoodsPriceVO();
					}
					avo.setTargetId(block.getSpecialActivityId());
					avo.setAmount(avo.getAmount() + product.getRetailPrice()*shoppingCartEntry.getAmount());
					avo.setTargetType(4);

					if(activityMap.isEmpty())
					{
						activityMap.put(block.getSpecialActivityId(), avo);
					}else {
						boolean flag3 = true;
						for (Integer i  : activityMap.keySet()) {
							if(block.getSpecialActivityId().equals(activityMap.get(i).getTargetId()))
							{
								flag3 = false;
							}
						}
						if(flag3)
						{
							activityMap.put(block.getSpecialActivityId(), avo);
						}
					}
				}
			}

		}
		pvo.setCatProductList(new ArrayList<GoodsPriceVO>(map.values()));
		pvo.setActivityProductList(new ArrayList<GoodsPriceVO>(activityMap.values()));
		pvo.setProductList(productList);
		pvo.setShopProductList(new ArrayList<GoodsPriceVO>(shopMap.values()));

		double diff = 0;
		//计算最优优惠券
		List<UserOrderCoupon> uocList = new ArrayList<>();
		List<UserOrderCoupon> couponlist;
		for (Integer cid : couponid) {
			couponlist = userCouponService.selectByCouponId(cid);
			if(!CollectionUtils.isEmpty(couponlist))
			{
				uocList.add(couponlist.get(0));
			}
		}
//       UserOrderCoupon calcCoupon = null;
//       Integer useCouponId = order.getCouponId();
//       if (useCouponId != null && useCouponId>0) {
//            List<UserOrderCoupon> couponlist= userCouponService.selectByCouponId(useCouponId);
//           calcCoupon = CollectionUtils.isEmpty(couponlist)?null:couponlist.get(0);
//       }else{
//    	   calcCoupon = null;
//       }
		for (UserOrderCoupon calcCoupon : uocList) {
			if (calcCoupon.getTargetType() != null && 1 == calcCoupon.getTargetType()) {
				diff = diff+calCouponForGoods(pvo.getShopProductList(), calcCoupon);
			}
			if (calcCoupon.getTargetType() != null && 2 == calcCoupon.getTargetType()) {
				diff = diff+calCouponForGoods(pvo.getCatProductList(), calcCoupon);
			}
			if (calcCoupon.getTargetType() != null && 3 == calcCoupon.getTargetType()) {
				diff = diff+calCouponForGoods(pvo.getProductList(), calcCoupon);
			}
			if (calcCoupon.getTargetType() != null && 4 == calcCoupon.getTargetType()) {
				diff = diff+calCouponForGoods(pvo.getActivityProductList(), calcCoupon);
			}
		}
//       calcCoupon = calcCoupon == null ? new UserOrderCoupon(): calcCoupon;

		//如果差额为0，最优优惠券也不能使用
//       if(0==getDiff(new CopyOnWriteArrayList<UserOrderCoupon>(), pvo, calcCoupon)){
//           calcCoupon = new UserOrderCoupon();
//       }


		if (CollectionUtils.isEmpty(uocList)){
			Object o = resultMap.get("TOTALPRICE");
			BigDecimal amount = new BigDecimal(o.toString());
			resultMap.put("TOTALPRICE",new NumberTool().format("#0.00",amount.doubleValue()));
		}else {
			BigDecimal b = new BigDecimal(diff);
			Object o = resultMap.get("TOTALPRICE");
			BigDecimal amount = new BigDecimal(o.toString());
			resultMap.put("TOTALPRICE",new NumberTool().format("#0.00", amount.subtract(b).doubleValue()));
		}
		double checkMoney = Double.parseDouble(resultMap.get("TOTALPRICE").toString());
		if(checkMoney != needPrice){
			return new SimpleJsonResponse(false, "订单金额异常！");
		}
		order.setNeedPay(checkMoney);

		//添加优惠掉的金额
		double d= 0d;
		for (Double couponMoney : couponMoneys) {
			d += couponMoney;
		}
		order.setCouponMoney(d);

		List<OrderEntry> entryList = new ArrayList<>();
		for (int i = 0; i < entryIds.length; i++) {
			if (amounts[i] <= 0) {
				logger.info("SubmitOrder fail, amounts less than or equal to zero!");
				return new SimpleJsonResponse(false, "网络错误，请重新操作");
			}
			Map<Boolean, String> result = checkOrder(order, website, entryIds[i], amounts[i], entrySourceTypes[i], address);
			String message = result.get(false);
			if (message != null && !message.isEmpty()) {
				return new SimpleJsonResponse(false, message);
			}

			OrderEntry entry = new OrderEntry();
			entry.setStorageUnitId(entryIds[i]);
			entry.setSourceEntryType(entrySourceTypes[i]);
			entry.setSourceObjectId(entrySourceIds[i]);
			if (amounts[i] < 0) {
				return new SimpleJsonResponse(false, "订单异常，请重新操作");
			}
			entry.setAmount(amounts[i]);
			entryList.add(entry);
		}

		//如果是提货券
		//增加提货券，这里如果券满足时强制用券
		//如果商品只有一个，且为提货券类商品，显示提货券数量
		//根据员工和产品ID查找专享价
		boolean isEnterpriseZoneProduct = false;
		Integer thisProductTotalVoucher = 0;
		Product product = null;
		Integer productId =new Long(entryList.get(0).getSourceObjectId()).intValue();
		Map<Integer,String> catMap = userDefinedCateService.findEnterpriseCatMap(productId);
		if(entryList!=null&&entryList.size()==1) {
			EnterpriseExclusiveProductPoolEntry productPoolEntry = productPoolEntryService.selectProductPoolEntry(websiteUser, productId);
			product = productService.findOne(productId);
			//是否为企业专区大礼包产品,根据这个值显示立即购买
			if (productPoolEntry != null && websiteUser != null && websiteUser.getId() > 0) {
				isEnterpriseZoneProduct = userDefinedCateService.isEnterpriseZoneProduct(productId);
				// 根据用户查询出可兑换券的数量，返回前台，做数量限制
				for(Map.Entry<Integer, String> entry:catMap.entrySet()) {
					thisProductTotalVoucher += enterprisePackageVoucherService.countUseableByUserPhoneAndProductName(entry.getValue(), websiteUser);
				}
				// 如果券不足则不使用
				if (thisProductTotalVoucher.intValue() < entryList.get(0).getAmount()) {
					thisProductTotalVoucher = 0;
				}
			}
		}
		//如果使用了提货券，则实付款为0
		boolean isVoucher = false;
		if (isEnterpriseZoneProduct && thisProductTotalVoucher.intValue() > 0
				&& thisProductTotalVoucher.intValue() >= entryList.get(0).getAmount()) {
			order.setVoucherNum(entryList.get(0).getAmount());
			isVoucher=true;
		}
		//增加提货券结束
		//如果使用了提货券，则无法使用优惠券
		if(isVoucher) {
			order.setCouponId(null);
			couponIdInit1 = null;
			couponId1 = null;
			couponMoneys = null;
		}

		ShoppingOrder myOrder;
		try {
			try {
				myOrder = orderService.create(order, entryList, website,needPrice);
				if(isVoucher && myOrder.getNeedPay()==0d) {
					shoppingOrderDao.setStatus(order, OrderStatus.waiting_shipment);
				}
				if(isVoucher) {
					int total = entryList.get(0).getAmount();
					for(Map.Entry<Integer, String> entry:catMap.entrySet()) {
						Integer count = enterprisePackageVoucherService.countUseableByUserPhoneAndProductName(entry.getValue(), websiteUser);
						if (total > 0 && count != null && count.intValue() > 0) {
							if (count.intValue() >= total) {
								enterprisePackageVoucherService.minusUseableByUserPhoneAndProductName(entry.getValue(),
										websiteUser, total, myOrder.getId());
								break;
							} else {
								enterprisePackageVoucherService.minusUseableByUserPhoneAndProductName(entry.getValue(),
										websiteUser, count, myOrder.getId());
								total = total - count;
							}
						}
					}
				}
				double needPayCheck = 0d;
				//double shipmentFee = 0d;nnn
				//拆分订单 并进行价格比对
				List<ShoppingOrder> doSplitBeforePay = orderService.doSplitBeforePay(myOrder,couponIdInit1,couponId1,userRemark1,couponMoneys);
				if(CollectionUtils.isNotEmpty(doSplitBeforePay)){
//                	for (ShoppingOrder checkOrder : doSplitBeforePay) {
//                		needPayCheck += checkOrder.getNeedPay();
//                		shipmentFee += checkOrder.getShipmentFee();
//					}
//                	if(needPayCheck != needPrice){
//                		return new SimpleJsonResponse(false, "订单提交金额有误！");
//                	}
				}else{
					return new SimpleJsonResponse(false, "订单提交失败");
				}
				order.setShipmentFee(shipmentFee2);
				shoppingOrderDao.save(order);
			} catch(OrderCreateException e) {
				logger.info("", e);
				return new SimpleJsonResponse(false, e.getMessage());
			}
		} catch (Exception e) {
			logger.info("", e);
			return new SimpleJsonResponse(false, "订单提交失败");
		}

		fenXiaoOrderService.create(order.getKey(),fenxiaoUserIds,entrySourceIds,amounts);



		//订单生成成功后清空购物车
		for (int i = 0; i < entryIds.length; i++) {
			shoppingCart.decrease(entryIds[i], amounts[i], ShoppingCartSourceEntryType.product, entrySourceIds[i]);
		}
		shoppingCart.flush();
		currentUser.setIntegral(websiteUser.getIntegral() - (int) myOrder.getNeedPay());
		session.setAttribute("currentUser", currentUser);
		return new SimpleJsonResponse(true, myOrder.getId());

	}

	private double getShipmentFee(GeoRegion province, OrderEntry entry)
	{
		double shipmentFee = 0d;
		if(entry!=null){
			//搜索产品
			Product product = productService.findOne(((Long)entry.getSourceObjectId()).intValue());
			//如果是供应商并且ID
			if(product.getOwnerType() == OwnerType.provider ){
				//找出供应商
				WebsiteAdministrator provider = websiteAdministratorService.findOne(product.getOwnerId());
				if(provider.isProvider() && provider.getSettingId() != null && provider.getSettingId() > 0) {
					//通过供应商ID找出快递费用模板
					ShipmentFeeSettingsExt shipmentFeeSettings = shipmentFeeSettingsExtService.findOne(provider.getSettingId());
					if(shipmentFeeSettings == null) {
						return 0.00;
					}
					//key是省份ID,通过key找到对应的价格
					Map<String,Double> settingMap = shipmentFeeSettings.getSettings();
					if(shipmentFee == 0) {
						shipmentFee = settingMap.get(String.valueOf(province.getId()));
						return shipmentFee;
					}
				}
			}
		}

		return shipmentFee;
	}


	@RequestMapping(value = "/shoppingCart/test.php", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	public @ResponseBody SimpleJsonResponse submitOrder2(@RequestParam("orderId") long orderId
	) throws IOException {
		ShoppingOrder findOne = orderService.findOne(orderId);
		List<ShoppingOrder> doSplitShoppingOrder = test.doSplitShoppingOrder(findOne,  OrderStatus.waiting_audit);
		return new SimpleJsonResponse(true, doSplitShoppingOrder);

	}

}
