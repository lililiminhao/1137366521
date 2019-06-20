package lmf.shiminka_shop_mall;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lmf.order.entity.ShoppingOrder;
import com.lmf.order.repository.ShoppingOrderDao;
import com.lmf.order.repository.impl.ShoppingOrderDaoImpl;
import com.lmf.order.service.OrderService;
import com.lmf.website.service.WebsiteService;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@ContextConfiguration(locations="classpath*:/applicationContext.xml")
public class UnitTest {

	@Autowired
    private OrderService orderService;
	
	@Test
	public void test() {
		//ShoppingOrder order = orderService.findOne(1734);
		//orderService.shiminkaPayTransactionDispose(order, "123456", "", "");

	}
	

}

