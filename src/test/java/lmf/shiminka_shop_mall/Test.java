package lmf.shiminka_shop_mall;

import com.lmf.extend.pay.smkpay.json.SmkRefundRequestJson;
import com.lmf.order.entity.OrderPayLog;

import java.util.Date;

public class Test {

	public static void main(String[] args) {
		OrderPayLog payLog=new OrderPayLog();
		payLog.setPaiedTime(new Date());
//		double s1=86.11;
		double s1=25.58;
		double s2=0;
//		double s2=0;
		System.out.println( (int) Math.ceil(s1 * 100));
		SmkRefundRequestJson refundJson = new SmkRefundRequestJson(payLog.getPaiedTime(), new Date());
//		refundJson.amount = new BigDecimal(s1).multiply(new BigDecimal(100)).intValue()+new BigDecimal(s2).multiply(new BigDecimal(100)).intValue();
		refundJson.amount = (int) Math.ceil(s1 * 100)+(int) Math.ceil(s2* 100);
		System.out.println(refundJson.amount);
	}
}

