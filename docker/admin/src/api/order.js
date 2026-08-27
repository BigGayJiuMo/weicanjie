import request from "./request";

// 后台分页
export function adminOrderPage(params) {
  return request({
    url: "/admin/order/page",
    method: "get",
    params
  });
}

// 后台详情
export function adminOrderDetail(id) {
  return request({
    url: `/admin/order/detail/${id}`,
    method: "get"
  });
}

// 修改订单状态
export function adminOrderUpdateStatus(id, status) {
  return request({
    url: `/admin/order/status/${id}`,
    method: "post",
    params: { status }
  });
}

// 用户申请退款（保持原路径）
export function requestRefund(data) {
  return request({
    url: "/order/refund/apply",
    method: "post",
    data
  });
}

// 后台同意退款（新路径）
export function adminRefundApprove(orderId) {
  return request({
    url: `/admin/order/refund/approve/${orderId}`,
    method: "post"
  });
}

// 后台拒绝退款（新路径）
export function adminRefundReject(orderId) {
  return request({
    url: `/admin/order/refund/reject/${orderId}`,
    method: "post"
  });
}
