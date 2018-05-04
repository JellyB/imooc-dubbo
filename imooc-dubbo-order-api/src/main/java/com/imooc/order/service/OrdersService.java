package com.imooc.order.service;

import com.imooc.order.pojo.Orders;

public interface OrdersService {

	/**
	 * @Description: 根据订单id查询订单
	 */
	public Orders getOrder(String orderId);
	
	/**
	 * @Description: 下订单
	 */
	public boolean createOrder(String itemId);

}

