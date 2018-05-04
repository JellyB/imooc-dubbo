package com.imooc.web.service.impl;

import com.imooc.curator.utils.DistributedLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imooc.item.service.ItemsService;
import com.imooc.order.service.OrdersService;
import com.imooc.web.service.CulsterService;

@Service("buyService")
public class CulsterServiceImpl implements CulsterService {
	
	final static Logger log = LoggerFactory.getLogger(CulsterServiceImpl.class);
	
	@Autowired
	private ItemsService itemService;

	@Autowired
	private OrdersService ordersService;


	@Autowired
	private DistributedLock distributedLock;
	
	@Override
	public void doBuyItem(String itemId) {
		// 减少库存
		itemService.displayReduceCounts(itemId, 1);
		
		// 创建订单
		ordersService.createOrder(itemId);
	}
	
	@Override
	public boolean displayBuy(String itemId){
		try{
			int buyCounts = 6;

			distributedLock.getLock();
			// 1. 判断库存
			int stockCounts = itemService.getItemCounts(itemId);
			if (stockCounts < buyCounts) {

				log.info("库存剩余{}件，用户需求量{}件，库存不足，订单创建失败...",
						stockCounts, buyCounts);
				distributedLock.releaseLock();
				return false;
			}

			//Thread.sleep(3000);

			// 2. 创建订单
			boolean isOrderCreated = ordersService.createOrder(itemId);

			// 3. 创建订单成功后，扣除库存
			if (isOrderCreated) {
				log.info("订单创建成功...");
				itemService.displayReduceCounts(itemId, buyCounts);
			} else {
				distributedLock.releaseLock();
				log.info("订单创建失败...");
				return false;
			}
			distributedLock.releaseLock();
			return true;
		}catch (Exception e){
			distributedLock.releaseLock();
			log.error(e.getMessage(), e);
		}
		return true;
	}
	
}

