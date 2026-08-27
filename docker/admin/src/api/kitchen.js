import request from "./request";

// 后厨订单列表
export function kitchenOrderList(params) {
  return request({
    url: "/kitchen/order/list",
    method: "get",
    params
  });
}

// 接单
export function kitchenAccept(orderId) {
  return request({
    url: `/kitchen/order/accept/${orderId}`,
    method: "post"
  });
}

// 完成制作
export function kitchenFinish(orderId) {
  return request({
    url: `/kitchen/order/finish/${orderId}`,
    method: "post"
  });
}
