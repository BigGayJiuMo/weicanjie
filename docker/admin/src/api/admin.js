import request from "./request";

// 管理员登录
export function adminLogin(data) {
  return request({
    url: "/admin/login",
    method: "post",
    data,
  });
}

// 获取商家账号列表
export function getMerchantList(params) {
  return request({
    url: "/admin/merchant/list",
    method: "get",
    params,
  });
}

// 新建商家账号
export function createMerchant(data) {
  return request({
    url: "/admin/merchant/add",
    method: "post",
    data,
  });
}

// 删除商家账号
export function deleteMerchant(id) {
  return request({
    url: `/admin/merchant/delete/${id}`,
    method: "delete",
  });
}

// 重置密码
export function resetMerchantPassword(id) {
  return request({
    url: `/admin/merchant/resetPassword/${id}`,
    method: "post",
  });
}

// 发送短信验证码（模拟）
export function sendCode(data) {
  return request({
    url: "/admin/merchant/sendCode",
    method: "post",
    params: data,
  });
}

// 验证码改密码
export function changePasswordByCode(data) {
  return request({
    url: "/admin/merchant/changePasswordByCode",
    method: "post",
    params: data,
  });
}

// 验证码绑定手机
export function bindPhoneByCode(data) {
  return request({
    url: "/admin/merchant/bindPhoneByCode",
    method: "post",
    params: data,
  });
}